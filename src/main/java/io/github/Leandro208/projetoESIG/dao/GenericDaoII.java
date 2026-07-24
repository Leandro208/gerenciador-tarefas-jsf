package io.github.Leandro208.projetoESIG.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import io.github.Leandro208.projetoESIG.connection.ConnectionFactory;
import io.github.Leandro208.projetoESIG.dominio.BaseEntity;

public class GenericDaoII <T extends BaseEntity> implements Serializable {

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
			manager.getTransaction().commit();
		} catch (Exception e) {
			if (manager.getTransaction().isActive()) {
				manager.getTransaction().rollback();
			}
			throw new RuntimeException("Erro ao salvar entidade", e);
		} finally {
			manager.close();
		}
	}

	
	public void remover(Class<T> clazz, Long id) {
		EntityManager manager = ConnectionFactory.getEntityManager();
		try {
			manager.getTransaction().begin();
			T entidade = manager.find(clazz, id);
			manager.remove(entidade);
			manager.getTransaction().commit();
		} catch (Exception e) {
			if (manager.getTransaction().isActive()) {
				manager.getTransaction().rollback();
			}
			throw new RuntimeException("Erro ao remover entidade", e);
		} finally {
			manager.close();
		}
	}
	
}
