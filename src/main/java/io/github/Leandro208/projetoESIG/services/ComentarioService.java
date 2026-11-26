package io.github.Leandro208.projetoESIG.services;

import java.io.Serializable;
import java.util.List;

import io.github.Leandro208.projetoESIG.dao.ComentarioDAO;
import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dominio.Comentario;

public class ComentarioService implements BaseService<Comentario>, Serializable {

	private static final long serialVersionUID = 1L;

	public List<Comentario> buscarPorTarefa(Long idTarefa) {
		return new ComentarioDAO().buscarPorTarefa(idTarefa);
	}

	public Comentario buscarPorId(Long id) {
		try {
			return new ComentarioDAO().findById(id, Comentario.class);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}
}
