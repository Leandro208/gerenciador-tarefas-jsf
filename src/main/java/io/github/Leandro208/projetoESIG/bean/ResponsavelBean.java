package io.github.Leandro208.projetoESIG.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import io.github.Leandro208.projetoESIG.dao.DAOException;
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

	public void listarResponsaveis() throws DAOException {
		listaReponsaveis = responsavelService.buscarTodos();
		Long idLogado = UsuarioUtils.getLogado().getIdResponsavel();
		listaReponsaveis = listaReponsaveis.stream()
			.filter(r -> !r.getId().equals(idLogado))
			.collect(Collectors.toList());
	}
	
	public String alterarFuncao() throws DAOException {
		 if(responsavel.getUsuario().getFuncao().equals(Funcao.USER)) {
			 responsavel.getUsuario().setFuncao(Funcao.ADM);
		 } else {
			 responsavel.getUsuario().setFuncao(Funcao.USER);
		 }
		Operacao operacao = new OperacaoCadastro();
		operacao.setComando(ListaComando.ALTERAR_FUNCAO_USUARIO);
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

	public List<Responsavel> getListaReponsaveis() throws DAOException {
		listarResponsaveis();
		return listaReponsaveis;
	}

	public void setListaReponsaveis(List<Responsavel> listaReponsaveis) {
		this.listaReponsaveis = listaReponsaveis;
	}

	

	
}
