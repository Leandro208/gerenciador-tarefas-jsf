package io.github.Leandro208.projetoESIG.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DescricaoTarefaSanitizerTest {
	@Test
	public void preservaFormatacaoInlineSeguraDoEditor() {
		String html = "<p style=\"text-align:center\"><span style=\"color: rgb(255, 0, 0); "
				+ "background-color: rgb(255, 255, 0); font-size:18px; font-family:serif\">Texto</span></p>";
		String resultado = DescricaoTarefaSanitizer.sanitizar(html);

		assertTrue(resultado.contains("color:rgb(255, 0, 0)"));
		assertTrue(resultado.contains("background-color:rgb(255, 255, 0)"));
		assertTrue(resultado.contains("font-size:18px"));
		assertTrue(resultado.contains("font-family:serif"));
		assertTrue(resultado.contains("text-align:center"));
	}

	@Test
	public void converteClassesDeFormatacaoDoQuill() {
		String resultado = DescricaoTarefaSanitizer.sanitizar(
				"<p class=\"ql-align-right ql-size-large ql-font-serif ql-indent-2\">Texto</p>");

		assertTrue(resultado.contains("text-align:right"));
		assertTrue(resultado.contains("font-size:large"));
		assertTrue(resultado.contains("font-family:serif"));
		assertTrue(resultado.contains("padding-left:6em"));
	}

	@Test
	public void preservaNegritoDoEditor() {
		String resultado = DescricaoTarefaSanitizer.sanitizar(
				"<p><strong>negrito</strong> "
				+ "<span style=\"font-weight: bold; font-style: italic; text-decoration: underline; "
				+ "font-family: 'Arial Black'; font-size: 16px\">também em negrito</span></p>");

		assertTrue(resultado.contains("<strong>negrito</strong>"));
		assertTrue(resultado.contains("font-weight:bold"));
		assertTrue(resultado.contains("font-style:italic"));
		assertTrue(resultado.contains("text-decoration:underline"));
		assertTrue(resultado.contains("font-family:arial black"));
		assertTrue(resultado.contains("font-size:16px"));
	}

	@Test
	public void removeHtmlAtivoEAtributosCssNaoPermitidos() {
		String resultado = DescricaoTarefaSanitizer.sanitizar(
				"<script>alert(1)</script><p onclick=\"alert(1)\" "
				+ "style=\"color:red;background-image:url(javascript:alert(1));position:fixed\">Seguro</p>");

		assertFalse(resultado.contains("<script"));
		assertFalse(resultado.contains("onclick"));
		assertFalse(resultado.contains("background-image"));
		assertFalse(resultado.contains("position:fixed"));
		assertTrue(resultado.contains("color:red"));
	}
}
