package io.github.Leandro208.projetoESIG.services;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import io.github.Leandro208.projetoESIG.dao.HistoricoTarefaDAO;
import io.github.Leandro208.projetoESIG.dominio.HistoricoTarefa;

public class HistoricoTarefaService implements Serializable {
	private static final long serialVersionUID = 1L;

	public List<HistoricoTarefa> buscarPorTarefa(Long idTarefa) {
		if (idTarefa == null) return Collections.emptyList();
		HistoricoTarefaDAO dao = new HistoricoTarefaDAO();
		try {
			return dao.buscarPorTarefa(idTarefa);
		} finally {
			dao.close();
		}
	}
}
