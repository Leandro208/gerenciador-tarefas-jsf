package io.github.Leandro208.projetoESIG.persistence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;
import io.github.Leandro208.projetoESIG.dominio.BaseEntity;
import io.github.Leandro208.projetoESIG.dominio.Equipe;
import io.github.Leandro208.projetoESIG.dominio.HistoricoTarefa;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dominio.Tarefa;
import io.github.Leandro208.projetoESIG.dominio.Usuario;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.util.AccessControl;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;

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
		GenericDAOImpl dao = new GenericDAOImpl(operacao);
		try {
			Tarefa solicitada = (Tarefa) operacao.getEntidade();
			Tarefa tarefa = dao.findById(solicitada.getId(), Tarefa.class);
			if (tarefa == null) {
				throw new DAOException("Tarefa não encontrada.");
			}
			List<HistoricoTarefa> eventos = new ArrayList<>();
			adicionarEvento(eventos, tarefa, "Status", tarefa.getStatus(), solicitada.getStatus(),
					nome(tarefa.getStatus()), nome(solicitada.getStatus()));
			tarefa.setStatus(solicitada.getStatus());
			dao.updateTarefaComHistorico(tarefa, eventos);
		} finally {
			dao.close();
		}
	}

	private void atribuirTarefa(Operacao operacao) throws DAOException {
		GenericDAOImpl dao = new GenericDAOImpl(operacao);
		try {
			Tarefa solicitada = (Tarefa) operacao.getEntidade();
			Tarefa tarefa = dao.findById(solicitada.getId(), Tarefa.class);
			if (tarefa == null) throw new DAOException("Tarefa não encontrada.");
			Responsavel responsavel = solicitada.getResponsavel() == null ? null
					: dao.findById(solicitada.getResponsavel().getId(), Responsavel.class);
			if (solicitada.getResponsavel() != null && responsavel == null) {
				throw new DAOException("Responsável não encontrado.");
			}
			List<HistoricoTarefa> eventos = new ArrayList<>();
			adicionarEvento(eventos, tarefa, "Responsável", id(tarefa.getResponsavel()), id(responsavel),
					nome(tarefa.getResponsavel()), nome(responsavel));
			tarefa.setResponsavel(responsavel);
			dao.updateTarefaComHistorico(tarefa, eventos);
		} finally {
			dao.close();
		}
	}

	@Override
	protected Object alterar(Operacao operacao) throws DAOException {
		GenericDAOImpl dao = new GenericDAOImpl(operacao);
		try {
			Tarefa recebida = (Tarefa) operacao.getEntidade();
			Tarefa atual = dao.findById(recebida.getId(), Tarefa.class);
			if (atual == null) throw new DAOException("Tarefa não encontrada.");
			List<HistoricoTarefa> eventos = new ArrayList<>();
			adicionarEvento(eventos, atual, "Título", atual.getTitulo(), recebida.getTitulo(), atual.getTitulo(), recebida.getTitulo());
			adicionarEvento(eventos, atual, "Descrição", atual.getDescricao(), recebida.getDescricao(), atual.getDescricao(), recebida.getDescricao());
			adicionarEvento(eventos, atual, "Responsável", id(atual.getResponsavel()), id(recebida.getResponsavel()), nome(atual.getResponsavel()), nome(recebida.getResponsavel()));
			adicionarEvento(eventos, atual, "Prioridade", atual.getPrioridade(), recebida.getPrioridade(), nome(atual.getPrioridade()), nome(recebida.getPrioridade()));
			adicionarEvento(eventos, atual, "Status", atual.getStatus(), recebida.getStatus(), nome(atual.getStatus()), nome(recebida.getStatus()));
			adicionarEvento(eventos, atual, "Prazo", data(atual.getDeadline()), data(recebida.getDeadline()), data(atual.getDeadline()), data(recebida.getDeadline()));
			adicionarEvento(eventos, atual, "Equipe", id(atual.getEquipe()), id(recebida.getEquipe()), nome(atual.getEquipe()), nome(recebida.getEquipe()));
			atual.setTitulo(recebida.getTitulo());
			atual.setDescricao(recebida.getDescricao());
			atual.setResponsavel(recebida.getResponsavel());
			atual.setPrioridade(recebida.getPrioridade());
			atual.setStatus(recebida.getStatus());
			atual.setDeadline(recebida.getDeadline());
			atual.setEquipe(recebida.getEquipe());
			dao.updateTarefaComHistorico(atual, eventos);
			return atual;
		} finally {
			dao.close();
		}
	}

	private void adicionarEvento(List<HistoricoTarefa> eventos, Tarefa tarefa, String campo,
			Object comparacaoAntiga, Object comparacaoNova, String valorAntigo, String valorNovo) {
		if (Objects.equals(comparacaoAntiga, comparacaoNova)) return;
		UsuarioDTO usuario = UsuarioUtils.getLogado();
		HistoricoTarefa evento = new HistoricoTarefa();
		evento.setTarefa(tarefa);
		evento.setCampo(campo);
		evento.setValorAnterior(valorAntigo);
		evento.setValorNovo(valorNovo);
		evento.setUsuario(usuario == null || usuario.getId() == null ? null : new Usuario(usuario.getId()));
		evento.setDataAlteracao(new Date());
		eventos.add(evento);
	}

	private Long id(Object entidade) {
		return entidade instanceof BaseEntity ? ((BaseEntity) entidade).getId() : null;
	}

	private String nome(Object valor) {
		if (valor == null) return "Não informado";
		if (valor instanceof Responsavel) return ((Responsavel) valor).getNome();
		if (valor instanceof Equipe) return ((Equipe) valor).getNome();
		return valor.toString();
	}

	private String data(Date valor) {
		return valor == null ? "Não informado" : new SimpleDateFormat("dd/MM/yyyy").format(valor);
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
