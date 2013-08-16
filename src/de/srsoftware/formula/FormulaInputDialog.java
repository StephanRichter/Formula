package de.srsoftware.formula;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;

import tools.srsoftware.SuggestField;
import tools.srsoftware.Tools;

/**
 * 
 * Beschreibung
 * 
 * @version 1.0 vom 28.06.2007
 * @author Stephan Richter
 */

public class FormulaInputDialog extends JDialog implements ActionListener, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Anfang Variablen
	private FormulaPanel formulaPanel = new FormulaPanel();
	private SuggestField inputTextField = new SuggestField();
	private String formula = null;
	private String oldFormula = null;
	private JButton addSum = new JButton();
	private JButton fractal = new JButton();
	private JButton product = new JButton();
	private JButton root = new JButton();
	private JButton integral = new JButton();
	private JButton cases = new JButton();
	private JButton ceiling = new JButton();
	private JButton floor = new JButton();
	private JButton small = new JButton();
	private JButton big = new JButton();
	private JButton bold = new JButton();
	private JButton italic = new JButton();
	private JButton underlined = new JButton();
	private JButton overlined = new JButton();
	private JButton subscript = new JButton();
	private JButton superscript = new JButton();
	private JButton okButton = new JButton();
	private JButton type = new JButton();
	private JButton matrix = new JButton();
	private JComboBox arrowMenu = new JComboBox();
	private static int savedX = -1;
	private static int savedY = -1;
	private static final int frameWidth = 870;
	private static final int frameHeight = 400;
	private static FormulaLanguagePack languagePack=null;

	// Ende Variablen

	private void init(String title, String text, boolean modal) {
		if (Tools.language.equals("English")){
			languagePack=new FormulaLanguagePack_English(); // auch verwendet in StarTreePanel und MindmapNode; FormulaInputDialog verwendet FormulaLanguagePack 
		} else languagePack = new FormulaLanguagePack_German(); // auch verwendet in StarTreePanel und MindmapNode; FormulaInputDialog verwendet FormulaLanguagePack

		oldFormula = text;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				dispose(false);
			}
		});
		setSize(frameWidth, frameHeight);
		if ((savedX < 0) && (savedY < 0)) {
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			savedX = (d.width - getSize().width) / 2;
			savedY = (d.height - getSize().height) / 2;
		}
		setLocation(savedX, savedY);
		Container cp = getContentPane();
		cp.setLayout(null);
		// Anfang Komponenten

		formulaPanel.setBounds(0, 32, frameWidth-10, frameHeight-170);
		formulaPanel.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 15));
		formulaPanel.setForeground(Color.black);
		formulaPanel.setBackground(Color.white);
		cp.add(formulaPanel);
		inputTextField.setBounds(0, 0, 849, 27);
		inputTextField.setEditable(true);
		inputTextField.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 17));
		inputTextField.setForeground(Color.black);
		inputTextField.setBackground(Color.white);
		inputTextField.setText((text == null) ? "" : text);
		if (text != null) inputTextField.selectAll();
		inputTextField.addKeyListener(this);
		inputTextField.addActionListener(this);
		cp.add(inputTextField);
		addSum.setBounds(0, 264, 83, 25);
		addSum.setText(languagePack.SUM());
		cp.add(addSum);
		addSum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addSumActionPerformed(evt);
			}
		});

		fractal.setBounds(88, 264, 100, 25);
		fractal.setText(languagePack.FRACTION());
		cp.add(fractal);
		fractal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				fractalActionPerformed(evt);
			}
		});

		product.setBounds(0, 290, 83, 25);
		product.setText(languagePack.PRODUCT());
		cp.add(product);
		product.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				productActionPerformed(evt);
			}
		});

		root.setBounds(88, 290, 100, 25);
		root.setText(languagePack.ROOT());
		cp.add(root);
		root.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				rootActionPerformed(evt);
			}
		});
		
		small.setBounds(88, 316, 100, 25);
		small.setText(languagePack.SMALL());
		cp.add(small);
		small.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				smallActionPerformed(evt);
			}
		});
		
		big.setBounds(88, 342, 100, 25);
		big.setText(languagePack.BIG());
		cp.add(big);
		big.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				bigActionPerformed(evt);
			}
		});

		integral.setBounds(0, 316, 83, 25);
		integral.setText(languagePack.INTEGRAL());
		cp.add(integral);
		integral.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				integralActionPerformed(evt);
			}
		});

		cases.setBounds(193, 264, 157, 25);
		cases.setText(languagePack.CASE_DIST());
		cp.add(cases);
		cases.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				casesActionPerformed(evt);
			}
		});

		ceiling.setBounds(355, 264, 85, 25);
		ceiling.setText(languagePack.CEILING());
		cp.add(ceiling);
		ceiling.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ceilingActionPerformed(evt);
			}
		});

		floor.setBounds(355, 290, 85, 25);
		floor.setText(languagePack.FLOOR());
		cp.add(floor);
		floor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				floorActionPerformed(evt);
			}
		});
		
		bold.setBounds(455, 264, 125, 25);
		bold.setText(languagePack.BOLD());
		cp.add(bold);
		bold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				boldActionPerformed(evt);
			}
		});

		italic.setBounds(455, 290, 125, 25);
		italic.setText(languagePack.ITALIC());
		cp.add(italic);
		italic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				italicActionPerformed(evt);
			}
		});

		underlined.setBounds(455, 316, 125, 25);
		underlined.setText(languagePack.UNDERLINED());
		cp.add(underlined);
		underlined.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				underlinedActionPerformed(evt);
			}
		});

		overlined.setBounds(455, 342, 125, 25);
		overlined.setText(languagePack.OVERLINED());
		cp.add(overlined);
		overlined.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				overlinedActionPerformed(evt);
			}
		});

		subscript.setBounds(193, 316, 157, 25);
		subscript.setText(languagePack.SUBSCRIPT());
		cp.add(subscript);
		subscript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				subscriptActionPerformed(evt);
			}
		});

		superscript.setBounds(193, 342, 157, 25);
		superscript.setText(languagePack.SUPERSCRIPT());
		cp.add(superscript);
		superscript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				superscriptActionPerformed(evt);
			}
		});

		type.setBounds(355, 342, 95, 25);
		type.setText(languagePack.TYPED());
		cp.add(type);
		type.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				typeActionPerformed(evt);
			}
		});

		matrix.setBounds(0, 342, 83, 25);
		matrix.setText(languagePack.MATRIX());
		cp.add(matrix);
		matrix.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				matrixActionPerformed(evt);
			}
		});		
		okButton.setBounds(707, 342, 83, 25);
		okButton.setText(languagePack.OK());
		cp.add(okButton);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});

		arrowMenu.setBounds(590, 264, 200, 25);
		arrowMenu.addItem(languagePack.ARROWS());
		arrowMenu.addItem("\u2190 (<-)");
		arrowMenu.addItem("\u21D0 (<=)");
		arrowMenu.addItem("\u2194 (<->)");
		arrowMenu.addItem("\u21D4 (<=>)");
		arrowMenu.addItem("\u2193 (downarrow)");
		arrowMenu.addItem("\u21D3 (Downarrow)");
		arrowMenu.addItem("\u2192 (->)");
		arrowMenu.addItem("\u21D2 (=>)");
		arrowMenu.addItem("\u2191 (uparrow)");
		arrowMenu.addItem("\u21D1 (Uparrow)");
		arrowMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = arrowMenu.getSelectedItem().toString();
				if (s.contains("(")) insertText('\\' + s.substring(s.indexOf('(') + 1, s.indexOf(')')) + ' ');
			}
		});
		cp.add(arrowMenu);
		// Ende Komponenten

		setResizable(false);
	}

	protected void bigActionPerformed(ActionEvent evt) {
		format("big");		
	}

	protected void smallActionPerformed(ActionEvent evt) {
		format("small");
	}

	protected void okButtonActionPerformed(ActionEvent evt) {
		dispose(true);
	}

	private FormulaInputDialog(JFrame owner, String title, boolean modal) {
		// Dialog-Initialisierung
		super(owner, title, modal);
		init(title, null, modal);
	}

	private FormulaInputDialog(JDialog owner, String title, boolean modal) {
		// Dialog-Initialisierung
		super(owner, title, modal);
		init(title, null, modal);
	}

	private FormulaInputDialog(JFrame owner, String title, String text, boolean modal) {
		// Dialog-Initialisierung
		super(owner, title, modal);
		init(title, text, modal);
	}

	private FormulaInputDialog(JDialog owner, String title, String text, boolean modal) {
		// Dialog-Initialisierung
		super(owner, title, modal);
		init(title, text, modal);
	}

	// Anfang Ereignisprozeduren
	public void actionPerformed(ActionEvent e) {
		dispose(true);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}
	
	public void dispose(boolean save){
		savedX=this.getX();
		savedY=this.getY();
		if (!save) formula=oldFormula;
		super.dispose();
	}

	public void keyReleased(KeyEvent e) {
		setFormula(inputTextField.getText());
		if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			formula = oldFormula;
			dispose(false);
		}
	}

	private static String readInput(JDialog owner, String title) {
		FormulaInputDialog fp = new FormulaInputDialog(owner, title, true);
		fp.setVisible(true);
		return fp.getResult();
	}

	private static String readInput(JDialog owner, String title, String code) {
		FormulaInputDialog fp = new FormulaInputDialog(owner, title, code, true);
		fp.setVisible(true);
		return fp.getResult();
	}
	
	private static void initLocation(JFrame owner){
		if (owner!=null && (savedX < 0) && (savedY < 0)){
			savedX=owner.getLocation().x+((owner.getWidth()-frameWidth)/2);
			savedY=owner.getLocation().y+((owner.getHeight()-frameHeight)/2);
		}
		
	}

	public static String readInput(JFrame owner, String title) {
		initLocation(owner);
		FormulaInputDialog fp = new FormulaInputDialog(owner, title, true);		
		fp.setVisible(true);
		return fp.getResult();
	}

	public static String readInput(JFrame owner, String title, String code) {
		initLocation(owner);
		FormulaInputDialog fp = new FormulaInputDialog(owner, title, code, true);
		fp.setVisible(true);
		
		return fp.getResult();
	}


	private void setFormula(String code) {	
		formula=code;
		formulaPanel.setFormula(code);
		repaint();
		//System.out.println("set formula to "+formula);
		inputTextField.requestFocus();
	}

	private void insertText(String tx) {
		String tmp = inputTextField.getText();
		int pos = inputTextField.getCaretPosition();
		tmp = tmp.substring(0, pos) + tx + tmp.substring(pos);
		inputTextField.setText(tmp);
		inputTextField.setCaretPosition(pos + tx.length());
		setFormula(tmp);
	}

	private void format(String code) {
		int selStart = inputTextField.getSelectionStart();
		int selEnd = inputTextField.getSelectionEnd();
		if (selStart < selEnd) {
			inputTextField.replaceSelection("\\" + code + "{" + inputTextField.getSelectedText() + "}");
			inputTextField.setSelectionStart(selStart);
			inputTextField.setSelectionEnd(selEnd + 3 + code.length());
			setFormula(inputTextField.getText());
		} else
			insertText("\\" + code + "{");
	}

	private void addSumActionPerformed(ActionEvent evt) {
		insertText("\\sum{" + readInput(this, languagePack.LOWER_SUM_BOUND()) + ",");
	}

	private void productActionPerformed(ActionEvent evt) {
		insertText("\\prod{" + readInput(this, languagePack.LOWER_PRODUCT_BOUND()) + ",");
	}

	private void fractalActionPerformed(ActionEvent evt) {
		insertText("\\frac{" + readInput(this, languagePack.ENTER_DIVIDENT()) + ",");
	}

	private void rootActionPerformed(ActionEvent evt) {
		insertText(readInput(this, languagePack.BASE_AND_EXPONENT(), "\\root{") + "}");
	}

	private void integralActionPerformed(ActionEvent evt) {
		insertText(readInput(this, languagePack.INTEGRAL_BOUNDS(), "\\integr{") + "}");
	}

	private void casesActionPerformed(ActionEvent evt) {
		insertText(readInput(this, languagePack.CASES(), "\\cases{") + "}");
	}

	private void ceilingActionPerformed(ActionEvent evt) {
		format("ceil");
	}

	private void floorActionPerformed(ActionEvent evt) {
		format("floor");
	}

	private void boldActionPerformed(ActionEvent evt) {
		format("bold");
	}

	private void italicActionPerformed(ActionEvent evt) {
		format("it");
	}

	private void underlinedActionPerformed(ActionEvent evt) {
		format("underline");
	}

	private void overlinedActionPerformed(ActionEvent evt) {
		format("overline");
	}

	private void subscriptActionPerformed(ActionEvent evt) {
		format("_");
	}

	private void superscriptActionPerformed(ActionEvent evt) {
		format("^");
	}

	private void typeActionPerformed(ActionEvent evt) {
		format("type");
	}

	private void matrixActionPerformed(ActionEvent evt) {
		format("matrix");
	}

	// Ende Ereignisprozeduren
	private String getResult() {
		return formula;
	}

}
