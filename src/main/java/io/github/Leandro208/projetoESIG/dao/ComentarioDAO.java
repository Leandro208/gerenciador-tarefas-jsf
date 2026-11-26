package io.github.Leandro208.projetoESIG.dao;

import java.util.List;

import io.github.Leandro208.projetoESIG.dominio.Comentario;

public class ComentarioDAO extends GenericDAOImpl {

	public List<Comentario> buscarPorTarefa(Long idTarefa) {
		return getSession().createQuery(
				"SELECT c FROM Comentario c "
						+ "LEFT JOIN FETCH c.autor "
						+ "LEFT JOIN FETCH c.registroAcesso ra "
						+ "LEFT JOIN FETCH ra.usuario "
						+ "WHERE c.tarefa.id = :idTarefa "
						+ "ORDER BY c.dataCadastro DESC", Comentario.class)
				.setParameter("idTarefa", idTarefa)
				.getResultList();
	}
}
