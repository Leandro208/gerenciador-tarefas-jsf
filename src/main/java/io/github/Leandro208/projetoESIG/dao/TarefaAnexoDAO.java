package io.github.Leandro208.projetoESIG.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;

import io.github.Leandro208.projetoESIG.dominio.Tarefa;
import io.github.Leandro208.projetoESIG.dominio.TarefaAnexo;
import io.github.Leandro208.projetoESIG.dominio.Usuario;
import io.github.Leandro208.projetoESIG.dto.TarefaAnexoArquivoDTO;
import io.github.Leandro208.projetoESIG.dto.TarefaAnexoDTO;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.util.AccessControl;

public class TarefaAnexoDAO extends GenericDAOImpl {
	public static final int LIMITE_POR_TAREFA = 10;

	public List<TarefaAnexoDTO> listar(Long idTarefa) {
		List<?> linhas = getSession().createQuery(
				"SELECT a.id, a.nomeArquivo, a.tipoMime, a.tamanho, a.dataUpload, u.id "
				+ "FROM TarefaAnexo a LEFT JOIN a.autor u WHERE a.tarefa.id = :idTarefa "
				+ "ORDER BY a.dataUpload DESC, a.id DESC")
				.setParameter("idTarefa", idTarefa).getResultList();
		List<TarefaAnexoDTO> anexos = new ArrayList<>();
		for (Object linha : linhas) {
			Object[] valores = (Object[]) linha;
			anexos.add(new TarefaAnexoDTO((Long) valores[0], (String) valores[1], (String) valores[2],
					((Number) valores[3]).longValue(), (Date) valores[4], (Long) valores[5]));
		}
		return anexos;
	}

	public void salvar(Long idTarefa, UsuarioDTO usuario, String nome, String mime, byte[] conteudo)
			throws DAOException {
		EntityManager em = getSession();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Tarefa tarefa = em.find(Tarefa.class, idTarefa, LockModeType.PESSIMISTIC_WRITE);
			if (tarefa == null) throw new DAOException("A tarefa não foi encontrada.");
			Long idEquipe = tarefa.getEquipe() == null ? null : tarefa.getEquipe().getId();
			if (!AccessControl.podeAcessarEquipe(usuario, idEquipe)) {
				throw new SecurityException("Acesso não autorizado à tarefa.");
			}
			Long quantidade = (Long) em.createQuery(
					"SELECT COUNT(a.id) FROM TarefaAnexo a WHERE a.tarefa.id = :idTarefa")
					.setParameter("idTarefa", idTarefa).getSingleResult();
			if (quantidade >= LIMITE_POR_TAREFA) {
				throw new DAOException("Esta tarefa já atingiu o limite de 10 anexos.");
			}
			TarefaAnexo anexo = new TarefaAnexo();
			anexo.setTarefa(tarefa);
			anexo.setAutor(em.getReference(Usuario.class, usuario.getId()));
			anexo.setNomeArquivo(nome);
			anexo.setTipoMime(mime);
			anexo.setTamanho(conteudo.length);
			anexo.setDataUpload(new Date());
			anexo.setConteudo(conteudo);
			em.persist(anexo);
			tx.commit();
		} catch (Exception e) {
			if (tx.isActive()) tx.rollback();
			if (e instanceof DAOException) throw (DAOException) e;
			if (e instanceof SecurityException) throw (SecurityException) e;
			throw new DAOException("Não foi possível salvar o anexo.", e);
		}
	}

	public TarefaAnexoArquivoDTO buscarArquivoPermitido(Long idAnexo, UsuarioDTO usuario) {
		List<?> linhas = getSession().createQuery(
				"SELECT a.nomeArquivo, a.tipoMime, e.id FROM TarefaAnexo a "
				+ "JOIN a.tarefa t LEFT JOIN t.equipe e WHERE a.id = :idAnexo")
				.setParameter("idAnexo", idAnexo).getResultList();
		if (linhas.isEmpty()) return null;
		Object[] valores = (Object[]) linhas.get(0);
		if (!AccessControl.podeAcessarEquipe(usuario, (Long) valores[2])) return null;
		byte[] conteudo = (byte[]) getSession().createQuery(
				"SELECT a.conteudo FROM TarefaAnexo a WHERE a.id = :idAnexo")
				.setParameter("idAnexo", idAnexo).getSingleResult();
		return new TarefaAnexoArquivoDTO((String) valores[0], (String) valores[1], conteudo);
	}

	public void excluir(Long idAnexo, UsuarioDTO usuario) throws DAOException {
		EntityManager em = getSession();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			List<?> linhas = em.createQuery(
					"SELECT t.id, e.id, u.id FROM TarefaAnexo a JOIN a.tarefa t "
					+ "LEFT JOIN t.equipe e LEFT JOIN a.autor u WHERE a.id = :idAnexo")
					.setParameter("idAnexo", idAnexo).getResultList();
			if (linhas.isEmpty()) throw new DAOException("O anexo não foi encontrado.");
			Object[] valores = (Object[]) linhas.get(0);
			Long idTarefa = (Long) valores[0];
			Long idEquipe = (Long) valores[1];
			Long idAutor = (Long) valores[2];
			Tarefa tarefa = em.find(Tarefa.class, idTarefa, LockModeType.PESSIMISTIC_WRITE);
			if (tarefa == null || !AccessControl.podeAcessarEquipe(usuario, idEquipe)) {
				throw new SecurityException("Acesso não autorizado à tarefa.");
			}
			if (!usuario.isAdm() && !usuario.getId().equals(idAutor)) {
				throw new SecurityException("Somente quem enviou o anexo ou um administrador pode excluí-lo.");
			}
			em.createQuery("DELETE FROM TarefaAnexo a WHERE a.id = :idAnexo")
					.setParameter("idAnexo", idAnexo).executeUpdate();
			tx.commit();
		} catch (Exception e) {
			if (tx.isActive()) tx.rollback();
			if (e instanceof DAOException) throw (DAOException) e;
			if (e instanceof SecurityException) throw (SecurityException) e;
			throw new DAOException("Não foi possível excluir o anexo.", e);
		}
	}
}
