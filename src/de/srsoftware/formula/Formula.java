package de.srsoftware.formula;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Vector;

public class Formula { // ------------------
	

	
	private String code;
	private BufferedImage image;
	private FormulaFont lastFont;
	
	/***************************** Test *****************************/

	public static void main(String[] args) {
		String code="Dies ist ein {fett \\it{krasser}} \\Alpha -Test! Mach \\underline{das} mal nach!";
		System.out.println(code);
		Formula f = new Formula(code);
		System.out.println(f.getText());
	}

	/***************************** HTML generation *****************************/
	
	/**
	 * this method is used to export the current formula to html
	 * @return html code of this formula
	 */
	public String getHtmCode() {
		String result = connectBrackets(code);
		result = replaceSpecialSigns(result);
		result = replaceBoldSymbols(result);
		result = replaceTypeSymbols(result);
		result = replaceItalicSymbols(result);
		result = replaceRGBTags(result);
		result = replaceColorTags(result);
		result = replaceSuperscriptSymbols(result);
		result = replaceSubscriptSymbols(result);
		result = replaceUnderlinedSymbols(result);
		result = replaceSymbolsWithArrow(result);
		result = replaceCases(result);
		result = replaceRootedSymbols(result);
		result = replaceFracs(result);
		result = StrReplace(result, "~CASES~", "<td style=\"border-left-style: solid; border-left-width: 2px\">");
		return result;
	}
	/**
	 * This method adds closing brackets to the code, if they are missing
	 * @param inp the chunk of code to be healed
	 * @return the chunk of code with appended brackets
	 */
	private static String connectBrackets(String inp) {
		String result = new String(inp);
		int l = result.length();
		int count = 0;
		for (int i = 1; i < l; i++) {
			switch (result.charAt(i)) {
			case '{': {
				count++;
				break;
			}
			case '}': {
				count--;
				break;
			}
			}
		}
		while (count > 0) {
			count--;
			result = result + '}';
		}
		return result;
	}
	private static String replaceSpecialSigns(String inp) {
		inp = StrReplace(inp, "<", "&lt;");
		inp = StrReplace(inp, ">", "&gt;");
		inp = StrReplace(inp, "##", "<br>");
		inp = StrReplace(inp, "\\* ", "&middot;");
		inp = StrReplace(inp, "\\n ", "<br>");
		inp = StrReplace(inp, "\\(+) ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\(C) ", "�");
		inp = StrReplace(inp, "\\(R) ", "�");
		inp = StrReplace(inp, "\\(x) ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\(X) ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\&lt;= ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\&lt;=&gt; ", "<font face=symbol></font>");
		inp = StrReplace(inp, "\\=&gt; ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\&lt;- ", "&larr;");
		inp = StrReplace(inp, "\\&lt;-&gt; ", "&harr;");
		inp = StrReplace(inp, "\\-&gt; ", "&rarr;");
		inp = StrReplace(inp, "\\=pi", "3.141592");
		inp = StrReplace(inp, "\\alpha ", "&alpha;");
		inp = StrReplace(inp, "\\Alpha ", "&Alpha;");
		inp = StrReplace(inp, "\\angstr ", "&Aring;");
		inp = StrReplace(inp, "\\beta ", "&beta;");
		inp = StrReplace(inp, "\\Beta ", "&Beta;");
		inp = StrReplace(inp, "\\bomb ", "<font face=\"Wingdings\">M</font>");
		inp = StrReplace(inp, "\\book ", "<font face=\"Wingdings\">&</font>");
		inp = StrReplace(inp, "\\bool", "<i>IB</i>");
		inp = StrReplace(inp, "\\box ", "<font face=\"Wingdings 2\">�</font>");
		inp = StrReplace(inp, "\\cap ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\Cap ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\cdot ", "&middot;");
		inp = StrReplace(inp, "\\chi ", "&chi;");
		inp = StrReplace(inp, "\\Chi ", "&Chi;");
		// inp=StrReplace(inp,"\\complex ","<font face=symbol></font>");
		inp = StrReplace(inp, "\\corr ", "&<font face=symbol>@</font>");
		inp = StrReplace(inp, "\\cup ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\Cup ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\d ", "&part;");
		inp = StrReplace(inp, "\\delta ", "&delta;");
		inp = StrReplace(inp, "\\Delta ", "&delta;");
		inp = StrReplace(inp, "\\disk ", "<font face=\"Wingdings\"><</font>");
		inp = StrReplace(inp, "\\emptyset ", "&Oslash;");
		inp = StrReplace(inp, "\\epsilon ", "&epsilon;");
		inp = StrReplace(inp, "\\Epsilon ", "&Epsilon;");
		inp = StrReplace(inp, "\\eq ", "&equiv;");
		inp = StrReplace(inp, "\\eta ", "&eta;");
		inp = StrReplace(inp, "\\Eta ", "&Eta;");
		inp = StrReplace(inp, "\\exists ", "<font face=symbol>$</font>");
		inp = StrReplace(inp, "\\eye", "<font face=\"Webdings\">N</font>");
		inp = StrReplace(inp, "\\forall ", "<font face=symbol>\"</font>");
		inp = StrReplace(inp, "\\gamma ", "&gamma;");
		inp = StrReplace(inp, "\\Gamma ", "&Gamma;");
		inp = StrReplace(inp, "\\geq ", "&ge;");
		inp = StrReplace(inp, "\\in ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\infty ", "&infin;");
		inp = StrReplace(inp, "\\info ", "<font face=\"Webdings\">i</font>");
		// inp=StrReplace(inp,"\\int ","<font face=symbol></font>");
		inp = StrReplace(inp, "\\iota ", "&iota;");
		inp = StrReplace(inp, "\\Iota ", "&Iota;");
		inp = StrReplace(inp, "\\kappa ", "&kappa;");
		inp = StrReplace(inp, "\\Kappa ", "&Kappa;");
		inp = StrReplace(inp, "\\lambda ", "&lambda;");
		inp = StrReplace(inp, "\\Lambda ", "&Lambda;");
		inp = StrReplace(inp, "\\leftarrow ", "&larr;");
		inp = StrReplace(inp, "\\Leftarrow ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\leftrightarrow ", "&harr;");
		inp = StrReplace(inp, "\\Leftrightarrow ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\leq ", "&le;");
		inp = StrReplace(inp, "\\mu ", "&mu;");
		inp = StrReplace(inp, "\\Mu ", "&Mu;");
		inp = StrReplace(inp, "\\my ", "&mu;");
		inp = StrReplace(inp, "\\My ", "&Mu;");
		// inp=StrReplace(inp,"\\natural ","<font face=symbol></font>");
		inp = StrReplace(inp, "\\nabla ", "&nabla;");
		inp = StrReplace(inp, "\\neq ", "&ne;");
		inp = StrReplace(inp, "\\nok ", "<font face=\"Wingdings 2\">O</font>");
		inp = StrReplace(inp, "\\nokbox ", "<font face=\"Wingdings 2\">Q</font>");
		inp = StrReplace(inp, "\\not ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\notin ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\nu ", "&nu;");
		inp = StrReplace(inp, "\\ny ", "&nu;");
		inp = StrReplace(inp, "\\Nu ", "&Nu;");
		inp = StrReplace(inp, "\\Ny ", "&Nu;");
		inp = StrReplace(inp, "\\ok ", "<font face=\"Wingdings 2\">P</font>");
		inp = StrReplace(inp, "\\okbox ", "<font face=\"Wingdings 2\">R</font>");
		inp = StrReplace(inp, "\\omega ", "&omega;");
		inp = StrReplace(inp, "\\Omega ", "&Omega;");
		inp = StrReplace(inp, "\\omicron ", "&omicron;");
		inp = StrReplace(inp, "\\omikron ", "&omicron;");
		inp = StrReplace(inp, "\\Omicron ", "&omikron;");
		inp = StrReplace(inp, "\\Omikron ", "&omikron;");
		inp = StrReplace(inp, "\\partial ", "&part;");
		inp = StrReplace(inp, "\\pen ", "<font face=\"Wingdings\">!</font>");
		inp = StrReplace(inp, "\\phi ", "&phi;");
		inp = StrReplace(inp, "\\Phi ", "&Phi;");
		inp = StrReplace(inp, "\\phone ", "<font face=\"Wingdings\">)</font>");
		inp = StrReplace(inp, "\\pi ", "&pi;");
		inp = StrReplace(inp, "\\Pi ", "&Pi;");
		inp = StrReplace(inp, "\\pm ", "&plusmn;");
		inp = StrReplace(inp, "\\psi ", "&psi;");
		inp = StrReplace(inp, "\\Psi ", "&Psi;");
		inp = StrReplace(inp, "\\real ", "&<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\rho ", "&rho;");
		inp = StrReplace(inp, "\\Rho ", "&Rho;");
		inp = StrReplace(inp, "\\rightarrow ", "&#rarr;");
		inp = StrReplace(inp, "\\Rightarrow ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\round ", "&asymp;");
		inp = StrReplace(inp, "\\sigma ", "&sigma;");
		inp = StrReplace(inp, "\\Sigma ", "&Sigma;");
		inp = StrReplace(inp, "\\smile ", "<font face=\"Wingdings\">J</font>");
		inp = StrReplace(inp, "\\subset ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\subseteq ", "<font face=symbol>�</font>");
		inp = StrReplace(inp, "\\tau ", "&tau;");
		inp = StrReplace(inp, "\\Tau ", "&Tau;");
		inp = StrReplace(inp, "\\theta ", "&theta;");
		inp = StrReplace(inp, "\\Theta ", "&Theta;");
		inp = StrReplace(inp, "\\TM ", "&trade;");
		inp = StrReplace(inp, "\\tool ", "<font face=\"Webdings 2\">@</font>");
		inp = StrReplace(inp, "\\upsilon ", "&upsilon;");
		inp = StrReplace(inp, "\\ypsilon ", "&upsilon;");
		inp = StrReplace(inp, "\\Upsilon ", "&Upsilon;");
		inp = StrReplace(inp, "\\Ypsilon ", "&Upsilon;");
		inp = StrReplace(inp, "\\xi ", "&xi;");
		inp = StrReplace(inp, "\\Xi ", "&Xi;");
		inp = StrReplace(inp, "\\zeta ", "&zeta;");
		inp = StrReplace(inp, "\\Zeta ", "&Zeta;");
		return inp;
	}
	private static String replaceBoldSymbols(String inp) {
		int i = inp.indexOf("\\bold{");
		while (i > -1) {
			int j = i + 6;
			int b = 1;
			char c = '?';
			while ((j < inp.length()) && (b > 0)) {
				c = inp.charAt(j);
				switch (c) {
				case '{': {
					b++;
					break;
				}
				case '}': {
					b--;
					break;
				}
				}
				j++;
			}
			inp = inp.substring(0, i) + "<b>" + inp.substring(i + 6, j - 1) + "</b>" + inp.substring(j);
			i = inp.indexOf("\\bold{");
		}
		return inp;
	}
	private static String replaceTypeSymbols(String inp) {
		int i = inp.indexOf("\\type{");
		while (i > -1) {
			int j = i + 6;
			int b = 1;
			char c = '?';
			while ((j < inp.length()) && (b > 0)) {
				c = inp.charAt(j);
				switch (c) {
				case '{': {
					b++;
					break;
				}
				case '}': {
					b--;
					break;
				}
				}
				j++;
			}
			inp = inp.substring(0, i) + "<font face=courier>" + inp.substring(i + 6, j - 1) + "</font>" + inp.substring(j);
			i = inp.indexOf("\\type{");
		}
		return inp;
	}
	private static String replaceItalicSymbols(String inp) {
		int i = inp.indexOf("\\it{");
		while (i > -1) {
			int j = i + 4;
			int b = 1;
			char c = '?';
			while ((j < inp.length()) && (b > 0)) {
				c = inp.charAt(j);
				switch (c) {
				case '{': {
					b++;
					break;
				}
				case '}': {
					b--;
					break;
				}
				}
				j++;
			}
			inp = inp.substring(0, i) + "<i>" + inp.substring(i + 4, j - 1) + "</i>" + inp.substring(j);
			i = inp.indexOf("\\it{");
		}
		return inp;
	}
	private static String replaceRGBTags(String inp) {
		int i = inp.indexOf("\\rgb{");
		while (i > -1) {
			int j = i + 5;
			int b = 1;
			char c = '?';
			while ((j < inp.length()) && (b > 0)) {
				c = inp.charAt(j);
				switch (c) {
				case '{': {
					b++;
					break;
				}
				case '}': {
					b--;
					break;
				}
				}
				j++;
			}
			inp = inp.substring(0, i) + "<font color=" + inp.substring(i + 5, i + 11) + ">" + inp.substring(i + 12, j - 1) + "</font>" + inp.substring(j);
			i = inp.indexOf("\\rgb{");
		}
		return inp;
	}
	private static String replaceColorTags(String inp) {
		int i = inp.indexOf("\\color{");
		while (i > -1) {
			int j = i + 7;
			int b = 1;
			char c = '?';
			while ((j < inp.length()) && (b > 0)) {
				c = inp.charAt(j);
				switch (c) {
				case '{': {
					b++;
					break;
				}
				case '}': {
					b--;
					break;
				}
				}
				j++;
			}
			inp = inp.substring(0, i) + "<font color=" + inp.substring(i + 11, i + 13) + inp.substring(i + 9, i + 11) + inp.substring(i + 7, i + 9) + ">" + inp.substring(i + 14, j - 1) + "</font>" + inp.substring(j);
			i = inp.indexOf("\\color{");
		}
		return inp;
	}
	private static String replaceSuperscriptSymbols(String inp) {
		int i = inp.indexOf("\\^{");
		while (i > -1) {
			int j = i + 3;
			int b = 1;
			char c = '?';
			while ((j < inp.length()) && (b > 0)) {
				c = inp.charAt(j);
				switch (c) {
				case '{': {
					b++;
					break;
				}
				case '}': {
					b--;
					break;
				}
				}
				j++;
			}
			inp = inp.substring(0, i) + "<sup>" + inp.substring(i + 3, j - 1) + "</sup>" + inp.substring(j);
			i = inp.indexOf("\\^{");
		}
		return inp;
	}
	private static String replaceSubscriptSymbols(String inp) {
		int i = inp.indexOf("\\_{");
		while (i > -1) {
			int j = i + 3;
			int b = 1;
			char c = '?';
			while ((j < inp.length()) && (b > 0)) {
				c = inp.charAt(j);
				switch (c) {
				case '{': {
					b++;
					break;
				}
				case '}': {
					b--;
					break;
				}
				}
				j++;
			}
			inp = inp.substring(0, i) + "<sub>" + inp.substring(i + 3, j - 1) + "</sub>" + inp.substring(j);
			i = inp.indexOf("\\_{");
		}
		return inp;
	}
	private static String replaceUnderlinedSymbols(String inp) {
		int i = inp.indexOf("\\underline{");
		while (i > -1) {
			int j = i + 11;
			int b = 1;
			char c = '?';
			while ((j < inp.length()) && (b > 0)) {
				c = inp.charAt(j);
				switch (c) {
				case '{': {
					b++;
					break;
				}
				case '}': {
					b--;
					break;
				}
				}
				j++;
			}
			inp = inp.substring(0, i) + "<u>" + inp.substring(i + 11, j - 1) + "</u>" + inp.substring(j);
			i = inp.indexOf("\\underline{");
		}
		return inp;
	}
	private static String replaceSymbolsWithArrow(String inp) {
		int i = inp.indexOf("\\arrow{");
		while (i > -1) {
			int j = i + 7;
			int b = 1;
			char c = '?';
			while ((j < inp.length()) && (b > 0)) {
				c = inp.charAt(j);
				switch (c) {
				case '{': {
					b++;
					break;
				}
				case '}': {
					b--;
					break;
				}
				}
				j++;
			}
			inp = inp.substring(0, i) + "<span style=\"text-decoration: overline\">" + inp.substring(i + 7, j - 1) + "</span><sup><sup>&gt;</sup></sup>" + inp.substring(j);
			i = inp.indexOf("\\arrow{");
		}
		return inp;
	}
	private static String replaceCases(String inp) {
		int i = inp.indexOf("\\cases{");
		if (i > -1) {
			int j = i + 7;
			int b = 1;
			char c = '?';
			while ((j < inp.length()) && (b > 0)) {
				c = inp.charAt(j);
				switch (c) {
				case '{': {
					b++;
					break;
				}
				case '}': {
					b--;
					break;
				}
				}
				j++;
			}
			String prefix = inp.substring(0, i);
			String infix = inp.substring(i + 7, j - 1);
			String suffix = inp.substring(j);

			i = 0; // Position
			b = 0; // z�hlt Klammern
			int last = 0;
			int s = 0; // z�hlt Semikolons
			while (i < infix.length()) {
				if (infix.charAt(i) == '{') b++;
				if (infix.charAt(i) == '}') b--;
				if ((infix.charAt(i) == ';') && (b == 0)) {
					s++;
					infix = infix.substring(0, last) + replaceCases(infix.substring(last, i)) + "<br>" + replaceCases(infix.substring(i + 1));
					last = i;
				}
				i++;
			}
			if (s == 0) {
				i = 0; // Position
				b = 0; // z�hlt Klammern
				while (i < infix.length()) {
					if (infix.charAt(i) == '{') b++;
					if (infix.charAt(i) == '}') b--;
					if ((infix.charAt(i) == ',') && (b == 0)) {
						infix = infix.substring(0, last) + replaceCases(infix.substring(last, i)) + "<br>" + replaceCases(infix.substring(i + 1));
						last = i;
					}
					i++;
				}
			}

			inp = "<table border=\"0\" cellspacing=\"0\" bordercolor=\"#000000\">\n";
			inp = inp + "  <tr>\n    <td>" + prefix + "</td>\n    ";
			inp = inp + "~CASES~" + infix + "</td>\n    ";
			inp = inp + "<td>" + replaceCases(suffix) + "</td>\n  </tr>\n</table>";
		}
		return inp;
	}
	private static String replaceRootedSymbols(String inp) {
		int i = inp.indexOf("\\root{");
		while (i > -1) {
			int j = i + 6;
			int b = 1;
			char c = '?';
			while ((j < inp.length()) && (b > 0)) {
				c = inp.charAt(j);
				switch (c) {
				case '{': {
					b++;
					break;
				}
				case '}': {
					b--;
					break;
				}
				}
				j++;
			}
			String infix = inp.substring(i + 6, j - 1);
			System.out.println(infix);
			b = 0;
			int l = 0;
			int k = 0; // mekr Position des letzten Kommas
			int s = 0; // mekr Position des letzten Semikolon
			while (l < infix.length()) {
				switch (infix.charAt(l)) {
				case '{': {
					b++;
					break;
				}
				case '}': {
					b--;
					break;
				}
				case ',': {
					if (b == 0) k = l;
				}
				case ';': {
					if (b == 0) s = l;
				}
				}
				l++;
			}
			if (s + k > 0) {
				if (s > 0) k = s; // wenn Semikolon gefunden, dann Semikolon nehmen, sonst komma nehmen nehmen
				String rad = infix.substring(0, k);
				infix = infix.substring(k + 1);
				System.out.println(rad);
				System.out.println(infix);
				inp = inp.substring(0, i) + "<sup>" + rad + "</sup>&radic;<span style=\"text-decoration: overline\">" + infix + "</span>" + inp.substring(j);
			} else
				inp = inp.substring(0, i) + "&radic;<span style=\"text-decoration: overline\">" + infix + "</span>" + inp.substring(j);
			i = inp.indexOf("\\root{");
		}
		return inp;
	}
	private static String replaceFracs(String inp) {
		int i = inp.indexOf("\\frac{");
		if (i > -1) {
			int j = i + 6;
			int b = 1;
			char c = '?';
			while ((j < inp.length()) && (b > 0)) {
				c = inp.charAt(j);
				switch (c) {
				case '{': {
					b++;
					break;
				}
				case '}': {
					b--;
					break;
				}
				}
				j++;
			}
			String prefix = inp.substring(0, i);
			String infix1 = inp.substring(i + 6, j - 1);
			String suffix = inp.substring(j);
			b = 0;
			int l = 0;
			int k = 0; // mekr Position des letzten Kommas
			int s = 0; // mekr Position des letzten Semikolon
			while (l < infix1.length()) {
				switch (infix1.charAt(l)) {
				case '{': {
					b++;
					break;
				}
				case '}': {
					b--;
					break;
				}
				case ',': {
					if (b == 0) k = l;
					break;
				}
				case ';': {
					if (b == 0) s = l;
					break;
				}
				}
				l++;
			}

			if (s > 0) k = s; // wenn Semikolon gefunden, dann Semikolon nehmen, sonst komma nehmen nehmen
			String infix2 = infix1.substring(k + 1);
			infix1 = infix1.substring(0, k);
			inp = "<table border=\"0\" cellspacing=\"0\" bordercolor=\"#000000\">\n";
			inp = inp + "  <tr>\n    <td rowspan=\"2\">" + prefix + "</td>\n    ";
			inp = inp + "<td style=\"border-bottom-style: solid; border-bottom-width: 1px\" align=\"center\">" + replaceFracs(infix1) + "</td>\n    ";
			inp = inp + "<td rowspan=\"2\">" + replaceFracs(suffix) + "</td>\n  </tr>\n";
			inp = inp + "  <tr><td align=\"center\">" + replaceFracs(infix2) + "</td>\n</tr></table>";
		}
		return inp;
	}
	private static String StrReplace(String source, String pattern, String news) {
		int i = source.indexOf(pattern);
		while (i > -1) {
			source = source.substring(0, i) + news + source.substring(i + pattern.length());
			i = source.indexOf(pattern);
		}
		return source;
	} // <font face=symbol></font>

  /**************** end of HTML export methods *******************/
	
	


	/***************************** Konstruktor ************************************/
	public Formula(String code) {
		this.code = doReplacements(code);
	}
	/***************************** Rendering ************************************/

	private static BufferedImage render(StringBuffer code, FormulaFont font) {
		Vector<BufferedImage> parts=new Vector<BufferedImage>();
		while (code.length()>0){
			parts.add(renderLine(code, font));			
		}
		return composeColumn(parts,font.align);
	}
	
	private static BufferedImage renderLine(StringBuffer code, FormulaFont font) {
		Vector<BufferedImage> parts=new Vector<BufferedImage>();
		StringBuffer chunk=new StringBuffer();
		while (code.length()>0){
			if (code.charAt(0)=='\\'){				
				parts.add(renderText(chunk.toString(), font));
				if (code.length()>2 && code.charAt(1)=='n' && code.charAt(2)==' '){ // \n command found
					code.delete(0, 3);
					return composeLine(parts);
				}
				parts.add(parseCommand(code,font));
				chunk=new StringBuffer();				
			} else {
				transferChar(code, chunk);
			}
		}
		parts.add(renderText(chunk.toString(), font));
		return composeLine(parts);
	}
	
	private static BufferedImage renderText(String text, FormulaFont font){
		if (text==null || text.isEmpty()) return null;
		int width=font.stringWidth(text);
		int height=font.getHeight();
		if (height<1 || width<1) return null;
		BufferedImage result=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=graphics(result,font);
		try {
			g.drawString(text, 0,height*3/4);
		} catch (ArrayIndexOutOfBoundsException aoobe){
			aoobe.printStackTrace();
			return null;
		}
		return result;
	}
	
	private static BufferedImage composeLine(Vector<BufferedImage> parts) {
		int height=0;
		int width=0;
		for (BufferedImage image:parts){
			if (image!=null){
				height=Math.max(height, image.getHeight());
				width+=image.getWidth();
			}
		}
		if (height==0 || width==0){
			return null;
		}
		BufferedImage result=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D) result.createGraphics();
		int x=0;
		for (BufferedImage image:parts){
			if (image!=null){
				g.drawImage(image, x, (height-image.getHeight())/2, null);
				x+=image.getWidth();
				image=null;
			}
		}
		return result;		
	}
	
	private static BufferedImage composeColumn(Vector<BufferedImage> parts,int alignment) {
		int height=0;
		int width=0;
		for (BufferedImage image:parts){
			if (image!=null){
				width=Math.max(width, image.getWidth());
				height+=image.getHeight();
			}
		}
		if (height==0 || width==0){
			return null;
		}
		BufferedImage result=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D) result.createGraphics();
		int y=0;
		for (BufferedImage image:parts){
			if (image!=null){
				switch (alignment){
				case FormulaFont.CENTER:
					g.drawImage(image, (width-image.getWidth())/2, y, null); break;
				case FormulaFont.RIGHT:
					g.drawImage(image, width-image.getWidth(), y, null); break;
				default:
					g.drawImage(image, 0, y, null); break;
				}				
				y+=image.getHeight();
				image=null;
			}
		}
		return result;		
	}
	
	private static BufferedImage parseCommand(StringBuffer code, FormulaFont font) {
		String command=readCommand(code);
		if (code.length()==0){
			return renderText(command, font);
		}
		if (code.charAt(0)=='{'){
			code.deleteCharAt(0);
			StringBuffer chunk=findMatchingBracket(code);
			return renderCommand(command,chunk,font);
		}
		if (code.charAt(0)==' '){
			code.deleteCharAt(0);
			return renderCommand(command,font);
		}
		System.out.println(command);
		return renderText(code.toString(), font);
	}
	
	private static BufferedImage renderCommand(String command, FormulaFont font) {
		command=command.substring(1);
		if (command.equals("##")) {
			return renderText("##", font);
		} 
		if (command.equals("eye")) {
			BufferedImage image=renderText("OO", font);
			Graphics2D g=graphics(image, font);
			g.drawString("\u2219 \u2219", font.getHeight()/10, font.getHeight()*3/4);
			return image;
		}
		if (command.equals("nokbox")) {
			BufferedImage image=renderText("\u25a1", font);
			Graphics2D g=graphics(image, font);
			g.drawString("\u2717", font.getHeight()/10, font.getHeight()*3/4);
			return image;
		}
		if (command.equals("okbox")) {
			BufferedImage image=renderText("\u25a1", font);
			Graphics2D g=graphics(image, font);
			g.drawString("\u2713", font.getHeight()/10, font.getHeight()*3/4);
			return image;
		}
		if (command.equals("\\")) {
			return renderText("\\", font);
		}
		if (command.isEmpty()) return null;
		System.err.println("unknown command '"+command+"'");
		return renderText(command, font);
	}
	private static BufferedImage renderCommand(String cmd, StringBuffer code, FormulaFont font) {
		cmd=cmd.substring(1);
		if (cmd.equals("^")) return renderSuperscript(code,font);
		if (cmd.equals("_")) return renderSubscript(code,font);
		if (cmd.equals("~")) return renderTilde(code,font);
		if (cmd.equals("arrow")) return renderVector(code,font);
		if (cmd.equals("big")) return render(code,font.bigger());
		if (cmd.equals("block")) return renderBlock(code,font.leftAligned());
		if ((cmd.equals("bold")) || (cmd.equals("bf"))) return render(code, font.bold());
		if (cmd.equals("cases")) return renderCases(code,font);
		if (cmd.equals("cap")) return renderIntervall(code, "\u22C2", font);
		if (cmd.equals("Cap")) return renderIntervall(code, "\u22C0", font);
		if (cmd.equals("ceil")) return renderCeiling(code, font);
		if (cmd.equals("center")) return renderBlock(code, font.centered());
		if (cmd.equals("color")) return renderColored(code,font);
		if (cmd.equals("cup")) return renderIntervall(code, "\u22C3", font);
		if (cmd.equals("cup+")) return renderIntervall(code, "\u228e", font);
		if (cmd.equals("Cup")) return renderIntervall(code, "\u22c1", font);
		if (cmd.equals("det")) return renderDeterminant(code,font);
		if (cmd.equals("dot")) return rendetWithDot(code,font);
		if (cmd.equals("exists")) return renderIntervall(code, "\u2203", font);
		if (cmd.equals("floor")) return renderFloor(code,font);
		if (cmd.equals("forall")) return renderIntervall(code, "\u2200", font);
		if (cmd.equals("frac")) return renderFrac(code,font);
		if (cmd.equals("hat")) return renderWithHat(code,font);
		if (cmd.equals("index")) return render("\\block{"+code+"}", font.smaller());
		if (cmd.equals("integr")) return renderIntervall(code, "\u222B", font);
		if (cmd.equals("interv")) return renderIntervall(code,font);
		if ((cmd.equals("it")) || (cmd.equals("italic"))) return render(code, font.italic());
		if (cmd.equals("lim")) return renderBlock(" \\n lim\\n \\^{" + code + "}", font);
		if (cmd.equals("matrix")) return renderMatrix(code,font);
		if (cmd.equals("overline")) return overline(code,font);
		if (cmd.equals("prod")) return renderIntervall(code, "\u220F", font);
		if (cmd.equals("rblock")) return renderBlock(code,font.rightAligned());
		if (cmd.equals("rgb")) return renderRGBColored(code,font);
		if (cmd.equals("root")) return renderRoot(code,font);
		if (cmd.equals("set")) return renderSet(code,font);
		if (cmd.equals("small")) return render(code,font.smaller());
		if (cmd.equals("strike")) return renderStriked(code,font);
		if (cmd.equals("sum")) return renderIntervall(code, "\u2211", font);
		if (cmd.equals("tilde")) return renderTilde(code,font);
		if (cmd.equals("type")) return render(code,font.monospaced());
		if (cmd.equals("underline")) return underline(code,font);
		if (cmd.equals("vector")) return renderVector(code,font);
		System.out.println(cmd+"("+code+")");
    return render(code, font);
	}
	
	private static BufferedImage renderSet(StringBuffer code, FormulaFont font) {
		BufferedImage image=renderBlock(code, font);
		if (image==null) return null;
		int h=image.getHeight();
		int w=image.getWidth()+10;
		BufferedImage result=new BufferedImage(w+10,h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=graphics(result,font);
		g.drawImage(image,10,0,null);
		g.drawLine(5, 5,10, 0); 																	// /
		g.drawLine(5, h/2-5,  5,  5); 													// |
		g.drawLine(0,  h/2,  5,  h/2-5); 															// /
		g.drawLine(0,  h/2,  5,  h/2+5); 															// \
		g.drawLine(5,  h/2+5, 5,  h - 5); 					// |
		g.drawLine(5,  h-5,  10, h); 	// \
		
		g.drawLine( w,  0, w+5 , 5); 																// /
		g.drawLine( w+5,  5,  w+5,  h/2-5); 											// |
		g.drawLine( w+5, h/2-5,  w+10,  h/2); 										// /
		g.drawLine( w+10,  h/2,  w+5,  h/2+5); 											// \
		g.drawLine( w+5,  h/2+5,  w+5,  h - 5); 		 	// |
		g.drawLine( w+5,  h - 5, w,  h); 	// \

		return result;
	}
	private static BufferedImage renderWithHat(StringBuffer code, FormulaFont font) {
		BufferedImage image = render(code,font);
		if (image==null) return null;
		int d=font.getHeight()/3;
		BufferedImage result=new BufferedImage(image.getWidth(), image.getHeight()+d, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = graphics(result, font);
		g.drawImage(image, 0, d, null);
		g.drawLine(0, d, image.getWidth()/2, 0);
		g.drawLine(image.getWidth()/2, 0, image.getWidth(), d);
	  return result;
  }

	private static BufferedImage rendetWithDot(StringBuffer code, FormulaFont font) {
		BufferedImage image = render(code,font);
		if (image==null) return null;
		int d=font.getHeight()/3;
		BufferedImage result=new BufferedImage(image.getWidth(), image.getHeight()+d, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = graphics(result, font);
		g.drawImage(image, 0, d, null);
		g.drawOval(result.getWidth() / 2 - font.getHeight()/6, 0, font.getHeight()/4, font.getHeight()/4);
	  return result;
  }
	private static BufferedImage renderDeterminant(StringBuffer code, FormulaFont font) {
			BufferedImage image=renderBlock(code, font);
			if (image==null) return null;
			int h=image.getHeight();
			int w=image.getWidth();
			BufferedImage result=new BufferedImage(w+20,h,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g=graphics(result,font);
			g.drawImage(image,10,0,null);
			g.drawLine(5, 0, 5, h - 1); // |
			g.drawLine(w + 15, 0, 15 + w, h -1); // |
			return result;
		}
	private static BufferedImage renderRGBColored(StringBuffer parameters, FormulaFont font) {
		Vector<String> para=readParameters(parameters.toString());
		if (para.size()>1){
			String param=para.firstElement();
			int r = 0;
			int y = 0;
			int b = 0;
			try {
				r = Integer.decode("0x" + param.substring(0, 2)).intValue();
				y = Integer.decode("0x" + param.substring(2, 4)).intValue();
				b = Integer.decode("0x" + param.substring(4, 6)).intValue();
			} catch (Exception e) {}
			return render(parameters.substring(param.length()+1),font.color(new Color(r,y,b)));
		}
		return render(parameters,font);
	}
	private static BufferedImage renderColored(StringBuffer parameters, FormulaFont font) {
		Vector<String> para=readParameters(parameters.toString());
		if (para.size()>1){
			String param=para.firstElement();
			int r = 0;
			int y = 0;
			int b = 0;
			try {
				b = Integer.decode("0x" + param.substring(0, 2)).intValue();
				y = Integer.decode("0x" + param.substring(2, 4)).intValue();
				r = Integer.decode("0x" + param.substring(4, 6)).intValue();
			} catch (Exception e) {}
			return render(parameters.substring(param.length()+1),font.color(new Color(r,y,b)));
		}
		return render(parameters,font);
	}
	private static BufferedImage renderIntervall(StringBuffer parameters, FormulaFont font) {
		Vector<String> para=readParameters(parameters.toString());
		FormulaFont smallFont = font.smaller();
		if (para.size()>1){
			BufferedImage lo = render(para.get(0),smallFont);
			BufferedImage hi = render(para.get(1),smallFont);
			int h=font.getHeight()+lo.getHeight()+hi.getHeight();
			int w=Math.max(lo.getWidth(), hi.getWidth());
			BufferedImage result = new BufferedImage(2+w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g=graphics(result,font);
			g.drawImage(hi, 2, 0, null);
			g.drawImage(lo, 2, h-lo.getHeight(), null);
			g.drawLine(0, smallFont.getHeight()/3, 0, h-smallFont.getHeight()/2);
			return result;
		}
		if (para.size()>0){
			BufferedImage boundary = render(para.firstElement(),smallFont);
			if (boundary==null) return null;
			int h=font.getHeight()+boundary.getHeight();
			BufferedImage result = new BufferedImage(2+boundary.getWidth(), h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g=graphics(result, font);
			g.drawImage(boundary, 2, h-boundary.getHeight(), null);
			g.drawLine(0, 0, 0, h);
			return result;
		}
		BufferedImage result = new BufferedImage(2, font.getHeight()+smallFont.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=graphics(result, font);
		g.drawLine(0, 0, 0, result.getHeight());
		return result;
	}
	private static Graphics2D graphics(BufferedImage image, FormulaFont font) {
		Graphics2D g=image.createGraphics();
		font.applyTo(g);
		return g;
	}
	private static BufferedImage renderRoot(StringBuffer parameters, FormulaFont font) {
		Vector<String> para=readParameters(parameters.toString());
		if (para.size()>1){
			BufferedImage exp = render(para.get(0),font.smaller());
			BufferedImage rad = render(para.get(1),font);
			
			int h=Math.max(rad.getHeight(),rad.getHeight()/2+exp.getHeight());
			BufferedImage result = new BufferedImage(rad.getWidth()+font.getHeight()/2+exp.getWidth(), h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g=graphics(result,font);
			g.drawImage(exp, 0, h-exp.getHeight()-rad.getHeight()/2, null);
			g.drawImage(rad, exp.getWidth()+font.getHeight()/2, h-rad.getHeight(), null);
			g.drawLine(0, h-rad.getHeight()/2, exp.getWidth(), h-rad.getHeight()/2);
			g.drawLine(exp.getWidth(), h-rad.getHeight()/2, exp.getWidth()+font.getHeight()/4,h);
			g.drawLine(exp.getWidth()+font.getHeight()/4,h,exp.getWidth()+font.getHeight()/2,h-rad.getHeight());
			g.drawLine(exp.getWidth()+font.getHeight()/2,h-rad.getHeight(),exp.getWidth()+font.getHeight()/2+rad.getWidth(),h-rad.getHeight());
			g.drawLine(exp.getWidth()+font.getHeight()/2+rad.getWidth(),h-rad.getHeight(),exp.getWidth()+font.getHeight()/2+rad.getWidth(),h-rad.getHeight()+font.getHeight()/4);
			return result;
		}
		BufferedImage rad = render(para.get(0),font);
		if (rad==null) return null;
		int h=rad.getHeight();
		BufferedImage result = new BufferedImage(rad.getWidth()+font.getHeight()/2+1, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=graphics(result,font);
		g.drawImage(rad, font.getHeight()/2, h-rad.getHeight(), null);
		g.drawLine(0, h/2, font.getHeight()/4,h);
		g.drawLine(font.getHeight()/4,h,font.getHeight()/2,0);
		g.drawLine(font.getHeight()/2,0,font.getHeight()/2+rad.getWidth(),0);
		g.drawLine(font.getHeight()/2+rad.getWidth(),0,font.getHeight()/2+rad.getWidth(),font.getHeight()/4);
		return result;
	}
	private static BufferedImage renderFloor(StringBuffer code, FormulaFont font) {
		BufferedImage image=render(code, font);
		if (image==null) return null;
		int d=font.getHeight()/3;
		int h=image.getHeight()+d;
		int w=image.getWidth()+d;
		BufferedImage result=new BufferedImage(image.getWidth()+d, image.getHeight()+d, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=graphics(result,font);
		g.drawImage(image, d/2, d/2, null);
		g.drawLine(0, 0, 0, h-1);
		g.drawLine(0, h-1, d/2, h-1);
		
		g.drawLine(w-1, 0, w-1, h-1);
		g.drawLine(w-1-d/2, h-1, w-1, h-1);

		return result;
	}
	private static BufferedImage renderCeiling(StringBuffer code, FormulaFont font) {
		BufferedImage image=render(code, font);
		if (image==null) return null;
		int d=font.getHeight()/3;
		int h=image.getHeight()+d;
		int w=image.getWidth()+d;
		BufferedImage result=new BufferedImage(image.getWidth()+d, image.getHeight()+d, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=graphics(result,font);
		g.drawImage(image, d/2, d/2, null);
		g.drawLine(0, 0, d/2, 0);
		g.drawLine(0, 0, 0, h);

		g.drawLine(w-d/2, 0, w-1, 0);
		g.drawLine(w-1, 0, w-1, h);

		return result;
	}
	private static BufferedImage renderMatrix(StringBuffer code, FormulaFont font) {
		BufferedImage image=renderBlock(code, font);
		if (image==null) return null;
		int h=image.getHeight();
		int w=image.getWidth();
		BufferedImage result=new BufferedImage(w+20,h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=graphics(result,font);
		g.drawImage(image,10,0,null);
		g.drawLine(5, 5, 10, 0); // /
		g.drawLine(5, 5, 5, h - 5); // |
		g.drawLine(5, h - 5, 10, h); // \

		g.drawLine(w + 15, 5, 10 + w, 0);     // \
		g.drawLine(w + 15, 5, 15 + w, h - 5); // |
		g.drawLine(w + 15, h - 5, 10 + w, h); // /

		return result;
	}
	private static BufferedImage renderFrac(StringBuffer parameters, FormulaFont font) {
		Vector<String> para=readParameters(parameters.toString());
		BufferedImage numerator = render(para.firstElement(),font);
		BufferedImage denominator=render(para.lastElement(),font);
		if (numerator==null) return null;
		if (denominator==null) return null;
		int width=Math.max(numerator.getWidth(), denominator.getWidth());
		BufferedImage result=new BufferedImage(width, numerator.getHeight()+denominator.getHeight()+1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=graphics(result,font);
		g.drawImage(numerator, (width-numerator.getWidth())/2, 0, null);		
		g.drawImage(denominator, (width-denominator.getWidth())/2, numerator.getHeight()+1, null);
		g.drawLine(0, numerator.getHeight()+1, width, numerator.getHeight()+1);
		return result;
	}
	private static BufferedImage render(String code, FormulaFont font) {
		return render(new StringBuffer(code),font);
	}
	private static BufferedImage renderIntervall(StringBuffer parameters, String symbol, FormulaFont font) {
		Vector<String> para=readParameters(parameters.toString());
		Vector<BufferedImage> parts=new Vector<BufferedImage>();
		
		if (para.size()>1){
			parts.add(render(para.get(1),font.smaller()));
		}
		parts.add(renderText(symbol, font.bigger()));
		if (para.size()>0){
			parts.add(render(para.get(0),font.smaller()));
		}
		return composeColumn(parts,FormulaFont.CENTER);
	}
	private static Vector<String> readParameters(String parameters) {		
		if (parameters.contains(";")){			
			return new Vector<String>(Arrays.asList(parameters.split(";")));
		}
		if (parameters.contains(",")){
			return new Vector<String>(Arrays.asList(parameters.split(",")));
		}
		Vector<String> res=new Vector<String>();
		res.add(parameters);
		return res;
	}
	private static BufferedImage renderCases(StringBuffer code, FormulaFont font) {
		BufferedImage block = renderBlock(code, font);
		int h=block.getHeight();
		BufferedImage result=new BufferedImage(block.getWidth()+10,h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=graphics(result,font);
		g.drawImage(block, 10, 0, null);
		g.drawLine(5, 5, 10, 0);                                 // /
		g.drawLine(5, h/2 - 5, 5, 5);                            // |
		g.drawLine(0, h/2, 5, h/2-5);                              // /
		g.drawLine(0, h/2, 5, h/2+5);                              // \
		g.drawLine(5, h/2+5, 5, h - 5);          // |
		g.drawLine(5, h - 5, 10, h); // \
		return result;
	}
	
	private static BufferedImage renderBlock(StringBuffer code, FormulaFont font) {
		return renderBlock(code.toString(), font);
	}	
	private static BufferedImage renderBlock(String code, FormulaFont font) {
		if (code.contains("\\n ")){
			// do nothing
		} else if (code.contains(";")){
			code=code.replace(";", "\\n ");
		} else {
			code=code.replace(",", "\\n ");
		}
		return render(new StringBuffer(code),font);
	}
	private static BufferedImage renderVector(StringBuffer code, FormulaFont font) {
		BufferedImage image=render(code,font);
		if (image==null) return null;
		int d=font.getHeight()/4;
		BufferedImage result=new BufferedImage(image.getWidth()+d, image.getHeight()+d, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=graphics(result,font);
		g.drawImage(image, 0, d, null);
		g.drawLine(0, d, image.getWidth()+d, d);		
		g.drawLine(image.getWidth(), 0, image.getWidth()+d, d);		
		g.drawLine(image.getWidth(), d+d, image.getWidth()+d, d);	
		
		return result;
	}
	private static BufferedImage renderTilde(StringBuffer code, FormulaFont font) {
		BufferedImage image=render(code,font);
		if (image==null) return null;
		BufferedImage result=new BufferedImage(image.getWidth(), image.getHeight()*5/4, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D) result.getGraphics();
		g.drawImage(image, 0, result.getHeight()-image.getHeight(), null);
		g.drawImage(renderText("~", font),-2,-font.getHeight()/3,null);
		return result;
	}
	private static BufferedImage renderSubscript(StringBuffer code, FormulaFont font) {
		FormulaFont small = font.smaller();
		BufferedImage image=render(code,small);
		if (image==null) return null;
		BufferedImage result=new BufferedImage(image.getWidth(), image.getHeight()+font.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D) result.getGraphics();
		g.drawImage(image, 0, image.getHeight(), null);
		return result;
	}
	
	private static BufferedImage renderSuperscript(StringBuffer code, FormulaFont font) {
		FormulaFont small = font.smaller();
		BufferedImage image=render(code,small);
		if (image==null) return null;
		BufferedImage result=new BufferedImage(image.getWidth(), image.getHeight()+font.getHeight(), BufferedImage.TYPE_INT_ARGB);		
		Graphics2D g=(Graphics2D) result.getGraphics();
		g.drawImage(image, 0, 0, null);
		return result;
	}
	private static BufferedImage renderStriked(StringBuffer code, FormulaFont font) {
		BufferedImage image=render(code,font);
		Graphics2D g = (Graphics2D)image.getGraphics();
		g.setColor(font.col);
		g.drawLine(0, image.getHeight()/2, image.getWidth(), image.getHeight()/2);
		return image;
	}
	private static BufferedImage overline(StringBuffer code, FormulaFont font) {
		BufferedImage image=render(code,font);
		Graphics2D g = (Graphics2D)image.getGraphics();
		g.setColor(font.col);
		g.drawLine(0, 0, image.getWidth(), 0);
		return image;
	}
	private static BufferedImage underline(StringBuffer code, FormulaFont font) {
		BufferedImage image=render(code,font);
		Graphics2D g = (Graphics2D)image.getGraphics();
		g.setColor(font.col);
		g.drawLine(0, image.getHeight()-1, image.getWidth(), image.getHeight()-1);
		return image;
	}
	private static StringBuffer findMatchingBracket(StringBuffer code) {
		int count=1;
		StringBuffer chunk=new StringBuffer();
		while (code.length()>0){
			if (code.charAt(0)=='{'){
				count++;
			}
			if (code.charAt(0)=='}'){
				count--;
			}
			if (count==0){
				code.deleteCharAt(0);
				break;
			}
			transferChar(code, chunk);
		}
		return chunk;
	}
	
	private static String readCommand(StringBuffer code) {
		StringBuffer command=new StringBuffer();
		transferChar(code, command);
		while (code.length()>0){		
			if (code.charAt(0)=='{'){
				break;
			}
			if (code.charAt(0)==' '){
				break;
			}
			transferChar(code, command);			
		}
		return command.toString();
	}
	
	private static void transferChar(StringBuffer origin, StringBuffer destination) {
		destination.append(origin.charAt(0));
		origin.deleteCharAt(0);
	}
	
	

	/****************************** Abfragen **************************************/
	public String toString() {
		return code;
	}

	public String toXML() {
		return "<formula>" + this.toString() + "</formula>";
	}
	
	public BufferedImage image(FormulaFont font){
		if (image==null || !font.equals(lastFont)){
			image=render(new StringBuffer(code),font);
			lastFont=font;
		}
		return image;
	}
	
	public Dimension getSize(FormulaFont font) {
		BufferedImage img = image(font);
		if (img==null) return new Dimension();
		return new Dimension(img.getWidth(),img.getHeight());
	}

	
	public String getText() {
		String text = code;
		// suchen von formatierungen die in \blabla{...} eingeschlossen sind
		int positionOfClosingBracket = text.indexOf('}');
		while (positionOfClosingBracket > 0) {
			int positionOfOpeningBracket = text.lastIndexOf('{', positionOfClosingBracket);
			int positionOfBackslash = text.lastIndexOf('\\', positionOfOpeningBracket);
			if (positionOfBackslash >= 0) {
				String command=text.substring(positionOfBackslash+1,positionOfOpeningBracket);
				if (command.equals("rgb")||command.equals("color")) positionOfOpeningBracket=Math.min(text.indexOf(",",positionOfOpeningBracket),text.indexOf(";",positionOfOpeningBracket));
				if (command.equals("^")) {
					text = text.substring(0, positionOfBackslash) + "^("+ text.substring(positionOfOpeningBracket + 1, positionOfClosingBracket) + ")" + text.substring(positionOfClosingBracket + 1);
        } else {
  				if (command.equals("_")) {
  					text = text.substring(0, positionOfBackslash) + "_"+ text.substring(positionOfOpeningBracket + 1, positionOfClosingBracket) + " " + text.substring(positionOfClosingBracket + 1);
          } else {
          	try {
          		text = text.substring(0, positionOfBackslash) + text.substring(positionOfOpeningBracket + 1, positionOfClosingBracket) + text.substring(positionOfClosingBracket + 1);
          	} catch (IndexOutOfBoundsException iobe){
          		System.err.println(text);
          		throw iobe;
          	}
          }
        }				
				positionOfClosingBracket = text.indexOf('}');
			} else
				positionOfClosingBracket = text.indexOf('}', positionOfClosingBracket + 1);
		}

		// Suchen von Zeilenumbrüchen
		int positionOfLinebreak;
		while ((positionOfLinebreak = text.indexOf("\\n ")) >= 0)
			text = text.substring(0, positionOfLinebreak) + '\n' + text.substring(positionOfLinebreak + 3);
		while ((positionOfLinebreak = text.indexOf("##")) >= 0)
			text = text.substring(0, positionOfLinebreak) + '\n' + text.substring(positionOfLinebreak + 2);
		while ((positionOfLinebreak = text.indexOf("e^(")) >= 0)
			text = text.substring(0, positionOfLinebreak) + "exp(" + text.substring(positionOfLinebreak + 3);

		// System.out.println(text);
		// suchen von Formatierungen, die aus einfachen Tags "\tag " bestehen
		int positionOfBackslash = text.indexOf('\\');
		while (positionOfBackslash >= 0) {
			int positionOfSpace = text.indexOf(' ', positionOfBackslash);
			if (positionOfSpace >= 0) {
				text = text.substring(0, positionOfBackslash) + text.substring(positionOfBackslash + 1, positionOfSpace) + text.substring(positionOfSpace + 1);
				positionOfBackslash = text.indexOf('\\');
			} else
				positionOfBackslash = text.indexOf('\\', positionOfBackslash + 1);
		}
		return text; // hier muss noch Konvertierung stattfinden
	}

	/****************************** Hilfsoperationen ******************************/
	
	String doReplacements(String input){
		input=input.replace("\\> ", "\u227b");
		input=input.replace("\\< ", "\u227a");
		input=input.replace("\\* ", "\u00b7");
		input=input.replace("\\(+) ", "\u2295");
		input=input.replace("\\(C) ", "\u00A9");
		input=input.replace("\\(R) ", "\u00AE");
		input=input.replace("\\(x) ", "\u2297");
		input=input.replace("\\(X) ", "\u2297");
			input=input.replace("\\(-) ", "\u2296");
			input=input.replace("\\(/) ", "\u2298");
			input=input.replace("\\<= ", "\u21d0");
			input=input.replace("\\<=> ", "\u21d4");
			input=input.replace("\\=> ", "\u21d2");
			input=input.replace("\\<- ", "\u2190");
			input=input.replace("\\<-> ", "\u2194");
			input=input.replace("\\-> ", "\u2192");
			input=input.replace("\\~> ", "\u219D");
			input=input.replace("\\=pi ", "3.141592");
			input=input.replace("\\ae ", "æ");
			input=input.replace("\\alpha ", "\u03b1");
			input=input.replace("\\Alpha ", "\u0391");
			input=input.replace("\\angstr ", "\u212b");
			input=input.replace("\\beta ", "\u03b2");
			input=input.replace("\\Beta ", "\u0392");
			input=input.replace("\\bomb ", "\u21af");
			input=input.replace("\\book ", "\u27bd");
			input=input.replace("\\bool ", "IB");
			input=input.replace("\\box ", "\u25a1");
			input=input.replace("\\cap ", "\u22C2");
			input=input.replace("\\Cap ", "\u22C0");
			input=input.replace("\\cdot ", "\u00b7");
			input=input.replace("\\chi ", "\u03c7");
			input=input.replace("\\Chi ", "\u03a7");
			input=input.replace("\\circ ", "\u2218");
			input=input.replace("\\complex ", "\u2102");
			input=input.replace("\\corr ", "\u2259");
			input=input.replace("\\CO2 ", "CO\\_{2}");			
			input=input.replace("\\cup ", "\u22c3");
			input=input.replace("\\cup+ ", "\u228e");
			input=input.replace("\\Cup ", "\u22c1");
			input=input.replace("\\d ", "\u2202");
			input=input.replace("\\delta ", "\u03b4");
			input=input.replace("\\Delta ", "\u0394");
			input=input.replace("\\disk ", "\u25d9");
			input=input.replace("\\downarrow ", "\u2193");
			input=input.replace("\\Downarrow ", "\u21D3");
			input=input.replace("\\emptyset ", "\u2205");
			input=input.replace("\\epsilon ", "\u03b5");
			input=input.replace("\\Epsilon ", "\u0395");
			input=input.replace("\\eq ", "\u2261");
			input=input.replace("\\eta ", "\u03b7");
			input=input.replace("\\Eta ", "\u0397");
			input=input.replace("\\exists ", "\u2203");
			input=input.replace("\\forall ", "\u2200");
			input=input.replace("\\gamma ", "\u03b3");
			input=input.replace("\\Gamma ", "\u0393");
			input=input.replace("\\geq ", "\u2265");
			input=input.replace("\\H2O ", "H\\_{2}O");
			input=input.replace("\\in ", "\u2208");
			input=input.replace("\\infty ", "\u221e");
			input=input.replace("\\info ", "\u2139");
			input=input.replace("\\int ", "\u2124");
			input=input.replace("\\iota ", "\u0269");
			input=input.replace("\\Iota ", "\u0399");
			input=input.replace("\\kappa ", "\u03ba");
			input=input.replace("\\Kappa ", "\u039a");
			input=input.replace("\\lambda ", "\u03bb");
			input=input.replace("\\Lambda ", "\u039b");
			input=input.replace("\\leftarrow ", "\u2190");
			input=input.replace("\\Leftarrow ", "\u21D0");
			input=input.replace("\\leftrightarrow ", "\u2194");
			input=input.replace("\\Leftrightarrow ", "\u21D4");
			input=input.replace("\\leq ", "\u2264");
			input=input.replace("\\mp ", "\u2213");
			input=input.replace("\\mu ", "\u03bc");
			input=input.replace("\\my ", "\u03bc");
			input=input.replace("\\Mu ", "\u039c");
			input=input.replace("\\My ", "\u039c");
			input=input.replace("\\nabla ", "\u2207");
			input=input.replace("\\natural ", "\u2115");
			input=input.replace("\\neq ", "\u2260");
			input=input.replace("\\nok ", "\u2717");
		
		input=input.replace("\\not ", "\u00ac");
		input=input.replace("\\notin ", "\u2209");
		input=input.replace("\\nu ", "\u03bd");
		input=input.replace("\\ny ", "\u03bd");
		input=input.replace("\\Nu ", "\u039d");
		input=input.replace("\\Ny ", "\u039d");
		input=input.replace("\\ok ", "\u2713");
		input=input.replace("\\omega " ,"\u03c9");
		input=input.replace("\\Omega " ,"\u03a9");
		input=input.replace("\\omicron " ,"\u03bf");
		input=input.replace("\\omikron " ,"\u03bf");
		input=input.replace("\\Omicron " ,"\u039f");
		input=input.replace("\\Omikron " ,"\u039f");
		input=input.replace("\\partial " ,"\u2202");
		input=input.replace("\\pen " ,"\u270f");
		input=input.replace("\\phi " ,"\u03c6");
		input=input.replace("\\Phi " ,"\u03a6");
		input=input.replace("\\phone " ,"\u2706");
		input=input.replace("\\pi " ,"\u03C0");
		input=input.replace("\\Pi " ,"\u03a0");
		input=input.replace("\\pm " ,"\u00b1");
		input=input.replace("\\prop " ,"\u221d");
		input=input.replace("\\psi " ,"\u03c8");
		input=input.replace("\\Psi " ,"\u03a8");
		input=input.replace("\\real " ,"\u211d");
		input=input.replace("\\rho " ,"\u03c1");
		input=input.replace("\\Rho " ,"\u03a1");
		input=input.replace("\\rightarrow " ,"\u2192");
		input=input.replace("\\Rightarrow " ,"\u21d2");
		input=input.replace("\\round " ,"\u2248"); 
		input=input.replace("\\sigma " ,"\u03c3");
		input=input.replace("\\Sigma " ,"\u03a3");
		input=input.replace("\\smile " ,"\u263A");
		input=input.replace("\\star " ,"\u22c6");
		input=input.replace("\\subset " ,"\u2282");
		input=input.replace("\\subseteq " ,"\u2286");
		input=input.replace("\\superset " ,"\u2283");
		input=input.replace("\\superseteq " ,"\u2287");
		input=input.replace("\\tau " ,"\u03c4"); 
		input=input.replace("\\Tau " ,"\u03a4");
		input=input.replace("\\theta " ,"\u03b8");
		input=input.replace("\\teta " ,"\u03d1");
		input=input.replace("\\Theta " ,"\u0398");
		input=input.replace("\\times " ,"\u00D7");
		input=input.replace("\\TM " ,"\u2122"); 
		input=input.replace("\\tool " ,"\u2692");
		input=input.replace("\\uparrow " ,"\u2191"); 
		input=input.replace("\\Uparrow " ,"\u21d1");
		input=input.replace("\\updownarrow " ,"\u2195");
		input=input.replace("\\Updownarrow " ,"\u21d5");
		input=input.replace("\\upleftarrow " ,"\u2196");
		input=input.replace("\\upsilon " ,"\u03c5");
		input=input.replace("\\ypsilon " ,"\u03c5");
		input=input.replace("\\Upsilon " ,"\u03a5");
		input=input.replace("\\Ypsilon " ,"\u03a5");
		input=input.replace("\\xi " ,"\u03be");
		input=input.replace("\\Xi " ,"\u039e");
		input=input.replace("\\zeta " ,"\u03b6");
		input=input.replace("\\Zeta ", "\u0396");
		return input;
	}

}
