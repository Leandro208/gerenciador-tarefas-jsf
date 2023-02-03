package io.github.Leandro208.projetoESIG.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;


import io.github.Leandro208.projetoESIG.entities.Responsavel;
import io.github.Leandro208.projetoESIG.enums.Funcao;
import io.github.Leandro208.projetoESIG.services.ResponsavelService;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;

@ManagedBean
public class ResponsavelBean {

	private Responsavel responsavel;
	private List<Responsavel> listaReponsaveis;
	private ResponsavelService responsavelService;

	public ResponsavelBean() {
		responsavel = new Responsavel();
		listaReponsaveis = new ArrayList<>();
		responsavelService = new ResponsavelService();
		listarResponsaveis();
	}

	public String salvar() {
		responsavelService.salvar(responsavel);
		limpar();
		return "login.jsf";
	}

	public void listarResponsaveis() {
		listaReponsaveis = responsavelService.buscarTodos();
		listaReponsaveis.remove(UsuarioUtils.getLogado());
	}
	
	public String alterarFuncao(Responsavel r) {
		 if(r.getFuncao().equals(Funcao.USER)) {
			 r.setFuncao(Funcao.ADM);
		 } else {
			 r.setFuncao(Funcao.USER);
		 }
		 responsavelService.salvar(r);
		 listarResponsaveis();
		 return "";
	}
	
	private void limpar() {
		responsavel = new Responsavel();
	}

	public Responsavel getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Responsavel responsavel) {
		this.responsavel = responsavel;
	}

	public ResponsavelService getResponsavelService() {
		return responsavelService;
	}

	public void setResponsavelService(ResponsavelService responsavelService) {
		this.responsavelService = responsavelService;
	}

	public List<Responsavel> getListaReponsaveis() {
		return listaReponsaveis;
	}

	public void setListaReponsaveis(List<Responsavel> listaReponsaveis) {
		this.listaReponsaveis = listaReponsaveis;
	}

	

	
}
