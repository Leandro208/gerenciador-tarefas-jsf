package io.github.Leandro208.projetoESIG.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import io.github.Leandro208.projetoESIG.entities.Equipe;
import io.github.Leandro208.projetoESIG.entities.Responsavel;
import io.github.Leandro208.projetoESIG.enums.Funcao;
import io.github.Leandro208.projetoESIG.services.EquipeService;
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
	
	public List<Responsavel> completeResponsaveis(String query) {
        String queryLowerCase = query.toLowerCase();
        List<Responsavel> todosResponsaveis = responsavelService.buscarTodos();
        return todosResponsaveis.stream().filter(t -> t.getNome().toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
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
	
	
	public List<SelectItem> getComboEquipes(){
		List<SelectItem> itensComboBoxEquipe = new ArrayList<>();
		List<Equipe> equipes = new EquipeService().buscarTodos();
		for(Equipe equipe : equipes) {
			itensComboBoxEquipe.add(new SelectItem(equipe, equipe.getNome(), null, false, false));
		}
		return itensComboBoxEquipe;
	}
	
	public String campoAtribuir(Responsavel r) {
		
		String result = "";
		 if( r == null || r.getId() == null ){
			result = " | Delegar Para Mim";
		} else if(r.getId() == UsuarioUtils.getLogado().getId()) {
			result = " | Deixar tarefa";
		}  
		return result;
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
