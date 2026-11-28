package io.github.Leandro208.projetoESIG.dominio;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
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

@Entity
@Table(name = "tarefa_anexo", schema = "gestao")
public class TarefaAnexo implements BaseEntity, Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_tarefa", nullable = false)
	private Tarefa tarefa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario autor;

	@Column(name = "nome_arquivo", nullable = false, length = 255)
	private String nomeArquivo;

	@Column(name = "tipo_mime", nullable = false, length = 100)
	private String tipoMime;

	@Column(name = "tamanho", nullable = false)
	private long tamanho;

	@Column(name = "data_upload", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataUpload;

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "conteudo", nullable = false, columnDefinition = "bytea")
	private byte[] conteudo;

	@Override public Long getId() { return id; }
	@Override public void setId(Long id) { this.id = id; }
	public Tarefa getTarefa() { return tarefa; }
	public void setTarefa(Tarefa tarefa) { this.tarefa = tarefa; }
	public Usuario getAutor() { return autor; }
	public void setAutor(Usuario autor) { this.autor = autor; }
	public String getNomeArquivo() { return nomeArquivo; }
	public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }
	public String getTipoMime() { return tipoMime; }
	public void setTipoMime(String tipoMime) { this.tipoMime = tipoMime; }
	public long getTamanho() { return tamanho; }
	public void setTamanho(long tamanho) { this.tamanho = tamanho; }
	public Date getDataUpload() { return dataUpload; }
	public void setDataUpload(Date dataUpload) { this.dataUpload = dataUpload; }
	public byte[] getConteudo() { return conteudo; }
	public void setConteudo(byte[] conteudo) { this.conteudo = conteudo; }
}
