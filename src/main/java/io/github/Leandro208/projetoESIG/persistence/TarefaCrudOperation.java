package io.github.Leandro208.projetoESIG.persistence;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAO;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;
import io.github.Leandro208.projetoESIG.dominio.Tarefa;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.util.AccessControl;

public class TarefaCrudOperation extends CadastroCrudOperation {

	@Override
	public void operar(Operacao operacao) throws DAOException {
		validate(operacao);
		if(operacao.getComando().equals(ListaComando.CADASTRAR_TAREFA)) {
			criar(operacao);
		} else if(operacao.getComando().equals(ListaComando.ALTERAR_TAREFA)) {
			alterar(operacao);
		} else if(operacao.getComando().equals(ListaComando.ATRIBUIR_TAREFA)) {
			atribuirTarefa(operacao);
		} else if(operacao.getComando().equals(ListaComando.MOVER_TAREFA)) {
			moverTarefa(operacao);
		} else if (operacao.getComando().equals(ListaComando.REMOVER_TAREFA)) {
			remover(operacao);
		}
	}

	private void moverTarefa(Operacao operacao) throws DAOException {
		GenericDAO dao = new GenericDAOImpl(operacao);
		try {
			Tarefa tarefa = (Tarefa) operacao.getEntidade();
			dao.updateField(tarefa.getId(), Tarefa.class,"status", tarefa.getStatus());
		} finally {
			dao.close();
		}
	}

	private void atribuirTarefa(Operacao operacao) throws DAOException {
		GenericDAO dao = new GenericDAOImpl(operacao);
		try {
			Tarefa tarefa = (Tarefa) operacao.getEntidade();
			Responsavel responsavel = tarefa.getResponsavel() == null ? null
					: dao.findById(tarefa.getResponsavel().getId(), Responsavel.class);
			if (tarefa.getResponsavel() != null && responsavel == null) {
				throw new DAOException("Responsável não encontrado.");
			}
			dao.updateField(tarefa.getId(), Tarefa.class,"responsavel", responsavel);
		} finally {
			dao.close();
		}
	}

	@Override
	public void validate(Operacao operacao) {
		if (operacao.getComando().equals(ListaComando.CADASTRAR_TAREFA)
				|| operacao.getComando().equals(ListaComando.ALTERAR_TAREFA)
				|| operacao.getComando().equals(ListaComando.REMOVER_TAREFA)) {
			AccessControl.requireAdmin();
			return;
		}
		UsuarioDTO user = AccessControl.requireUser();
		Tarefa recebida = (Tarefa) operacao.getEntidade();
		if (recebida == null || recebida.getId() == null) {
			throw new SecurityException("Tarefa inválida.");
		}
		GenericDAOImpl dao = new GenericDAOImpl();
		try {
			Tarefa atual = dao.findById(recebida.getId(), Tarefa.class);
			if (atual == null || !AccessControl.podeAcessarEquipe(user,
					atual.getEquipe() == null ? null : atual.getEquipe().getId())) {
				throw new SecurityException("Acesso não autorizado à tarefa.");
			}
			if (operacao.getComando().equals(ListaComando.ATRIBUIR_TAREFA)
					&& !user.isAdm() && atual.getResponsavel() != null
					&& (user.getIdResponsavel() == null
							|| !user.getIdResponsavel().equals(atual.getResponsavel().getId()))) {
				throw new SecurityException("Somente o responsável atual pode alterar a atribuição.");
			}
			if (operacao.getComando().equals(ListaComando.ATRIBUIR_TAREFA)
					&& recebida.getResponsavel() != null) {
				Long idResponsavel = recebida.getResponsavel().getId();
				if (idResponsavel == null || (!user.isAdm() && !idResponsavel.equals(user.getIdResponsavel()))) {
					throw new SecurityException("Usuários só podem atribuir tarefas a si mesmos.");
				}
			Responsavel responsavel = dao.findById(idResponsavel, Responsavel.class);
			Long equipeTarefa = atual.getEquipe() == null ? null : atual.getEquipe().getId();
			Long equipeResponsavel = responsavel == null || responsavel.getEquipe() == null
					? null : responsavel.getEquipe().getId();
			if (responsavel == null || equipeTarefa == null || !equipeTarefa.equals(equipeResponsavel)) {
				throw new SecurityException("O responsável deve pertencer à equipe da tarefa.");
			}
			}
			if (operacao.getComando().equals(ListaComando.ATRIBUIR_TAREFA)
					&& recebida.getResponsavel() == null && !user.isAdm()
					&& atual.getResponsavel() != null
					&& (user.getIdResponsavel() == null
							|| !user.getIdResponsavel().equals(atual.getResponsavel().getId()))) {
				throw new SecurityException("Somente o responsável atual pode liberar esta tarefa.");
			}
		} catch (DAOException e) {
			throw new IllegalStateException("Não foi possível validar a permissão da tarefa.", e);
		} finally {
			dao.close();
		}
	}

	
}
