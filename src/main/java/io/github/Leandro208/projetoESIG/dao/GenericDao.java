package io.github.Leandro208.projetoESIG.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import io.github.Leandro208.projetoESIG.connection.ConnectionFactory;
import io.github.Leandro208.projetoESIG.entities.BaseEntity;
import io.github.Leandro208.projetoESIG.util.Message;

public class GenericDao <T extends BaseEntity> implements Serializable {

	private static final long serialVersionUID = 1L; 
	
	public T buscarPorId(Class<T> clazz, Long id) {
        EntityManager manager = ConnectionFactory.getEntityManager();
        try {
            return manager.find(clazz, id);
        } finally {
            manager.close();
        }
    }
	
	@SuppressWarnings("unchecked")
	public List<T> buscarTodos(String hql){
		EntityManager manager = ConnectionFactory.getEntityManager();
		try {
			return manager.createQuery(hql).getResultList();
		} finally {
			manager.close();
		}
		
	}
	
	public void salvar(T entidade) {
		EntityManager manager = ConnectionFactory.getEntityManager();
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
			System.out.println(e.getMessage());
			e.printStackTrace();
			manager.getTransaction().rollback();
		} finally {
			manager.close();
		}
	}

	
	public void remover(Class<T> clazz, Long id) {
		EntityManager manager = ConnectionFactory.getEntityManager();
		try {
			T entidade = manager.find(clazz, id);
			manager.getTransaction().begin();
			manager.remove(entidade);
			Message.info("Excluido com sucesso!");
			manager.getTransaction().commit();
		} catch (Exception e) {
			manager.getTransaction().rollback();
		} finally {
			manager.close();
		}
	}
	
}
