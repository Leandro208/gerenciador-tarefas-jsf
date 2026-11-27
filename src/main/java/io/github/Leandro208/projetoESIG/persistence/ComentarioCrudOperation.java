package io.github.Leandro208.projetoESIG.persistence;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;
import io.github.Leandro208.projetoESIG.dominio.Comentario;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dominio.Tarefa;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.util.AccessControl;

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
			validarPermissao(dao, operacao);
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

	private void validarPermissao(GenericDAOImpl dao, Operacao operacao) throws DAOException {
		UsuarioDTO user = AccessControl.requireUser();
		Comentario recebido = (Comentario) operacao.getEntidade();
		if (operacao.getComando().equals(ListaComando.CADASTRAR_COMENTARIO)) {
			if (recebido.getTarefa() == null || recebido.getTarefa().getId() == null) {
				throw new SecurityException("Tarefa inválida para comentário.");
			}
			Tarefa tarefa = dao.findById(recebido.getTarefa().getId(), Tarefa.class);
			if (tarefa == null || !AccessControl.podeAcessarEquipe(user,
					tarefa.getEquipe() == null ? null : tarefa.getEquipe().getId())) {
				throw new SecurityException("Acesso não autorizado ao comentário.");
			}
			if (user.getIdResponsavel() == null) {
				throw new SecurityException("O usuário não possui um responsável associado.");
			}
			Responsavel autor = dao.findById(user.getIdResponsavel(), Responsavel.class);
			if (autor == null) throw new SecurityException("O responsável não foi encontrado.");
			recebido.setAutor(autor);
			recebido.setTarefa(tarefa);
			return;
		}
		Comentario atual = recebido == null || recebido.getId() == null ? null
				: dao.findById(recebido.getId(), Comentario.class);
		if (atual == null || atual.getTarefa() == null || !AccessControl.podeAcessarEquipe(user,
				atual.getTarefa().getEquipe() == null ? null : atual.getTarefa().getEquipe().getId())
				|| !(user.isAdm() || (atual.getAutor() != null
				&& user.getIdResponsavel() != null
				&& user.getIdResponsavel().equals(atual.getAutor().getId())))) {
			throw new SecurityException("Acesso não autorizado ao comentário.");
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
