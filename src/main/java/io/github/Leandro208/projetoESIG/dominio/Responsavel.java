package io.github.Leandro208.projetoESIG.dominio;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "responsavel", schema = "pessoa")
public class Responsavel implements BaseEntity, Serializable {

	private static final long serialVersionUID = 1606071181997167603L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Size(min=3, max=50, message="Nome deve ter entre 8 e 50 caracteres.")
	@NotBlank(message="Nome não pode estar vazio.")
	private String nome;
	
	@Column(name="data_nascimento")
	@NotNull(message="Data de nascimento deve ser definida.") 
	private Date dataNascimento;
	
	@ManyToOne
	@JoinColumn(name="id_equipe")
	private Equipe equipe;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_usuario")
	private Usuario usuario;
	
	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;
	
	public Responsavel() {
		this.usuario = new Usuario();
	}
	
	public Responsavel(Long id, String nome, String email, Date dataNascimento) {
		super();
		this.id = id;
		this.nome = nome;
		this.dataNascimento = dataNascimento;
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

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Equipe getEquipe() {
		return equipe;
	}

	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Responsavel that = (Responsavel) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Responsavel{" +
				"nome='" + nome + '\'' +
				'}';
	}
}
