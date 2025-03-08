package io.github.Leandro208.projetoESIG.persistence;

import java.util.Objects;

public class Comando {
	
	private int id;
	private String classe;

	public Comando(int id, String classe) {
		this.id = id;
		this.classe = classe;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
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
		Comando other = (Comando) obj;
		return id == other.id;
	}
	
	

}
