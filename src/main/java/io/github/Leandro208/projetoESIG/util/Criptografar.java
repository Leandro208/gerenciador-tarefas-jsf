package io.github.Leandro208.projetoESIG.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Password hashing with gradual migration from the former unsalted SHA-256 format. */
public final class Criptografar {
	private static final String PREFIX = "pbkdf2-sha256";
	private static final int ITERATIONS = 210000;
	private static final int SALT_BYTES = 16;
	private static final int KEY_BITS = 256;
	private static final SecureRandom RANDOM = new SecureRandom();

	private Criptografar() {
	}

	public static String encriptografar(String senha) {
		if (senha == null) {
			throw new IllegalArgumentException("A senha não pode ser nula.");
		}
		byte[] salt = new byte[SALT_BYTES];
		RANDOM.nextBytes(salt);
		byte[] hash = derivar(senha.toCharArray(), salt, ITERATIONS);
		return PREFIX + "$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
				+ "$" + Base64.getEncoder().encodeToString(hash);
	}

	public static boolean verificar(String senha, String armazenada) {
		if (senha == null || armazenada == null) {
			return false;
		}
		if (!armazenada.startsWith(PREFIX + "$")) {
			return MessageDigest.isEqual(hashLegado(senha), armazenada.getBytes(StandardCharsets.US_ASCII));
		}
		try {
			String[] partes = armazenada.split("\\$", -1);
			if (partes.length != 4) return false;
			int iteracoes = Integer.parseInt(partes[1]);
			if (iteracoes < 10000 || iteracoes > 1000000) return false;
			byte[] salt = Base64.getDecoder().decode(partes[2]);
			byte[] esperado = Base64.getDecoder().decode(partes[3]);
			return MessageDigest.isEqual(esperado, derivar(senha.toCharArray(), salt, iteracoes));
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static boolean precisaAtualizar(String armazenada) {
		return armazenada == null || !armazenada.startsWith(PREFIX + "$");
	}

	private static byte[] derivar(char[] senha, byte[] salt, int iteracoes) {
		PBEKeySpec spec = new PBEKeySpec(senha, salt, iteracoes, KEY_BITS);
		try {
			return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
		} catch (Exception e) {
			throw new IllegalStateException("Não foi possível proteger a senha.", e);
		} finally {
			spec.clearPassword();
			Arrays.fill(senha, '\0');
		}
	}

	private static byte[] hashLegado(String senha) {
		try {
			return toHex(MessageDigest.getInstance("SHA-256").digest(senha.getBytes())).getBytes(StandardCharsets.US_ASCII);
		} catch (Exception e) {
			throw new IllegalStateException("Não foi possível verificar a senha.", e);
		}
	}

	private static String toHex(byte[] bytes) {
		StringBuilder hex = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) hex.append(String.format("%02x", b));
		return hex.toString();
	}
}
