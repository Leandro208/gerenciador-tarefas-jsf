package io.github.Leandro208.projetoESIG.bean;

public abstract class AbstractCadastroBean<T> {

	protected T obj;
	
	public abstract String cadastrar();
	
}
