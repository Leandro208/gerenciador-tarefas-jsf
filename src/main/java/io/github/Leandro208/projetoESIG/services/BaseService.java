package io.github.Leandro208.projetoESIG.services;

public interface BaseService<T> {
	public T buscarPorId(Long id);
	public void salvar(T t);
	public void remover(T t);
}
