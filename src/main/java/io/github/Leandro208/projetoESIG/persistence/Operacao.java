package io.github.Leandro208.projetoESIG.persistence;

import io.github.Leandro208.projetoESIG.entities.BaseEntity;

public interface Operacao {
	public Comando getComando();
	public void setComando(Comando comando);
	public BaseEntity getEntidade();
	public void setEntidade(BaseEntity entidade);
}
