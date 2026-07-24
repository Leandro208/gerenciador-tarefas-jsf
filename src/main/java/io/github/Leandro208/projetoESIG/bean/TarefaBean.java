package io.github.Leandro208.projetoESIG.bean;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;


import io.github.Leandro208.projetoESIG.dto.FormConsultaTarefaDto;
import io.github.Leandro208.projetoESIG.dominio.Equipe;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dominio.Tarefa;
import io.github.Leandro208.projetoESIG.enums.PrioridadeEnum;
import io.github.Leandro208.projetoESIG.enums.StatusEnum;
import io.github.Leandro208.projetoESIG.services.EquipeService;
import io.github.Leandro208.projetoESIG.services.ResponsavelService;
import io.github.Leandro208.projetoESIG.services.TarefaService;
import io.github.Leandro208.projetoESIG.util.MonitorTarefas;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;
import io.github.Leandro208.projetoESIG.persistence.OperacaoCadastro;
import io.github.Leandro208.projetoESIG.persistence.ListaComando;
import io.github.Leandro208.projetoESIG.persistence.Operacao;

@ManagedBean
@SessionScoped
public class TarefaBean extends AbstractBean{

	private Tarefa tarefa;
	
	private Equipe equipe;

	private FormConsultaTarefaDto formConsulta;

	private Map<Integer, List<Tarefa>> listaTarefas;

	private TarefaService tarefaService;

	private ResponsavelService responsavelService;

	private MonitorTarefas monitor;
	
	private final String FORM_TAREFA = "adm/formTarefa.jsf";

	private final String LISTA_TAREFA = "/restricted/listaTarefa.jsf";
	
	public TarefaBean() throws ParseException {
		tarefa = new Tarefa();
		equipe = new Equipe();
		if(UsuarioUtils.usuarioTemEquipe()) {
			equipe = UsuarioUtils.getLogado().getEquipe();
		}
		tarefaService = new TarefaService();
		responsavelService = new ResponsavelService();
		formConsulta = new FormConsultaTarefaDto();
		listaTarefas = new java.util.HashMap<>();
		monitor = tarefaService.monitoramento();
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
		Operacao operacao = new OperacaoCadastro();
		operacao.setComando(ListaComando.MOVER_TAREFA);
		operacao.setEntidade(tarefa);
		try {
			realizarOperacao(operacao);
		} catch (Exception e) {
			e.printStackTrace();
		}
		carregarTarefas();
		dashboard();
		//PrimeFaces.current().executeScript("window.location.reload();");
	}

	public String remover() throws ParseException {
		tarefaService.remover(tarefa);
		carregarTarefas();
		dashboard();
		limpar();
		return navegar(LISTA_TAREFA);
	}
	
	public String visualizarTarefa() {
		return "tarefaView.jsf";
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
	

	public List<SelectItem> getComboEquipes(){
		List<SelectItem> itensComboBoxEquipe = new ArrayList<>();
		List<Equipe> equipes = new EquipeService().buscarTodos();
		for(Equipe equipe : equipes) {
			boolean isSelecionado = tarefa.getEquipe() != null && tarefa.getEquipe().getId() != null
					&& tarefa.getEquipe().equals(equipe);
			itensComboBoxEquipe.add(new SelectItem(equipe, equipe.getNome(), null, false, false, isSelecionado));
			
		}
		
		return itensComboBoxEquipe;
	}
	
	public List<SelectItem> getComboResponsaveis() {
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

}
