package io.github.Leandro208.projetoESIG.dao;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import io.github.Leandro208.projetoESIG.dominio.HistoricoTarefa;

public class HistoricoTarefaDAO extends GenericDAOImpl {
	public List<HistoricoTarefa> buscarPorTarefa(Long idTarefa) {
		List<HistoricoTarefa> historicos = getSession().createQuery(
				"SELECT h FROM HistoricoTarefa h LEFT JOIN FETCH h.usuario "
				+ "WHERE h.tarefa.id = :idTarefa "
				+ "ORDER BY h.dataAlteracao DESC, h.id DESC", HistoricoTarefa.class)
				.setParameter("idTarefa", idTarefa).getResultList();
		List<Object[]> nomes = getSession().createNativeQuery(
				"SELECT h.id, COALESCE(r.nome, u.email) "
				+ "FROM gestao.historico_tarefa h "
				+ "LEFT JOIN pessoa.usuario u ON u.id = h.id_usuario "
				+ "LEFT JOIN pessoa.responsavel r ON r.id_usuario = u.id "
				+ "WHERE h.id_tarefa = :idTarefa")
				.setParameter("idTarefa", idTarefa).getResultList();
		Map<Long, String> nomesPorHistorico = new HashMap<>();
		for (Object[] linha : nomes) {
			nomesPorHistorico.put(((Number) linha[0]).longValue(), (String) linha[1]);
		}
		for (HistoricoTarefa historico : historicos) {
			String nome = nomesPorHistorico.get(historico.getId());
			historico.setAutorNome(nome == null || nome.trim().isEmpty() ? "Usuário indisponível" : nome);
		}
		return historicos;
	}
}
