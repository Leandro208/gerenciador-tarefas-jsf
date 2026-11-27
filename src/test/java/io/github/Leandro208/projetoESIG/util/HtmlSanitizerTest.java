package io.github.Leandro208.projetoESIG.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HtmlSanitizerTest {

	@Test
	public void removeScript() {
		String resultado = HtmlSanitizer.sanitizar("<script>alert(1)</script><p>ok</p>");
		assertEquals("<p>ok</p>", resultado);
	}

	@Test
	public void removeAtributoDeEvento() {
		String resultado = HtmlSanitizer.sanitizar("<p onclick=\"alert(1)\">a</p>");
		assertEquals("<p>a</p>", resultado);
	}

	@Test
	public void removeHrefJavascript() {
		String resultado = HtmlSanitizer.sanitizar("<a href=\"javascript:alert(1)\">a</a>");
		assertEquals("<a>a</a>", resultado);
	}

	@Test
	public void removeImagem() {
		String resultado = HtmlSanitizer.sanitizar("<img src=x onerror=alert(1)>");
		assertTrue(resultado.isEmpty());
	}

	@Test
	public void mantemFormatacaoPermitida() {
		String html = "<p><strong>negrito</strong> <em>italico</em> <u>sublinhado</u> <s>riscado</s></p>";
		assertEquals(html, HtmlSanitizer.sanitizar(html));
	}

	@Test
	public void mantemListas() {
		String resultado = HtmlSanitizer.sanitizar("<ul><li>um</li><li>dois</li></ul>");
		assertTrue(resultado.contains("<ul>"));
		assertTrue(resultado.contains("<li>um</li>"));
		assertTrue(resultado.contains("<li>dois</li>"));
	}

	@Test
	public void conteudoVazioDoEditorEConsideradoVazio() {
		assertTrue(HtmlSanitizer.vazio("<p><br></p>"));
		assertTrue(HtmlSanitizer.vazio("<p>&nbsp;</p>"));
		assertTrue(HtmlSanitizer.vazio("   "));
		assertTrue(HtmlSanitizer.vazio(null));
	}

	@Test
	public void textoPreenchidoNaoEConsideradoVazio() {
		assertFalse(HtmlSanitizer.vazio("<p>conteudo</p>"));
		assertFalse(HtmlSanitizer.vazio("<ul><li>item</li></ul>"));
	}
}
