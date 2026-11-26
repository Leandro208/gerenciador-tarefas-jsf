package io.github.Leandro208.projetoESIG.services;

import java.io.Serializable;
import java.util.List;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.EquipeDAO;
import io.github.Leandro208.projetoESIG.dao.GenericDAO;
import io.github.Leandro208.projetoESIG.dominio.Equipe;

public class EquipeService implements BaseService<Equipe>, Serializable {

	private static final long serialVersionUID = 1L;
	
	public List<Equipe> buscarTodos() throws DAOException {
		EquipeDAO dao = new EquipeDAO();
		try {
			return (List<Equipe>) dao.findAll(Equipe.class);
		} finally {
			dao.close();
		}
	}

	public Equipe buscarPorId(Long idEquipe) throws DAOException {
		EquipeDAO dao = new EquipeDAO();
		try {
            return dao.findById(idEquipe,Equipe.class);
        } finally {
			dao.close();
		}
    }

}
