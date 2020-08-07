package monsterMayhem;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;


	/**
	 * Classe des élements Asteroid.
	 * Les Asteroid, comme tous les GameElement, sont des Polygon. Leur taille se divise de 2 lorsqu'ils n'ont plus de vie, leur vie étant définie par leur taille/5.
	 * @author Hordecall
	 */
	class Asteroid extends GameElement{
	
	private static final long serialVersionUID = -1891236362983929128L;
	private boolean rotatingLeft, rotatingRight;
	public int hp;
	public int size;
	private boolean isPaused;
	private static int id = 0;
	private int pID;
	private Color color;
	
	/**
	 * Constructeur d'Asteroid.
	 * @param int loaction de depart X, int location de depart Y, double angle de deplacement, double vitesse de deplacement X, double vitesse de deplacement Y, int taille
	 * @return none
	 * @author Hordecall
	 */	
	Asteroid (int startLocX, int startLocY, double startAngle, double startVelocityX, double startVelocityY, int size)
	{
		this.setCurrentX(startLocX);
		this.setCurrentY(startLocY);
		this.setAngle(startAngle);
		this.setDeltaX(startVelocityX);
		this.setDeltaY(startVelocityY);
		
		this.size = size;
		this.hp = size/5;
		
		isPaused=false;
		this.setRemove(false);
		this.hasCollided(false);
		id++;
		pID = id;
		
		this.color = Color.orange;
	}
	
	/**
	 * Constructeur d'Asteroid (variante avec couleur)
	 * @param int loaction de depart X, int location de depart Y, double angle de deplacement, double vitesse de deplacement X, double vitesse de deplacement Y, int taille, Color couleur
	 * @return none
	 * @author Hordecall
	 */	
	Asteroid (int startLocX, int startLocY, double startAngle, double startVelocityX, double startVelocityY, int size, Color color)
	{
		this.setCurrentX(startLocX);
		this.setCurrentY(startLocY);
		this.setAngle(startAngle);
		this.setDeltaX(startVelocityX);
		this.setDeltaY(startVelocityY);
		
		this.size = size;
		this.hp = size/5;
		isPaused=false;
		id++;
		pID = id;
		this.color = color;
		this.setRemove(false);
		this.hasCollided(false);

	}
	
	public void init(){
		
		
		Point2D.Double sp = new Point2D.Double(0,0);
		Point2D.Double spA = new Point2D.Double(sp.x, sp.y-10);
		Point2D.Double spB = new Point2D.Double(spA.x+getRandomSize(), spA.y);
		Point2D.Double spC = new Point2D.Double(spB.x+5+getRandomSize(), spB.y+5+getRandomSize());
		Point2D.Double spD = new Point2D.Double(spC.x+5+getRandomSize(), spC.y+5+getRandomSize());
		Point2D.Double spE = new Point2D.Double(spD.x-5-getRandomSize(), spD.y+5+getRandomSize());
		Point2D.Double spF = new Point2D.Double(spE.x-5-getRandomSize(), spE.y+5+getRandomSize());
		Point2D.Double spG = new Point2D.Double(spF.x-5-getRandomSize(), spF.y);
		Point2D.Double spH = new Point2D.Double(spG.x-5-getRandomSize(), spG.y);
		Point2D.Double spI = new Point2D.Double(spH.x-5-getRandomSize(), spH.y-5-getRandomSize());
		Point2D.Double spJ = new Point2D.Double(spI.x-5-getRandomSize(), spI.y-5-getRandomSize());
		Point2D.Double spK = new Point2D.Double(spJ.x+5+getRandomSize(), spJ.y-5-getRandomSize());
		Point2D.Double spL = new Point2D.Double(spK.x+5+getRandomSize(), spK.y-5-getRandomSize());
		
		Point2D.Double[] p2dTab = {sp, spA, spB, spC, spD, spE, spF, spG, spH, spI, spJ, spK, spL, spA};
		
		this.setOriginalShape(new Polygon());
		
		for (int i = 0; i < p2dTab.length; i++)
		{
			getOriginalShape().addPoint((int)p2dTab[i].x,(int) p2dTab[i].y);
		}

		this.setNewShape(getOriginalShape());
		
		this.setDx(0.01);
		this.setDy(0.01);
		this.setDeltaAngle(0.1);
				
		int rand = (int)(Math.random()*2);
		if (rand > rand/2)
		{
			rotatingLeft = true;
			rotatingRight = false;
		}	
		else
		{
			rotatingRight= true;
			rotatingLeft = false;
		}
				
	}
	
	public void update() {

		if (hp != 0)
		{
			if (this.getCurrentX() >= 1024 || this.getCurrentX() <= 0) // rebond des asteroides sur les bords
			{
				this.setDeltaX(-this.getDeltaX());
			}
			
			if (this.getCurrentY() >= 768 || this.getCurrentY() <= 0)
			{
				this.setDeltaY(-this.getDeltaY());
			}
			
			if (!isPaused)
			{
				advance();
				render();
			}
		}
		
	}
	
	@Override
	public void advance() {

		    if (this.getAngle() < 0)
		      this.setAngle(getAngle()+2 * Math.PI);
		    if (this.getAngle() > 2 * Math.PI)
		      this.setAngle(getAngle() - 2 * Math.PI);
		    
		    this.setCurrentX(this.getCurrentX()+ this.getDeltaX());
		    this.setCurrentY(this.getCurrentY()- this.getDeltaY());

		    this.setDx(-Math.sin(this.getAngle())/100);
		    this.setDy(Math.cos(this.getAngle())/100);
		    
		    if (this.getDeltaX()>8) this.setDeltaX(8);
		    if (this.getDeltaX()<-8) this.setDeltaX(-8);
		    if (this.getDeltaY()>8) this.setDeltaY(8);
		    if (this.getDeltaY()<-8) this.setDeltaY(-8);
		    	    
		    if (rotatingLeft) {
		        this.setAngle(this.getAngle()+ Math.PI / (Math.pow(size, 4)));
		        if (getAngle() > 2 * Math.PI)
		          this.setAngle(this.getAngle() -2 * Math.PI);
		      }
		    if (rotatingRight) {
		        this.setAngle(this.getAngle()-Math.PI / (Math.pow(size, 4)));
		        if (getAngle() < 0)
		          this.setAngle( this.getAngle()+ 2 * Math.PI);
		      }
		  }   
		  
	  
		public void paint (Graphics l)
		{	
			Graphics2D render = (Graphics2D) l;
			BasicStroke stroke = new BasicStroke(2);
			render.setStroke(stroke);
			
			render.setColor(this.color);
			render.fill(getNewShape());
			
			if (this.color == Color.BLACK)
			{
				render.setColor(Color.red);
				render.draw(getNewShape());
			}
				
		}
		

		/**
		 * Retourne l'identifiant de l'Asteroid.
		 * @return int ID l'identifiant de l'Asteroid.
		 */
		public int getID()
		{
			return pID;
		}

		/**
		 * Retourne la couleur d'un Asteroid.
		 * @return Color la couleur de l'asteroide.
		 */
		public Color getColor() {
			return this.color;
		}
		
		/**
		 * Change la couleur d'un Asteroid.
		 * @param col la nouvelle couleur
		 */
		public void setColor(Color col)
		{
			this.color = col;
		}
		
		/**
		 * Assigne une couleur aléatoire à l'asteroide.
		 */
		public void setRandomColor(){
			
			this.color = new Color((int) (Math.random()*133+122),(int) (Math.random()*133+122),(int) (Math.random()*133+122), 255);
		}
		
		/**
		 * Retourne un double en fonction de la taille de l'astéroide pour l'aléatoirité de la forme.
		 * @return double
		 */
		private double getRandomSize(){
			
			double rSize = new Double (Math.random()*size);
			return rSize;	
		}

		/**
		 * Retourne la taille d'un Asteroid (pour le calcul des scores).
		 * @return Integer la taille de l'asteroide.
		 */
		public int getAsteroidSize(){
			return this.size;
		}

		public void pause() {
		
			isPaused = !isPaused;
			
		}

		

}
