package de.srsoftware.formula;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * @author srichter
 * This class is used to set font settings during rendering
 */
public class FormulaFont{
	
	final static int CENTER=1;
	public final static int LEFT=0;
	final static int RIGHT=2;
	
	Color col;
	Font font;
	FontMetrics metrics;
	int align;
	
	public FormulaFont(Color c,Font f,int align) {
		this.align=align;
		col=c;
		font=f;
		metrics=new Canvas().getFontMetrics(font);
	}
	
	public FormulaFont(Color c,Font f) {
		this(c,f,LEFT);
	}

	public FormulaFont(Font font) {
		this(Color.black,font);
	}

	public FormulaFont bold() {
		return new FormulaFont(col, new Font(font.getFontName(), font.getStyle() | Font.BOLD, font.getSize()),align);
	}

	public int stringWidth(String text) {
		return metrics.stringWidth(text);
	}

	public int getHeight() {
		return metrics.getHeight();
	}

	public FormulaFont italic() {
		return new FormulaFont(col, new Font(font.getFontName(), font.getStyle() | Font.ITALIC, font.getSize()),align);
	}

	public FormulaFont monospaced() {
		return new FormulaFont(col, new Font("Monospaced", font.getStyle(), font.getSize()),align);
	}

	public FormulaFont smaller() {
		return new FormulaFont(col, new Font(font.getFontName(), font.getStyle(), font.getSize()*3/4),align);
	}

	public FormulaFont bigger() {
		return new FormulaFont(col, new Font(font.getFontName(), font.getStyle(), font.getSize()*4/3),align);
	}

	public FormulaFont color(Color color) {
		return new FormulaFont(color, new Font(font.getFontName(), font.getStyle(), font.getSize()),align);
	}
	public FormulaFont rightAligned() {
    return new FormulaFont(col, font,RIGHT);
  }

	public FormulaFont leftAligned() {
		return new FormulaFont(col, font,LEFT);
  }		

	public FormulaFont centered() {
		return new FormulaFont(col, font,CENTER);
  }
	
	public FormulaFont scale(float factor){
		Font newFont = font.deriveFont(font.getSize2D()*factor);
		return new FormulaFont(col, newFont, align);
	}
	
	public FormulaFont withSize(float size){
		Font newFont = font.deriveFont(size);
		System.out.println(newFont.getSize2D());
		return new FormulaFont(col, newFont, align);		
	}
	
	public void applyTo(Graphics2D g) {
		g.setFont(font);
		g.setColor(col);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FormulaFont) {
			FormulaFont otherFont = (FormulaFont) obj;
			if (otherFont.align!=align) return false;
			if (!otherFont.col.equals(col)) return false;
			if (!otherFont.font.equals(font)) return false;
			return true;
		}
		return false;
	}
}
