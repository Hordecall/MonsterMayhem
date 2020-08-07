package monsterMayhem;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

/**
 * Un joli fond étoilé qui scintille de mille feux.
 * @author hero
 *
 */
public class Background extends JComponent {
	
	private static final long serialVersionUID = -6906920306285772764L;
	private Rectangle[] stars = new Rectangle[2500];
	private boolean funkyTime;
	
	Background(){
		
		for (int i = 0; i < stars.length; i++)
		{
			stars[i] = new Rectangle((int) (Math.random()*1024), (int) (Math.random()*768), 1, 1);
		}
		
		this.setSize(1024, 768);	
		funkyTime = false;
	}
	
	
	public void paint(Graphics g)
	{
			Graphics2D h = (Graphics2D) g;
			h.setColor(Color.WHITE);
			for (int i = 0; i < stars.length; i++)
			{		
				h.setColor(new Color((int) (Math.random()*133+122),(int) (Math.random()*133+122),(int) (Math.random()*133+122), 255));
				if (funkyTime)
				{
					stars[i].setLocation(stars[i].getLocation().x+getRandom(), stars[i].getLocation().y + getRandom());
				}
				h.fill(stars[i]);
			}	
	}
	
	/**
	 * Retourne 1 ou -1 pour l'animation des étoiles.
	 * @return 1 ou -1;
	 */
	public int getRandom()
	{
		int res = 0;
		double rnd = Math.random();
		if (rnd > 0.5)
		{
			res = 1;
		}
		if (Math.random() > 0.5)
		{
			res = -res;
		}
		
		return res;
	}
	
	/**
	 * Lance ou arrête l'animation des étoiles.
	 */
	public void toggleFunky()
	{
		this.funkyTime = !funkyTime;
	}
	
}
