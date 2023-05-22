package io.github.Leandro208.projetoESIG.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;


import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;

import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpSession;

import io.github.Leandro208.projetoESIG.entities.Responsavel;
import io.github.Leandro208.projetoESIG.services.ResponsavelService;
import io.github.Leandro208.projetoESIG.util.Criptografar;
import io.github.Leandro208.projetoESIG.util.Message;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String email;
	private String senha;
	private Responsavel responsavel;
	
	private Calendar horaAtual;

	private ResponsavelService service;

	public LoginBean() {
		this.email = new String("");
		this.senha = new String("");
		responsavel = new Responsavel();
		service = new ResponsavelService();
		horaAtual = Calendar.getInstance();
		horaAtual.set(Calendar.SECOND, 0);
		horaAtual.set(Calendar.MINUTE, 0);
		horaAtual.set(Calendar.HOUR_OF_DAY, 0);

	}

	public String logar() {
			//buscando Responsavel no dao
			List<Responsavel> res = new ArrayList<>();
			res = service.buscarTodos();
			for (Responsavel r : res) {
				if (r.getEmail().equals(email) && r.getSenha().equals(Criptografar.encriptografar(senha))) {
					responsavel = r;
					System.out.println("achou");
				}
			}
		
		if (responsavel.getId() != null && responsavel.getId() != 0) {
			// se for diferente de null ele da o acesso
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
					.getSession(false);

			session.setAttribute("responsavel", responsavel);

			return "/restricted/index?faces-redirect=true";
		}
		System.out.println("Não achou");
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
		responsavel = new Responsavel();
		email = new String("");
		senha = new String("");
	}
	
	public void relogio(){
        horaAtual.set(Calendar.SECOND, horaAtual.get(Calendar.SECOND)+1);
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

	public Calendar getHoraAtual() {
		return horaAtual;
	}

	public void setHoraAtual(Calendar horaAtual) {
		this.horaAtual = horaAtual;
	}

	

}
