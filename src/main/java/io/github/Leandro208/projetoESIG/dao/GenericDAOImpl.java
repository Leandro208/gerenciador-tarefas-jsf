package io.github.Leandro208.projetoESIG.dao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;

import io.github.Leandro208.projetoESIG.connection.ConnectionFactory;
import io.github.Leandro208.projetoESIG.entities.BaseEntity;
import io.github.Leandro208.projetoESIG.entities.RegistroEntrada;
import io.github.Leandro208.projetoESIG.exception.NoDataException;
import io.github.Leandro208.projetoESIG.util.ReflectionUtils;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;

public class GenericDAOImpl implements GenericDAO {

	protected static final int INSERIR = 1;
	protected static final int ATUALIZAR = 2;
	protected static final int REMOVER = 3;

	private EntityManager em;

	protected EntityManager getSession() {
		if (em == null || !em.isOpen()) {
			em = ConnectionFactory.getEntityManager();
		}
		return em;
	}

	protected void changeOperation(BaseEntity entidade, int operacao) throws DAOException {

		try {
			switch (operacao) {
			case INSERIR:
				initializeCreationFields(entidade);
				getSession().persist(entidade);
				break;
			case ATUALIZAR:
				getSession().merge(entidade);
				break;
			case REMOVER:
				getSession().remove(entidade);
				break;
			}
			commit();

		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	private void initializeCreationFields(BaseEntity entidade) {
        Field[] dataCadastro = ReflectionUtils.getFieldsByName(entidade, "dataCadastro");
        Field[] registroEntrada = ReflectionUtils.getFieldsByName(entidade, "registroEntrada");
        Field[] ativo = ReflectionUtils.getFieldsByName(entidade, "ativo");

        if (dataCadastro.length > 0) {
            for (Field field : dataCadastro) {
                ReflectionUtils.setFieldValue(entidade, field, new Date());
            }
        }

        if (registroEntrada.length > 0) {
            for (Field field : registroEntrada) {
                ReflectionUtils.setFieldValue(entidade, field, getCriador(field));
            }
        }

        if (ativo.length > 0) {
            for (Field field : ativo) {
                ReflectionUtils.setFieldValue(entidade, field, true); 
            }
        }
    }
	
	private BaseEntity getCriador(Field fieldCriador) {
		if (fieldCriador == null)
			return null;
		if (fieldCriador.getType().equals(RegistroEntrada.class)) {
			if (UsuarioUtils.getLogado() != null)
				return UsuarioUtils.getLogado().getRegistroEntrada();
			else
				return null;
		} else {
			return UsuarioUtils.getLogado();
		}
	}

	@Override
	public void create(BaseEntity entidade) throws DAOException {
		changeOperation(entidade, INSERIR);
	}

	@Override
	public void update(BaseEntity entidade) throws DAOException {
		changeOperation(entidade, ATUALIZAR);
	}

	
	@Override
	public void commit(){
		try {
			em.getTransaction().begin();
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			e.printStackTrace();
		}
	}

	@Override
	public void remove(BaseEntity entidade) throws DAOException {
		changeOperation(entidade, REMOVER);
	}

	@Override
	public <T extends BaseEntity> T findById(Long id, Class<T> clazz) throws DAOException {
		try {
			T entidade = (T) getSession().find(clazz, id);
			return entidade;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	@Override
	public <T> Collection<T> findAll(Class<T> clazz) throws DAOException {
		try {
	        return getSession()
	            .createQuery("SELECT e FROM " + clazz.getSimpleName() + " e", clazz)
	            .getResultList();
	    } catch (Exception e) {
	        throw new DAOException("Erro ao buscar todas as entidades de " + clazz.getSimpleName(), e);
	    }
	}
	@Override
	public <T extends BaseEntity> void updateField(Long id, Class<T> clazz, String fieldName, Object newValue) throws DAOException {
	    try {
	        EntityManager entityManager = getSession();
	        T entity = entityManager.find(clazz, id);
	        
	        if (entity == null) {
	            throw new DAOException("Entidade com ID " + id + " não encontrada.");
	        }
	        
	        Field field = clazz.getDeclaredField(fieldName);
	        field.setAccessible(true);
	        field.set(entity, newValue);
	        
	        entityManager.getTransaction().begin();
	        entityManager.merge(entity);
	        entityManager.getTransaction().commit();
	    }  catch (Exception e) {
	        throw new DAOException("Erro inesperado ao atualizar campo", e);
	    }
	}

}
