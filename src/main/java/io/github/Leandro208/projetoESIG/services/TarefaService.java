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
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dto.FormConsultaTarefaDto;
import io.github.Leandro208.projetoESIG.dominio.Tarefa;
import io.github.Leandro208.projetoESIG.enums.StatusEnum;
import io.github.Leandro208.projetoESIG.util.MonitorTarefas;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;

public class TarefaService implements BaseService<Tarefa>, Serializable {
	private static final long serialVersionUID = 1L;

	public Map<Integer, List<Tarefa>> buscarTodos(FormConsultaTarefaDto form) {
		TarefaDAO dao = new TarefaDAO();
		List<Tarefa> tarefas = dao.filter(form);

		Map<Integer, List<Tarefa>> mapTarefas = new HashMap<>();
		mapTarefas.put(StatusEnum.BACKLOG.getCodigo(), new ArrayList<Tarefa>());
		mapTarefas.put(StatusEnum.CONCLUIDO.getCodigo(), new ArrayList<Tarefa>());
		mapTarefas.put(StatusEnum.EM_ANDAMENTO.getCodigo(), new ArrayList<Tarefa>());
		for (Tarefa t : tarefas) {
			mapTarefas.get(t.getStatus().getCodigo()).add(t);
		}
		return mapTarefas;
	}

	public MonitorTarefas monitoramento() {
		FormConsultaTarefaDto dto = new FormConsultaTarefaDto();
		dto.setResponsavel(new Responsavel());
		dto.getResponsavel().setId(UsuarioUtils.getLogado().getIdResponsavel());
		if (!UsuarioUtils.usuarioTemEquipe()) {
			return new MonitorTarefas();
		}
		dto.setEquipe(UsuarioUtils.getLogado().getEquipe());

		Map<Integer, List<Tarefa>> tarefas = buscarTodos(dto);
		int encerrados = tarefas.get(StatusEnum.CONCLUIDO.getCodigo()).size();
		int andamento = tarefas.get(StatusEnum.EM_ANDAMENTO.getCodigo()).size();
		return new MonitorTarefas(andamento, encerrados);
	}
}
