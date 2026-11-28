package io.github.Leandro208.projetoESIG.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import io.github.Leandro208.projetoESIG.dto.FormConsultaTarefaDto;
import io.github.Leandro208.projetoESIG.dominio.Tarefa;
import io.github.Leandro208.projetoESIG.util.ValidatorUtils;

public class TarefaDAO extends GenericDAOImpl{

	public Object[] resumoIndicadores(Long idResponsavel, Long idEquipe) {
		String sql = "SELECT "
				+ "COUNT(*) FILTER (WHERE t.status <> 'CONCLUIDO' AND t.deadline < CURRENT_DATE) AS atrasadas, "
				+ "COUNT(*) FILTER (WHERE t.status <> 'CONCLUIDO' AND t.deadline >= CURRENT_DATE "
				+ "AND t.deadline <= CURRENT_DATE + 7) AS proximas, "
				+ "COUNT(*) FILTER (WHERE t.status = 'CONCLUIDO' "
				+ "AND t.data_finalizacao >= date_trunc('month', CURRENT_DATE) "
				+ "AND t.data_finalizacao < CURRENT_DATE + INTERVAL '1 day') AS concluidas_no_periodo, "
				+ "COUNT(*) FILTER (WHERE t.status = 'EM_ANDAMENTO') AS andamento "
				+ "FROM gestao.tarefa t WHERE t.id_responsavel = :idResponsavel "
				+ "AND t.id_equipe = :idEquipe";

		Object[] resultado = (Object[]) getSession().createNativeQuery(sql)
				.setParameter("idResponsavel", idResponsavel)
				.setParameter("idEquipe", idEquipe)
				.getSingleResult();
		return resultado;
	}

	public List<Tarefa> filter(FormConsultaTarefaDto form) {
	    StringBuilder sql = new StringBuilder("SELECT * FROM gestao.tarefa t WHERE 1=1");
	    
	    Map<String, Object> parameters = new HashMap<>();

	    if (form.getNumero() != null && form.getNumero() != 0) {
	        sql.append(" AND t.id = :numero");
	        parameters.put("numero", form.getNumero());
	    }

	    if (form.getTitulo() != null && !form.getTitulo().isEmpty()) {
	        sql.append(" AND (t.titulo ILIKE :titulo OR t.descricao ILIKE :descricao)");
	        String titulo = "%" + form.getTitulo().toLowerCase() + "%";
	        parameters.put("titulo", titulo);
	        parameters.put("descricao", titulo);
	    }

	    if (form.getResponsavel() != null && form.getResponsavel().getId() != null) {
	        sql.append(" AND t.id_responsavel = :idResponsavel");
	        parameters.put("idResponsavel", form.getResponsavel().getId());
	    }

	    if (form.getSituacao() != null) {
	        sql.append(" AND t.status = :situacao");
	        parameters.put("situacao", form.getSituacao().toString());
	    }

	    if (form.getPrioridade() != null) {
	        sql.append(" AND t.prioridade = :prioridade");
	        parameters.put("prioridade", form.getPrioridade().toString());
	    }

	    if (form.getPrazoDe() != null) {
	        sql.append(" AND t.deadline >= :prazoDe");
	        parameters.put("prazoDe", form.getPrazoDe());
	    }

	    if (form.getPrazoAte() != null) {
	        sql.append(" AND t.deadline <= :prazoAte");
	        parameters.put("prazoAte", form.getPrazoAte());
	    }

	    if (!ValidatorUtils.isEmpty(form.getEquipe())) {
	        sql.append(" AND t.id_equipe = :idEquipe");
	        parameters.put("idEquipe", form.getEquipe().getId());
	    }

	    sql.append(" ORDER BY t.id");

	    Query query = getSession().createNativeQuery(sql.toString(), Tarefa.class);

	    for (Map.Entry<String, Object> param : parameters.entrySet()) {
	        query.setParameter(param.getKey(), param.getValue());
	    }
	    @SuppressWarnings("unchecked")
		List<Tarefa> tarefas = query.getResultList();
	    return tarefas;
	}

}
