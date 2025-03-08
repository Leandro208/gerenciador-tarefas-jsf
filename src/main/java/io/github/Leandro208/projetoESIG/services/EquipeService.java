package io.github.Leandro208.projetoESIG.services;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.github.Leandro208.projetoESIG.dao.GenericDaoII;
import io.github.Leandro208.projetoESIG.entities.Equipe;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;

public class EquipeService implements BaseService<Equipe>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GenericDaoII<Equipe> dao;
	
	public EquipeService() {
		this.dao = new GenericDaoII<Equipe>();
	}



	public void salvar(Equipe t) {
		t.setDataCadastro(new Date());
		t.setRegistroEntrada(UsuarioUtils.getLogado().getRegistroEntrada());
		dao.salvar(t);
	}

	public void remover(Equipe t) {
		dao.remover(Equipe.class, t.getId());	
	}
	
	public List<Equipe> buscarTodos(){
		StringBuilder hql = new StringBuilder("select e from Equipe e order by e.nome");
		return dao.buscarTodos(hql.toString());
	}



	public Equipe buscarPorId(Long valueOf) {
		// TODO Auto-generated method stub
		return null;
	}

}
