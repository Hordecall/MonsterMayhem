package monsterMayhem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.geom.AffineTransform;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
/**
 * Classe qui s'occupe de la lecture des scores et de l'affichage des scores.
 * Les scores sont lus depuis le fichier scores.dat du dossier res.
 * Il n'y a que dix entrées sauvegardées.
 * 
 * @author hero
 *
 */
public class HighScores extends JLayeredPane {

	private static final long serialVersionUID = -1734162451305147911L;
	private File fontFile;
	private Font font2;
	private GraphicsEnvironment ge;
	private GridLayout grid;
	private JLabel bg;
	private JLabel[] scoreLabels;
	private AffineTransform af2;
	private JPanel scores;
	private File scoresFile;
	private TreeMap<Integer, String> highScores;
	
	HighScores() throws FontFormatException, IOException{
		
		scoresFile = new File("res/scores.dat");

		af2 = new AffineTransform();
		af2.setToScale(12, 12);
		fontFile = new File("res/prstartk.ttf");
		font2 = Font.createFont(Font.TRUETYPE_FONT, fontFile.getCanonicalFile());
		font2 = font2.deriveFont(af2);
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(font2);
	
		grid = new GridLayout(11,4);
			
		ImageIcon i = new ImageIcon("res/scores.png");
		bg = new JLabel(i);
		bg.setSize(1024,768);
		bg.setLocation(0,0);
				
		this.add(bg, 0, 0);
		this.setSize(1024,768);
		this.setLocation(0,0);
		this.setForeground(Color.WHITE);
	
		readFile();
	}
	
	/**
	 * Lit les scores depuis le fichier.
	 * @throws IOException
	 */
	public void readFile() throws IOException
	{		
		System.out.println("Reading file ...");
		highScores = new TreeMap<Integer, String>();

		FileInputStream fis = new FileInputStream(scoresFile);
		DataInputStream dis = new DataInputStream(fis);
		int counter = 0;
			
		while (counter < 10)
		{
			int score = dis.readInt();
//			System.out.println("Score : " + score);
			String name = dis.readUTF();
//			System.out.println("Name : " + name);
			highScores.put(score, name);
			counter++;
		}		
		
		fis.close();
		dis.close();
		System.out.println("Done.");
		
		sortAndFill();
		
	}
	
	/**
	 * Ecrit le fichier des scores.
	 * @throws IOException
	 */
	public void writeFile() throws IOException
	{
		FileOutputStream fos = new FileOutputStream(scoresFile);
		DataOutputStream das = new DataOutputStream(fos);
		
		NavigableMap<Integer, String> map = highScores.descendingMap();
		Set set = map.entrySet();
		Iterator<Entry> it = set.iterator();
		int counter = 0;
		
		while (it.hasNext() && counter < 11)
		{
			Object o = it.next();
			Map.Entry<Integer, String> entry = (Entry<Integer, String>) o;
			das.writeInt(entry.getKey()); 
			das.writeUTF(entry.getValue());
		}
		
		das.close();
		fos.close();
	}
	
	/**
	 * Met les données lues par readFile dans des JLabel().
	 * Est appellé à chaque lecture, pour une lecture dynamique des scores (même si les scores ne sont mis à jour qu'à la fin de partie).
	 */
	public void sortAndFill()
	{
		System.out.println("Filling high scores ...");
		scores = new JPanel(grid);
		scores.setSize(420, 559);
		scores.setLocation(302, 105);
		scores.setBackground(Color.BLACK);
		scoreLabels = new JLabel[33];
		
		NavigableMap<Integer, String> map = highScores.descendingMap();
		Set<?> set = map.entrySet();
		Iterator<?> it = set.iterator();
			
		int pos = 1;
		int counter = 3;
		scoreLabels[0] = new JLabel("pos#");
		scoreLabels[1] = new JLabel("name ");
		scoreLabels[2] = new JLabel("score");
		
		while (it.hasNext())
		{
			Object o = it.next();
			Map.Entry<Integer, String> entry = (Entry<Integer, String>) o;
			scoreLabels[counter] = new JLabel("#"+pos);
			scoreLabels[counter+1] = new JLabel(entry.getValue());
			scoreLabels[counter+2] = new JLabel(String.valueOf(entry.getKey()));
//			System.out.println("Score : " + entry.getKey() + " Name : "+ entry.getValue());
			counter+=3;
			pos++;
		}
		
		for (int i = 0; i < scoreLabels.length; i ++)
		{
			scoreLabels[i].setBackground(Color.BLACK);
			scoreLabels[i].setForeground(Color.WHITE);
			scoreLabels[i].setFont(font2);

			scores.add(scoreLabels[i], BorderLayout.CENTER);
		}
		
		this.add(scores, 1, 0);
		System.out.println("Done.");

	}

	/**
	 * Rajoute une entrée dans le tableau des scores.
	 * @param score
	 * @param name
	 */
	public void addEntry(int score, String name) {

		highScores.put(score, name);
	}
	

}
