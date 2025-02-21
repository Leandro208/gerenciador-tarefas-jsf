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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "registro_entrada")
public class RegistroEntrada implements BaseEntity, Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Responsavel usuario;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	
	@Column(name = "data_saida")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataSaida;
	
	private String ip;

	public RegistroEntrada() {
	}

	public RegistroEntrada(Long id, Responsavel usuario, Date data, String ip) {
		this.id = id;
		this.usuario = usuario;
		this.data = data;
		this.ip = ip;
	}

	@Override
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Responsavel getUsuario() {
		return usuario;
	}

	public void setUsuario(Responsavel usuario) {
		this.usuario = usuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
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
		RegistroEntrada other = (RegistroEntrada) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "[usuario=" + usuario.getEmail() + ", entrou em =" + data
				+ ", ip=" + ip + "]";
	}
	
}
