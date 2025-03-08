package io.github.Leandro208.projetoESIG.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import io.github.Leandro208.projetoESIG.dto.FormConsultaTarefaDto;
import io.github.Leandro208.projetoESIG.entities.Tarefa;
import io.github.Leandro208.projetoESIG.util.ValidatorUtils;

public class TarefaDAO extends GenericDAOImpl{

	public List<Tarefa> filter(FormConsultaTarefaDto form) {
	    StringBuilder sql = new StringBuilder("SELECT * FROM tarefa t WHERE 1=1");
	    
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

	    if (!ValidatorUtils.isEmpty(form.getIdEquipe())) {
	        sql.append(" AND t.id_equipe = :idEquipe");
	        parameters.put("idEquipe", form.getIdEquipe());
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
