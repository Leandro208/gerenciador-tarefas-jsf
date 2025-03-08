package io.github.Leandro208.projetoESIG.persistence;

public interface CrudOperation {
	public void operar(Operacao operacao);
	public void validate(Operacao operacao);
}
