package io.github.Leandro208.projetoESIG.dto;

import java.io.Serializable;
import java.util.Date;

import io.github.Leandro208.projetoESIG.dominio.Equipe;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dominio.Tarefa;
import io.github.Leandro208.projetoESIG.enums.StatusEnum;
import io.github.Leandro208.projetoESIG.enums.PrioridadeEnum;

public class FormConsultaTarefaDto implements Serializable {
private static final long serialVersionUID = 1L;
private Long numero;
	
	private String titulo;
	
	private Responsavel responsavel;
	
	private StatusEnum situacao;

	private PrioridadeEnum prioridade;

	private Date prazoDe;

	private Date prazoAte;
	
	private Equipe equipe;
	
	public FormConsultaTarefaDto() {}
	
	public FormConsultaTarefaDto(Tarefa t) {
		this.numero = t.getId();
		this.titulo = t.getTitulo();
		this.responsavel = t.getResponsavel();
		this.situacao = t.getStatus();
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Responsavel getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Responsavel responsavel) {
		this.responsavel = responsavel;
	}

	public StatusEnum getSituacao() {
		return situacao;
	}

	public void setSituacao(StatusEnum situacao) {
		this.situacao = situacao;
	}

	public PrioridadeEnum getPrioridade() { return prioridade; }

	public void setPrioridade(PrioridadeEnum prioridade) { this.prioridade = prioridade; }

	public Date getPrazoDe() { return prazoDe; }

	public void setPrazoDe(Date prazoDe) { this.prazoDe = prazoDe; }

	public Date getPrazoAte() { return prazoAte; }

	public void setPrazoAte(Date prazoAte) { this.prazoAte = prazoAte; }

	public Equipe getEquipe() {
		return equipe;
	}

	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}
}
