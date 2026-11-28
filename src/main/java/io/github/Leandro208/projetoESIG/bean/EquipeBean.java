package io.github.Leandro208.projetoESIG.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dominio.Equipe;
import io.github.Leandro208.projetoESIG.exception.NegocioException;
import io.github.Leandro208.projetoESIG.persistence.ListaComando;
import io.github.Leandro208.projetoESIG.persistence.Operacao;
import io.github.Leandro208.projetoESIG.persistence.OperacaoCadastro;
import io.github.Leandro208.projetoESIG.services.EquipeService;
import io.github.Leandro208.projetoESIG.util.AccessControl;

@ManagedBean
@SessionScoped
public class EquipeBean extends AbstractBean {

	private EquipeService service;
	private Equipe equipe;
	private List<Equipe> listaEquipes;
	
	public EquipeBean() {
		service = new EquipeService();
		equipe = new Equipe();
	}

	public String entrarCadastro() {
		AccessControl.requireAdmin();
		limpar();
		setConfirmButton(BOTAO_CADASTRAR);
		return navegar("/restricted/adm/formEquipe.jsf");
	}
	
	public String cadastrar() throws DAOException {
		AccessControl.requireAdmin();
		Operacao operacao = new OperacaoCadastro();
		operacao.setComando(ListaComando.CADASTRAR);
		operacao.setEntidade(equipe);
		try {
			realizarOperacao(operacao);
		} catch (NegocioException ne){
			addMensagensErro(ne.getListaMensagens());
			return null;
		} catch (Exception e) {
			addMensagemErroPadrao();
			return null;
		}
		limpar();
		listaEquipes = null;
		addMensagem("Equipe cadastrada com sucesso!");
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
		if (listaEquipes == null) try {
			listaEquipes = service.buscarTodos();
		} catch (DAOException e) {
			listaEquipes = new ArrayList<>();
		}
		return listaEquipes;
	}

	public void setListaEquipes(List<Equipe> listaEquipes) {
		this.listaEquipes = listaEquipes;
	}
	
	
}
