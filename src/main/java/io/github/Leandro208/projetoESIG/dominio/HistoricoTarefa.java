package io.github.Leandro208.projetoESIG.dominio;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "historico_tarefa", schema = "gestao")
public class HistoricoTarefa implements BaseEntity, Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_tarefa", nullable = false)
	private Tarefa tarefa;

	@Column(nullable = false, length = 100)
	private String campo;

	@Column(name = "valor_anterior", columnDefinition = "TEXT")
	private String valorAnterior;

	@Column(name = "valor_novo", columnDefinition = "TEXT")
	private String valorNovo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@Transient
	private String autorNome;

	@Column(name = "data_alteracao", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAlteracao;

	@Override
	public Long getId() { return id; }
	@Override
	public void setId(Long id) { this.id = id; }
	public Tarefa getTarefa() { return tarefa; }
	public void setTarefa(Tarefa tarefa) { this.tarefa = tarefa; }
	public String getCampo() { return campo; }
	public void setCampo(String campo) { this.campo = campo; }
	public String getValorAnterior() { return valorAnterior; }
	public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }
	public String getValorNovo() { return valorNovo; }
	public void setValorNovo(String valorNovo) { this.valorNovo = valorNovo; }
	public String getAutorNome() { return autorNome; }
	public void setAutorNome(String autorNome) { this.autorNome = autorNome; }
	public Usuario getUsuario() { return usuario; }
	public void setUsuario(Usuario usuario) { this.usuario = usuario; }
	public Date getDataAlteracao() { return dataAlteracao; }
	public void setDataAlteracao(Date dataAlteracao) { this.dataAlteracao = dataAlteracao; }
}
