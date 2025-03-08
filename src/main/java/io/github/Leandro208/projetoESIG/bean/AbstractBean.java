package io.github.Leandro208.projetoESIG.bean;

import io.github.Leandro208.projetoESIG.persistence.CrudOperation;
import io.github.Leandro208.projetoESIG.persistence.Operacao;
import io.github.Leandro208.projetoESIG.util.Message;

public abstract class AbstractBean {

	protected final String BOTAO_CADASTRAR = "Cadastrar";
	
	protected final String BOTAO_ALTERAR = "Alterar";
	
	private String confirmButton = BOTAO_CADASTRAR;

	public void realizarOperacao(Operacao operacao) throws Exception {
		Class<?> clazz = Class.forName(operacao.getComando().getClasse());
		CrudOperation crudOperation = (CrudOperation) clazz.getDeclaredConstructor().newInstance();
		crudOperation.operar(operacao);

	}

	public void addMensagem(String mensagem) {
		Message.info(mensagem);
	}

	public void addMensagemErro(String mensagem) {
		Message.erro(mensagem);
	}

	public void addMensagemWarning(String mensagem) {
		Message.warn(mensagem);
	}

	public void addMensagemErroPadrao() {
		addMensagemErro("Um erro ocorreu durante a execução desta operação. ");
	}

	public String getConfirmButton() {
		return confirmButton;
	}

	public void setConfirmButton(String confirmButton) {
		this.confirmButton = confirmButton;
	}
	
	public <U> U getDAO(Class<U> daoClass) {
	    try {
	        return daoClass.getDeclaredConstructor().newInstance();
	    } catch (Exception e) {
	        throw new RuntimeException("Erro ao instanciar a classe: " + daoClass.getName(), e);
	    }
	}

}
