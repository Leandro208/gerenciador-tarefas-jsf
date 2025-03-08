package io.github.Leandro208.projetoESIG.dao;

import java.util.Collection;

import io.github.Leandro208.projetoESIG.entities.BaseEntity;

public interface GenericDAO {

	public void create(BaseEntity entidade) throws DAOException;

	public void update(BaseEntity entidade) throws DAOException;

	void remove(BaseEntity entidade) throws DAOException;

	public <T extends BaseEntity> T findById(Long id, Class<T> clazz) throws DAOException;

	public <T> Collection<T> findAll(Class<T> clazz) throws DAOException;

	void commit();

	<T extends BaseEntity> void updateField(Long id, Class<T> clazz, String fieldName, Object newValue) throws DAOException;

}
