package de.srsoftware.formula;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.text.StyleContext.SmallAttributeSet;

public class Formula { // ------------------
	
	private final static int CENTER=1;
	private final static int LEFT=0;
	private final static int RIGHT=2;
	
	/***************************** Zeichenoperationen *****************************/

	public static void main(String[] args) {
		String code="Dies ist ein {fett \\it{krasser}} \\Alpha -Test! Mach \\underline{das} mal nach!";
		System.out.println(code);
		Formula f = new Formula(code);
		System.out.println(f.getText());
	}
	/***************************** Initialisierung ********************************/
	/****************************** Abfragen **************************************/
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

	private static String StrReplace(String source, String pattern, String news) {
		int i = source.indexOf(pattern);
		while (i > -1) {
			source = source.substring(0, i) + news + source.substring(i + pattern.length());
			i = source.indexOf(pattern);
		}
		return source;
	} // <font face=symbol></font>

	private String code;

	private int height;

	private int width;

	private boolean boxDrawn = false;

	private static BufferedImage image;
	
	private class FormulaFont{
		Color col;
		Font font;
		FontMetrics metrics;
		public FormulaFont(Color c,Font f) {
			col=c;
			font=f;
			metrics=new Canvas().getFontMetrics(font);
		}
		
		public FormulaFont(){
			this(Color.black,new Font("SansSerif",Font.PLAIN,14));
		}

		public FormulaFont bold() {
			return new FormulaFont(col, new Font(font.getFontName(), font.getStyle() | Font.BOLD, font.getSize()));
		}

		public int stringWidth(String text) {
			return metrics.stringWidth(text);
		}

		public int getHeight() {
			return metrics.getHeight();
		}

		public void applyTo(Graphics2D g) {
			g.setFont(font);
			g.setColor(col);
		}

		public FormulaFont italic() {
			return new FormulaFont(col, new Font(font.getFontName(), font.getStyle() | Font.ITALIC, font.getSize()));
		}

		public FormulaFont monospaced() {
			return new FormulaFont(col, new Font("Monospaced", font.getStyle(), font.getSize()));
		}

		public FormulaFont smaller() {
			return new FormulaFont(col, new Font(font.getFontName(), font.getStyle(), font.getSize()*3/4));
		}

		public FormulaFont bigger() {
			return new FormulaFont(col, new Font(font.getFontName(), font.getStyle(), font.getSize()*4/3));
		}		
	}

	/***************************** Konstruktor ************************************/
	public Formula(String code) {
		code = doReplacements(code);
		System.out.println();
		image=render(new StringBuffer(code),new FormulaFont());
		resetDimension();
	}
	
	private static BufferedImage render(StringBuffer code, FormulaFont font) {
		Vector<BufferedImage> parts=new Vector<BufferedImage>();
		while (code.length()>0){
			parts.add(renderLine(code, font));			
		}
		return composeColumn(parts,LEFT);
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
				case CENTER:
					g.drawImage(image, (width-image.getWidth())/2, y, null); break;
				case RIGHT:
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
		System.out.println(command);
		return renderText(code.toString(), font);
	}
	
	private static BufferedImage renderCommand(String cmd, StringBuffer code, FormulaFont font) {
		cmd=cmd.substring(1);
		if (cmd.equals("^")) return renderSuperscript(code,font);
		if (cmd.equals("_")) return renderSubscript(code,font);
		if (cmd.equals("~")) return renderTilde(code,font);
		if (cmd.equals("arrow")) return renderVector(code,font);
		if (cmd.equals("big")) return render(code,font.bigger());
		if (cmd.equals("block")) return renderBlock(code,font);
		if ((cmd.equals("bold")) || (cmd.equals("bf"))) return render(code, font.bold());
		if (cmd.equals("cases")) return renderCases(code,font);
		if (cmd.equals("cap")) return renderIntervall(code, "\u22C2", font);
		if (cmd.equals("Cap")) return renderIntervall(code, "\u22C0", font);
		if (cmd.equals("ceil")) return renderCeiling(code, font);
//		if (cmd.equals("color")) return drawColored(g, new Point(x, y), param, visible);
		if (cmd.equals("cup")) return renderIntervall(code, "\u22C3", font);
		if (cmd.equals("cup+")) return renderIntervall(code, "\u228e", font);
		if (cmd.equals("Cup")) return renderIntervall(code, "\u22c1", font);
//		if (cmd.equals("det")) return drawDeterminant(g, new Point(x, y), param, visible);
//		if (cmd.equals("dot")) return drawWithDot(g, new Point(x, y), param, visible);
		if (cmd.equals("exists")) return renderIntervall(code, "\u2203", font);
		if (cmd.equals("floor")) return renderFloor(code,font);
		if (cmd.equals("forall")) return renderIntervall(code, "\u2200", font);
		if (cmd.equals("frac")) return renderFrac(code,font);
//		if (cmd.equals("hat")) return drawWithHat(g, new Point(x, y), param, visible);
//		if (cmd.equals("index")) return drawSmaller(g, new Point(x, y + 1), "\\block{"+param+"}", visible);
		if (cmd.equals("integr")) return renderIntervall(code, "\u222B", font);
		if (cmd.equals("interv")) return renderIntervall(code,font);
		if ((cmd.equals("it")) || (cmd.equals("italic"))) return render(code, font.italic());
		if (cmd.equals("lim")) return renderBlock(" \\n lim\\n \\^{" + code + "}", font);
		if (cmd.equals("matrix")) return renderMatrix(code,font);
		if (cmd.equals("overline")) return overline(code,font);
		if (cmd.equals("prod")) return renderIntervall(code, "\u220F", font);
//		if (cmd.equals("rblock")) return drawRBlock(g, new Point(x, y), param, visible);
//		if (cmd.equals("rgb")) return drawRGBColored(g, new Point(x, y), param, visible);
		if (cmd.equals("root")) return renderRoot(code,font);
//		if (cmd.equals("set")) return drawSet(g, new Point(x, y), param, visible);
		if (cmd.equals("small")) return render(code,font.smaller());
		if (cmd.equals("strike")) return drawStriked(code,font);
		if (cmd.equals("sum")) return renderIntervall(code, "\u2211", font);
		if (cmd.equals("tilde")) return renderTilde(code,font);
		if (cmd.equals("type")) return render(code,font.monospaced());
		if (cmd.equals("underline")) return underline(code,font);
		if (cmd.equals("vector")) return renderVector(code,font);
		System.out.println(cmd+"("+code+")");
    return render(code, font);
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
			Graphics2D g=(Graphics2D) result.getGraphics();
			g.drawImage(hi, 2, 0, null);
			g.drawImage(lo, 2, h-lo.getHeight(), null);
			font.applyTo(g);
			g.drawLine(0, smallFont.getHeight()/3, 0, h-smallFont.getHeight()/2);
			return result;
		}
		if (para.size()>0){
			BufferedImage boundary = render(para.firstElement(),smallFont);
			if (boundary==null) return null;
			int h=font.getHeight()+boundary.getHeight();
			BufferedImage result = new BufferedImage(2+boundary.getWidth(), h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g=(Graphics2D) result.getGraphics();
			g.drawImage(boundary, 2, h-boundary.getHeight(), null);
			font.applyTo(g);
			g.drawLine(0, 0, 0, h);
			return result;
		}
		BufferedImage result = new BufferedImage(2, font.getHeight()+smallFont.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D) result.getGraphics();
		font.applyTo(g);
		g.drawLine(0, 0, 0, result.getHeight());
		return result;
	}
	private static BufferedImage renderRoot(StringBuffer parameters, FormulaFont font) {
		Vector<String> para=readParameters(parameters.toString());
		if (para.size()>1){
			BufferedImage exp = render(para.get(0),font.smaller());
			BufferedImage rad = render(para.get(1),font);
			
			int h=Math.max(rad.getHeight(),rad.getHeight()/2+exp.getHeight());
			BufferedImage result = new BufferedImage(rad.getWidth()+font.getHeight()/2+exp.getWidth(), h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g=(Graphics2D)result.getGraphics();
			g.drawImage(exp, 0, h-exp.getHeight()-rad.getHeight()/2, null);
			g.drawImage(rad, exp.getWidth()+font.getHeight()/2, h-rad.getHeight(), null);
			font.applyTo(g);
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
		Graphics2D g=(Graphics2D)result.getGraphics();
		g.drawImage(rad, font.getHeight()/2, h-rad.getHeight(), null);
		font.applyTo(g);
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
		Graphics2D g=(Graphics2D) result.getGraphics();
		g.drawImage(image, d/2, d/2, null);
		font.applyTo(g);
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
		Graphics2D g=(Graphics2D) result.getGraphics();
		g.drawImage(image, d/2, d/2, null);
		font.applyTo(g);
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
		Graphics2D g=(Graphics2D) result.getGraphics();
		g.drawImage(image,10,0,null);
		font.applyTo(g);		
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
		int width=Math.max(numerator.getWidth(), denominator.getWidth());
		BufferedImage result=new BufferedImage(width, numerator.getHeight()+denominator.getHeight()+1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D) result.getGraphics();
		font.applyTo(g);
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
		return composeColumn(parts,CENTER);
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
		Graphics2D g=(Graphics2D) result.getGraphics();
		g.drawImage(block, 10, 0, null);
		font.applyTo(g);
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
		Graphics2D g=(Graphics2D) result.getGraphics();
		g.drawImage(image, 0, d, null);
		font.applyTo(g);
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
	private static BufferedImage drawStriked(StringBuffer code, FormulaFont font) {
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
				System.err.println("found space delimited command '"+command+"'!");
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
	public void draw(Graphics g, int x, int y) {
		internDraw(g, new Point(x, y), true);
	}

	public void draw(Graphics g, Point pos) {
		// Zeichnet die Formel/den Text mit der linken oberen Ecke an Pos
		internDraw(g, pos, true);
	}

	public int getHeight(Graphics g) {
		if (height == -1) {
			Dimension size = internDraw(g, new Point(10, 10), false);
			height = size.height;
			width = size.width;
		}
		return height;
	}

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

	public Dimension getSize(Graphics g) {
		return new Dimension(getWidth(g), getHeight(g));
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

	public int getWidth(Graphics g) {
		if (width == -1) {
			Dimension size = internDraw(g, new Point(10, 10), false);
			height = size.height;
			width = size.width;
		}
		return width;
	}

	/***************************** Konstruktor ************************************/
	/***************************** Initialisierung ********************************/
	public void resetDimension() {
		height = -1;
		width = -1;
	}

	public String toString() {
		return code;
	}

	public String toXML() {
		return "<formula>" + this.toString() + "</formula>";
	}

	/****************************** Abfragen **************************************/
	/****************************** Hilfsoperationen ******************************/
	private String delete(String source, int pos) {
		if (pos > 0) return source.substring(0, pos - 1) + source.substring(pos + 1);
		return source.substring(pos + 1);
	}

	private Dimension drawBigger(Graphics g, Point origin, String param, boolean visible) {
		Font cf = g.getFont();
		g.setFont(new Font(cf.getFontName(), cf.getStyle(), (cf.getSize() * 4) / 3));
		Dimension result = formulaLine(g, origin.x, origin.y, param, visible);
		g.setFont(cf);
		return result;
	}

	private Dimension drawBlock(Graphics g, Point origin, String param, boolean visible) {
		int beginIndex = 0;
		int endIndex = param.indexOf(';');
		Dimension result = new Dimension(0, 0);
		if (endIndex < 0) { // Keine Trennung mit Semikolons
			endIndex = param.indexOf(',');
			if (endIndex < 0) { // �berhaupt keine Trennung
				result = formulaLine(g, origin.x, origin.y, param, visible);
			} else { // Trennung mit Kommata
				result = formulaLine(g, origin.x, origin.y, param.substring(beginIndex, endIndex), visible);
				beginIndex = endIndex + 1;
				endIndex = param.indexOf(',', beginIndex);
				while (endIndex > -1) {
					Dimension lineDim = formulaLine(g, origin.x, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
					result.width = Math.max(result.width, lineDim.width);
					result.height += lineDim.height;
					beginIndex = endIndex + 1;
					endIndex = param.indexOf(',', beginIndex);
				}
				endIndex = param.length();
				Dimension lineDim = formulaLine(g, origin.x, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
				result.width = Math.max(result.width, lineDim.width);
				result.height += lineDim.height;
			}
		} else { // Trennung mittels Semikolons
			result = formulaLine(g, origin.x, origin.y, param.substring(beginIndex, endIndex), visible);
			beginIndex = endIndex + 1;
			endIndex = param.indexOf(';', beginIndex);
			while (endIndex > -1) {
				Dimension lineDim = formulaLine(g, origin.x, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
				result.width = Math.max(result.width, lineDim.width);
				result.height += lineDim.height;
				beginIndex = endIndex + 1;
				endIndex = param.indexOf(';', beginIndex);
			}
			endIndex = param.length();
			Dimension lineDim = formulaLine(g, origin.x, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
			result.width = Math.max(result.width, lineDim.width);
			result.height += lineDim.height;
		}
		return result;
	}

	private Dimension drawBold(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt fett
		Font cf = g.getFont();
		g.setFont(new Font(cf.getFontName(), cf.getStyle() | Font.BOLD, cf.getSize()));
		Dimension result = formulaLine(g, origin.x, origin.y, param, visible);
		g.setFont(cf);
		return result;
	}

	private void drawBoxes(Graphics g, Dimension Box1, Dimension Box2, Dimension Box3, Point pos, String fS, String cmd, String tS) {
		int maxHeight = Math.max(Math.max(Box1.height, Box2.height), Box3.height);
		outText(g, pos.x, pos.y + (maxHeight - Box1.height) / 2, fS, true);
		outText(g, pos.x + Box1.width, pos.y + (maxHeight - Box2.height) / 2, cmd, true);
		formulaLine(g, pos.x + Box1.width + Box2.width, pos.y + (maxHeight - Box3.height) / 2, tS, true);
		boxDrawn = true;
	}

	private Dimension drawCases(Graphics g, Point origin, String param, boolean visible) {
		int beginIndex = 0;
		int endIndex = param.indexOf(';');
		Dimension result = new Dimension(0, 0);
		if (endIndex < 0) { // Keine Trennung mit Semikolons
			endIndex = param.indexOf(',');
			if (endIndex < 0) { // �berhaupt keine Trennung
				result = formulaLine(g, origin.x + 10, origin.y, param, visible);
			} else { // Trennung mit Kommata
				result = formulaLine(g, origin.x + 10, origin.y, param.substring(beginIndex, endIndex), visible);
				beginIndex = endIndex + 1;
				endIndex = param.indexOf(',', beginIndex);
				while (endIndex > -1) {
					Dimension lineDim = formulaLine(g, origin.x + 10, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
					result.width = Math.max(result.width, lineDim.width);
					result.height += lineDim.height;
					beginIndex = endIndex + 1;
					endIndex = param.indexOf(',', beginIndex);
				}
				endIndex = param.length();
				Dimension lineDim = formulaLine(g, origin.x + 10, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
				result.width = Math.max(result.width, lineDim.width);
				result.height += lineDim.height;
			}
		} else { // Trennung mittels Semikolons
			result = formulaLine(g, origin.x + 10, origin.y, param.substring(beginIndex, endIndex), visible);
			beginIndex = endIndex + 1;
			endIndex = param.indexOf(';', beginIndex);
			while (endIndex > -1) {
				Dimension lineDim = formulaLine(g, origin.x + 10, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
				result.width = Math.max(result.width, lineDim.width);
				result.height += lineDim.height;
				beginIndex = endIndex + 1;
				endIndex = param.indexOf(';', beginIndex);
			}
			endIndex = param.length();
			Dimension lineDim = formulaLine(g, origin.x + 10, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
			result.width = Math.max(result.width, lineDim.width);
			result.height += lineDim.height;
		}
		if (visible) {
			int h = result.height / 2;
			g.drawLine(origin.x + 5, origin.y + 5, origin.x + 10, origin.y); // /
			g.drawLine(origin.x + 5, origin.y - 5 + h, origin.x + 5, origin.y + 5); // |
			g.drawLine(origin.x, origin.y + h, origin.x + 5, origin.y - 5 + h); // /
			g.drawLine(origin.x, origin.y + h, origin.x + 5, origin.y + 5 + h); // \
			g.drawLine(origin.x + 5, origin.y + 5 + h, origin.x + 5, origin.y + result.height - 5); // |
			g.drawLine(origin.x + 5, origin.y + result.height - 5, origin.x + 10, origin.y + result.height); // \
		}
		result.width += 10;
		return result;
	}

	private Dimension drawCeil(Graphics g, Point origin, String param, boolean visible) {
		int dx = g.getFontMetrics().charWidth(' ') / 2;
		Dimension result = formulaLine(g, origin.x + 5, origin.y + 3, param, visible);
		if (visible) {
			g.drawLine(origin.x + 2, origin.y + 4, origin.x + 2, origin.y + result.height);
			g.drawLine(origin.x + 2, origin.y + 4, origin.x + dx + 2, origin.y + 4);

			g.drawLine(origin.x + 7 + result.width, origin.y + 4, origin.x + 7 + result.width, origin.y + result.height);
			g.drawLine(origin.x + 7 + result.width, origin.y + 4, origin.x + 7 + result.width - dx, origin.y + 4);
		}
		result.width += 9;
		result.height += 3;
		return result;
	}

	private Dimension drawColored(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt farbig
		Color prev = g.getColor();
		int r = 0;
		int y = 0;
		int b = 0;
		try {
			r = Integer.decode("0x" + param.substring(4, 6)).intValue();
			y = Integer.decode("0x" + param.substring(2, 4)).intValue();
			b = Integer.decode("0x" + param.substring(0, 2)).intValue();
		} catch (Exception e) {}
		g.setColor(new Color(r, y, b));
		int komma = param.indexOf(',');
		Dimension result = formulaLine(g, origin.x, origin.y, (komma > -1) ? param.substring(komma + 1) : ("\\ color{" + param), visible);
		g.setColor(prev);
		return result;
	}

	private Dimension drawDeterminant(Graphics g, Point origin, String param, boolean visible) {
		Dimension result = drawBlock(g, origin, param, false);
		int dist = 2;
		if (visible) {
			g.drawLine(origin.x + dist, origin.y + 2 * dist, origin.x + dist, origin.y + result.height - 2 * dist);
			drawBlock(g, new Point(origin.x + 2 * dist, origin.y), param, true);
			g.drawLine(origin.x + result.width + 3 * dist, origin.y + dist, origin.x + result.width + 3 * dist, origin.y + result.height - dist);
		}
		result.width += 4 * dist;
		return result;
	}

	private Dimension drawFloor(Graphics g, Point origin, String param, boolean visible) {
		int dx = g.getFontMetrics().charWidth(' ') / 2;
		Dimension result = formulaLine(g, origin.x + 5, origin.y, param, visible);
		if (visible) {
			g.drawLine(origin.x + 2, origin.y + 4, origin.x + 2, origin.y + result.height);
			g.drawLine(origin.x + 2, origin.y + result.height, origin.x + dx + 2, origin.y + result.height);

			g.drawLine(origin.x + 7 + result.width, origin.y + 4, origin.x + 7 + result.width, origin.y + result.height);
			g.drawLine(origin.x + 7 + result.width, origin.y + result.height, origin.x + 7 + result.width - dx, origin.y + result.height);
		}
		result.width += 9;
		result.height += 2;
		return result;
	}

	/** Zeichnet einen Bruch **/
	private Dimension drawFrac(Graphics g, Point origin, String param, boolean visible) {
		String[] paras = nextPara(param);
		String zs = paras[0];
		String ns = (paras.length > 1) ? paras[1] : "";

		Dimension zdim = formulaLine(g, origin, zs, false);
		Dimension ndim = formulaLine(g, origin, ns, false);
		Dimension result = new Dimension(Math.max(zdim.width, ndim.width), ndim.height + zdim.height);
		if (visible) {
			formulaLine(g, new Point(origin.x + (result.width - zdim.width) / 2, origin.y), zs, true);
			formulaLine(g, new Point(origin.x + (result.width - ndim.width) / 2, origin.y + zdim.height), ns, true);
			g.drawLine(origin.x, origin.y + zdim.height, origin.x + result.width, origin.y + zdim.height);
		}
		return result;
	}
	
	private Dimension drawHigher(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt hochgestellt
		Font cf = g.getFont();
		g.setFont(new Font(cf.getFontName(), cf.getStyle(), (cf.getSize() * 3) / 4));
		Dimension result = formulaLine(g, origin.x, origin.y - cf.getSize() / 3, param, visible);
		result.height *= 1.2;
		g.setFont(cf);
		return result;
	}

	private Dimension drawIntervall(Graphics g, Point origin, String param, boolean visible) {
		Font cf = g.getFont();
		int fontHeight = g.getFontMetrics().getHeight();

		String[] paras = nextPara(param);
		String lower = paras[0];
		if (lower.equals("")) lower = null;
		String upper = null;
		if (paras.length > 1) {
			upper = paras[1];
		}

		g.setFont(new Font(cf.getFontName(), cf.getStyle(), fontHeight * 4 / 3));

		g.setFont(cf);

		g.setFont(new Font(cf.getFontName(), cf.getStyle(), fontHeight / 2));
		Dimension upperD = formulaLine(g, origin, upper, false);
		Dimension lowerD = formulaLine(g, origin, lower, false);

		int resX = Math.max(upperD.width, lowerD.width) + 5;
		int resY = fontHeight * 3;

		if (visible) {
			formulaLine(g, origin.x + 5, origin.y, upper, true);
			formulaLine(g, origin.x + 5, origin.y + resY - upperD.height, lower, true);
			g.setFont(new Font(cf.getFontName(), cf.getStyle(), fontHeight * 4 / 3));
			g.drawLine(origin.x + 2, origin.y + fontHeight / 5, origin.x + 2, origin.y + resY - fontHeight / 5);
		}
		g.setFont(cf);
		return new Dimension(resX, resY);
	}

	/** Zum Zeichnen von Summen, Produkten und Integralen **/
	/**
	 * old code private Dimension drawIntervallSign(Graphics g, Point origin, String param, String sign,boolean visible){ if (param.equals("")) return formulaLine(g,origin,sign+param,visible);
	 * 
	 * Font cf=g.getFont(); int fontHeight=g.getFontMetrics().getHeight();
	 * 
	 * String[] paras=nextPara(param); String lower=paras[0]; if (lower.equals("")) lower=null; String upper=null; if (paras.length>1){ upper=paras[1]; } Dimension upperD=formulaLine(g,origin,upper,false); Dimension lowerD=formulaLine(g,origin,lower,false);
	 * 
	 * g.setFont(new Font(cf.getFontName(),cf.getStyle(),fontHeight*4/3)); Dimension sumD=formulaLine(g,origin,sign,false); g.setFont(cf);
	 * 
	 * int resX=Math.max(sumD.width,Math.max(upperD.width,lowerD.width)); int resY=sumD.height+upperD.height+lowerD.height;
	 * 
	 * if (visible){ formulaLine(g,origin.x+(resX-upperD.width)/2,origin.y,upper,true); formulaLine(g,origin.x+(resX-lowerD.width)/2,origin.y+upperD.height+sumD.height,lower,true); g.setFont(new Font(cf.getFontName(),cf.getStyle(),fontHeight*4/3)); formulaLine(g,origin.x+(resX-sumD.width)/2,origin.y+upperD.height,sign,true); g.setFont(cf); } return new Dimension(resX,resY); } old code
	 **/

	private Dimension drawIntervallSign(Graphics g, Point origin, String param, String sign, boolean visible) {
		if (param.equals("")) return formulaLine(g, origin, sign + param, visible);

		Font cf = g.getFont();
		int fontHeight = g.getFontMetrics().getHeight();

		String[] paras = nextPara(param);
		String lower = paras[0];
		if (lower.equals("")) lower = null;
		String upper = null;
		if (paras.length > 1) {
			upper = paras[1];
		}

		g.setFont(new Font(cf.getFontName(), cf.getStyle(), fontHeight * 4 / 3));
		Dimension sumD = formulaLine(g, origin, sign, false);

		g.setFont(cf);

		g.setFont(new Font(cf.getFontName(), cf.getStyle(), fontHeight / 2));
		Dimension upperD = formulaLine(g, origin, upper, false);
		Dimension lowerD = formulaLine(g, origin, lower, false);

		int resX = Math.max(sumD.width, Math.max(upperD.width, lowerD.width));
		int resY = sumD.height + upperD.height / 3 + lowerD.height;

		if (visible) {
			formulaLine(g, origin.x + (resX - upperD.width) / 2, origin.y, upper, true);
			formulaLine(g, origin.x + (resX - lowerD.width) / 2, origin.y + upperD.height * 1 / 4 + sumD.height, lower, true);
			g.setFont(new Font(cf.getFontName(), cf.getStyle(), fontHeight * 4 / 3));
			formulaLine(g, origin.x + (resX - sumD.width) / 2, origin.y + upperD.height * 1 / 2, sign, true);
		}
		g.setFont(cf);
		return new Dimension(resX, resY);
	}
	
	private Dimension drawItalic(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt kursiv
		Font cf = g.getFont();
		g.setFont(new Font(cf.getFontName(), cf.getStyle() | Font.ITALIC, cf.getSize()));
		Dimension result = formulaLine(g, origin.x, origin.y, param, visible);
		g.setFont(cf);
		return result;
	}

	private Dimension drawLower(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt tiefergestellt
		Font cf = g.getFont();
		int fontHeight = g.getFontMetrics().getHeight();
		g.setFont(new Font(cf.getFontName(), cf.getStyle(), (cf.getSize() * 3) / 4));
		Dimension result = formulaLine(g, origin.x, origin.y + fontHeight / 2 + cf.getSize() / 5, param, visible);
		result.height *= 1.8;
		g.setFont(cf);
		return result;
	}

	private Dimension drawMatrix(Graphics g, Point origin, String param, boolean visible) {
		int beginIndex = 0;
		int endIndex = param.indexOf(';');
		Dimension result = new Dimension(0, 0);
		if (endIndex < 0) { // Keine Trennung mit Semikolons
			endIndex = param.indexOf(',');
			if (endIndex < 0) { // �berhaupt keine Trennung
				result = formulaLine(g, origin.x + 10, origin.y, param, visible);
			} else { // Trennung mit Kommata
				result = formulaLine(g, origin.x + 10, origin.y, param.substring(beginIndex, endIndex), visible);
				beginIndex = endIndex + 1;
				endIndex = param.indexOf(',', beginIndex);
				while (endIndex > -1) {
					Dimension lineDim = formulaLine(g, origin.x + 10, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
					result.width = Math.max(result.width, lineDim.width);
					result.height += lineDim.height;
					beginIndex = endIndex + 1;
					endIndex = param.indexOf(',', beginIndex);
				}
				endIndex = param.length();
				Dimension lineDim = formulaLine(g, origin.x + 10, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
				result.width = Math.max(result.width, lineDim.width);
				result.height += lineDim.height;
			}
		} else { // Trennung mittels Semikolons
			result = formulaLine(g, origin.x + 10, origin.y, param.substring(beginIndex, endIndex), visible);
			beginIndex = endIndex + 1;
			endIndex = param.indexOf(';', beginIndex);
			while (endIndex > -1) {
				Dimension lineDim = formulaLine(g, origin.x + 10, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
				result.width = Math.max(result.width, lineDim.width);
				result.height += lineDim.height;
				beginIndex = endIndex + 1;
				endIndex = param.indexOf(';', beginIndex);
			}
			endIndex = param.length();
			Dimension lineDim = formulaLine(g, origin.x + 10, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
			result.width = Math.max(result.width, lineDim.width);
			result.height += lineDim.height;
		}
		if (visible) {
			g.drawLine(origin.x + 5, origin.y + 5, origin.x + 10, origin.y); // /
			g.drawLine(origin.x + 5, origin.y + 5, origin.x + 5, origin.y + result.height - 5); // |
			g.drawLine(origin.x + 5, origin.y + result.height - 5, origin.x + 10, origin.y + result.height); // \

			g.drawLine(origin.x + result.width + 15, origin.y + 5, origin.x + 10 + result.width, origin.y); // /
			g.drawLine(origin.x + result.width + 15, origin.y + 5, origin.x + 15 + result.width, origin.y + result.height - 5); // |
			g.drawLine(origin.x + result.width + 15, origin.y + result.height - 5, origin.x + 10 + result.width, origin.y + result.height); // \
		}
		result.width += 20;
		return result;
	}

	private Dimension drawOverlined(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt �berstrichen
		Dimension result = formulaLine(g, origin.x, origin.y, param, visible);
		if (visible) g.drawLine(origin.x, origin.y + 2, origin.x + result.width, origin.y + 2);
		result.height++;
		return result;
	}

	/** Zeichnet parametrisierte Formel-Kommandos **/
	private Dimension drawParameterFormula(Graphics g, int x, int y, String cmd, String param, boolean visible) {
		if (cmd.equals("^")) return drawHigher(g, new Point(x, y), param, visible);
		if (cmd.equals("_")) return drawLower(g, new Point(x, y), param, visible);
		if (cmd.equals("~")) return drawWithTilde(g, new Point(x, y), param, visible);
		if (cmd.equals("arrow")) return drawUnderArrow(g, new Point(x, y), param, visible);
		if (cmd.equals("big")) return drawBigger(g, new Point(x, y + 1), param, visible);
		if (cmd.equals("block")) return drawBlock(g, new Point(x, y), param, visible);
		if ((cmd.equals("bold")) || (cmd.equals("bf"))) return drawBold(g, new Point(x, y), param, visible);
		if (cmd.equals("cases")) return drawCases(g, new Point(x, y), param, visible);
		if (cmd.equals("cap")) return drawIntervallSign(g, new Point(x, y), param, "\u22C2", visible);
		if (cmd.equals("Cap")) return drawIntervallSign(g, new Point(x, y), param, "\u22C0", visible);
		if (cmd.equals("ceil")) return drawCeil(g, new Point(x, y), param, visible);
		if (cmd.equals("color")) return drawColored(g, new Point(x, y), param, visible);
		if (cmd.equals("cup")) return drawIntervallSign(g, new Point(x, y), param, "\u22C3", visible);
		if (cmd.equals("cup+")) return drawIntervallSign(g, new Point(x, y), param, "\u228e", visible);
		if (cmd.equals("Cup")) return drawIntervallSign(g, new Point(x, y), param, "\u22c1", visible);
		if (cmd.equals("det")) return drawDeterminant(g, new Point(x, y), param, visible);
		if (cmd.equals("dot")) return drawWithDot(g, new Point(x, y), param, visible);
		if (cmd.equals("exists")) return drawIntervallSign(g, new Point(x, y), param, "\u2203", visible);
		if (cmd.equals("floor")) return drawFloor(g, new Point(x, y), param, visible);
		if (cmd.equals("forall")) return drawIntervallSign(g, new Point(x, y), param, "\u2200", visible);
		if (cmd.equals("frac")) return drawFrac(g, new Point(x, y), param, visible);
		if (cmd.equals("hat")) return drawWithHat(g, new Point(x, y), param, visible);
		if (cmd.equals("index")) return drawSmaller(g, new Point(x, y + 1), "\\block{"+param+"}", visible);
		if (cmd.equals("integr")) return drawIntervallSign(g, new Point(x, y), param, "\u222B", visible);
		if (cmd.equals("interv")) return drawIntervall(g, new Point(x, y), param, visible);
		if ((cmd.equals("it")) || (cmd.equals("italic"))) return drawItalic(g, new Point(x, y), param, visible);
		if (cmd.equals("lim")) return drawBlock(g, new Point(x, y), ";lim;\\^{" + param + "}", visible);
		if (cmd.equals("matrix")) return drawMatrix(g, new Point(x, y), param, visible);
		if (cmd.equals("overline")) return drawOverlined(g, new Point(x, y), param, visible);
		if (cmd.equals("prod")) return drawIntervallSign(g, new Point(x, y), param, "\u220F", visible);
		if (cmd.equals("rblock")) return drawRBlock(g, new Point(x, y), param, visible);
		if (cmd.equals("rgb")) return drawRGBColored(g, new Point(x, y), param, visible);
		if (cmd.equals("root")) return drawRoot(g, new Point(x, y), param, visible);
		if (cmd.equals("set")) return drawSet(g, new Point(x, y), param, visible);
		if (cmd.equals("small")) return drawSmaller(g, new Point(x, y + 1), param, visible);
		if (cmd.equals("strike")) return drawStriked(g, new Point(x, y), param, visible);
		if (cmd.equals("sum")) return drawIntervallSign(g, new Point(x, y), param, "\u2211", visible);
		if (cmd.equals("tilde")) return drawWithTilde(g, new Point(x, y), param, visible);
		if (cmd.equals("type")) return drawTyped(g, new Point(x, y), param, visible);
		if (cmd.equals("underline")) return drawUnderlined(g, new Point(x, y), param, visible);
		if (cmd.equals("vector")) return drawVector(g, new Point(x, y), param, visible);
		return formulaLine(g, x, y, "\\ " + cmd + "{" + param + "}", visible);
	}

	private Dimension drawRBlock(Graphics g, Point origin, String param, boolean visible) {		
		int width=drawBlock(g, origin, param, false).width;
		int beginIndex = 0;
		int endIndex = param.indexOf(';');
		Dimension result = new Dimension(0, 0);
		if (endIndex < 0) { // Keine Trennung mit Semikolons
			endIndex = param.indexOf(',');
			if (endIndex < 0) { // �berhaupt keine Trennung
				int w=formulaLine(g, origin, param, false).width;
				result = formulaLine(g, width-w+origin.x, origin.y, param, visible);
			} else { // Trennung mit Kommata
				int w=formulaLine(g, origin, param.substring(beginIndex, endIndex), false).width;
				result = formulaLine(g, width-w+origin.x, origin.y, param.substring(beginIndex, endIndex), visible);
				beginIndex = endIndex + 1;
				endIndex = param.indexOf(',', beginIndex);
				while (endIndex > -1) {
					w=formulaLine(g, origin, param.substring(beginIndex, endIndex), false).width;
					Dimension lineDim = formulaLine(g, width-w+origin.x, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
					result.width = Math.max(result.width, lineDim.width);
					result.height += lineDim.height;
					beginIndex = endIndex + 1;
					endIndex = param.indexOf(',', beginIndex);
				}
				endIndex = param.length();
				w=formulaLine(g, origin, param.substring(beginIndex, endIndex), false).width;
				Dimension lineDim = formulaLine(g, width-w+origin.x, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
				result.width = Math.max(result.width, lineDim.width);
				result.height += lineDim.height;
			}
		} else { // Trennung mittels Semikolons
			int w=formulaLine(g, origin, param.substring(beginIndex, endIndex), false).width;
			result = formulaLine(g, width-w+origin.x, origin.y, param.substring(beginIndex, endIndex), visible);
			beginIndex = endIndex + 1;
			endIndex = param.indexOf(';', beginIndex);
			while (endIndex > -1) {
				w=formulaLine(g, origin, param.substring(beginIndex, endIndex), false).width;
				Dimension lineDim = formulaLine(g, width-w+origin.x, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
				result.width = Math.max(result.width, lineDim.width);
				result.height += lineDim.height;
				beginIndex = endIndex + 1;
				endIndex = param.indexOf(';', beginIndex);
			}
			endIndex = param.length();
			w=formulaLine(g, origin, param.substring(beginIndex, endIndex), false).width;
			Dimension lineDim = formulaLine(g, width-w+origin.x, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
			result.width = Math.max(result.width, lineDim.width);
			result.height += lineDim.height;
		}
		return result;
	}

	private Dimension drawRGBColored(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt farbig
		Color prev = g.getColor();
		int r = 0;
		int y = 0;
		int b = 0;
		try {
			r = Integer.decode("0x" + param.substring(0, 2)).intValue();
			y = Integer.decode("0x" + param.substring(2, 4)).intValue();
			b = Integer.decode("0x" + param.substring(4, 6)).intValue();
		} catch (Exception e) {}
		g.setColor(new Color(r, y, b));
		int komma = param.indexOf(',');
		Dimension result = formulaLine(g, origin.x, origin.y, (komma > -1) ? param.substring(komma + 1) : ("\\ rgb{" + param), visible);
		g.setColor(prev);
		return result;
	}

	/** Zeichnet eine Wurzel **/
	private Dimension drawRoot(Graphics g, Point origin, String param, boolean visible) {
		String[] paras = nextPara(param);
		String rad = paras[0];
		String exp = " ";
		if (paras.length > 1) { // Bruch mit explizit angegebenem Exponent
			exp = rad;
			rad = paras[1];
		}
		Dimension radD = formulaLine(g, origin, rad, false);
		Font cf = g.getFont();
		g.setFont(new Font(cf.getFontName(), cf.getStyle(), (cf.getSize() * 3) / 4));

		Dimension expD = formulaLine(g, origin, exp, false);
		Point expP = new Point(origin);
		Point radP = new Point(origin.x + 5 + expD.width, origin.y);
		if (expD.height < (radD.height / 2)) {
			expP.y = origin.y + (radD.height / 2) - expD.height;
		} else {
			radP.y = origin.y + expD.height - (radD.height / 2);
		}
		if (visible) {
			formulaLine(g, expP, exp, true);
			g.setFont(cf);
			formulaLine(g, radP, rad, true);
			g.drawLine(expP.x, expP.y + expD.height, expP.x + expD.width, expP.y + expD.height);
			g.drawLine(expP.x + expD.width, expP.y + expD.height, radP.x - 3, radP.y + radD.height - 2);
			g.drawLine(radP.x - 3, radP.y + radD.height - 2, radP.x, radP.y + 2);
			g.drawLine(radP.x, radP.y + 2, radP.x + radD.width, radP.y + 2);
		} else
			g.setFont(cf);

		return new Dimension(radD.width + 5 + expD.width, Math.max(radD.height, expD.height + radD.height / 2));
	}

	private Dimension drawSet(Graphics g, Point origin, String param, boolean visible) {
		int beginIndex = 0;
		int endIndex = param.indexOf(';');
		Dimension result = new Dimension(0, 0);
		Dimension lineDim=new Dimension();
		if (endIndex < 0) { // Keine Trennung mit Semikolons
			endIndex = param.indexOf(',');
			
			if (endIndex < 0) { // �berhaupt keine Trennung
				result = formulaLine(g, origin.x + 10, origin.y, param, visible);
			} else { // Trennung mit Kommata
				result = formulaLine(g, origin.x + 10, origin.y, param.substring(beginIndex, endIndex), visible);
				beginIndex = endIndex + 1;
				endIndex = param.indexOf(',', beginIndex);
				while (endIndex > -1) {
					lineDim = formulaLine(g, origin.x + 10, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
					result.width = Math.max(result.width, lineDim.width);
					result.height += lineDim.height;
					beginIndex = endIndex + 1;
					endIndex = param.indexOf(',', beginIndex);
				}
				endIndex = param.length();
				lineDim = formulaLine(g, origin.x + 10, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
			}
		} else { // Trennung mittels Semikolons
			result = formulaLine(g, origin.x + 10, origin.y, param.substring(beginIndex, endIndex), visible);
			beginIndex = endIndex + 1;
			endIndex = param.indexOf(';', beginIndex);
			while (endIndex > -1) {
				lineDim = formulaLine(g, origin.x + 10, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
				result.width = Math.max(result.width, lineDim.width);
				result.height += lineDim.height;
				beginIndex = endIndex + 1;
				endIndex = param.indexOf(';', beginIndex);
			}
			endIndex = param.length();
			lineDim = formulaLine(g, origin.x + 10, origin.y + result.height, param.substring(beginIndex, endIndex), visible);
		}
		result.width = Math.max(result.width, lineDim.width)+10;
		result.height += lineDim.height;

		if (visible) {
			int h = result.height / 2;
			g.drawLine(origin.x + 5, origin.y + 5, origin.x + 10, origin.y); // /
			g.drawLine(origin.x + 5, origin.y - 5 + h, origin.x + 5, origin.y + 5); // |
			g.drawLine(origin.x, origin.y + h, origin.x + 5, origin.y - 5 + h); // /
			g.drawLine(origin.x, origin.y + h, origin.x + 5, origin.y + 5 + h); // \
			g.drawLine(origin.x + 5, origin.y + 5 + h, origin.x + 5, origin.y + result.height - 5); // |
			g.drawLine(origin.x + 5, origin.y + result.height - 5, origin.x + 10, origin.y + result.height); // \
			
			origin.x+=result.width;

			g.drawLine(origin.x +5, origin.y + 5, origin.x , origin.y); // /
			g.drawLine(origin.x +5, origin.y - 5 + h, origin.x +5, origin.y + 5); // |
			g.drawLine(origin.x + 10, origin.y + h, origin.x +5, origin.y - 5 + h); // /
			g.drawLine(origin.x +10, origin.y + h, origin.x +5, origin.y + 5 + h); // \
			g.drawLine(origin.x +5, origin.y + 5 + h, origin.x +5, origin.y + result.height - 5); // |
			g.drawLine(origin.x +5, origin.y + result.height - 5, origin.x, origin.y + result.height); // \

		}
		result.width += 10;
		return result;
	}

	private Dimension drawSmaller(Graphics g, Point origin, String param, boolean visible) {
		Font cf = g.getFont();
		g.setFont(new Font(cf.getFontName(), cf.getStyle(), (cf.getSize() * 3) / 4));
		Dimension result = formulaLine(g, origin.x, origin.y, param, visible);
		g.setFont(cf);
		return result;
	}

	private Dimension drawStriked(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt unterstrichen
		Dimension result = formulaLine(g, origin.x, origin.y, param, visible);
		if (visible) g.drawLine(origin.x, origin.y + result.height/2, origin.x + result.width, origin.y + result.height/2);
		result.height++;
		return result;
	}

	private Dimension drawTyped(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt typisiert
		Font cf = g.getFont();
		g.setFont(new Font("Monospaced", cf.getStyle(), cf.getSize()));
		Dimension result = formulaLine(g, origin.x, origin.y, param, visible);
		g.setFont(cf);
		return result;
	}

	private Dimension drawUnderArrow(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt �berstrichen
		Dimension result = formulaLine(g, origin.x, origin.y + 3, param, visible);
		if (visible) {
			g.drawLine(origin.x, origin.y + 5, origin.x + result.width, origin.y + 5);
			g.drawLine(origin.x + result.width, origin.y + 5, origin.x + result.width - 5, origin.y + 1);
			g.drawLine(origin.x + result.width, origin.y + 5, origin.x + result.width - 5, origin.y + 9);
		}
		result.height += 3;
		return result;
	}

	private Dimension drawUnderlined(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt unterstrichen
		Dimension result = formulaLine(g, origin.x, origin.y, param, visible);
		if (visible) g.drawLine(origin.x, origin.y + result.height, origin.x + result.width, origin.y + result.height);
		result.height++;
		return result;
	}

	private Dimension drawVector(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt �berstrichen
		Dimension result = formulaLine(g, origin.x, origin.y, param, visible);
		int s = g.getFont().getSize() / 6;

		if (visible) {
			g.drawLine(origin.x, origin.y + s, origin.x + result.width, origin.y + s);
			g.drawLine(origin.x + result.width - s, origin.y, origin.x + result.width, origin.y + s);
			g.drawLine(origin.x + result.width - s, origin.y + s + s, origin.x + result.width, origin.y + s);
		}
		result.height++;
		return result;
	}

	private Dimension drawWithDot(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt �berstrichen
		Dimension result = formulaLine(g, origin.x, origin.y, param, visible);
		if (visible) {
			g.drawOval(origin.x + result.width / 2 - 2, origin.y + 1, 3, 3);
			// g.drawLine(origin.x,origin.y+2,origin.x+result.width/2,origin.y);
			// g.drawLine(origin.x+result.width/2,origin.y,origin.x+result.width,origin.y+2);
		}
		result.height++;
		return result;
	}
	
	private Dimension drawWithHat(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt �berstrichen
		Dimension result = formulaLine(g, origin.x, origin.y, param, visible);
		if (visible) {
			g.drawLine(origin.x, origin.y + 2, origin.x + result.width / 2, origin.y);
			g.drawLine(origin.x + result.width / 2, origin.y, origin.x + result.width, origin.y + 2);
		}
		result.height++;
		return result;
	}

	private Dimension drawWithTilde(Graphics g, Point origin, String param, boolean visible) {
		// Zeichnet den �bergebenen Abschnitt �berstrichen
		int i = g.getFont().getSize() / 2;
		Dimension dummy = formulaLine(g, origin.x, origin.y - i, "~", visible);
		Dimension result = formulaLine(g, origin.x, origin.y + dummy.height / 6, param, visible);
		result.height = result.height + dummy.height / 3;
		return result;
	}

	/** Zeichnet eine Formel-Zeile **/
	private Dimension formulaLine(Graphics g, int x, int y, String code, boolean visible) {
		// Zeichnet eine Formel-Zeile
		return formulaLine(g, new Point(x, y), code, visible);
	}
	
	/** Zeichnet eine Formel-Zeile **/
	private Dimension formulaLine(Graphics g, Point origin, String code, boolean visible) {
		if ((code == null) || (code.equals(" "))) return new Dimension(0, 0);
		int escapeIndex = code.indexOf('\\');
		if (escapeIndex == -1) return outText(g, origin.x, origin.y, code, visible);// code enth�lt keine Kommandos
		// ab hier: Behandlung von Codes mit Kommandos
		Point pos = new Point(origin);
		String firstString = code.substring(0, escapeIndex);
		Dimension firstBox = new Dimension(0, 0);
		if (escapeIndex > 0) firstBox = outText(g, pos.x, pos.y, firstString, false); // Ausma�e der ersten Box berechnen
		Dimension secondBox = new Dimension(0, 0);
		Dimension thirdBox = new Dimension(0, 0);

		int cmdEndIndex = escapeIndex; // escapeIndex enth�lt die Position des leading "\"
		int codeLength = code.length(); // l�nge des gesamten Codes
		while ((cmdEndIndex < codeLength) && (code.charAt(cmdEndIndex) != '{') && (code.charAt(cmdEndIndex) != ' '))
			cmdEndIndex++;
		// cmdEndIndex enth�lt ab hier das Ende des comando-Strings
		String cmd = code.substring(escapeIndex + 1, cmdEndIndex);
		if (cmdEndIndex < codeLength) {
			if (code.charAt(cmdEndIndex) == ' ') { // parameterloses Kommando
				String thirdString = code.substring(cmdEndIndex + 1);
				if (cmdEndIndex + 1 < code.length()) thirdBox = formulaLine(g, origin, thirdString, false);
				boxDrawn = !visible;
				if (cmd.equals("##")) {
					secondBox = outText(g, pos.x, pos.y, "\\##", false);
					if (visible) drawBoxes(g, firstBox, secondBox, thirdBox, pos, firstString, "\\##", thirdString);
				}
				if (cmd.equals("eye")) {
					secondBox = outText(g, pos.x, pos.y, "OO", false);
					if (visible) {
						int s = g.getFont().getSize();
						outText(g, pos.x + firstBox.width + s / 4, pos.y + s / 5 + (g.getFont().getSize() - secondBox.height) / 2, "\u2219 \u2219", true);
						drawBoxes(g, firstBox, secondBox, thirdBox, pos, firstString, "OO", thirdString);
					}
				}
				if (cmd.equals("nokbox")) {
					secondBox = outText(g, pos.x, pos.y, "\u25a1", false);
					if (visible) drawBoxes(g, firstBox, secondBox, thirdBox, pos, firstString, "\u2717", thirdString);
					if (visible) drawBoxes(g, firstBox, secondBox, thirdBox, pos, firstString, "\u25a1", thirdString);
				}
				if (cmd.equals("okbox")) {
					secondBox = outText(g, pos.x, pos.y, "\u25a1", false);
					if (visible) drawBoxes(g, firstBox, secondBox, thirdBox, pos, firstString, "\u2713", thirdString);
					if (visible) drawBoxes(g, firstBox, secondBox, thirdBox, pos, firstString, "\u25a1", thirdString);
				}

				if (!boxDrawn) {
					secondBox = outText(g, pos.x, pos.y, "\\" + cmd, false);
					if (visible) drawBoxes(g, firstBox, secondBox, thirdBox, pos, firstString, "\\" + cmd, thirdString);
				}
			} else { // parametrisiertes Kommando
				int paramEndIndex = cmdEndIndex + 1;
				int bracketCount = 1;
				if (code.charAt(cmdEndIndex) == '{') {
					while ((paramEndIndex < codeLength) && (bracketCount > 0)) {
						switch (code.charAt(paramEndIndex)) {
						case '}': {
							bracketCount--;
							break;
						}
						case '{': {
							bracketCount++;
							break;
						}
						}
						paramEndIndex++;
					}
				}
				if ((1 + cmdEndIndex) >= codeLength) cmdEndIndex--;
				paramEndIndex--;
				String param = code.substring(cmdEndIndex + 1, paramEndIndex);

				String thirdString = null;
				if (paramEndIndex < codeLength - 1) {
					thirdString = code.substring(paramEndIndex + 1);
					thirdBox = formulaLine(g, origin, thirdString, false);
				}
				secondBox = drawParameterFormula(g, 10, 10, cmd, param, false);
				if (visible) {
					int maxHeight = Math.max(firstBox.height, Math.max(secondBox.height, thirdBox.height));
					outText(g, origin.x, origin.y + (maxHeight - firstBox.height) / 2, firstString, true);
					drawParameterFormula(g, pos.x + firstBox.width, pos.y + (maxHeight - secondBox.height) / 2, cmd, param, true);
					if (thirdString != null) formulaLine(g, pos.x + firstBox.width + secondBox.width, pos.y + (maxHeight - thirdBox.height) / 2, thirdString, true);
				}
			}
		}
		Dimension result = new Dimension(firstBox);
		result.width += secondBox.width + thirdBox.width;
		result.height = Math.max(result.height, secondBox.height);
		result.height = Math.max(result.height, thirdBox.height);
		return result;
	}

	private Dimension internDraw(Graphics g, Point pos, boolean visible) {
		// Zeichnet die Formel, linke Obere Ecke = pos
		// pos.y+=(5*g.getFontMetrics().getHeight())/6;
		if ((code == null) || (code.equals(" "))) return new Dimension(0, 0);
		int i = code.indexOf("\\n "); // Test, ob Zeilenumbr�che vorhanden
		if (i > -1) { // Zeilenumbruch vorhanden
			int j = 0;
			Dimension result = new Dimension(0, 0);
			while (i > -1) {
				Dimension lineDim = formulaLine(g, pos, code.substring(j, i), visible);
				result.height += lineDim.height;
				result.width = Math.max(lineDim.width, result.width);
				pos.y += lineDim.height;
				j = i + 3;
				i = code.indexOf("\\n ", j);
			}
			Dimension lineDim = formulaLine(g, pos, code.substring(j), visible);
			result.height += lineDim.height;
			result.width = Math.max(lineDim.width, result.width);
			return result;
		} else
			/* kein Zeilenumbruch vorhanden */return formulaLine(g, pos, code, visible);
	}

	private String[] nextPara(String inp) {
		int len = inp.length();
		int komma = Integer.MAX_VALUE;
		int semikolon = Integer.MAX_VALUE;
		int i = len - 1;
		int bracket = 0;
		while (i >= 0) {
			switch (inp.charAt(i)) {
			case '{': {
				bracket++;
				break;
			}
			case '}': {
				bracket--;
				break;
			}
			case ',': {
				if (bracket == 0) komma = i;
				break;
			}
			case ';': {
				if (bracket == 0) semikolon = i;
				break;
			}
			}
			i--;
		}
		int separator = (semikolon < Integer.MAX_VALUE) ? semikolon : komma;
		String[] result = { inp };
		if (separator < Integer.MAX_VALUE) {
			String[] dummy = { inp.substring(0, separator), inp.substring(separator + 1) };
			result = dummy;
		}
		return result;
	}

	/****************************** Hilfsoperationen ******************************/
	/***************************** Zeichenoperationen *****************************/
	private Dimension outText(Graphics g, int x, int y, String tx, boolean visible) {
		FontMetrics fm = g.getFontMetrics();
		int fontHeight = fm.getHeight();
		y += (5 * fontHeight) / 6;
		int breakIndex = tx.indexOf("##");
		while ((breakIndex > 0) && (tx.charAt(breakIndex - 1) == '\\')) {
			tx = delete(tx, breakIndex - 1);
			breakIndex = tx.indexOf("##", breakIndex + 1);
		}
		if (breakIndex == -1) { // tx enthält keine einfachen Zeilenumbrüche
			if (visible) {
				g.drawString(tx, x, y);
			}
			return new Dimension(fm.stringWidth(tx), fm.getHeight());
		} else { // tx enthält einfache Zeilenumbrüche
			Dimension result = new Dimension(0, 0);
			int j = 0;
			while (breakIndex != -1) {
				String outTx = tx.substring(j, breakIndex);
				if (visible) g.drawString(outTx, x, y);
				y += fontHeight;
				result.height += fontHeight;
				result.width = Math.max(fm.stringWidth(outTx), result.width);
				j = breakIndex + 2;
				breakIndex = tx.indexOf("##", j);
			}
			String outTx = tx.substring(j);
			if (visible) g.drawString(outTx, x, y);
			y += fontHeight;
			result.height += fontHeight;
			result.width = Math.max(fm.stringWidth(outTx), result.width);
			return result;
		}
	}

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
		
	private static BufferedImage renderText(String text, FormulaFont font){
		if (text==null || text.isEmpty()) return null;
		int width=font.stringWidth(text);
		int height=font.getHeight();
		BufferedImage result=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = result.createGraphics();
		font.applyTo(g);
		g.drawString(text, 0,height*3/4);
		return result;
	}
	
	public BufferedImage image(){
		return image;
	}
}
