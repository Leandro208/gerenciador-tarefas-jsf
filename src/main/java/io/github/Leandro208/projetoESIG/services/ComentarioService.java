package io.github.Leandro208.projetoESIG.services;

import java.io.Serializable;
import java.util.List;

import io.github.Leandro208.projetoESIG.dao.ComentarioDAO;
import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dominio.Comentario;

public class ComentarioService implements BaseService<Comentario>, Serializable {

	private static final long serialVersionUID = 1L;

	public List<Comentario> buscarPorTarefa(Long idTarefa) {
		ComentarioDAO dao = new ComentarioDAO();
		try {
			return dao.buscarPorTarefa(idTarefa);
		} finally {
			dao.close();
		}
	}

	public Comentario buscarPorId(Long id) throws DAOException {
		ComentarioDAO dao = new ComentarioDAO();
		try {
			return dao.findById(id, Comentario.class);
		} finally {
			dao.close();
		}

	}
}
