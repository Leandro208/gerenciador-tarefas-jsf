package io.github.Leandro208.projetoESIG.enums;

public enum Funcao {
	USER(1), ADM(2);
	
	private final int codigo;

	private Funcao(int codigo) {
		this.codigo = codigo;
	}

	public int getCodigo() {
		return codigo;
	}
	
	public static Funcao valueOf(int codigo) {
		for (Funcao f : Funcao.values()) {
			if (f.getCodigo() == codigo) {
				return f;
			}
		}
		throw new IllegalArgumentException("Código de Funcao inválido!");
	}
}
