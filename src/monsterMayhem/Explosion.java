package monsterMayhem;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

/**
 * Classe d'un objet représentant une explosion. Crée 120 rectangles d'une couleur aléatoire, et les déplace avec un peu d'aléatoirité.
 * @author hero
 */
public class Explosion extends JComponent implements Pause {
	
	private static final long serialVersionUID = -340814081390262264L;
	private Rectangle[] sprites;
	Color[] colors = {Color.RED, Color.orange, Color.yellow, Color.GREEN, Color.BLUE, Color.PINK};
	private int life = 255;
	private int lifeFactor = 8;
	private boolean alive;
	private boolean isPaused;


	Explosion(int startX, int startY)
	{
		alive = true;
		this.setLocation(startX, startY);
		sprites = new Rectangle[120];
		for (int i = 0; i < sprites.length; i++)
		{
			sprites[i] = new Rectangle(startX, startY, 2, 2);
		}
		isPaused = false;
	}
	

	public void update() {
		
			for (int i = 0; i < sprites.length; i++)
			{
				sprites[i].setLocation(sprites[i].getLocation().x+getRandom(), sprites[i].getLocation().y+getRandom());
				
			}
			life -= lifeFactor;
			if (life < 1)
				alive = false;
		
}

	public void paint (Graphics l)
	{	
		Graphics2D render = (Graphics2D) l;
		int alpha = life*4;
		if (alpha > 255)
			alpha = 255;
	
		for (int i = 0; i < sprites.length; i++)
		{
			Color color = colors[(int)(Math.random()*colors.length)];
			render.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
			render.fill(sprites[i]);	
		}
		
	}
	
	private int getRandom()
	{
		int r = (int)(Math.random()*16);
		if (Math.random()> 0.5)
		{
			r = -r;
		}
	
		return r;
	}
	
	/**
	 * Permet de savoir quand retirer l'explosion du jeu.
	 * @return
	 */
	public boolean isAlive()
	{
		return alive;
	}
	
	/**
	 * Permet de changer la durée d'une explosion.
	 * Base : 8. Plus c'est petit plus c'est long.
	 * @param time
	 */
	public void setLifeFactor(int time)
	{
		this.lifeFactor = time;
	}


	@Override
	public void pause() {
		if (!isPaused)
		{
			isPaused = true;
		}
		
	}
}
