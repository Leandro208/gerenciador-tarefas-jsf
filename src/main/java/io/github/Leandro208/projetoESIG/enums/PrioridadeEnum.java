package io.github.Leandro208.projetoESIG.enums;

public enum PrioridadeEnum {
	ALTA(1), MEDIA(2), BAIXA(3);

	private final int codigo;

	private PrioridadeEnum(int codigo) {
		this.codigo = codigo;
	}

	public int getCodigo() {
		return codigo;
	}

	public static PrioridadeEnum valueOf(int codigo) {
		for (PrioridadeEnum p : PrioridadeEnum.values()) {
			if (p.getCodigo() == codigo) {
				return p;
			}
		}
		throw new IllegalArgumentException("Código de prioridade inválido!");
	}

}
