package io.github.Leandro208.projetoESIG.bean;

import javax.faces.bean.ManagedBean;

import io.github.Leandro208.projetoESIG.entities.Responsavel;
import io.github.Leandro208.projetoESIG.services.ResponsavelService;

@ManagedBean
public class ResponsavelBean {

	private Responsavel responsavel;

	private ResponsavelService responsavelService;

	public ResponsavelBean() {
		responsavel = new Responsavel();
		responsavelService = new ResponsavelService();
	}

	public String salvar() {
		responsavelService.salvar(responsavel);
		limpar();
		return "login.jsf";
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

}
