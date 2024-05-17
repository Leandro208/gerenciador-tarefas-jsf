package io.github.Leandro208.projetoESIG.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Equipe implements Base, Serializable {


	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotEmpty(message = "O nome não pode estar vazio!")
	private String nome;
	
	private Integer tarefas_concluidas;
	
	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;
	
	@ManyToOne
	@JoinColumn(name="id_registro_entrada")
	private RegistroEntrada registroEntrada;

	public Equipe() {
	}

	public Equipe(Long id, String nome, Integer tarefas_concluidas) {
		super();
		this.id = id;
		this.nome = nome;
		this.tarefas_concluidas = tarefas_concluidas;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getTarefas_concluidas() {
		return tarefas_concluidas;
	}

	public void setTarefas_concluidas(Integer tarefas_concluidas) {
		this.tarefas_concluidas = tarefas_concluidas;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	@Override
	public String toString() {
		return "Equipe [id=" + id + ", nome=" + nome + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Equipe other = (Equipe) obj;
		return Objects.equals(id, other.id);
	}
	
}
