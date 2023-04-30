package io.github.Leandro208.projetoESIG.entities;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import io.github.Leandro208.projetoESIG.enums.PrioridadeEnum;
import io.github.Leandro208.projetoESIG.enums.StatusEnum;


@Entity
public class Tarefa implements Base, Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotEmpty(message="Título não pode ser nulo")
	private String titulo;
	
	@NotEmpty(message="Descrição não pode ser nulo")
	@Size(max = 255, message="A descrição precisa ter no maximo 255 caracteres, escreva uma descrição mais breve.")
	private String descricao;

	//demora do krl
	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name="id_responsavel")
	private Responsavel responsavel;

	@Enumerated(EnumType.STRING)
	private PrioridadeEnum prioridade;
	
	@Enumerated(EnumType.STRING)
	private StatusEnum status;
	
	@Temporal(TemporalType.DATE)
	@NotNull(message="Deadline precisa ser definido")
	private Date deadline;
	
	private Date dataFinalizacao;
	
	public Tarefa() {
		status = StatusEnum.EM_ANDAMENTO;
	}
	
	public Tarefa(Long id, String titulo, String descricao, Responsavel responsavel, PrioridadeEnum prioridade,
			StatusEnum status, Date data) {
		super();
		this.id = id;
		this.titulo = titulo;
		this.descricao = descricao;
		this.responsavel = responsavel;
		this.prioridade = prioridade;
		this.status = status;
		this.deadline = data;
	}
	
	public boolean isFinished() {
		return status == StatusEnum.CONCLUIDO;
	}
	
	public String getDias() throws ParseException {
		
		if(status.equals(StatusEnum.CONCLUIDO)) {
			if(dataFinalizacao.after(deadline)) {
				return "Finalizada com atraso";
			}
			return "Finalizada no prazo";
		}
		String dias = "";
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date hoje = new Date();
		String data = df.format(hoje);
		hoje = df.parse(data);
		
		if(hoje.after(deadline)) {
			dias = "Atrasado ";
		}
		
		Long x = Math.abs(hoje.getTime() - deadline.getTime());
		Long diferenca = TimeUnit.DAYS.convert(x, TimeUnit.MILLISECONDS);
		dias += diferenca.toString();
		
		if(diferenca==0) {
			return "Entrega hoje";
		}
		else if(diferenca > 1) {
			dias += " Dias";
		} else dias += " Dia";
		
		return dias;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {

		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	

	public Responsavel getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Responsavel responsavel) {
		this.responsavel = responsavel;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date data) {
		this.deadline = data;
	}

	public PrioridadeEnum getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(PrioridadeEnum prioridade) {
		this.prioridade = prioridade;
	}

	public StatusEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEnum status) {
		this.status = status;
	}

	public Date getDataFinalizacao() {
		return dataFinalizacao;
	}

	public void setDataFinalizacao(Date dataFinalizacao) {
		this.dataFinalizacao = dataFinalizacao;
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
		Tarefa other = (Tarefa) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Tarefa [id=" + id + ", titulo=" + titulo + ", descricao=" + descricao + ", responsavel=" + responsavel
				+ ", prioridade=" + prioridade + ", status=" + status + ", deadline=" + deadline + "]";
	}
	

}