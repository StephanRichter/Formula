package de.srsoftware.formula;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.keawe.tools.translations.Translation;
import de.srsoftware.tools.panels.HorizontalPanel;
import de.srsoftware.tools.gui.SuggestField;
import de.srsoftware.tools.panels.VerticalPanel;

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

	private static void initLocation(JFrame owner) {
		if (owner != null && (savedX < 0) && (savedY < 0)) {
			savedX = owner.getLocation().x + ((owner.getWidth() - frameWidth) / 2);
			savedY = owner.getLocation().y + ((owner.getHeight() - frameHeight) / 2);
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

	// Anfang Variablen
	private FormulaPanel formulaPanel;
	private SuggestField inputTextField;
	private String formula = null;
	private String oldFormula = null;
	private JButton addSum, product, integral, matrix;
	private JButton fraction, root, small, big;
	private JButton cases, subscript, superscript;
	private JButton ceiling, floor, type;
	private JButton bold, italic, underlined, overlined;
	private JButton okButton;

	// Ende Variablen

	private JComboBox<String> arrowMenu;

	private static int savedX = -1;

	private static int savedY = -1;

	private static final int frameWidth = 800;

	private static final int frameHeight = 400;

	private static String t(String text) {
		return Translation.get(Formula.class, text);
	}

	private FormulaInputDialog(JDialog owner, String title, boolean modal) {
		// Dialog-Initialisierung
		super(owner, title, modal);
		init(title, null, modal);
	}

	private FormulaInputDialog(JDialog owner, String title, String text, boolean modal) {
		// Dialog-Initialisierung
		super(owner, title, modal);
		init(title, text, modal);
	}

	private FormulaInputDialog(JFrame owner, String title, boolean modal) {
		// Dialog-Initialisierung
		super(owner, title, modal);
		init(title, null, modal);
	}

	private FormulaInputDialog(JFrame owner, String title, String text, boolean modal) {
		// Dialog-Initialisierung
		super(owner, title, modal);
		init(title, text, modal);
	}

	// Anfang Ereignisprozeduren
	public void actionPerformed(ActionEvent e) {
		dispose(true);
	}

	public void dispose(boolean save) {
		savedX = this.getX();
		savedY = this.getY();
		if (!save) formula = oldFormula;
		super.dispose();
	}

	public void keyPressed(KeyEvent e) {}

	public void keyReleased(KeyEvent e) {
		setFormula(inputTextField.getText());
		if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			formula = oldFormula;
			dispose(false);
		}
	}

	public void keyTyped(KeyEvent e) {}

	private void addSumActionPerformed(ActionEvent evt) {
		insertText("\\sum{" + readInput(this, t("enter lower bounds for sum:")) + ",");
	}

	private void boldActionPerformed(ActionEvent evt) {
		format("bold");
	}

	private void casesActionPerformed(ActionEvent evt) {
		insertText(readInput(this, t("enter cases separated by comma or semicolon:"), "\\cases{") + "}");
	}

	private void ceilingActionPerformed(ActionEvent evt) {
		format("ceil");
	}

	private void floorActionPerformed(ActionEvent evt) {
		format("floor");
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

	private void fractalActionPerformed(ActionEvent evt) {
		insertText("\\frac{" + readInput(this, t("enter divisor:")) + ",");
	}

	// Ende Ereignisprozeduren
	private String getResult() {
		return formula;
	}

	private void init(String title, String text, boolean modal) {
		oldFormula = text;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				dispose(false);
			}
		});
		if ((savedX < 0) && (savedY < 0)) {
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			savedX = (d.width - getSize().width) / 2;
			savedY = (d.height - getSize().height) / 2;
		}
		setLocation(savedX, savedY);

		Component panel;
		add(panel = createPanel(text));
		Dimension dim = panel.getPreferredSize();
		setSize(dim.width, dim.height + 20);

		setResizable(false);
	}

	private Component createPanel(String text) {
		VerticalPanel vp = new VerticalPanel();
		vp.add(createInputTextField(text));
		vp.add(formulaPanel = createFormulaPanel());
		vp.add(createButtons());
		vp.skalieren();
		return vp;
	}

	private HorizontalPanel createButtons() {
		HorizontalPanel buttonPanel = new HorizontalPanel();

		buttonPanel.add(rowOne());
		buttonPanel.add(rowTwo());
		buttonPanel.add(rowThree());
		buttonPanel.add(rowFour());
		buttonPanel.add(arrowBox());
		buttonPanel.skalieren();

		return buttonPanel;
	}

	private JComponent arrowBox() {
		VerticalPanel arrowBox = new VerticalPanel();
		arrowMenu = new JComboBox<String>();
//		arrowMenu.setBounds(590, 264, 200, 25);
		arrowMenu.addItem(t("Arrows"));
		arrowMenu.addItem("\u2190 (<-)");
		arrowMenu.addItem("\u21D0 (<=)");
		arrowMenu.addItem("\u2194 (<->)");
		arrowMenu.addItem("\u21D4 (<=>)");
		arrowMenu.addItem("\u2193 (" + t("Downarrow (single)") + ")");
		arrowMenu.addItem("\u21D3 (" + t("Downarrow (double)") + ")");
		arrowMenu.addItem("\u2192 (->)");
		arrowMenu.addItem("\u21D2 (=>)");
		arrowMenu.addItem("\u2191 (" + t("Uparrow (single)") + ")");
		arrowMenu.addItem("\u21D1 (" + t("Uparrow (double)") + ")");
		arrowMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = arrowMenu.getSelectedItem().toString();
				if (s.contains("(")) insertText(s.substring(0, 1));
			}
		});
		arrowBox.add(arrowMenu);

		arrowBox.add(okButton = new JButton(t("Ok")));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});
		arrowBox.skalieren();

		Dimension dim = arrowBox.getPreferredSize();
		dim.height = dim.height + 50;
		arrowBox.setPreferredSize(dim);
		Point loc = okButton.getLocation();
		okButton.setLocation(loc.x + 100, loc.y + 50);
		return arrowBox;
	}

	private VerticalPanel rowFour() {
		VerticalPanel four = new VerticalPanel();

		four.add(type = new JButton(t("typewriter")));
		type.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				typeActionPerformed(evt);
			}
		});

		four.add(bold = new JButton(t("bold")));
		bold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				boldActionPerformed(evt);
			}
		});

		four.add(italic = new JButton(t("italic")));
		italic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				italicActionPerformed(evt);
			}
		});

		four.add(underlined = new JButton(t("underlined")));
		underlined.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				underlinedActionPerformed(evt);
			}
		});

		four.add(overlined = new JButton(t("overlined")));
		overlined.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				overlinedActionPerformed(evt);
			}
		});
		four.skalieren();
		return four;
	}

	private VerticalPanel rowThree() {
		VerticalPanel three = new VerticalPanel();

		three.add(cases = new JButton(t("cases")));
		cases.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				casesActionPerformed(evt);
			}
		});

		three.add(subscript = new JButton(t("subscript")));
		subscript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				subscriptActionPerformed(evt);
			}
		});

		three.add(superscript = new JButton(t("superscript")));
		superscript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				superscriptActionPerformed(evt);
			}
		});

		three.add(ceiling = new JButton(t("ceiling")));
		ceiling.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ceilingActionPerformed(evt);
			}
		});

		three.add(floor = new JButton(t("floor")));
		floor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				floorActionPerformed(evt);
			}
		});
		three.skalieren();
		return three;
	}

	private VerticalPanel rowTwo() {
		VerticalPanel two = new VerticalPanel();
		two.add(fraction = new JButton(t("fraction")));
		fraction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				fractalActionPerformed(evt);
			}
		});

		two.add(root = new JButton(t("root")));
		root.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				rootActionPerformed(evt);
			}
		});

		two.add(small = new JButton(t("small")));
		small.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				smallActionPerformed(evt);
			}
		});

		two.add(big = new JButton(t("big")));
		big.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				bigActionPerformed(evt);
			}
		});
		two.skalieren();
		return two;
	}

	private VerticalPanel rowOne() {
		VerticalPanel one = new VerticalPanel();
		one.add(addSum = new JButton(t("sum")));
		addSum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addSumActionPerformed(evt);
			}
		});

		one.add(product = new JButton(t("product")));
		product.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				productActionPerformed(evt);
			}
		});

		one.add(integral = new JButton(t("integral")));
		integral.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				integralActionPerformed(evt);
			}
		});

		one.add(matrix = new JButton(t("matrix")));
		matrix.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				matrixActionPerformed(evt);
			}
		});

		one.skalieren();
		return one;
	}

	private SuggestField createInputTextField(String text) {
		inputTextField = new SuggestField() {
			private static final long serialVersionUID = 1669460044993000108L;

			@Override
			public void paste() {
				Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				try {
					Object o = systemClipboard.getData(DataFlavor.stringFlavor);
					if (o != null) setText(o.toString().replace("\n","\\n "));
				} catch (UnsupportedFlavorException | IOException e) {}
				
			}
		};
		inputTextField.setPreferredSize(new Dimension(frameWidth - 10, 27));
		inputTextField.setSize(inputTextField.getPreferredSize());
		inputTextField.setEditable(true);
		inputTextField.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 17));
		inputTextField.setForeground(Color.black);
		inputTextField.setBackground(Color.white);
		inputTextField.setText((text == null) ? "" : text);
		if (text != null) inputTextField.selectAll();
		inputTextField.addKeyListener(this);
		inputTextField.addActionListener(this);
		return inputTextField;
	}

	private FormulaPanel createFormulaPanel() {
		FormulaPanel formulaPanel = new FormulaPanel();
		formulaPanel.setPreferredSize(new Dimension(frameWidth - 10, frameHeight - 170));
		formulaPanel.setSize(formulaPanel.getPreferredSize());
		formulaPanel.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 15));
		formulaPanel.setForeground(Color.black);
		formulaPanel.setBackground(Color.white);
		return formulaPanel;
	}

	private void insertText(String tx) {
		String tmp = inputTextField.getText();
		int pos = inputTextField.getCaretPosition();
		tmp = tmp.substring(0, pos) + tx + tmp.substring(pos);
		inputTextField.setText(tmp);
		inputTextField.setCaretPosition(pos + tx.length());
		setFormula(tmp);
	}

	private void integralActionPerformed(ActionEvent evt) {
		insertText(readInput(this, t("enter integral bounds, separated by comma:"), "\\integr{") + "}");
	}

	private void italicActionPerformed(ActionEvent evt) {
		format("it");
	}

	private void matrixActionPerformed(ActionEvent evt) {
		format("matrix");
	}

	private void overlinedActionPerformed(ActionEvent evt) {
		format("overline");
	}

	private void productActionPerformed(ActionEvent evt) {
		insertText("\\prod{" + readInput(this, t("enter lower bounds for product:")) + ",");
	}

	private void rootActionPerformed(ActionEvent evt) {
		insertText(readInput(this, t("enter (degree,) radicand of the root expression:"), "\\root{") + "}");
	}

	private void setFormula(String code) {
		formula = code;
		formulaPanel.setFormula(code);
		repaint();
		// System.out.println("set formula to "+formula);
		inputTextField.requestFocus();
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

	private void underlinedActionPerformed(ActionEvent evt) {
		format("underline");
	}

	protected void bigActionPerformed(ActionEvent evt) {
		format("big");
	}

	protected void okButtonActionPerformed(ActionEvent evt) {
		dispose(true);
	}

	protected void smallActionPerformed(ActionEvent evt) {
		format("small");
	}

	public static void main(String[] args) {
		JFrame app = new JFrame("Test");
		String code = FormulaInputDialog.readInput(app, "Input dialog for Test");
		System.out.println(code);
		System.err.println(code);

		JPanel panel = new FormulaPanel(code);
		panel.setPreferredSize(new Dimension(800, 600));
		panel.setSize(panel.getPreferredSize());
		app.add(panel);
		app.pack();
		app.setDefaultCloseOperation(EXIT_ON_CLOSE);
		app.setVisible(true);
	}
}
