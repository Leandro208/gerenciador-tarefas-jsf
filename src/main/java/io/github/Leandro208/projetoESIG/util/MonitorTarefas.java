package io.github.Leandro208.projetoESIG.util;

import java.io.Serializable;

public class MonitorTarefas implements Serializable {
	private static final long serialVersionUID = 1L;

	private int andamento;
	private int atrasadas;
	private int proximas;
	private int concluidasNoPeriodo;
	

	public MonitorTarefas() {
		this.andamento = 0;
		this.atrasadas = 0;
		this.proximas = 0;
		this.concluidasNoPeriodo = 0;
	}

	public MonitorTarefas(int andamento, int concluidas) {
		this.andamento = andamento;
		this.concluidasNoPeriodo = concluidas;
	}

	public MonitorTarefas(int andamento, int atrasadas, int proximas, int concluidasNoPeriodo) {
		this.andamento = andamento;
		this.atrasadas = atrasadas;
		this.proximas = proximas;
		this.concluidasNoPeriodo = concluidasNoPeriodo;
	}

	public int getAndamento() {
		return andamento;
	}

	public void setAndamento(int andamento) {
		this.andamento = andamento;
	}

	public int getAtrasadas() {
		return atrasadas;
	}

	public void setAtrasadas(int atrasadas) {
		this.atrasadas = atrasadas;
	}

	public int getProximas() {
		return proximas;
	}

	public void setProximas(int proximas) {
		this.proximas = proximas;
	}

	public int getConcluidasNoPeriodo() {
		return concluidasNoPeriodo;
	}

	public void setConcluidasNoPeriodo(int concluidasNoPeriodo) {
		this.concluidasNoPeriodo = concluidasNoPeriodo;
	}

	/** Mantém compatibilidade com expressões e chamadas já existentes. */
	public int getConcluidas() {
		return concluidasNoPeriodo;
	}

	public void setConcluidas(int concluidas) {
		this.concluidasNoPeriodo = concluidas;
	}


}
