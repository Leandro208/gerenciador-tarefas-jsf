package io.github.Leandro208.projetoESIG.services;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipFile;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.TarefaAnexoDAO;
import io.github.Leandro208.projetoESIG.dto.TarefaAnexoArquivoDTO;
import io.github.Leandro208.projetoESIG.dto.TarefaAnexoDTO;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.util.AccessControl;

public class TarefaAnexoService implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final long TAMANHO_MAXIMO = 10L * 1024L * 1024L;
	private static final String MIME_PDF = "application/pdf";
	private static final String MIME_JPEG = "image/jpeg";
	private static final String MIME_PNG = "image/png";
	private static final String MIME_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	private static final String MIME_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	public List<TarefaAnexoDTO> listar(Long idTarefa, UsuarioDTO usuarioSessao) {
		UsuarioDTO usuario = AccessControl.requireUser(usuarioSessao);
		if (new TarefaService().buscarPorIdPermitido(idTarefa, usuario) == null) {
			throw new SecurityException("Acesso não autorizado à tarefa.");
		}
		TarefaAnexoDAO dao = new TarefaAnexoDAO();
		try {
			return dao.listar(idTarefa);
		} finally {
			dao.close();
		}
	}

	public void adicionar(Long idTarefa, UsuarioDTO usuarioSessao, String nomeOriginal, byte[] conteudo)
			throws DAOException {
		UsuarioDTO usuario = AccessControl.requireUser(usuarioSessao);
		if (idTarefa == null) throw new IllegalArgumentException("Selecione uma tarefa válida.");
		if (conteudo == null || conteudo.length == 0) throw new IllegalArgumentException("Selecione um arquivo.");
		if (conteudo.length > TAMANHO_MAXIMO) throw new IllegalArgumentException("O arquivo deve ter no máximo 10 MB.");
		String nome = normalizarNome(nomeOriginal);
		String mime = validarConteudo(nome, conteudo);
		TarefaAnexoDAO dao = new TarefaAnexoDAO();
		try {
			dao.salvar(idTarefa, usuario, nome, mime, conteudo);
		} finally {
			dao.close();
		}
	}

	public TarefaAnexoArquivoDTO buscarArquivoPermitido(Long idAnexo, UsuarioDTO usuarioSessao) {
		UsuarioDTO usuario = AccessControl.requireUser(usuarioSessao);
		if (idAnexo == null) return null;
		TarefaAnexoDAO dao = new TarefaAnexoDAO();
		try {
			return dao.buscarArquivoPermitido(idAnexo, usuario);
		} finally {
			dao.close();
		}
	}

	public void excluir(Long idAnexo, UsuarioDTO usuarioSessao) throws DAOException {
		UsuarioDTO usuario = AccessControl.requireUser(usuarioSessao);
		if (idAnexo == null) throw new IllegalArgumentException("Selecione um anexo válido.");
		TarefaAnexoDAO dao = new TarefaAnexoDAO();
		try {
			dao.excluir(idAnexo, usuario);
		} finally {
			dao.close();
		}
	}

	private String normalizarNome(String nomeOriginal) {
		if (nomeOriginal == null) throw new IllegalArgumentException("O arquivo não possui nome válido.");
		String nome = nomeOriginal.replace('\\', '/');
		nome = nome.substring(nome.lastIndexOf('/') + 1).replaceAll("[\\p{Cntrl}]", "").trim();
		if (nome.isEmpty() || nome.length() > 255) {
			throw new IllegalArgumentException("O nome do arquivo deve ter entre 1 e 255 caracteres.");
		}
		return nome;
	}

	private String validarConteudo(String nome, byte[] conteudo) {
		String lower = nome.toLowerCase(Locale.ROOT);
		if (lower.endsWith(".pdf") && iniciaCom(conteudo, new byte[] {'%', 'P', 'D', 'F', '-'})) return MIME_PDF;
		if ((lower.endsWith(".jpg") || lower.endsWith(".jpeg"))
				&& iniciaCom(conteudo, new byte[] {(byte) 0xff, (byte) 0xd8, (byte) 0xff})
				&& terminaCom(conteudo, new byte[] {(byte) 0xff, (byte) 0xd9})) return MIME_JPEG;
		if (lower.endsWith(".png") && iniciaCom(conteudo,
				new byte[] {(byte) 0x89, 'P', 'N', 'G', 13, 10, 26, 10})) return MIME_PNG;
		if (lower.endsWith(".docx") && validarOfficeZip(conteudo, "word/document.xml")) return MIME_DOCX;
		if (lower.endsWith(".xlsx") && validarOfficeZip(conteudo, "xl/workbook.xml")) return MIME_XLSX;
		throw new IllegalArgumentException("Formato inválido. Envie PDF, JPG, PNG, DOCX ou XLSX válido.");
	}

	private boolean iniciaCom(byte[] conteudo, byte[] assinatura) {
		if (conteudo.length < assinatura.length) return false;
		for (int i = 0; i < assinatura.length; i++) if (conteudo[i] != assinatura[i]) return false;
		return true;
	}

	private boolean terminaCom(byte[] conteudo, byte[] assinatura) {
		if (conteudo.length < assinatura.length) return false;
		int inicio = conteudo.length - assinatura.length;
		for (int i = 0; i < assinatura.length; i++) if (conteudo[inicio + i] != assinatura[i]) return false;
		return true;
	}

	private boolean validarOfficeZip(byte[] conteudo, String entradaObrigatoria) {
		if (!iniciaCom(conteudo, new byte[] {'P', 'K', 3, 4})) return false;
		Path temporario = null;
		try {
			temporario = Files.createTempFile("tarefa-anexo-", ".zip");
			Files.write(temporario, conteudo);
			try (ZipFile zip = new ZipFile(temporario.toFile())) {
				return zip.getEntry("[Content_Types].xml") != null && zip.getEntry(entradaObrigatoria) != null;
			}
		} catch (IOException e) {
			return false;
		} finally {
			if (temporario != null) {
				try { Files.deleteIfExists(temporario); } catch (IOException ignored) { }
			}
		}
	}
}
