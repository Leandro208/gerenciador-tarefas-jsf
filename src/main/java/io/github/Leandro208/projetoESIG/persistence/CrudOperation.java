package io.github.Leandro208.projetoESIG.persistence;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.exception.NegocioException;

public interface CrudOperation {
	public void operar(Operacao operacao) throws DAOException, NegocioException;
	public void validate(Operacao operacao) throws NegocioException;
}
