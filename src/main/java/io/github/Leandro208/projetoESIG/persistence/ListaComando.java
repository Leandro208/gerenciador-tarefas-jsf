package io.github.Leandro208.projetoESIG.persistence;

public class ListaComando {
	
	public static final Comando CADASTRAR = new Comando(1, "io.github.Leandro208.projetoESIG.persistence.CadastroCrudOperation");
	public static final Comando ALTERAR = new Comando(2, "io.github.Leandro208.projetoESIG.persistence.CadastroCrudOperation");
	public static final Comando REMOVER = new Comando(3, "io.github.Leandro208.projetoESIG.persistence.CadastroCrudOperation");

	
	
	public static final Comando CADASTRAR_TAREFA = new Comando(10, "io.github.Leandro208.projetoESIG.persistence.TarefaCrudOperation");
	public static final Comando ALTERAR_TAREFA = new Comando(11, "io.github.Leandro208.projetoESIG.persistence.TarefaCrudOperation");

}
