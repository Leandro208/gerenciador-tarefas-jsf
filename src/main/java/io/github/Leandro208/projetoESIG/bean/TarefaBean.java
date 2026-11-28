package io.github.Leandro208.projetoESIG.bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.Part;


import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dominio.Comentario;
import io.github.Leandro208.projetoESIG.dominio.HistoricoTarefa;
import io.github.Leandro208.projetoESIG.dto.FormConsultaTarefaDto;
import io.github.Leandro208.projetoESIG.dto.TarefaAnexoDTO;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.dominio.Equipe;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dominio.Tarefa;
import io.github.Leandro208.projetoESIG.enums.PrioridadeEnum;
import io.github.Leandro208.projetoESIG.enums.StatusEnum;
import io.github.Leandro208.projetoESIG.services.ComentarioService;
import io.github.Leandro208.projetoESIG.services.HistoricoTarefaService;
import io.github.Leandro208.projetoESIG.services.EquipeService;
import io.github.Leandro208.projetoESIG.services.ResponsavelService;
import io.github.Leandro208.projetoESIG.services.TarefaService;
import io.github.Leandro208.projetoESIG.services.TarefaAnexoService;
import io.github.Leandro208.projetoESIG.util.MonitorTarefas;
import io.github.Leandro208.projetoESIG.util.DescricaoTarefaSanitizer;
import io.github.Leandro208.projetoESIG.util.HtmlSanitizer;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;
import io.github.Leandro208.projetoESIG.util.AccessControl;
import io.github.Leandro208.projetoESIG.persistence.OperacaoCadastro;
import io.github.Leandro208.projetoESIG.persistence.ListaComando;
import io.github.Leandro208.projetoESIG.persistence.Operacao;

@ManagedBean
@SessionScoped
public class TarefaBean extends AbstractBean{
	private static final Logger LOGGER = Logger.getLogger(TarefaBean.class.getName());

	private Tarefa tarefa;
	
	private Equipe equipe;

	private Comentario comentario;

	private FormConsultaTarefaDto formConsulta;

	private Map<Integer, List<Tarefa>> listaTarefas;

	private List<Comentario> comentarios;
	private List<HistoricoTarefa> historicoTarefa;
	private List<TarefaAnexoDTO> anexosTarefa;
	private transient Part arquivoAnexo;
	private Long idTarefaAnexo;

	private TarefaService tarefaService;
	private EquipeService equipeService;

	private ResponsavelService responsavelService;

	private ComentarioService comentarioService;
	private HistoricoTarefaService historicoTarefaService;
	private TarefaAnexoService tarefaAnexoService;

	private MonitorTarefas monitor;
	private List<Equipe> equipesCache;
	private List<Responsavel> responsaveisCache;
	
	private final String FORM_TAREFA = "adm/formTarefa.jsf";

	private final String LISTA_TAREFA = "/restricted/listaTarefa.jsf";

	private boolean exibindoFormComentario = false;

	private boolean modoEdicao = false;
	
	public TarefaBean() {
		tarefa = new Tarefa();
		equipe = new Equipe();
		if(UsuarioUtils.usuarioTemEquipe()) {
			equipe = UsuarioUtils.getLogado().getEquipe();
		}
		tarefaService = new TarefaService();
		equipeService = new EquipeService();
		responsavelService = new ResponsavelService();
		comentarioService = new ComentarioService();
		historicoTarefaService = new HistoricoTarefaService();
		tarefaAnexoService = new TarefaAnexoService();
		formConsulta = new FormConsultaTarefaDto();
		listaTarefas = new HashMap<>();
		monitor = new MonitorTarefas();
		comentario = new Comentario();
		comentarios = new ArrayList<>();
		anexosTarefa = new ArrayList<>();
	}
	
	public String entrarCadastro() {
		setConfirmButton(BOTAO_CADASTRAR);
		return navegar(FORM_TAREFA);
	}
	
	public String cadastrar() throws ParseException {
		AccessControl.requireAdmin();
		tarefa.setDescricao(DescricaoTarefaSanitizer.sanitizar(tarefa.getDescricao()));
		
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
		if (formConsulta.getPrazoDe() != null && formConsulta.getPrazoAte() != null
				&& formConsulta.getPrazoDe().after(formConsulta.getPrazoAte())) {
			addMensagemErro("A data inicial do prazo deve ser anterior ou igual à data final.");
			return null;
		}
		carregarTarefas();
		return "";
	}

	public void limparFiltros() {
		Equipe equipeConsulta = formConsulta == null ? equipe : formConsulta.getEquipe();
		formConsulta = new FormConsultaTarefaDto();
		formConsulta.setEquipe(equipeConsulta);
		carregarTarefas();
	}

	public String editar() throws ParseException {
		AccessControl.requireAdmin();
		setConfirmButton(BOTAO_ALTERAR);
		return navegar(FORM_TAREFA);
	}

	public void moverTarefa() throws ParseException  {
		executarMoverTarefa(this.tarefa);
		carregarHistoricoTarefa();
	}

	private boolean executarMoverTarefa(Tarefa tarefa) throws ParseException {
		Operacao operacao = new OperacaoCadastro();
		operacao.setComando(ListaComando.MOVER_TAREFA);
		operacao.setEntidade(tarefa);
		boolean sucesso = true;
		try {
			realizarOperacao(operacao);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Falha ao mover tarefa", e);
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
		AccessControl.requireAdmin();
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
		Tarefa carregada = tarefa == null ? null : tarefaService.buscarPorIdPermitido(tarefa.getId(), getLogado());
		if (carregada == null) {
			addMensagemErro("Acesso não autorizado à tarefa.");
			return navegar("/restricted/listaTarefa.jsf");
		}
		tarefa = carregada;
		cancelarComentario();
		carregarComentarios();
		carregarHistoricoTarefa();
		idTarefaAnexo = tarefa.getId();
		carregarAnexos(idTarefaAnexo);
		return "tarefaView.jsf?faces-redirect=true";
	}

	public String enviarAnexo() {
		Long id = idTarefaAnexo != null ? idTarefaAnexo : (tarefa == null ? null : tarefa.getId());
		try {
			UsuarioDTO usuario = AccessControl.requireUser();
			if (tarefaService.buscarPorIdPermitido(id, usuario) == null) {
				throw new SecurityException("Acesso não autorizado à tarefa.");
			}
			if (arquivoAnexo == null || arquivoAnexo.getSize() == 0) {
				throw new IllegalArgumentException("Selecione um arquivo para enviar.");
			}
			if (arquivoAnexo.getSize() > TarefaAnexoService.TAMANHO_MAXIMO) {
				throw new IllegalArgumentException("O arquivo deve ter no máximo 10 MB.");
			}
			byte[] conteudo = lerArquivoLimitado(arquivoAnexo.getInputStream());
			tarefaAnexoService.adicionar(id, usuario, arquivoAnexo.getSubmittedFileName(), conteudo);
			tarefa = tarefaService.buscarPorIdPermitido(id, usuario);
			idTarefaAnexo = id;
			carregarAnexos(id);
			carregarHistoricoTarefa();
			addMensagem("Anexo enviado com sucesso.");
			return redirecionarParaTarefa();
		} catch (IllegalArgumentException | SecurityException e) {
			addMensagemErro(e.getMessage());
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Falha ao ler arquivo anexado à tarefa", e);
			addMensagemErro("Não foi possível ler o arquivo enviado.");
		} catch (DAOException e) {
			addMensagemErro(e.getMessage());
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Falha ao enviar anexo da tarefa", e);
			addMensagemErroPadrao();
		} finally {
			arquivoAnexo = null;
		}
		return null;
	}

	public String removerAnexo(Long idAnexo) {
		Long id = idTarefaAnexo != null ? idTarefaAnexo : (tarefa == null ? null : tarefa.getId());
		try {
			UsuarioDTO usuario = AccessControl.requireUser();
			tarefaAnexoService.excluir(idAnexo, usuario);
			tarefa = tarefaService.buscarPorIdPermitido(id, usuario);
			carregarAnexos(id);
			carregarHistoricoTarefa();
			addMensagem("Anexo removido.");
			return redirecionarParaTarefa();
		} catch (SecurityException | IllegalArgumentException e) {
			addMensagemErro(e.getMessage());
		} catch (DAOException e) {
			addMensagemErro(e.getMessage());
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Falha ao excluir anexo da tarefa", e);
			addMensagemErroPadrao();
		}
		return null;
	}

	private String redirecionarParaTarefa() {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
		return "/restricted/tarefaView.jsf?faces-redirect=true";
	}

	private void carregarAnexos(Long idTarefa) {
		if (idTarefa == null) {
			anexosTarefa = new ArrayList<>();
			return;
		}
		try {
			anexosTarefa = tarefaAnexoService.listar(idTarefa, getLogado());
		} catch (SecurityException e) {
			anexosTarefa = new ArrayList<>();
			addMensagemErro("Acesso não autorizado aos anexos desta tarefa.");
		}
	}

	private byte[] lerArquivoLimitado(InputStream input) throws IOException {
		try (InputStream stream = input; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[8192];
			int total = 0;
			int leitura;
			while ((leitura = stream.read(buffer)) != -1) {
				total += leitura;
				if (total > TarefaAnexoService.TAMANHO_MAXIMO) {
					throw new IllegalArgumentException("O arquivo deve ter no máximo 10 MB.");
				}
				output.write(buffer, 0, leitura);
			}
			return output.toByteArray();
		}
	}
	
	//atribui  tarefa ao usuario logado
	public void delegar() throws ParseException {
		AccessControl.requireUser();
		if (tarefa.getResponsavel() != null && tarefa.getResponsavel().getId() != null
				&& !tarefa.getResponsavel().getId().equals(getLogado().getIdResponsavel())
				&& !getLogado().isAdm()) {
			addMensagemErro("Somente o responsável atual ou um administrador pode liberar esta tarefa.");
			return;
		}
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
			carregarHistoricoTarefa();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Falha ao atribuir tarefa", e);
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
			LOGGER.log(Level.SEVERE, "Falha ao salvar comentário", e);
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
			LOGGER.log(Level.SEVERE, "Falha ao remover comentário", e);
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

	public void carregarHistoricoTarefa() {
		if (tarefa == null || tarefa.getId() == null) {
			historicoTarefa = new ArrayList<>();
			return;
		}
		historicoTarefa = historicoTarefaService.buscarPorTarefa(tarefa.getId());
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

	public List<SelectItem> getPrioridadesConsulta() {
		List<SelectItem> itens = new ArrayList<>();
		for (PrioridadeEnum prioridade : PrioridadeEnum.values()) {
			itens.add(new SelectItem(prioridade, prioridade.name()));
		}
		return itens;
	}
	

	public List<SelectItem> getComboEquipes() throws DAOException {
		List<SelectItem> itensComboBoxEquipe = new ArrayList<>();
		if (equipesCache == null) equipesCache = equipeService.buscarTodos();
		for(Equipe equipe : equipesCache) {
			boolean isSelecionado = tarefa.getEquipe() != null && tarefa.getEquipe().getId() != null
					&& tarefa.getEquipe().equals(equipe);
			itensComboBoxEquipe.add(new SelectItem(equipe, equipe.getNome(), null, false, false, isSelecionado));
			
		}
		
		return itensComboBoxEquipe;
	}
	
	public List<SelectItem> getComboResponsaveis() throws DAOException {
		List<SelectItem> itensComboBoxResponsaveis = new ArrayList<>();
		if (responsaveisCache == null) responsaveisCache = responsavelService.buscarTodos();
		for (Responsavel r : responsaveisCache) {
			itensComboBoxResponsaveis.add(new SelectItem(r, r.getNome()));
		}
		return itensComboBoxResponsaveis;
	}

	public void prepararPainel() {
		limpar();
		dashboard();
	}

	public void dashboard() {
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

	public List<HistoricoTarefa> getHistoricoTarefa() {
		return historicoTarefa == null ? new ArrayList<HistoricoTarefa>() : historicoTarefa;
	}

	public List<TarefaAnexoDTO> getAnexosTarefa() {
		return anexosTarefa == null ? new ArrayList<TarefaAnexoDTO>() : anexosTarefa;
	}
	public Part getArquivoAnexo() { return arquivoAnexo; }
	public void setArquivoAnexo(Part arquivoAnexo) { this.arquivoAnexo = arquivoAnexo; }
	public Long getIdTarefaAnexo() { return idTarefaAnexo; }
	public void setIdTarefaAnexo(Long idTarefaAnexo) { this.idTarefaAnexo = idTarefaAnexo; }
}
