package io.github.Leandro208.projetoESIG.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * Sanitiza o HTML gerado pelo editor rico antes de grava-lo no banco.
 * Mantem apenas as formatacoes suportadas (negrito, italico, listas,
 * titulos, links etc.) e remove tags/atributos que permitem XSS.
 */
public final class HtmlSanitizer {

	private static final Safelist SAFELIST = criarSafelist();

	private HtmlSanitizer() {
	}

	private static Safelist criarSafelist() {
		return Safelist.relaxed()
				.addTags("s", "hr", "font")
				.addAttributes("font", "face", "size", "color")
				.addAttributes("p", "align")
				.addAttributes("div", "align")
				.addAttributes("h1", "align")
				.addAttributes("h2", "align")
				.addAttributes("h3", "align")
				.addAttributes("h4", "align")
				.addAttributes("h5", "align")
				.addAttributes("h6", "align")
				.removeTags("img", "table", "caption", "col", "colgroup",
						"tbody", "td", "tfoot", "th", "thead", "tr");
	}

	public static String sanitizar(String html) {
		if (html == null) {
			return "";
		}
		return Jsoup.clean(html, SAFELIST);
	}

	public static boolean vazio(String html) {
		if (html == null) {
			return true;
		}
		// &nbsp; nao e removido por String.trim() e nao deve contar como conteudo.
		return Jsoup.parse(html).text().replace('\u00a0', ' ').trim().isEmpty();
	}
}
