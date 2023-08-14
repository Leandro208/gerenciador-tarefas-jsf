package io.github.Leandro208.projetoESIG.bean;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;

import io.github.Leandro208.projetoESIG.dto.FormConsultaTarefaDto;
import io.github.Leandro208.projetoESIG.entities.Equipe;
import io.github.Leandro208.projetoESIG.entities.Responsavel;
import io.github.Leandro208.projetoESIG.entities.Tarefa;
import io.github.Leandro208.projetoESIG.enums.PrioridadeEnum;
import io.github.Leandro208.projetoESIG.enums.StatusEnum;
import io.github.Leandro208.projetoESIG.services.EquipeService;
import io.github.Leandro208.projetoESIG.services.ResponsavelService;
import io.github.Leandro208.projetoESIG.services.TarefaService;
import io.github.Leandro208.projetoESIG.util.MonitorTarefas;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;

@ManagedBean
@SessionScoped
public class TarefaBean {

	private Tarefa tarefa;

	private FormConsultaTarefaDto formConsulta;

	private HashMap<Integer, List<Tarefa>> listaTarefas;

	private TarefaService tarefaService;

	private ResponsavelService responsavelService;

	private MonitorTarefas monitor;

	public TarefaBean() throws ParseException {
		tarefa = new Tarefa();
		tarefaService = new TarefaService();
		responsavelService = new ResponsavelService();
		formConsulta = new FormConsultaTarefaDto();
		listaTarefas = tarefaService.buscarTodos(formConsulta);
		monitor = tarefaService.monitoramento();
	}

	public String cadastrar() throws ParseException {
		tarefaService.salvar(tarefa);
		carregarTarefas();
		limpar();
		dashboard();
		return "";
	}

	public String listar() {
		listaTarefas = tarefaService.buscarTodos(formConsulta);
		limpar();
		return "";
	}

	public String editar() throws ParseException {
		return "adm/formTarefa?faces-redirect=true";
	}

	public void moverTarefa() throws ParseException  {
		tarefaService.salvar(tarefa);
		carregarTarefas();
		dashboard();
		//PrimeFaces.current().executeScript("window.location.reload();");
	}

	public String remover() throws ParseException {
		tarefaService.remover(tarefa);
		carregarTarefas();
		dashboard();
		limpar();
		return "listaTarefa.jsf";
	}
	
	public String visualizarTarefa(Tarefa t) {
		this.tarefa = t;
		return "tarefaView.jsf";
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
		itensComboBoxResponsaveis.add(new SelectItem(new Responsavel(), "-SELECIONE-", null, false, false, true));
		for (Responsavel r : responsaveis) {
			boolean isSelecionado = tarefa.getResponsavel() != null && tarefa.getResponsavel().getId() != null
					&& tarefa.getResponsavel().equals(r);
			itensComboBoxResponsaveis.add(new SelectItem(r, r.getNome(), null, false, false, isSelecionado));

		}
		return itensComboBoxResponsaveis;
	}

	public String corDias(Tarefa t) throws ParseException {
		String dias = t.getDias();

		if (dias.contains("Atrasado"))
			return "red";
		else if (dias.contains("atraso"))
			return "orange";
		else if (dias.contains("hoje"))
			return "#ffd700";
		else if (dias.contains("prazo"))
			return "green";
		else
			return "blue";
	}
	
	public String selecionado(Tarefa t) {
		this.tarefa = t;
		return "listaTarefa?faces-redirect=true";
	}

	public void dashboard() throws ParseException {
		monitor = tarefaService.monitoramento();
	}

	public void limpar() {
		tarefa = new Tarefa();
		formConsulta = new FormConsultaTarefaDto();
	}

	public Tarefa getTarefa() {
		return tarefa;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
	}

	public HashMap<Integer, List<Tarefa>> getListaTarefas() {
		return listaTarefas;
	}

	public void setListaTarefas(HashMap<Integer, List<Tarefa>> listaTarefas) {
		this.listaTarefas = listaTarefas;
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
	
	

}
