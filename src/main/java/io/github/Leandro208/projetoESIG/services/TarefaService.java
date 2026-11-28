package io.github.Leandro208.projetoESIG.services;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAO;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;
import io.github.Leandro208.projetoESIG.dao.TarefaDAO;
import io.github.Leandro208.projetoESIG.dto.FormConsultaTarefaDto;
import io.github.Leandro208.projetoESIG.dominio.Tarefa;
import io.github.Leandro208.projetoESIG.enums.StatusEnum;
import io.github.Leandro208.projetoESIG.util.MonitorTarefas;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;
import io.github.Leandro208.projetoESIG.util.AccessControl;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;

public class TarefaService implements BaseService<Tarefa>, Serializable {
	private static final long serialVersionUID = 1L;

	public Map<Integer, List<Tarefa>> buscarTodos(FormConsultaTarefaDto form) {
		UsuarioDTO usuario = AccessControl.requireUser();
		AccessControl.limitarConsultaDeTarefas(usuario, form);
		TarefaDAO dao = new TarefaDAO();
		try{
			return agruparPorStatus(dao.filter(form));
		} finally {
			dao.close();
		}
	}

	static Map<Integer, List<Tarefa>> agruparPorStatus(List<Tarefa> tarefas) {
		Map<Integer, List<Tarefa>> agrupadas = new HashMap<>();
		for (StatusEnum status : StatusEnum.values()) {
			agrupadas.put(status.getCodigo(), new ArrayList<Tarefa>());
		}
		for (Tarefa tarefa : tarefas) {
			List<Tarefa> grupo = agrupadas.get(tarefa.getStatus().getCodigo());
			if (grupo == null) throw new IllegalArgumentException("Status não suportado na tarefa.");
			grupo.add(tarefa);
		}
		return agrupadas;
	}

	public Tarefa buscarPorIdPermitido(Long idTarefa, UsuarioDTO usuario) {
		if (idTarefa == null || usuario == null) return null;
		TarefaDAO dao = new TarefaDAO();
		try {
			Tarefa tarefa = dao.findById(idTarefa, Tarefa.class);
			if (tarefa == null || !AccessControl.podeAcessarEquipe(usuario,
					tarefa.getEquipe() == null ? null : tarefa.getEquipe().getId())) return null;
			return tarefa;
		} catch (DAOException e) {
			throw new IllegalStateException("Não foi possível validar o acesso à tarefa.", e);
		} finally {
			dao.close();
		}
	}

	public MonitorTarefas monitoramento() {
		UsuarioDTO usuario = AccessControl.requireUser();
		if (usuario.getEquipe() == null || usuario.getIdResponsavel() == null) {
			return new MonitorTarefas();
		}
		TarefaDAO dao = new TarefaDAO();
		try {
			Object[] resumo = dao.resumoIndicadores(usuario.getIdResponsavel(), usuario.getEquipe().getId());
			return new MonitorTarefas(contagem(resumo, 3), contagem(resumo, 0),
					contagem(resumo, 1), contagem(resumo, 2));
		} finally {
			dao.close();
		}
	}

	private int contagem(Object[] resumo, int indice) {
		return ((Number) resumo[indice]).intValue();
	}
}
