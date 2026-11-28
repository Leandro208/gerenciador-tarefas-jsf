package io.github.Leandro208.projetoESIG.dominio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.junit.Test;

import io.github.Leandro208.projetoESIG.enums.StatusEnum;

public class TarefaTest {

	@Test
	public void registraDataFinalizacaoAoConcluirEPreservaQuandoStatusNaoMuda() {
		Tarefa tarefa = new Tarefa();
		long antes = System.currentTimeMillis();

		tarefa.setStatus(StatusEnum.CONCLUIDO);

		long depois = System.currentTimeMillis();
		Date finalizacao = tarefa.getDataFinalizacao();
		assertNotNull(finalizacao);
		assertTrue(finalizacao.getTime() >= antes);
		assertTrue(finalizacao.getTime() <= depois);

		tarefa.setStatus(StatusEnum.CONCLUIDO);
		assertSame(finalizacao, tarefa.getDataFinalizacao());
	}

	@Test
	public void limpaDataFinalizacaoAoReabrirTarefa() {
		Tarefa tarefa = new Tarefa();
		tarefa.setStatus(StatusEnum.CONCLUIDO);

		tarefa.setStatus(StatusEnum.EM_ANDAMENTO);

		assertEquals(StatusEnum.EM_ANDAMENTO, tarefa.getStatus());
		assertNull(tarefa.getDataFinalizacao());
	}
}
