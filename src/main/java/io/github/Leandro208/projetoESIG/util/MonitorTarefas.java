package io.github.Leandro208.projetoESIG.util;

public class MonitorTarefas {

	private int andamento;
	private int concluidas;
	

	public MonitorTarefas() {
		this.andamento = 0;
		this.concluidas = 0;
	}

	public MonitorTarefas(int andamento, int concluidas) {
		this.andamento = andamento;
		this.concluidas = concluidas;
	}

	public int getAndamento() {
		return andamento;
	}

	public void setAndamento(int andamento) {
		this.andamento = andamento;
	}

	public int getConcluidas() {
		return concluidas;
	}

	public void setConcluidas(int concluidas) {
		this.concluidas = concluidas;
	}


}
