package io.github.Leandro208.projetoESIG.services;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.Leandro208.projetoESIG.dao.GenericDao;
import io.github.Leandro208.projetoESIG.dto.FormConsultaTarefaDto;
import io.github.Leandro208.projetoESIG.entities.Tarefa;
import io.github.Leandro208.projetoESIG.enums.Funcao;
import io.github.Leandro208.projetoESIG.enums.StatusEnum;
import io.github.Leandro208.projetoESIG.util.MonitorTarefas;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;

public class TarefaService implements BaseService<Tarefa>, Serializable {
	private static final long serialVersionUID = 1L;

	private GenericDao<Tarefa> dao;

	public TarefaService() {
		dao = new GenericDao<Tarefa>();
	}

	@Override
	public Tarefa buscarPorId(Long id) {
		return dao.buscarPorId(Tarefa.class, id);
	}

	@Override
	public void salvar(Tarefa t) {
		dao.salvar(t);
	}

	@Override
	public void remover(Tarefa t) {
		dao.remover(Tarefa.class, t.getId());
	}

	public void concluir(Tarefa t) throws ParseException {
		t.setStatus(StatusEnum.CONCLUIDO);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		t.setDataFinalizacao(new Date());
		String data = df.format(t.getDataFinalizacao());
		t.setDataFinalizacao(df.parse(data));
		dao.salvar(t);
	}

	public HashMap<Integer, List<Tarefa>> buscarTodos(FormConsultaTarefaDto form) {
		
		StringBuilder hql = new StringBuilder("select t from Tarefa t  where 1 = 1");
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
		if (UsuarioUtils.getLogado().getFuncao() == Funcao.USER) {
			hql.append(String.format(" and t.equipe.id = '%d'", UsuarioUtils.getLogado().getEquipe().getId()));
		}
		hql.append(" order by t.id");
		 
		
		HashMap<Integer, List<Tarefa>> tarefas = new HashMap<>();
		tarefas.put(StatusEnum.BACKLOG.getCodigo(), new ArrayList<Tarefa>());
		tarefas.put(StatusEnum.CONCLUIDO.getCodigo(), new ArrayList<Tarefa>());
		tarefas.put(StatusEnum.EM_ANDAMENTO.getCodigo(), new ArrayList<Tarefa>());
		for(Tarefa t : dao.buscarTodos(hql.toString())) {
			if(t.getStatus().getCodigo() == StatusEnum.BACKLOG.getCodigo()) {
				tarefas.get(StatusEnum.BACKLOG.getCodigo()).add(t);
			} else if(t.getStatus().getCodigo() == StatusEnum.CONCLUIDO.getCodigo()) {
				tarefas.get(StatusEnum.CONCLUIDO.getCodigo()).add(t);
			} else if(t.getStatus().getCodigo() == StatusEnum.EM_ANDAMENTO.getCodigo()) {
				tarefas.get(StatusEnum.EM_ANDAMENTO.getCodigo()).add(t);
			}
			
		}
		
		return tarefas;
	}

	public MonitorTarefas monitoramento() {
		
		int encerrados = buscarTodos(new FormConsultaTarefaDto()).get(StatusEnum.CONCLUIDO.getCodigo()).size();
		int andamento = buscarTodos(new FormConsultaTarefaDto()).get(StatusEnum.EM_ANDAMENTO.getCodigo()).size();

		MonitorTarefas monitor = new MonitorTarefas(andamento, encerrados);

		return monitor;
	}
}
