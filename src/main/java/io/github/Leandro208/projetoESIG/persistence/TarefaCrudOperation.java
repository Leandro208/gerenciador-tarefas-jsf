package io.github.Leandro208.projetoESIG.persistence;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAO;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;
import io.github.Leandro208.projetoESIG.dominio.Tarefa;

public class TarefaCrudOperation extends CadastroCrudOperation {

	@Override
	public void operar(Operacao operacao) throws DAOException {
		validate(operacao);
		if(operacao.getComando().equals(ListaComando.CADASTRAR_TAREFA)) {
			criar(operacao.getEntidade());
		} else if(operacao.getComando().equals(ListaComando.ALTERAR_TAREFA)) {
			alterar(operacao.getEntidade());
		} else if(operacao.getComando().equals(ListaComando.ATRIBUIR_TAREFA)) {
			atribuirTarefa(operacao);
		} else if(operacao.getComando().equals(ListaComando.MOVER_TAREFA)) {
			moverTarefa(operacao);
		}
	}

	private void moverTarefa(Operacao operacao) throws DAOException {
		GenericDAO dao = new GenericDAOImpl();
		try {
			Tarefa tarefa = (Tarefa) operacao.getEntidade();
			dao.updateField(tarefa.getId(), Tarefa.class,"status", tarefa.getStatus());
		}finally {
			dao.commit();
		}
	}

	private void atribuirTarefa(Operacao operacao) throws DAOException {
		GenericDAO dao = new GenericDAOImpl();
		try {
			Tarefa tarefa = (Tarefa) operacao.getEntidade();
			dao.updateField(tarefa.getId(), Tarefa.class,"responsavel", tarefa.getResponsavel());
		}finally {
            dao.commit();
		}
	}

	@Override
	public void validate(Operacao operacao) {
		
	}

	
}
