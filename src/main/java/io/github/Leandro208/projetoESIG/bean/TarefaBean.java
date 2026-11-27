package io.github.Leandro208.projetoESIG.bean;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;


import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dominio.Comentario;
import io.github.Leandro208.projetoESIG.dto.FormConsultaTarefaDto;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.dominio.Equipe;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dominio.Tarefa;
import io.github.Leandro208.projetoESIG.enums.PrioridadeEnum;
import io.github.Leandro208.projetoESIG.enums.StatusEnum;
import io.github.Leandro208.projetoESIG.services.ComentarioService;
import io.github.Leandro208.projetoESIG.services.EquipeService;
import io.github.Leandro208.projetoESIG.services.ResponsavelService;
import io.github.Leandro208.projetoESIG.services.TarefaService;
import io.github.Leandro208.projetoESIG.util.MonitorTarefas;
import io.github.Leandro208.projetoESIG.util.HtmlSanitizer;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;
import io.github.Leandro208.projetoESIG.persistence.OperacaoCadastro;
import io.github.Leandro208.projetoESIG.persistence.ListaComando;
import io.github.Leandro208.projetoESIG.persistence.Operacao;

@ManagedBean
@SessionScoped
public class TarefaBean extends AbstractBean{

	private Tarefa tarefa;
	
	private Equipe equipe;

	private Comentario comentario;

	private FormConsultaTarefaDto formConsulta;

	private Map<Integer, List<Tarefa>> listaTarefas;

	private List<Comentario> comentarios;

	private TarefaService tarefaService;

	private ResponsavelService responsavelService;

	private ComentarioService comentarioService;

	private MonitorTarefas monitor;
	
	private final String FORM_TAREFA = "adm/formTarefa.jsf";

	private final String LISTA_TAREFA = "/restricted/listaTarefa.jsf";

	private boolean exibindoFormComentario = false;

	private boolean modoEdicao = false;
	
	public TarefaBean() throws ParseException {
		tarefa = new Tarefa();
		equipe = new Equipe();
		if(UsuarioUtils.usuarioTemEquipe()) {
			equipe = UsuarioUtils.getLogado().getEquipe();
		}
		tarefaService = new TarefaService();
		responsavelService = new ResponsavelService();
		comentarioService = new ComentarioService();
		formConsulta = new FormConsultaTarefaDto();
		listaTarefas = new HashMap<>();
		monitor = tarefaService.monitoramento();
		comentario = new Comentario();
		comentarios = new ArrayList<>();
	}
	
	public String entrarCadastro() {
		return navegar(FORM_TAREFA);
	}
	
	public String cadastrar() throws ParseException {
		
		Operacao operacao = new OperacaoCadastro();
		if(getConfirmButton().equals(BOTAO_CADASTRAR)) {
			operacao.setComando(ListaComando.CADASTRAR_TAREFA);
		} else if(getConfirmButton().equals(BOTAO_ALTERAR)) {
			operacao.setComando(ListaComando.ALTERAR_TAREFA);
		}
		
		operacao.setEntidade(tarefa);
		try {
			realizarOperacao(operacao);
			addMensagem("Operação realizada com sucesso!");
		} catch (Exception e) {
			addMensagemErroPadrao();
			return null;
		}
		limpar();
		dashboard();
		return null;
	}

	public String listar() {
		carregarTarefas();
		return "";
	}

	public String editar() throws ParseException {
		setConfirmButton(BOTAO_ALTERAR);
		return navegar(FORM_TAREFA);
	}

	public void moverTarefa() throws ParseException  {
		executarMoverTarefa(this.tarefa);
	}

	private boolean executarMoverTarefa(Tarefa tarefa) throws ParseException {
		Operacao operacao = new OperacaoCadastro();
		operacao.setComando(ListaComando.MOVER_TAREFA);
		operacao.setEntidade(tarefa);
		boolean sucesso = true;
		try {
			realizarOperacao(operacao);
		} catch (Exception e) {
			e.printStackTrace();
			sucesso = false;
		}
		carregarTarefas();
		dashboard();
		return sucesso;
	}

	public void moverTarefaArrastada() {
		Map<String, String> parametros = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String id = parametros.get("tarefaId");
		String status = parametros.get("status");

		if (id == null || status == null) {
			return;
		}

		try {
			Tarefa tarefaMovida = new Tarefa();
			tarefaMovida.setId(Long.valueOf(id));
			tarefaMovida.setStatus(StatusEnum.valueOf(Integer.parseInt(status)));

			if (!executarMoverTarefa(tarefaMovida)) {
				addMensagemErro("Não foi possível mover a tarefa.");
			}
		} catch (Exception e) {
			addMensagemErro("Não foi possível mover a tarefa.");
		}
	}

	public String remover() throws ParseException {
		Operacao operacao = new OperacaoCadastro();
		operacao.setComando(ListaComando.REMOVER);
		operacao.setEntidade(tarefa);
		try {
			realizarOperacao(operacao);
			addMensagem("Operação realizada com sucesso!");
		} catch (Exception e) {
			addMensagemErroPadrao();
			return null;
		}
		carregarTarefas();
		dashboard();
		limpar();
		return navegar(LISTA_TAREFA);
	}
	
	public String visualizarTarefa() {
		cancelarComentario();
		carregarComentarios();
		return "tarefaView.jsf?faces-redirect=true";
	}
	
	//atribui  tarefa ao usuario logado
	public void delegar() throws ParseException {
		if(tarefa.getResponsavel() == null) {
			tarefa.setResponsavel(new Responsavel());
			tarefa.getResponsavel().setId(getLogado().getIdResponsavel());
			tarefa.getResponsavel().setNome(getLogado().getNome());
		} else  {
			tarefa.setResponsavel(null);
		}

		Operacao operacao = new OperacaoCadastro();
		operacao.setComando(ListaComando.ATRIBUIR_TAREFA);
		operacao.setEntidade(tarefa);
		try {
			realizarOperacao(operacao);
		} catch (Exception e) {
			e.printStackTrace();
		}
		carregarTarefas();
		dashboard();
	}
	
	
	public String visualizarQuadro() {
		formConsulta.setEquipe(equipe);
		carregarTarefas();
		return navegar(LISTA_TAREFA);
	}
	
	public String visualizarMeuQuadro() {
		this.equipe = UsuarioUtils.getLogado().getEquipe();
		return visualizarQuadro();
	}

	public void abrirFormComentario() {
		if (!isPodeComentar()) {
			addMensagemErro("Apenas membros da equipe da tarefa podem comentar.");
			return;
		}
		this.comentario = new Comentario();
		this.modoEdicao = false;
		this.exibindoFormComentario = true;
	}

	public void editarComentario(Comentario comentarioSelecionado) {
		if (!podeEditar(comentarioSelecionado)) {
			addMensagemErro("Você só pode editar os seus próprios comentários.");
			return;
		}
		Comentario copia = new Comentario();
		copia.setId(comentarioSelecionado.getId());
		// Sanitiza tambem na edicao: comentarios antigos podem ter sido
		// gravados como texto puro e nao devem ser injetados no editor.
		copia.setTexto(HtmlSanitizer.sanitizar(comentarioSelecionado.getTexto()));
		copia.setAutor(comentarioSelecionado.getAutor());
		this.comentario = copia;
		this.modoEdicao = true;
		this.exibindoFormComentario = true;
	}

	public void cancelarComentario() {
		this.comentario = new Comentario();
		this.exibindoFormComentario = false;
		this.modoEdicao = false;
	}

	public void comentar() {
		if (comentario == null) {
			addMensagemErroPadrao();
			return;
		}
		boolean edicao = this.modoEdicao;
		if (edicao) {
			if (!podeEditar(comentario)) {
				addMensagemErro("Você só pode editar os seus próprios comentários.");
				return;
			}
		} else if (!isPodeComentar()) {
			addMensagemErro("Apenas membros da equipe da tarefa podem comentar.");
			return;
		}

		String textoSanitizado = HtmlSanitizer.sanitizar(comentario.getTexto());
		if (HtmlSanitizer.vazio(textoSanitizado)) {
			addMensagemErro("O comentário não pode estar vazio.");
			return;
		}
		comentario.setTexto(textoSanitizado);

		Operacao operacao = new OperacaoCadastro();
		operacao.setEntidade(comentario);
		if (edicao) {
			operacao.setComando(ListaComando.ALTERAR_COMENTARIO);
		} else {
			comentario.setTarefa(tarefa);
			UsuarioDTO logado = getLogado();
			if (logado != null && logado.getIdResponsavel() != null) {
				Responsavel autor = new Responsavel();
				autor.setId(logado.getIdResponsavel());
				comentario.setAutor(autor);
			}
			operacao.setComando(ListaComando.CADASTRAR_COMENTARIO);
		}

		try {
			realizarOperacao(operacao);
			cancelarComentario();
			carregarComentarios();
			addMensagem(edicao ? "Comentário atualizado com sucesso!" : "Comentário publicado com sucesso!");
		} catch (Exception e) {
			e.printStackTrace();
			addMensagemErroPadrao();
		}
	}

	public void removerComentario(Comentario comentarioSelecionado) {
		if (!podeExcluir(comentarioSelecionado)) {
			addMensagemErro("Você não tem permissão para excluir este comentário.");
			return;
		}
		Operacao operacao = new OperacaoCadastro();
		operacao.setComando(ListaComando.REMOVER_COMENTARIO);
		operacao.setEntidade(comentarioSelecionado);
		try {
			realizarOperacao(operacao);
			if (comentario != null && comentario.getId() != null
					&& comentario.getId().equals(comentarioSelecionado.getId())) {
				cancelarComentario();
			}
			carregarComentarios();
			addMensagem("Comentário removido com sucesso!");
		} catch (Exception e) {
			e.printStackTrace();
			addMensagemErroPadrao();
		}
	}

	public boolean isPodeComentar() {
		UsuarioDTO logado = getLogado();
		if (logado == null || tarefa == null) {
			return false;
		}
		if (logado.isAdm()) {
			return true;
		}
		return tarefa.getEquipe() != null && logado.getEquipe() != null
				&& tarefa.getEquipe().equals(logado.getEquipe());
	}

	public boolean podeEditar(Comentario comentarioSelecionado) {
		UsuarioDTO logado = getLogado();
		return comentarioSelecionado != null
				&& comentarioSelecionado.getAutor() != null
				&& comentarioSelecionado.getAutor().getId() != null
				&& logado != null
				&& comentarioSelecionado.getAutor().getId().equals(logado.getIdResponsavel());
	}

	public boolean podeExcluir(Comentario comentarioSelecionado) {
		UsuarioDTO logado = getLogado();
		return logado != null && (logado.isAdm() || podeEditar(comentarioSelecionado));
	}

	public void carregarComentarios(){
		if (tarefa == null || tarefa.getId() == null) {
			comentarios = new ArrayList<>();
			return;
		}
		comentarios = comentarioService.buscarPorTarefa(tarefa.getId());
	}

	public List<Tarefa> getObjectsForKey(int key) {
        return listaTarefas.get(key);
    }
	public void carregarTarefas() {
		listaTarefas = tarefaService.buscarTodos(formConsulta);
	}

	
	public List<SelectItem> getComboNiveisPrioridade() {
		List<SelectItem> itensComboNiveisPrioridade = new ArrayList<>();
		for (PrioridadeEnum p : PrioridadeEnum.values()) {
			boolean isSelecionado = tarefa.getPrioridade() == p;
			itensComboNiveisPrioridade.add(new SelectItem(p, p.name(), null, false, false, isSelecionado));
		}
		return itensComboNiveisPrioridade;
	}

	public List<SelectItem> getNiveisStatus() {
		List<SelectItem> niveisStatus = new ArrayList<>();
		for (StatusEnum s : StatusEnum.values()) {
			niveisStatus.add(new SelectItem(s, s.name()));
		}
		return niveisStatus;
	}
	

	public List<SelectItem> getComboEquipes() throws DAOException {
		List<SelectItem> itensComboBoxEquipe = new ArrayList<>();
		List<Equipe> equipes = new EquipeService().buscarTodos();
		for(Equipe equipe : equipes) {
			boolean isSelecionado = tarefa.getEquipe() != null && tarefa.getEquipe().getId() != null
					&& tarefa.getEquipe().equals(equipe);
			itensComboBoxEquipe.add(new SelectItem(equipe, equipe.getNome(), null, false, false, isSelecionado));
			
		}
		
		return itensComboBoxEquipe;
	}
	
	public List<SelectItem> getComboResponsaveis() throws DAOException {
		List<SelectItem> itensComboBoxResponsaveis = new ArrayList<>();
		List<Responsavel> responsaveis = responsavelService.buscarTodos();
		for (Responsavel r : responsaveis) {
			itensComboBoxResponsaveis.add(new SelectItem(r, r.getNome()));
		}
		return itensComboBoxResponsaveis;
	}

	public void dashboard() throws ParseException {
		monitor = tarefaService.monitoramento();
	}

	public void limpar() {
		tarefa = new Tarefa();
		formConsulta = new FormConsultaTarefaDto();
	}

	public String getCampoAtribuir() {
		Responsavel r = tarefa.getResponsavel();
		String result = "";
		if( r == null || r.getId() == null ){
			result = " | Delegar Para Mim";
		} else if(r.getId().equals(UsuarioUtils.getLogado().getIdResponsavel())) {
			result = " | Deixar tarefa";
		}
		return result;
	}

	public Tarefa getTarefa() {
		return tarefa;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
	}

	public Map<Integer, List<Tarefa>> getListaTarefas() {
		return listaTarefas;
	}

	public FormConsultaTarefaDto getFormConsulta() {
		return formConsulta;
	}

	public void setFormConsulta(FormConsultaTarefaDto formConsulta) {
		this.formConsulta = formConsulta;
	}

	public MonitorTarefas getMonitor() {
		return monitor;
	}

	public void setMonitor(MonitorTarefas monitor) {
		this.monitor = monitor;
	}

	public Equipe getEquipe() {
		return equipe;
	}

	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}

	public Comentario getComentario() {
		return comentario;
	}

	public void setComentario(Comentario comentario) {
		this.comentario = comentario;
	}

	public boolean isExibindoFormComentario() {
		return exibindoFormComentario;
	}

	public void setExibindoFormComentario(boolean exibindoFormComentario) {
		this.exibindoFormComentario = exibindoFormComentario;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public List<Comentario> getComentarios() {
		return comentarios;
	}
}
