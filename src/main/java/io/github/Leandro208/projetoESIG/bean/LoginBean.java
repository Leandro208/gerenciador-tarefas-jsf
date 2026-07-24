package io.github.Leandro208.projetoESIG.bean;

import java.io.Serializable;


import javax.faces.bean.ManagedBean;

import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpSession;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dominio.Usuario;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.services.ResponsavelService;
import io.github.Leandro208.projetoESIG.util.Message;

@ManagedBean
@SessionScoped
public class LoginBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String email;
	private String senha;

	private ResponsavelService service;

	public LoginBean() {
		this.email = "";
		this.senha = "";
		service = new ResponsavelService();
	}

	public String logar() throws DAOException {
		//buscando Responsavel no dao
		UsuarioDTO usuario = service.verificarCredenciais(email, senha);
		
		if (usuario.getId() != null && usuario.getId() != 0) {
			// se for diferente de null ele da o acesso
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
					.getSession(false);
			session.setMaxInactiveInterval(360);
			usuario.setRegistroAcesso(service.registrarAcesso(new Usuario(usuario.getId())));
			session.setAttribute("usuario", usuario);

			return "/restricted/index?faces-redirect=true";
		}
		//se o usuario digitar os componentes errado exibe msg e carrega a pag
		Message.erro("Usuario não encontrado! Email ou senha errado!");
		
		return null;
	}

	public String logout() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		limpar();
		return "/login?faces-redirect=true";
	}

	private void limpar() {
		email = "";
		senha = "";
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

}
