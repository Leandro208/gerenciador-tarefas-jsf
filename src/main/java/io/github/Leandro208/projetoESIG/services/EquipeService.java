package io.github.Leandro208.projetoESIG.services;

import java.io.Serializable;
import java.util.List;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.EquipeDAO;
import io.github.Leandro208.projetoESIG.dao.GenericDAO;
import io.github.Leandro208.projetoESIG.dominio.Equipe;

public class EquipeService implements BaseService<Equipe>, Serializable {

	private static final long serialVersionUID = 1L;

	private GenericDAO dao;
	
	public EquipeService() {
		this.dao = new EquipeDAO();
	}
	
	public List<Equipe> buscarTodos() throws DAOException {
		return (List<Equipe>) dao.findAll(Equipe.class);
	}

	public Equipe buscarPorId(Long idEquipe) {
        try {
            return dao.findById(idEquipe,Equipe.class);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

}
