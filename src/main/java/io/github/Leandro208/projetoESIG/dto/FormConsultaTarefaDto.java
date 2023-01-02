package io.github.Leandro208.projetoESIG.dto;

import io.github.Leandro208.projetoESIG.entities.Responsavel;
import io.github.Leandro208.projetoESIG.entities.Tarefa;
import io.github.Leandro208.projetoESIG.enums.StatusEnum;

public class FormConsultaTarefaDto {
private Long numero;
	
	private String titulo;
	
	private Responsavel responsavel;
	
	private StatusEnum situacao;

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
}
