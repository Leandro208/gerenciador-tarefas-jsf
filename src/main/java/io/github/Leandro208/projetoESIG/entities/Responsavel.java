package io.github.Leandro208.projetoESIG.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import io.github.Leandro208.projetoESIG.enums.Funcao;
import io.github.Leandro208.projetoESIG.util.ValidatorUtils;

@Entity
public class Responsavel implements BaseEntity, Serializable {

	private static final long serialVersionUID = 1606071181997167603L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Size(min=3, max=50, message="Nome deve ter entre 8 e 50 caracteres.")
	@NotBlank(message="Nome não pode estar vazio.")
	private String nome;

	@Email(message="E-mail deve estar no formato correto.") 
	private String email;
	
	@Size(min=6, message="Senha deve ter no minimo 6 caracteres.")
	@NotBlank(message="Senha não pode estar vazio.")
	private String senha;
	
	@Column(name="data_nascimento")
	@NotNull(message="Data de nascimento deve ser definida.") 
	private Date dataNascimento;
	
	@Enumerated(EnumType.STRING)
	private Funcao funcao;
	
	@ManyToOne(cascade = CascadeType.ALL)
	private Equipe equipe;
	
	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;
	
	@Transient
	private RegistroEntrada entrada;
	
	public Responsavel() {
		funcao = Funcao.USER;
	}
	
	public Responsavel(Long id, String nome, String email, Date dataNascimento) {
		super();
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.dataNascimento = dataNascimento;
	}

	public boolean isAdm() {
		return funcao == Funcao.ADM;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
	public Funcao getFuncao() {
		return funcao;
	}

	public void setFuncao(Funcao funcao) {
		this.funcao = funcao;
	}

	public Equipe getEquipe() {
		return equipe;
	}

	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}
	
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	

	public RegistroEntrada getRegistroEntrada() {
		return entrada;
	}

	public void setRegistroEntrada(RegistroEntrada entrada) {
		this.entrada = entrada;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dataNascimento, email, id, nome);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Responsavel other = (Responsavel) obj;
		return Objects.equals(dataNascimento, other.dataNascimento) && Objects.equals(email, other.email)
				&& Objects.equals(id, other.id) && Objects.equals(nome, other.nome);
	}

	@Override
	public String toString() {
		return "Responsavel [id=" + id + ", nome=" + nome + ", email=" + email + ", senha=" + senha
				+ ", dataNascimento=" + dataNascimento + ", funcao=" + funcao + "]";
	}

	
}
