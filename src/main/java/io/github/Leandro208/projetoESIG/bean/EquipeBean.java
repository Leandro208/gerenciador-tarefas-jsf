package io.github.Leandro208.projetoESIG.bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import io.github.Leandro208.projetoESIG.dominio.Equipe;
import io.github.Leandro208.projetoESIG.exception.NegocioException;
import io.github.Leandro208.projetoESIG.persistence.ListaComando;
import io.github.Leandro208.projetoESIG.persistence.Operacao;
import io.github.Leandro208.projetoESIG.persistence.OperacaoCadastro;
import io.github.Leandro208.projetoESIG.services.EquipeService;

@ManagedBean
@SessionScoped
public class EquipeBean extends AbstractBean {

	private EquipeService service;
	private Equipe equipe;
	private List<Equipe> listaEquipes;
	
	public EquipeBean() {
		service = new EquipeService();
		equipe = new Equipe();
		listaEquipes = service.buscarTodos();
	}
	
	public String cadastrar() {
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
		}
		carregarEquipes();
		limpar();
		addMensagem("Equipe cadastrada com sucesso!");
		return "";
	}
	
	public void carregarEquipes() {
		listaEquipes = service.buscarTodos();
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
