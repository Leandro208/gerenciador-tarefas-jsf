package io.github.Leandro208.projetoESIG.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import io.github.Leandro208.projetoESIG.connection.ConnectionFactory;
import io.github.Leandro208.projetoESIG.entities.Base;
import io.github.Leandro208.projetoESIG.util.Message;

public class GenericDao <T extends Base> implements Serializable {

private static final long serialVersionUID = 1L; 
	
	private static EntityManager manager = ConnectionFactory.getEntityManager();
	
	public T buscarPorId(Class<T> clazz, Long id) {
		return manager.find(clazz, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> buscarTodos(String hql){
		return manager.createQuery(hql).getResultList();
	}
	
	public void salvar(T entidade) {
		try {
			manager.getTransaction().begin();
			if(entidade.getId() == null) {
				manager.persist(entidade);
			} else {
				manager.merge(entidade);
			}
			Message.info("Operação realizada com sucesso!");
			manager.getTransaction().commit();
		} catch (Exception e) {
			manager.getTransaction().rollback();
		}
	}

	
	public void remover(Class<T> clazz, Long id) {
		T entidade = buscarPorId(clazz, id);
		try {
			manager.getTransaction().begin();
			manager.remove(entidade);
			Message.info("Excluido com sucesso!");
			manager.getTransaction().commit();
		} catch (Exception e) {
			manager.getTransaction().rollback();
		}
	}
	
}
