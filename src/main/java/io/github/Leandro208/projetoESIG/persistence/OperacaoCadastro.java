package io.github.Leandro208.projetoESIG.persistence;

import io.github.Leandro208.projetoESIG.entities.BaseEntity;

public class OperacaoCadastro implements Operacao {
	private BaseEntity entidade;
	private Comando comando;
	
	@Override
	public Comando getComando() {
		return comando;
	}

	@Override
	public BaseEntity getEntidade() {
		return entidade;
	}

	@Override
	public void setComando(Comando comando) {
		this.comando = comando;
	}

	@Override
	public void setEntidade(BaseEntity entidade) {
		this.entidade = entidade;
		
	}
}
