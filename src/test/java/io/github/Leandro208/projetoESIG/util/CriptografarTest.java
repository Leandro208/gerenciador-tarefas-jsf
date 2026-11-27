package io.github.Leandro208.projetoESIG.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.security.MessageDigest;

import org.junit.Test;

public class CriptografarTest {
	@Test
	public void novoHashVerificaSenhaEUsaSaltAleatorio() {
		String primeiro = Criptografar.encriptografar("senha-forte-123");
		String segundo = Criptografar.encriptografar("senha-forte-123");

		assertTrue(Criptografar.verificar("senha-forte-123", primeiro));
		assertFalse(Criptografar.verificar("senha-incorreta", primeiro));
		assertNotEquals(primeiro, segundo);
		assertFalse(Criptografar.precisaAtualizar(primeiro));
	}

	@Test
	public void verificaHashLegadoSha256ParaPermitirMigracaoNoLogin() throws Exception {
		byte[] digest = MessageDigest.getInstance("SHA-256").digest("senha-antiga".getBytes());
		StringBuilder hashAntigo = new StringBuilder();
		for (byte b : digest) hashAntigo.append(String.format("%02x", b));

		assertTrue(Criptografar.verificar("senha-antiga", hashAntigo.toString()));
		assertTrue(Criptografar.precisaAtualizar(hashAntigo.toString()));
		assertFalse(Criptografar.verificar("outra-senha", hashAntigo.toString()));
	}
}
