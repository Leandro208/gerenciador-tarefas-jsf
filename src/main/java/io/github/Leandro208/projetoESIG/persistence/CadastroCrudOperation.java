package io.github.Leandro208.projetoESIG.persistence;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAO;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;

public class CadastroCrudOperation implements CrudOperation {

	@Override
	public void operar(Operacao operacao) {
		validate(operacao);
		if(operacao.getComando().equals(ListaComando.CADASTRAR)) {
			criar(operacao);
		} else if(operacao.getComando().equals(ListaComando.ALTERAR)) {
			alterar(operacao);
		} else if(operacao.getComando().equals(ListaComando.REMOVER)) {
			
		}
	}

	@Override
	public void validate(Operacao operacao) {

	}

	protected Object criar(Operacao operacao) {
		GenericDAO dao = new GenericDAOImpl();
		try {
			dao.create(operacao.getEntidade());
		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			dao.commit();
		}
		return operacao.getEntidade();
	}

	protected Object alterar(Operacao operacao) {
		GenericDAO dao = new GenericDAOImpl();
		try {
			dao.update(operacao.getEntidade());
		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			dao.commit();
		}
		return operacao.getEntidade();
	}
	
	protected Object remover(Operacao operacao) {
		GenericDAO dao = new GenericDAOImpl();
		try {
			dao.remove(operacao.getEntidade());
		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			dao.commit();
		}
		return operacao.getEntidade();
	}
}
