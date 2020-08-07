package monsterMayhem;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;


/**
 * La classe des tirs du vaisseau.
 * @see GameElement
 * @author hero
 *
 */
public class Shot extends GameElement implements Pause{
	

	private static final long serialVersionUID = 2166691386871547641L;
	private Point2D.Double a, b, c, d;
	private boolean isPaused;
	private boolean ultimate; //anime les couleurs si MonsterFrame.twinShot et MonsterFrame.pierceShot sont activés.
	
	Shot(double shipAngle, double startX, double startY )
	{	
		this.setCurrentX(startX);
		this.setCurrentY(startY);
		this.setAngle(shipAngle);
		this.setDeltaX(20*-Math.sin(shipAngle));
		this.setDeltaY(20*Math.cos(shipAngle));
		
		a = new Point2D.Double(-2,5);
		b = new Point2D.Double(2,5);
		c = new Point2D.Double(2,-5);
		d = new Point2D.Double(-2,-5);
		
		this.setOriginalShape(new Polygon());
		this.getOriginalShape().addPoint((int)a.x,(int) a.y);
		this.getOriginalShape().addPoint((int)b.x,(int) b.y);
		this.getOriginalShape().addPoint((int)c.x,(int) c.y);
		this.getOriginalShape().addPoint((int)d.x,(int) d.y);
		
		this.setNewShape(this.getOriginalShape());

		
		this.setDx(0.0);
		this.setDy(0.0);
		this.setDeltaAngle(0);
			
		isPaused = false;
		ultimate = false;
		this.setRemove(false);
		this.hasCollided(false);

	}
	
	
	public void update() {
	
		if (!isPaused)
		{
			advance();
			render();
			repaint();
		}		
		
	}
	
	public void advance() {
	
		this.setAngle(this.getAngle()+this.getDeltaAngle());
	    if (this.getAngle() < 0)
	      this.setAngle(this.getAngle() + 2 * Math.PI);
	    if (this.getAngle() > 2 * Math.PI)
	      this.setAngle(this.getAngle()-2 * Math.PI);
	    
	    this.setCurrentX(this.getCurrentX() + this.getDeltaX());
	    this.setCurrentY(this.getCurrentY() - this.getDeltaY());
	
	    setDx(-Math.sin(this.getAngle())/12);
	    setDy(Math.cos(this.getAngle())/12);
    	    
	    setDeltaX(getDeltaX()+getDx());
	    setDeltaY(getDeltaY()+getDy());
	    
	  }   

	
	public void paint (Graphics l)
	{	
		Graphics2D render = (Graphics2D) l;
		render.setColor(ultimate() ? new Color((int) (Math.random()*133+122),(int) (Math.random()*133+122),(int) (Math.random()*133+122), 255): Color.white);
		render.fill(getNewShape());			
	}

	public void pause() {
		
		isPaused= !isPaused;
	}
	
	public void ultimate(boolean state){
		this.ultimate = state;
	}
	
	public boolean ultimate()
	{
		return this.ultimate;
	}
	
}
