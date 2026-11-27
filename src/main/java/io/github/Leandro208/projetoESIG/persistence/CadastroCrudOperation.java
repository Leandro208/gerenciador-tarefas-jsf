package io.github.Leandro208.projetoESIG.persistence;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAO;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;
import io.github.Leandro208.projetoESIG.dominio.BaseEntity;
import io.github.Leandro208.projetoESIG.exception.NegocioException;
import io.github.Leandro208.projetoESIG.validacao.ListaMensagens;
import io.github.Leandro208.projetoESIG.util.AccessControl;

public class CadastroCrudOperation implements CrudOperation {

	@Override
	public void operar(Operacao operacao) throws DAOException, NegocioException {
		AccessControl.requireAdmin();
		validate(operacao);
		if(operacao.getComando().equals(ListaComando.CADASTRAR)) {
			criar(operacao);
		} else if(operacao.getComando().equals(ListaComando.ALTERAR)) {
			alterar(operacao);
		} else if(operacao.getComando().equals(ListaComando.REMOVER)) {
			remover(operacao);
		}
	}

	@Override
	public void validate(Operacao operacao) throws NegocioException {

	}

	protected Object criar(Operacao operacao) throws DAOException {
		GenericDAO dao = new GenericDAOImpl(operacao);
		try {
			dao.create(operacao.getEntidade());
		} finally {
			dao.close();
		}
		return operacao.getEntidade();
	}

	protected Object alterar(Operacao operacao) throws DAOException {
		GenericDAO dao = new GenericDAOImpl(operacao);
		try {
			dao.update(operacao.getEntidade());
		} finally {
			dao.close();
		}
		return operacao.getEntidade();
	}
	
	protected Object remover(Operacao operacao) throws DAOException {
		GenericDAO dao = new GenericDAOImpl(operacao);
		try {
			dao.remove(operacao.getEntidade());
		} finally {
			dao.close();
		}
		return operacao.getEntidade();
	}

	public void checkValidation(ListaMensagens mensagens) throws NegocioException {
		if (mensagens != null && mensagens.isErrorPresent()) {
			NegocioException e = new NegocioException();
			e.addMensagens(mensagens.getErrorMessages());
			throw e;
		}
	}
}
