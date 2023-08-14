package io.github.Leandro208.projetoESIG.bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import io.github.Leandro208.projetoESIG.entities.Equipe;
import io.github.Leandro208.projetoESIG.services.EquipeService;

@ManagedBean
@SessionScoped
public class EquipeBean {

	private EquipeService service;
	private Equipe equipe;
	private List<Equipe> listaEquipes;
	
	public EquipeBean() {
		service = new EquipeService();
		equipe = new Equipe();
		listaEquipes = service.buscarTodos();
	}
	
	public String cadastrar() {
		service.salvar(equipe);
		limpar();
		return "";
	}
	
	private void limpar() {
		equipe = new Equipe();
	}
	
	public Equipe getEquipe() {
		return equipe;
	}

	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}

	public List<Equipe> getListaEquipes() {
		return listaEquipes;
	}

	public void setListaEquipes(List<Equipe> listaEquipes) {
		this.listaEquipes = listaEquipes;
	}
	
	
}
