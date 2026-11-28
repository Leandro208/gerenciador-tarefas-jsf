package io.github.Leandro208.projetoESIG.dto;

import java.io.Serializable;

public class TarefaAnexoArquivoDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String nomeArquivo;
	private final String tipoMime;
	private final byte[] conteudo;

	public TarefaAnexoArquivoDTO(String nomeArquivo, String tipoMime, byte[] conteudo) {
		this.nomeArquivo = nomeArquivo;
		this.tipoMime = tipoMime;
		this.conteudo = conteudo;
	}

	public String getNomeArquivo() { return nomeArquivo; }
	public String getTipoMime() { return tipoMime; }
	public byte[] getConteudo() { return conteudo; }
}
