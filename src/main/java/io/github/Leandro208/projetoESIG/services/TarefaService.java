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
import io.github.Leandro208.projetoESIG.dao.GenericDaoII;
import io.github.Leandro208.projetoESIG.dao.TarefaDAO;
import io.github.Leandro208.projetoESIG.dao.TarefaDAO;
import io.github.Leandro208.projetoESIG.dto.FormConsultaTarefaDto;
import io.github.Leandro208.projetoESIG.entities.Equipe;
import io.github.Leandro208.projetoESIG.entities.Tarefa;
import io.github.Leandro208.projetoESIG.enums.Funcao;
import io.github.Leandro208.projetoESIG.enums.StatusEnum;
import io.github.Leandro208.projetoESIG.util.MonitorTarefas;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;
import io.github.Leandro208.projetoESIG.util.ValidatorUtils;

public class TarefaService implements BaseService<Tarefa>, Serializable {
	private static final long serialVersionUID = 1L;

	public TarefaService() {

	}


	public void concluir(Tarefa t) throws ParseException, DAOException {
		TarefaDAO dao = new TarefaDAO();
		t.setStatus(StatusEnum.CONCLUIDO);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		t.setDataFinalizacao(new Date());
		String data = df.format(t.getDataFinalizacao());
		t.setDataFinalizacao(df.parse(data));
		dao.update(t);
	}

	public HashMap<Integer, List<Tarefa>> buscarTodos(FormConsultaTarefaDto form) {
		
		TarefaDAO dao = new TarefaDAO();
		
		StringBuilder hql = new StringBuilder("select t from Tarefa t where 1 = 1");
		if (form.getNumero() != null && form.getNumero() != 0) {
			hql.append(String.format(" and t.id = %d", form.getNumero()));
		}
		if (form.getTitulo() != null && !form.getTitulo().equals("")) {
			hql.append(String.format(" and (lower(t.titulo) like '%s' or lower(t.descricao) like '%s')",
					"%" + form.getTitulo().toLowerCase() + "%", "%" + form.getTitulo().toLowerCase() + "%"));
		}
		if (form.getResponsavel() != null && form.getResponsavel().getId() != null) {
			hql.append(String.format(" and t.responsavel.id = '%d'", form.getResponsavel().getId()));
		}
		if (form.getSituacao() != null) {
			hql.append(String.format(" and t.status like '%s'", form.getSituacao().toString()));
		}
		hql.append(" order by t.id");
		 
		List<Tarefa> tarefas = dao.filter(form);
		
		HashMap<Integer, List<Tarefa>> mapTarefas = new HashMap<>();
		mapTarefas.put(StatusEnum.BACKLOG.getCodigo(), new ArrayList<Tarefa>());
		mapTarefas.put(StatusEnum.CONCLUIDO.getCodigo(), new ArrayList<Tarefa>());
		mapTarefas.put(StatusEnum.EM_ANDAMENTO.getCodigo(), new ArrayList<Tarefa>());
		for(Tarefa t : tarefas) {
			if(t.getStatus() == StatusEnum.BACKLOG) {
				mapTarefas.get(StatusEnum.BACKLOG.getCodigo()).add(t);
			} else if(t.getStatus() == StatusEnum.CONCLUIDO) {
				mapTarefas.get(StatusEnum.CONCLUIDO.getCodigo()).add(t);
			} else if(t.getStatus() == StatusEnum.EM_ANDAMENTO) {
				mapTarefas.get(StatusEnum.EM_ANDAMENTO.getCodigo()).add(t);
			}
			
		}
		
		return mapTarefas;
	}

	public MonitorTarefas monitoramento() {
		FormConsultaTarefaDto dto = new FormConsultaTarefaDto();
		dto.setResponsavel(UsuarioUtils.getLogado());
		if(dto.getResponsavel().getEquipe() == null) {
			return new MonitorTarefas();
		}
				
		int encerrados = buscarTodos(dto)
				.get(StatusEnum.CONCLUIDO.getCodigo()).size();
		int andamento = buscarTodos(dto)
				.get(StatusEnum.EM_ANDAMENTO.getCodigo()).size();

		MonitorTarefas monitor = new MonitorTarefas(andamento, encerrados);

		return monitor;
	}


	public void salvar(Tarefa tarefa) {
		// TODO Auto-generated method stub
		
	}


	public void remover(Tarefa tarefa) {
		// TODO Auto-generated method stub
		
	}
}
