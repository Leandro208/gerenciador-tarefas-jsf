package io.github.Leandro208.projetoESIG.persistence;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAO;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;
import io.github.Leandro208.projetoESIG.dominio.BaseEntity;
import io.github.Leandro208.projetoESIG.exception.NegocioException;
import io.github.Leandro208.projetoESIG.validacao.ListaMensagens;

public class CadastroCrudOperation implements CrudOperation {

	@Override
	public void operar(Operacao operacao) throws DAOException, NegocioException {
		validate(operacao);
		if(operacao.getComando().equals(ListaComando.CADASTRAR)) {
			criar(operacao.getEntidade());
		} else if(operacao.getComando().equals(ListaComando.ALTERAR)) {
			alterar(operacao.getEntidade());
		} else if(operacao.getComando().equals(ListaComando.REMOVER)) {
			remover(operacao.getEntidade());
		}
	}

	@Override
	public void validate(Operacao operacao) throws NegocioException {

	}

	protected Object criar(BaseEntity entidade) {
		GenericDAO dao = new GenericDAOImpl();
		try {
			dao.create(entidade);
		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			dao.commit();
		}
		return entidade;
	}

	protected Object alterar(BaseEntity entidade) {
		GenericDAO dao = new GenericDAOImpl();
		try {
			dao.update(entidade);
		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			dao.commit();
		}
		return entidade;
	}
	
	protected Object remover(BaseEntity entidade) {
		GenericDAO dao = new GenericDAOImpl();
		try {
			dao.remove(entidade);
		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			dao.commit();
		}
		return entidade;
	}

	public void checkValidation(ListaMensagens mensagens) throws NegocioException {
		if (mensagens != null && mensagens.isErrorPresent()) {
			NegocioException e = new NegocioException();
			e.addMensagens(mensagens.getErrorMessages());
			throw e;
		}
	}
}
