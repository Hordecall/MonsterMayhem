package monsterMayhem;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;

import java.awt.geom.Point2D;

/**
 * La classe du vaisseau controlé par le joueur.
 * @author Hordecall
 */
public class Ship extends GameElement implements Pause {


	private static final long serialVersionUID = 4178146370032292995L;
	private static final int max_velocity = 8;
	private Point2D.Double a, b, c, d;
	private boolean isThrusting, isAlive;
	private boolean isPaused;
	private boolean isInvincible;
	private Color color;


	Ship() 
	{	
			a = new Point2D.Double(0, 0);
			b = new Point2D.Double(-7, 4);
			c = new Point2D.Double(0, -20);
			d = new Point2D.Double(7, 4);
			
			setNewShape(new Polygon());
			getNewShape().addPoint((int) a.x, (int) a.y);
			getNewShape().addPoint((int) b.x, (int) b.y);
			getNewShape().addPoint((int) c.x, (int) c.y);
			getNewShape().addPoint((int) d.x, (int) d.y);
			
			setOriginalShape(getNewShape());
			color = Color.RED;
			
			this.isThrusting(false);
			this.rotatingLeft(false);
			this.rotatingRight(false);
			isAlive = true; 
			isPaused = false;
			isInvincible = true;
			this.setRemove(false);
			this.hasCollided(false);
			
			setDx(0);
			setDy(0);
			setCurrentY(500);
			setCurrentX(500);
			setDeltaAngle(0);
			setAngle(0);
		}

	/**
	 * Calcul des déplacements. Malgré de grands efforts et de nombreux tests, mes compétences en trigonométrie ne m'ont pas permis d'arriver à un résultat aussi probant que celui-ci.
	 * Cette technique a été développée par Mike Hall en 1998, et ré-adaptée à ce programme par mes soins. Merci à lui.
	 * @author Mike Hall
	 */
	public void advance() {

		    this.setAngle(this.getAngle() + this.getDeltaAngle());
		    if (this.getAngle() < 0)
		      this.setAngle(this.getAngle() + 2 * Math.PI);
		    if (this.getAngle() > 2 * Math.PI)
		      this.setAngle(this.getAngle() - 2 * Math.PI);
		    
		    this.setCurrentX(this.getCurrentX() + this.getDeltaX());
		    this.setCurrentY(this.getCurrentY() - this.getDeltaY());

		    setDx(-Math.sin(getAngle())/2);
		    setDy(Math.cos(getAngle())/2);
		    
		    if ((this.isThrusting() && (getDeltaX()+getDx()) < max_velocity) && (this.isThrusting() && (getDeltaX()+getDx()) > -max_velocity) && isAlive) 
		    {
		        setDeltaX(getDeltaX() + getDx());
		    }
		    
		    if ((this.isThrusting() && (getDeltaY()+getDy()) < max_velocity) && (this.isThrusting() && (getDeltaY()+getDy()) > -max_velocity) && isAlive)
		    {
		        setDeltaY(getDeltaY() + getDy());
		    }
		    
		    
		    if (this.isRotatingLeft()) {
		        setAngle(getAngle() + Math.PI / 32.0);
		        if (getAngle() > 2 * Math.PI)
		          setAngle(getAngle() - 2 * Math.PI);
		      }
		    if (this.isRotatingRight()) {
		        setAngle(getAngle() - Math.PI / 32.0);
		        if (getAngle() < 0)
		          setAngle(getAngle() + 2 * Math.PI);
		      }
		  }
	

	
	/**
	 * Mise à jour du rendu graphique et physique.
	 * Appellée par l'actionPerformed update de la classe Asteroids_Frame.
	 * @see MonsterFrame.actionPerformed();
	 */
	public void update() {

		if (!isPaused)
		{
			advance();
			render();
			repaint();
		}
					
	}

	public void paint (Graphics l)
	{	
		Graphics2D render = (Graphics2D) l;
		BasicStroke stroke = new BasicStroke(4);
			
		if (isInvincible)
		{
			render.setStroke(stroke);
			render.setColor(Color.yellow);
			render.draw(getNewShape());
		}

		render.setColor(Color.red);	
		render.fill(getNewShape());
			
	}
	
	
	public boolean isInvincible() {
		return isInvincible;
	}
	
	public void setInvincible(boolean state) {
		this.isInvincible = state;
	}
	
	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean state) {
		this.isAlive = state;
	}
	
	public boolean isThrusting()
	{
		return this.isThrusting;
	}
	
	public void isThrusting(boolean state)
	{
		this.isThrusting = state;
	}

	/**
	 * "Détruit" le vaisseau en mettant sa vélocité à 0.
	 */
	public void kill() {
		setDeltaX(0);
		setDeltaY(0);
		isAlive = false;
	}
	public void setColor(Color color)
	{
		this.color = color;
	}
	public Color getColor()
	{
		return color;
	}
	
	@Override
	public void pause() {

		isPaused = !isPaused;
		
	}
	


	
}
