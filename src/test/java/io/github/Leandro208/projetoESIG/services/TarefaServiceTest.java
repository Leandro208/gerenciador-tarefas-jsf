package io.github.Leandro208.projetoESIG.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.github.Leandro208.projetoESIG.dominio.Tarefa;
import io.github.Leandro208.projetoESIG.enums.StatusEnum;

public class TarefaServiceTest {

	@Test
	public void agrupaTarefasPorStatusEIncluiGruposVazios() {
		Tarefa emAndamento = new Tarefa();
		emAndamento.setStatus(StatusEnum.EM_ANDAMENTO);
		Tarefa backlog = new Tarefa();
		backlog.setStatus(StatusEnum.BACKLOG);

		Map<Integer, List<Tarefa>> resultado = TarefaService.agruparPorStatus(Arrays.asList(emAndamento, backlog));

		assertEquals(1, resultado.get(StatusEnum.EM_ANDAMENTO.getCodigo()).size());
		assertSame(emAndamento, resultado.get(StatusEnum.EM_ANDAMENTO.getCodigo()).get(0));
		assertEquals(1, resultado.get(StatusEnum.BACKLOG.getCodigo()).size());
		assertSame(backlog, resultado.get(StatusEnum.BACKLOG.getCodigo()).get(0));
		assertEquals(0, resultado.get(StatusEnum.CONCLUIDO.getCodigo()).size());
	}
}
