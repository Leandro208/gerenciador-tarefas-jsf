package io.github.Leandro208.projetoESIG.persistence;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;
import io.github.Leandro208.projetoESIG.dominio.Comentario;

/**
 * Operacoes de persistencia dos comentarios.
 * Diferente de CadastroCrudOperation, propaga a DAOException para que a
 * camada de apresentacao consiga informar a falha ao usuario.
 *
 * Na alteracao e na remocao o comentario e recarregado na mesma sessao,
 * evitando problemas com entidades/associacoes desanexadas e garantindo
 * que somente o texto seja alterado.
 */
public class ComentarioCrudOperation implements CrudOperation {

	@Override
	public void operar(Operacao operacao) throws DAOException {
		GenericDAOImpl dao = new GenericDAOImpl(operacao);
		try {
			if (operacao.getComando().equals(ListaComando.CADASTRAR_COMENTARIO)) {
				dao.create(operacao.getEntidade());
			} else if (operacao.getComando().equals(ListaComando.ALTERAR_COMENTARIO)) {
				alterar(dao, (Comentario) operacao.getEntidade());
			} else if (operacao.getComando().equals(ListaComando.REMOVER_COMENTARIO)) {
				remover(dao, (Comentario) operacao.getEntidade());
			}
		} finally {
			dao.close();
		}
	}

	private void alterar(GenericDAOImpl dao, Comentario recebido) throws DAOException {
		Comentario gerenciado = dao.findById(recebido.getId(), Comentario.class);
		if (gerenciado == null) {
			throw new DAOException("Comentário não encontrado.");
		}
		gerenciado.setTexto(recebido.getTexto());
		dao.update(gerenciado);
	}

	private void remover(GenericDAOImpl dao, Comentario recebido) throws DAOException {
		Comentario gerenciado = dao.findById(recebido.getId(), Comentario.class);
		if (gerenciado != null) {
			dao.remove(gerenciado);
		}
	}

	@Override
	public void validate(Operacao operacao) {
	}
}
