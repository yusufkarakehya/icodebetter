/*
 * @author Muhammed Kurt <muhammedkurt@outlook.com>
 * Sanitizes user input.
 * Can be used to prevent XSS.
 * For now this class is not fully capable to prevent all XSS attacks.
 * More info can be found at:
 * https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet
 * 
 * Adopted from these links:
 * http://www.iamcal.com/publish/articles/php/processing_html/
 * http://www.iamcal.com/publish/articles/php/processing_html_part_2/
 * https://github.com/mayconbordin/strutstool/blob/master/standardProject/src/java/com/framework/util/filter/HTMLInputFilter.java
 */

package iwb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlFilter {
	private static final Pattern commentPattern = Pattern.compile("<!--(.*?)-->", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern tagPattern = Pattern.compile("<(.*?)>", Pattern.DOTALL);
	private static final Pattern tagEndingPattern = Pattern.compile("^/([a-z0-9]+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern tagStartingPattern = Pattern.compile("^([a-z0-9]+)(.*?)(/?)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern tagParamPattern = Pattern.compile("([a-z0-9]+)\\s?=\\s?([\"'])(.*?)\\2", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern tagParamPattern2 = Pattern.compile("([a-z0-9]+)\\s?(=)\\s?([^\"\\s']+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern protocolPattern = Pattern.compile("^([^:]+):", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final String[] defaultAllowedTags = new String[]{"a", "abbr", "acronym", "address", "area", "b",
        															"big", "blockquote", "br", "button", "caption", "center", "cite",
															        "code", "col", "colgroup", "dd", "del", "dfn", "dir", "div", "dl", "dt",
															        "em", "fieldset", "font", "form", "h1", "h2", "h3", "h4", "h5", "h6",
															        "hr", "i", "img", "input", "ins", "kbd", "label", "legend", "li", "map",
															        "menu", "ol", "optgroup", "option", "p", "pre", "q", "s", "samp",
															        "select", "small", "span", "strike", "strong", "style", "sub", "sup", "table",
															        "tbody", "td", "textarea", "tfoot", "th", "thead", "tr", "tt", "u",
															        "ul", "var",
															        "section", "nav", "article", "aside", "header", "footer", "main",
															        "figure", "figcaption",
															        "data", "time", "mark", "ruby", "rt", "rp", "bdi", "wbr",
															        "datalist", "keygen", "output", "progress", "meter",
															        "details", "summary", "menuitem"};
	
	private static final String[] defaultAllowedAttributes = new String[]{"abbr", "accept", "accept-charset", "accesskey",
																	      "action", "align", "alt", "axis", "bgcolor", "border", "cellpadding",
																	      "cellspacing", "char", "charoff", "charset", "checked", "cite", "class",
																	      "clear", "cols", "colspan", "color", "compact", "coords", "datetime",
																	      "dir", "disabled", "enctype", "for", "frame", "headers", "height",
																	      "href", "hreflang", "hspace", "id", "ismap", "label", "lang",
																	      "longdesc", "maxlength", "media", "method", "multiple", "name",
																	      "nohref", "noshade", "nowrap", "prompt", "readonly", "rel", "rev",
																	      "rows", "rowspan", "rules", "scope", "selected", "shape", "size",
																	      "span", "src", "start", "style", "summary", "tabindex", "target", "title",
																	      "type", "usemap", "valign", "value", "vspace", "width",
																	      "high", "keytype", "list", "low", "max", "min", "novalidate", "open",
																	      "optimum", "pattern", "placeholder", "pubdate", "radiogroup",
																	      "required", "reversed", "spellcheck", "step", "wrap", "challenge", "contenteditable",
																	      "draggable", "dropzone", "autocomplete", "autosave"};
	
	private static final String[] defaultAllowedProtocols = new String[]{"http", "https", "mailto", "cid"};
	private static final String[] defaultAllowedProtocolAttributes = new String[]{"href", "src"};
	
	private Set<String> allowedTags;
	private Set<String> allowedAttributes;
	private Set<String> allowedProtocols;
	private Set<String> allowedProtocolAttributes;
	
	public HtmlFilter() {
		this(null, null, null, null);
	}
	
	public HtmlFilter(String[] allowedTags, String[] allowedAttributes, String[] allowedProtocols, String[] allowedProtocolAttributes) {
		this.allowedTags = new HashSet<String>(Arrays.asList(allowedTags == null ? defaultAllowedTags : allowedTags));
		this.allowedAttributes = new HashSet<String>(Arrays.asList(allowedAttributes == null ? defaultAllowedAttributes : allowedAttributes));
		this.allowedProtocols = new HashSet<String>(Arrays.asList(allowedProtocols == null ? defaultAllowedProtocols : allowedProtocols));
		this.allowedProtocolAttributes = new HashSet<String>(Arrays.asList(allowedProtocolAttributes == null ? defaultAllowedProtocolAttributes : allowedProtocolAttributes));
	}
	
	public String filter(String input) {
		input = escapeComments(input);
		return filterImpl(input);
	}
	
	private String filterImpl(String input) {
		Matcher matcher = tagPattern.matcher(input);
		StringBuffer strBuffer = new StringBuffer();
		while (matcher.find()){
			String tag = matcher.group(1);
			tag = processTag(tag);
			matcher.appendReplacement(strBuffer, Matcher.quoteReplacement(tag)); //be sure to escape any $ signs with literal $ signs so that they are not evaluated are regular expression groups
		}
		matcher.appendTail(strBuffer);
		return strBuffer.toString();
	}
	
	private String escapeComments(String input) {
		Matcher matcher = commentPattern.matcher(input);
		StringBuffer strBuffer = new StringBuffer();
		while (matcher.find()) {
			String match = matcher.group(1);
			matcher.appendReplacement(strBuffer, "<!--" + Matcher.quoteReplacement(htmlSpecialChars(match)) + "-->"); //be sure to escape any $ signs with literal $ signs so that they are not evaluated are regular expression groups
		}
		matcher.appendTail(strBuffer);
		return strBuffer.toString();
	}
	
	private String htmlSpecialChars(String s)
	{
		s = s.replaceAll("&", "&amp;");
		s = s.replaceAll("\"", "&quot;");
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");
		return s;
	}
	
	private String processTag(String input) {
		Matcher matcher = tagEndingPattern.matcher(input);
		if (matcher.find()) {
			String tagName = matcher.group(1).toLowerCase();
			if (allowedTags.contains(tagName)) {
				return "</" + tagName + ">";
			} else {
				return "";
			}
		}
		
		matcher = tagStartingPattern.matcher(input);
		if (matcher.find()) {
			String tagName = matcher.group(1);
			String tagBody = matcher.group(2);
			String tagEnding = matcher.group(3);
			if (allowedTags.contains(tagName)) {
				String tagParams = "";
				Matcher paramMatcher1 = tagParamPattern.matcher(tagBody);
				Matcher paramMatcher2 = tagParamPattern2.matcher(tagBody);
				
				List<String> paramNames = new ArrayList<String>();
				List<String> paramValues = new ArrayList<String>();
				List<Character> paramQuotes = new ArrayList<Character>();
				while (paramMatcher1.find()) {
					paramNames.add(paramMatcher1.group(1));
					paramQuotes.add(paramMatcher1.group(2).charAt(0));
					paramValues.add(paramMatcher1.group(3));
				}
				while (paramMatcher2.find()) {
					paramNames.add(paramMatcher2.group(1));
					paramQuotes.add(null);
					paramValues.add(paramMatcher2.group(3));
				}
				
				String paramName, paramQuote, paramValue;
				for	(int i = 0; i < paramNames.size(); i++) {
					paramName = paramNames.get(i);
					paramQuote = paramQuotes.get(i) != null ? paramQuotes.get(i).toString() : null;
					paramValue = paramValues.get(i);
					if (allowedAttributes.contains(paramName)) {
						if (allowedProtocolAttributes.contains(paramName)) {
							paramValue = processProtocolParam(paramValue);
						}
						tagParams += " " + paramName + (paramQuote == null ? "=\"" : ("=" + paramQuote)) + paramValue + (paramQuote == null ? "\"" : paramQuote);
					}
				}
				return "<" + tagName + tagParams + tagEnding + ">";
			} else {
				return "";
			}
		}
		
		return "";
	}
	
	private String processProtocolParam(String value) {
		Matcher matcher = protocolPattern.matcher(value);
		if (matcher.find()) {
			String protocol = matcher.group(1);
			if (!allowedProtocols.contains(protocol)) {
				value = "#" + value.substring(protocol.length() + 1, value.length());
				if (value.startsWith("#//")) 
					value = "#" + value.substring(3, value.length());
			}
		}
		return value;
	}
}