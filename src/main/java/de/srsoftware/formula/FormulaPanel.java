package de.srsoftware.formula;

import java.awt.*;
import javax.swing.*;
public class FormulaPanel extends JPanel{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static void main(String[] args){
  }
  private Formula formula=null;
  public FormulaPanel(){
    super();
  }
  public FormulaPanel(String code){
    super();
    setFormula(code);
  }
  public String getFormula(){
    return formula.toString();
  }
  public void paint(Graphics g){
    super.paint(g);
    if (formula!=null){
      g.drawImage(formula.image(new FormulaFont(g.getFont())), 0, 0, null);
    }
  }
  
  public void setFormula(String code){
    formula=new Formula(code);
  }
}
