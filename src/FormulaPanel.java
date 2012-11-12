import java.awt.*;
import javax.swing.*;
public class FormulaPanel extends JPanel{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Formula formula=null;
  public FormulaPanel(){
    super();
  }
  public FormulaPanel(String code){
    super();
    setFormula(code);
  }
  public void setFormula(String code){
    formula=new Formula(code);
  }
  public void paint(Graphics g){
    super.paint(g);
    if (formula!=null){
      formula.draw(g,new Point(20,20));
    }
  }
  public String getFormula(){
    return formula.toString();
  }
  
  public static void main(String[] args){
  }
}
