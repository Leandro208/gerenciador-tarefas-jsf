package io.github.Leandro208.projetoESIG.dominio;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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

import org.hibernate.validator.constraints.NotBlank;

import io.github.Leandro208.projetoESIG.util.HtmlSanitizer;

@Entity
@Table(name = "comentario", schema = "gestao")
public class Comentario implements BaseEntity, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_tarefa", nullable = false)
	private Tarefa tarefa;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_responsavel")
	private Responsavel autor;

	@NotBlank(message = "Comentário não pode ser vazio")
	@Column(columnDefinition = "TEXT")
	private String texto;

	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_acesso")
	private RegistroAcesso registroAcesso;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Tarefa getTarefa() {
		return tarefa;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
	}

	public Responsavel getAutor() {
		return autor;
	}

	public void setAutor(Responsavel autor) {
		this.autor = autor;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroAcesso getRegistroAcesso() {
		return registroAcesso;
	}

	public void setRegistroAcesso(RegistroAcesso registroAcesso) {
		this.registroAcesso = registroAcesso;
	}

	/**
	 * HTML pronto para renderizacao com escape="false". A sanitizacao ja
	 * ocorre na gravacao; repetir aqui protege comentarios antigos, gravados
	 * antes da sanitizacao existir.
	 */
	public String getTextoSeguro() {
		return HtmlSanitizer.sanitizar(texto);
	}

	public String getAutorNome() {
		if (autor != null && autor.getNome() != null && !autor.getNome().trim().isEmpty()) {
			return autor.getNome();
		}
		if (registroAcesso != null && registroAcesso.getUsuario() != null
				&& registroAcesso.getUsuario().getEmail() != null) {
			return registroAcesso.getUsuario().getEmail();
		}
		return "Usuário";
	}

	public Long getAutorUsuarioId() {
		if (autor != null && autor.getUsuario() != null) return autor.getUsuario().getId();
		if (registroAcesso != null && registroAcesso.getUsuario() != null) return registroAcesso.getUsuario().getId();
		return null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Comentario other = (Comentario) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Comentario [id=" + id + ", idTarefa=" + (tarefa != null ? tarefa.getId() : null) + "]";
	}
}
