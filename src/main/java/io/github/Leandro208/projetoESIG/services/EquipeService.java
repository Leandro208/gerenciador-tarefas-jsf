package io.github.Leandro208.projetoESIG.services;

import java.io.Serializable;
import java.util.List;

import io.github.Leandro208.projetoESIG.dao.GenericDao;
import io.github.Leandro208.projetoESIG.entities.Equipe;

public class EquipeService implements BaseService<Equipe>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GenericDao<Equipe> dao;
	
	public EquipeService() {
		this.dao = new GenericDao<Equipe>();
	}

	@Override
	public Equipe buscarPorId(Long id) {
		return dao.buscarPorId(Equipe.class, id);
	}

	@Override
	public void salvar(Equipe t) {
		dao.salvar(t);
	}

	@Override
	public void remover(Equipe t) {
		dao.remover(Equipe.class, t.getId());	
	}
	
	public List<Equipe> buscarTodos(){
		StringBuilder hql = new StringBuilder("select e from Equipe e order by e.nome");
		return dao.buscarTodos(hql.toString());
	}

}
