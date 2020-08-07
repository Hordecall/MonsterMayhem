package monsterMayhem;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Objet affiché lors du menu pause ou au début du jeu.
 * Remplacé par un JLabel avec une image.
 * 
 * @deprecated
 * @author hero
 *
 */
public class StartupMenu extends JComponent {

	private File fontFile;
	private Font font1, font2;
	private GraphicsEnvironment ge;
	private JTextArea title, subtitle, text;
	private JPanel centerPanel;
	private Rectangle rect;
	private AffineTransform af1, af2;
	private Color[] colors = {Color.WHITE, Color.BLUE, Color.GREEN, Color.yellow, Color.ORANGE, Color.RED};
	
	StartupMenu() throws FontFormatException, IOException{
	
		af1 = new AffineTransform();
		af1.setToScale(81, 81);
		af2 = new AffineTransform();
		af2.setToScale(32, 32);
		fontFile = new File("res/prstartk.ttf");
		font1 = Font.createFont(Font.TRUETYPE_FONT, fontFile.getCanonicalFile());
		font1 = font1.deriveFont(af1);
		font2 = Font.createFont(Font.TRUETYPE_FONT, fontFile.getCanonicalFile());
		font2 = font2.deriveFont(af2);
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(font1);
		ge.registerFont(font2);
				
		centerPanel = new JPanel();
		
		rect = new Rectangle(0,0,1024,768);

		title = new JTextArea();
		title.setEditable(false);
		title.setFocusable(false);
		title.setFont(font1);
		title.setSize(500, 81);
		title.setLocation(280, 200);
		title.setText("MOTHER MAYHEM");
		
		subtitle = new JTextArea();
		subtitle.setEditable(false);
		subtitle.setFocusable(false);
		subtitle.setSize(500, 32);
		subtitle.setLocation(310, 281);
		subtitle.setFont(font2);
		subtitle.setText("A JAVA PROJECT BY THOMAS BEDEAU");
		
		text = new JTextArea();
		text.setEditable(false);
		text.setFocusable(false);
		text.setSize(500, 128);
		text.setLocation(310, 333);
		text.setText("Press [P] to start (and pause the game) \nUse ARROW KEYS to Move  \nUse SPACEBAR to shoot \n\nBased on ASTEROIDS game by Atari \nMusic by CUSTOM NOBODY \nwww.customnobody.com");
		
		this.add(title);	
		this.add(subtitle);
		this.add(text);
		this.setSize(1024,768);
		this.setForeground(Color.WHITE);
	
	}
	
	public void paint(Graphics g){
		
		Graphics2D h = (Graphics2D) g;
		h.setColor(Color.black);
		h.fill(rect);
		title.setBackground(Color.BLACK);
		title.setForeground(colors[(int)(Math.random()*colors.length)]);
		title.setText("MOTHER MAYHEM");
		subtitle.setBackground(Color.BLACK);
		subtitle.setForeground(colors[(int)(Math.random()*colors.length)]);
		subtitle.setText(subtitle.getText());
		text.setBackground(Color.BLACK);
		text.setForeground(colors[(int)(Math.random()*colors.length)]);
		text.setText(text.getText());

		
	}
}
