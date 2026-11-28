package io.github.Leandro208.projetoESIG.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

/** Keeps the safe inline formats emitted by the task description editor. */
public final class DescricaoTarefaSanitizer {
	private static final Pattern COLOR = Pattern.compile(
			"(?i)(#[0-9a-f]{3,4}|#[0-9a-f]{6}|#[0-9a-f]{8}|(?:rgb|rgba|hsl|hsla)\\([0-9.% ,+-]+\\)|[a-z]{1,20})");
	private static final Pattern SIZE = Pattern.compile("(?i)(?:[0-9]{1,3}(?:\\.[0-9]+)?(?:px|pt|em|rem|%)|xx-small|x-small|small|medium|large|x-large|xx-large|xxx-large|smaller|larger)");
	private static final Pattern FONT_SIZE_LEGACY = Pattern.compile("[1-7]");
	private static final Pattern INDENT = Pattern.compile("ql-indent-([1-8])");
	private static final Pattern INDENT_STYLE = Pattern.compile("(?:0|[1-9][0-9]{0,2})(?:px|em|rem)");
	private static final String[] FONT_FAMILIES = {
			"arial", "arial black", "comic sans ms", "courier new", "narrow", "garamond",
			"georgia", "impact", "sans serif", "serif", "tahoma", "trebuchet ms", "verdana",
			"monospace", "sans-serif", "cursive", "fantasy", "system-ui"
	};
	private static final Safelist SAFELIST = criarSafelist();

	private DescricaoTarefaSanitizer() { }

	public static String sanitizar(String html) {
		if (html == null) return "";
		Document documento = Jsoup.parseBodyFragment(html);
		for (Element elemento : documento.body().getAllElements()) {
			List<String> estilos = new ArrayList<>();
			String estiloExistente = estiloSeguro(elemento.attr("style"));
			if (!estiloExistente.isEmpty()) estilos.add(estiloExistente);

			String classes = elemento.className();
			if (classes.contains("ql-align-center")) estilos.add("text-align:center");
			else if (classes.contains("ql-align-right")) estilos.add("text-align:right");
			else if (classes.contains("ql-align-justify")) estilos.add("text-align:justify");
			if (classes.contains("ql-direction-rtl")) estilos.add("direction:rtl");
			for (String classe : classes.split("\\s+")) {
				Matcher indent = INDENT.matcher(classe);
				if (indent.matches()) estilos.add("padding-left:" + (Integer.parseInt(indent.group(1)) * 3) + "em");
			}
			if (classes.contains("ql-size-small")) estilos.add("font-size:small");
			else if (classes.contains("ql-size-large")) estilos.add("font-size:large");
			else if (classes.contains("ql-size-huge")) estilos.add("font-size:xx-large");
			if (classes.contains("ql-font-serif")) estilos.add("font-family:serif");
			else if (classes.contains("ql-font-monospace")) estilos.add("font-family:monospace");
			else if (classes.contains("ql-font-sans-serif")) estilos.add("font-family:sans-serif");

			elemento.removeAttr("class");
			if (estilos.isEmpty()) elemento.removeAttr("style");
			else elemento.attr("style", String.join(";", estilos));
		}
		return Jsoup.clean(documento.body().html(), SAFELIST);
	}

	private static String estiloSeguro(String estilo) {
		List<String> seguros = new ArrayList<>();
		for (String declaracao : estilo.split(";")) {
			int separador = declaracao.indexOf(':');
			if (separador < 1) continue;
			String propriedade = declaracao.substring(0, separador).trim().toLowerCase(Locale.ROOT);
			String valor = declaracao.substring(separador + 1).trim();
			if (propriedade.equals("color") || propriedade.equals("background-color")) {
				if (COLOR.matcher(valor).matches()) seguros.add(propriedade + ":" + valor);
			} else if (propriedade.equals("font-size")) {
				if (SIZE.matcher(valor).matches()) seguros.add(propriedade + ":" + valor);
			} else if (propriedade.equals("font-weight")) {
				String peso = valor.toLowerCase(Locale.ROOT);
				if (peso.equals("normal") || peso.equals("bold") || peso.equals("bolder")
						|| peso.matches("[1-9]00")) seguros.add(propriedade + ":" + peso);
			} else if (propriedade.equals("font-style")) {
				String estiloFonte = valor.toLowerCase(Locale.ROOT);
				if (estiloFonte.equals("normal") || estiloFonte.equals("italic") || estiloFonte.equals("oblique")) {
					seguros.add(propriedade + ":" + estiloFonte);
				}
			} else if (propriedade.equals("font-family")) {
				String familia = familiaSegura(valor);
				if (familia != null) seguros.add(propriedade + ":" + familia);
			} else if (propriedade.equals("text-decoration") || propriedade.equals("text-decoration-line")) {
				String decoracao = decoracaoSegura(valor);
				if (decoracao != null) seguros.add("text-decoration:" + decoracao);
			} else if (propriedade.equals("vertical-align")) {
				String alinhamentoVertical = valor.toLowerCase(Locale.ROOT);
				if (alinhamentoVertical.matches("baseline|sub|super|top|text-top|middle|bottom|text-bottom")) {
					seguros.add(propriedade + ":" + alinhamentoVertical);
				}
			} else if (propriedade.equals("text-align")) {
				String alinhamento = valor.toLowerCase(Locale.ROOT);
				if (alinhamento.equals("left") || alinhamento.equals("center")
						|| alinhamento.equals("right") || alinhamento.equals("justify")) {
					seguros.add(propriedade + ":" + alinhamento);
				}
			} else if (propriedade.equals("direction")) {
				String direcao = valor.toLowerCase(Locale.ROOT);
				if (direcao.equals("ltr") || direcao.equals("rtl")) seguros.add(propriedade + ":" + direcao);
			} else if (propriedade.equals("padding-left")) {
				if (Pattern.matches("(?:[3-9]|1[0-9]|2[0-4])em", valor)) seguros.add(propriedade + ":" + valor);
			} else if (propriedade.equals("margin-left")) {
				if (INDENT_STYLE.matcher(valor).matches()) seguros.add(propriedade + ":" + valor);
			}
		}
		return String.join(";", seguros);
	}

	private static String familiaSegura(String valor) {
		String[] familias = valor.toLowerCase(Locale.ROOT).replace("'", "").replace("\"", "").split(",");
		List<String> aceitas = new ArrayList<>();
		for (String familia : familias) {
			String normalizada = familia.trim().replaceAll("\\s+", " ");
			boolean permitida = false;
			for (String fonte : FONT_FAMILIES) {
				if (fonte.equals(normalizada)) {
					permitida = true;
					break;
				}
			}
			if (!permitida) return null;
			aceitas.add(normalizada);
		}
		return aceitas.isEmpty() ? null : String.join(",", aceitas);
	}

	private static String decoracaoSegura(String valor) {
		String[] decoracoes = valor.toLowerCase(Locale.ROOT).trim().split("\\s+");
		List<String> aceitas = new ArrayList<>();
		for (String decoracao : decoracoes) {
			if (decoracao.equals("underline") || decoracao.equals("line-through")
					|| decoracao.equals("overline") || decoracao.equals("none")) {
				aceitas.add(decoracao);
			} else return null;
		}
		return aceitas.isEmpty() ? null : String.join(" ", aceitas);
	}

	private static Safelist criarSafelist() {
		Safelist safelist = Safelist.relaxed()
				.addTags("s", "hr", "font", "pre", "code")
				.addAttributes("font", "face", "size", "color", "style")
				.addAttributes("a", "style")
				.addAttributes("strong", "style")
				.addAttributes("b", "style")
				.addAttributes("em", "style")
				.addAttributes("i", "style")
				.addAttributes("u", "style")
				.addAttributes("s", "style")
				.addAttributes("sub", "style")
				.addAttributes("sup", "style")
				.addAttributes("font", "face", "size", "color", "style")
				.addAttributes("p", "style", "align")
				.addAttributes("div", "style", "align")
				.addAttributes("span", "style")
				.addAttributes("li", "style")
				.addAttributes("ol", "style")
				.addAttributes("ul", "style")
				.addAttributes("blockquote", "style")
				.addAttributes("pre", "style")
				.addAttributes("h1", "style", "align")
				.addAttributes("h2", "style", "align")
				.addAttributes("h3", "style", "align")
				.addAttributes("h4", "style", "align")
				.addAttributes("h5", "style", "align")
				.addAttributes("h6", "style", "align");
		return safelist;
	}
}
