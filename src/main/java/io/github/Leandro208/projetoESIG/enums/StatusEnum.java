package io.github.Leandro208.projetoESIG.enums;

public enum StatusEnum {
	EM_ANDAMENTO(1),
	CONCLUIDO(2),
	BACKLOG(3);
	
	private final int codigo;

	private StatusEnum(int codigo) {
		this.codigo = codigo;
	}

	public int getCodigo() {
		return codigo;
	}
	
	public static StatusEnum valueOf(int codigo) {
		for(StatusEnum s : StatusEnum.values()) {
			if (s.getCodigo() == codigo) {
				return s;
			}
		}
		throw new IllegalArgumentException("Código de Status inválido!");
	}
}
