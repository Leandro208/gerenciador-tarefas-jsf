package io.github.Leandro208.projetoESIG.persistence;

public class TarefaCrudOperation extends CadastroCrudOperation {

	@Override
	public void operar(Operacao operacao) {
		validate(operacao);
		if(operacao.getComando().equals(ListaComando.CADASTRAR_TAREFA)) {
			criar(operacao);
		} else if(operacao.getComando().equals(ListaComando.ALTERAR_TAREFA)) {
			alterar(operacao);
		}
	}

	@Override
	public void validate(Operacao operacao) {
		
	}

	
}
