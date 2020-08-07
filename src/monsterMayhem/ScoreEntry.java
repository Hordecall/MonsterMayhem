package monsterMayhem;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Classe pour l'entrée du nom dans les scores.
 * C'est un JLayeredPane qui s'affiche à la fin de la partie, à l'évènement "scoreSheet".
 * 
 * @author hero
 *
 */
public class ScoreEntry extends JLayeredPane implements MouseListener {

	private static final long serialVersionUID = -7996852708839871690L;
	private JLabel congrats, yourscore, scoreLabel, enter;
	private JButton ok;
	private JTextArea nameEntry;
	private GridLayout grid;
	private JPanel main;
	
	private AffineTransform af;
	private File fontFile;
	private Font font;
	private GraphicsEnvironment ge;
	
	private int score;
	private String name;
	private boolean isDone;
	
	private HighScores highScores;
	
	ScoreEntry(int score, HighScores highScores) throws FontFormatException, IOException
	{
		this.highScores = highScores;
		this.score = score;
		isDone = false;
		
		//gestion de la police d'ecriture
		af = new AffineTransform();
		af.setToScale(12, 12);
		fontFile = new File("res/prstartk.ttf");
		font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
		font = font.deriveFont(af);
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(font);
		
		//gestion de l'interface graphique
		grid = new GridLayout(0,1);
		main = new JPanel(grid); main.setBackground(Color.black); main.setForeground(Color.white);
		congrats = new JLabel("Congratulations !"); congrats.setFont(font); congrats.setBackground(Color.BLACK); congrats.setForeground(Color.white);
		yourscore = new JLabel("Your score : "); yourscore.setFont(font); yourscore.setBackground(Color.BLACK); yourscore.setForeground(Color.yellow);
		scoreLabel = new JLabel(""+this.score); scoreLabel.setFont(font); scoreLabel.setBackground(Color.BLACK); scoreLabel.setForeground(Color.WHITE);
		enter = new JLabel("Enter your name :"); enter.setFont(font); enter.setBackground(Color.black); enter.setForeground(Color.WHITE);
		nameEntry = new JTextArea(1,10); nameEntry.setFont(font); nameEntry.setBackground(Color.BLACK); nameEntry.setForeground(Color.WHITE); nameEntry.setCaretColor(Color.WHITE); nameEntry.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
		
		main.setSize(220,200);
		main.setLocation(20,50);
		main.add(congrats); main.add(yourscore); main.add(scoreLabel); main.add(enter); main.add(nameEntry);
		
		ok = new JButton("ok"); ok.setFont(font); ok.setBackground(Color.black); ok.setForeground(Color.BLACK);
		
		ok.addMouseListener(this);
		
		main.add(ok); 
		
		this.setSize(1024,768);
		this.add(main,0,0);
			
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		JButton source = (JButton) e.getSource();
		if (source.getText() == "ok")
		{
			if (!nameEntry.getText().isEmpty())
			{
				this.name = nameEntry.getText();
				highScores.addEntry(score, name);
				try {
					highScores.writeFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				isDone = true;
			}
			
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean isDone() {
		return isDone;
	}

	public void isDone(boolean b) {
		this.isDone = b;
		
	}
	
}
