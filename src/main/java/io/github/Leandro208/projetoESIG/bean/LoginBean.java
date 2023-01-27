package io.github.Leandro208.projetoESIG.bean;

import java.io.Serializable;
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

	private String email;
	private String senha;
	private Responsavel responsavel;
	private ResponsavelService service = new ResponsavelService();


	public String logar() {
		responsavel = new Responsavel();
		List<Responsavel> res = service.buscarTodos();

		for (Responsavel r : res) {
			if (email.equalsIgnoreCase(r.getEmail()) && senha.equalsIgnoreCase(r.getSenha())) {
				responsavel = r;
			}
		}

		if (responsavel.getId() != null) {
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
					.getSession(false);
			
			session.setAttribute("responsavel", responsavel);
			
			return "/restricted/index?faces-redirect=true";
		}
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario ou senha Invalida!", ""));

		return "";
	}

	public String logout() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

		responsavel = null;

		return "/login?faces-redirect=true";
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
