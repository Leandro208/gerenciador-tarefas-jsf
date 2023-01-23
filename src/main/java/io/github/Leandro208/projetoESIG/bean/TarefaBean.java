package io.github.Leandro208.projetoESIG.bean;

import java.util.ArrayList;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import io.github.Leandro208.projetoESIG.dto.FormConsultaTarefaDto;
import io.github.Leandro208.projetoESIG.entities.Responsavel;
import io.github.Leandro208.projetoESIG.entities.Tarefa;
import io.github.Leandro208.projetoESIG.enums.PrioridadeEnum;
import io.github.Leandro208.projetoESIG.enums.StatusEnum;
import io.github.Leandro208.projetoESIG.services.ResponsavelService;
import io.github.Leandro208.projetoESIG.services.TarefaService;

@ManagedBean
@SessionScoped
public class TarefaBean {
	
	private Tarefa tarefa;

	private FormConsultaTarefaDto formConsulta;

	private List<Tarefa> listaTarefas;

	private TarefaService tarefaService;

	private ResponsavelService responsavelService;

	public TarefaBean() {
		tarefa = new Tarefa();
		tarefaService = new TarefaService();
		responsavelService = new ResponsavelService();
		formConsulta = new FormConsultaTarefaDto();
		listaTarefas = tarefaService.buscarTodos(formConsulta);
	}

	public String cadastrar() {
		tarefaService.salvar(tarefa);
		carregarTarefas();
		limpar();
		return "";
	}

	public String listar() {
		listaTarefas = tarefaService.buscarTodos(formConsulta);
		limpar();
		return "";
	}

	public String editar(Tarefa t) {
		this.tarefa = t;
		return "formTarefa.jsf";
	}

	public String concluir(Tarefa t) {
		t.setStatus(StatusEnum.CONCLUIDO);
		tarefaService.salvar(t);
		return "listaTarefa.jsf";
	}

	public String remover(Tarefa t) {
		tarefaService.remover(t);
		carregarTarefas();
		return "listaTarefa.jsf";
	}

	private void carregarTarefas() {
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

	public List<SelectItem> getComboResponsaveis() {
		List<SelectItem> itensComboBoxResponsaveis = new ArrayList<>();
		List<Responsavel> responsaveis = responsavelService.buscarTodos();
		for (Responsavel r : responsaveis) {
			boolean isSelecionado = tarefa.getResponsavel() != null && tarefa.getResponsavel().getId() != null
					&& tarefa.getResponsavel().equals(r);
			itensComboBoxResponsaveis.add(new SelectItem(r, r.getNome(), null, false, false, isSelecionado));

		}
		return itensComboBoxResponsaveis;
	}

	private void limpar() {
		tarefa = new Tarefa();
		formConsulta = new FormConsultaTarefaDto();
	}

	public Tarefa getTarefa() {
		return tarefa;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
	}

	public List<Tarefa> getListaTarefas() {
		return listaTarefas;
	}

	public void setListaTarefas(List<Tarefa> listaTarefas) {
		this.listaTarefas = listaTarefas;
	}

	public FormConsultaTarefaDto getFormConsulta() {
		return formConsulta;
	}

	public void setFormConsulta(FormConsultaTarefaDto formConsulta) {
		this.formConsulta = formConsulta;
	}

}
