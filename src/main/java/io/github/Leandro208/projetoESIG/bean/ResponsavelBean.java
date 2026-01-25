package io.github.Leandro208.projetoESIG.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import io.github.Leandro208.projetoESIG.dominio.Equipe;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dominio.Usuario;
import io.github.Leandro208.projetoESIG.enums.Funcao;
import io.github.Leandro208.projetoESIG.exception.NegocioException;
import io.github.Leandro208.projetoESIG.persistence.ListaComando;
import io.github.Leandro208.projetoESIG.persistence.Operacao;
import io.github.Leandro208.projetoESIG.persistence.OperacaoCadastro;
import io.github.Leandro208.projetoESIG.services.EquipeService;
import io.github.Leandro208.projetoESIG.services.ResponsavelService;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;

@ManagedBean
public class ResponsavelBean extends AbstractBean{

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
		Operacao operacao = new OperacaoCadastro();
		operacao.setComando(ListaComando.CADASTRAR_USUARIO);
		operacao.setEntidade(responsavel);
		try {
			realizarOperacao(operacao);
		} catch (NegocioException ne){
			addMensagensErro(ne.getListaMensagens());
			return null;
		}
		catch (Exception e) {
			addMensagemErroPadrao();
		}
		limpar();
		return "login.jsf";
	}

	public void listarResponsaveis() {
		listaReponsaveis = responsavelService.buscarTodos();
		listaReponsaveis.remove(UsuarioUtils.getLogado());
	}
	
	public String alterarFuncao() {
		 if(responsavel.getUsuario().getFuncao().equals(Funcao.USER)) {
			 responsavel.getUsuario().setFuncao(Funcao.ADM);
		 } else {
			 responsavel.getUsuario().setFuncao(Funcao.USER);
		 }
		 //TODO refazer
		// responsavelService.salvar(r);
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

	public List<Responsavel> getListaReponsaveis() {
		return listaReponsaveis;
	}

	public void setListaReponsaveis(List<Responsavel> listaReponsaveis) {
		this.listaReponsaveis = listaReponsaveis;
	}

	

	
}
