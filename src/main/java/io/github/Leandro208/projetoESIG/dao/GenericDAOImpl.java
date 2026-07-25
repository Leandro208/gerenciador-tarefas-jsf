package io.github.Leandro208.projetoESIG.dao;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;

import io.github.Leandro208.projetoESIG.connection.ConnectionFactory;
import io.github.Leandro208.projetoESIG.dominio.BaseEntity;
import io.github.Leandro208.projetoESIG.dominio.LogDB;
import io.github.Leandro208.projetoESIG.dominio.RegistroAcesso;
import io.github.Leandro208.projetoESIG.dominio.Usuario;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.persistence.Operacao;
import io.github.Leandro208.projetoESIG.util.ReflectionUtils;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;

public class GenericDAOImpl implements GenericDAO {

	protected static final int INSERIR = 1;
	protected static final int ATUALIZAR = 2;
	protected static final int REMOVER = 3;

	private int codComando;

	private EntityManager em;

	public GenericDAOImpl() {}

	public GenericDAOImpl(Operacao operacao) {
		codComando = operacao.getComando().getId();
	}


	protected EntityManager getSession() {
		if (em == null || !em.isOpen()) {
			em = ConnectionFactory.getEntityManager();
		}
		return em;
	}

	protected void changeOperation(BaseEntity entidade, int operacao) throws DAOException {
		EntityManager session = getSession();
		try {

			session.getTransaction().begin();
			switch (operacao) {
			case INSERIR:
				initializeCreationFields(entidade);
				session.persist(entidade);
				break;
			case ATUALIZAR:
				session.merge(entidade);
				break;
			case REMOVER:
				session.remove(session.contains(entidade) ? entidade : session.merge(entidade));
				break;
			}
			logOperationBanco(entidade,operacao);
			session.getTransaction().commit();
		} catch (Exception e) {
			if (session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			throw new DAOException(e);
		}
	}

	private void initializeCreationFields(BaseEntity entidade) {
        Field[] dataCadastro = ReflectionUtils.getFieldsByName(entidade, "dataCadastro");
        Field[] registroAcesso = ReflectionUtils.getFieldsByName(entidade, "registroAcesso");
        Field[] ativo = ReflectionUtils.getFieldsByName(entidade, "ativo");

        if (dataCadastro.length > 0) {
            for (Field field : dataCadastro) {
                ReflectionUtils.setFieldValue(entidade, field, new Date());
            }
        }

        for (Field field : registroAcesso) {
            ReflectionUtils.setFieldValue(entidade, field, getCriador(field));
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
		if (fieldCriador.getType().equals(RegistroAcesso.class)) {
			if (UsuarioUtils.getLogado() != null)
				return UsuarioUtils.getLogado().getRegistroAcesso();
			else
				return null;
		} else {
			UsuarioDTO logado = UsuarioUtils.getLogado();
			if (logado != null) {
				return new Usuario(logado.getId());
			}
			return null;
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
	        entityManager.getTransaction().begin();
	        T entity = entityManager.find(clazz, id);
	        
	        if (entity == null) {
	            throw new DAOException("Entidade com ID " + id + " não encontrada.");
	        }
	        
	        Field field = clazz.getDeclaredField(fieldName);
	        field.setAccessible(true);
	        field.set(entity, newValue);
	        
	        entityManager.merge(entity);
			logUpdateFieldBanco(clazz, entity.getId(),new String[]{fieldName}, new Object[]{newValue});
	        entityManager.getTransaction().commit();

	    }  catch (Exception e) {
	        throw new DAOException("Erro inesperado ao atualizar campo", e);
	    }
	}

	private void logUpdateFieldBanco(Class<?> classe, Long id, String[] campos, Object[] valores ) throws DAOException {
		try{
			LogDB logDB = new LogDB();
			logDB.setData(new Date());
			logDB.setCodComando(this.codComando);
			logDB.setOperacao('U');
			logDB.setTabela(classe.getName());
			logDB.setIdElemento(id);

			StringBuilder alteracoes = new StringBuilder();
			for (int i = 0; i < valores.length; i++) {
				Object valor = valores[i];
				String informacao = valor == null ? "null" : valor.toString();
				if(valor instanceof BaseEntity){
					informacao = ((BaseEntity) valor).getId() == null ? "null" : ((BaseEntity) valor).getId().toString();
				}
				alteracoes.append(campos[i] + ": " + informacao);
				alteracoes.append("\n");
			}

			logDB.setAlteracao(alteracoes.toString());
			UsuarioDTO logado = UsuarioUtils.getLogado();
			if (logado != null) {
				logDB.setRegistroAcesso(logado.getRegistroAcesso());
			}
			getSession().persist(logDB);
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	private void logOperationBanco(BaseEntity entidade, int operacao ) throws DAOException {
		try{
			LogDB logDB = new LogDB();
			logDB.setData(new Date());
			logDB.setCodComando(this.codComando);

			if(operacao == INSERIR){
				logDB.setOperacao('I');
			} else if(operacao == ATUALIZAR){
				logDB.setOperacao('U');
			} else if(operacao == REMOVER){
				logDB.setOperacao('D');
			}

			logDB.setTabela(entidade.getClass().getName());
			logDB.setIdElemento(entidade.getId());

			logDB.setAlteracao(entidade.toString());
			UsuarioDTO logado = UsuarioUtils.getLogado();
			if (logado != null) {
				logDB.setRegistroAcesso(logado.getRegistroAcesso());
			}
			getSession().persist(logDB);
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
}
