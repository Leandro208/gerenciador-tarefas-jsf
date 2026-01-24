package io.github.Leandro208.projetoESIG.persistence;

import io.github.Leandro208.projetoESIG.dao.DAOException;

public interface CrudOperation {
	public void operar(Operacao operacao) throws DAOException;
	public void validate(Operacao operacao);
}
