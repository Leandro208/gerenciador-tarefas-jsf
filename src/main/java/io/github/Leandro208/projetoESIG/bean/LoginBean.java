package io.github.Leandro208.projetoESIG.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;

import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpSession;

import io.github.Leandro208.projetoESIG.entities.Responsavel;
import io.github.Leandro208.projetoESIG.services.ResponsavelService;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String email;
	private String senha;
	private Responsavel responsavel;

	private ResponsavelService service;

	public LoginBean() {
		this.email = new String("");
		this.senha = new String("");
		responsavel = new Responsavel();
		service = new ResponsavelService();

	}

	public String logar() {
			//buscando Responsavel no dao
			List<Responsavel> res = new ArrayList<>();
			res = service.buscarTodos();
			for (Responsavel r : res) {
				if (email.equalsIgnoreCase(r.getEmail()) && senha.equalsIgnoreCase(r.getSenha())) {
					responsavel = r;
				}
			}
		
		if (responsavel.getId() != null && responsavel.getId() != 0) {
			// se for diferente de null ele da o acesso
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
					.getSession(false);

			session.setAttribute("responsavel", responsavel);

			return "/restricted/index?faces-redirect=true";
		}
		//se o usuario digitar os componentes errado exibe msg e carrega a pag
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario ou senha Invalida!", ""));

		return "login?faces-redirect=true";
	}

	public String logout() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		limpar();
		return "/login?faces-redirect=true";
	}

	private void limpar() {
		responsavel = new Responsavel();
		email = new String("");
		senha = new String("");
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Responsavel getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Responsavel responsavel) {
		this.responsavel = responsavel;
	}

}
