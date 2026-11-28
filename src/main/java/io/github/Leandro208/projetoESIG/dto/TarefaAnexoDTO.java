package io.github.Leandro208.projetoESIG.dto;

import java.io.Serializable;
import java.util.Date;

public class TarefaAnexoDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Long id;
	private final String nomeArquivo;
	private final String tipoMime;
	private final long tamanho;
	private final Date dataUpload;
	private final Long autorUsuarioId;

	public TarefaAnexoDTO(Long id, String nomeArquivo, String tipoMime, long tamanho,
			Date dataUpload, Long autorUsuarioId) {
		this.id = id;
		this.nomeArquivo = nomeArquivo;
		this.tipoMime = tipoMime;
		this.tamanho = tamanho;
		this.dataUpload = dataUpload;
		this.autorUsuarioId = autorUsuarioId;
	}

	public Long getId() { return id; }
	public String getNomeArquivo() { return nomeArquivo; }
	public String getTipoMime() { return tipoMime; }
	public long getTamanho() { return tamanho; }
	public Date getDataUpload() { return dataUpload; }
	public Long getAutorUsuarioId() { return autorUsuarioId; }
	public boolean isImagem() { return "image/jpeg".equals(tipoMime) || "image/png".equals(tipoMime); }
	public String getTamanhoLegivel() {
		if (tamanho < 1024L) return tamanho + " B";
		if (tamanho < 1024L * 1024L) return String.format("%.1f KB", tamanho / 1024.0);
		return String.format("%.1f MB", tamanho / (1024.0 * 1024.0));
	}
}
