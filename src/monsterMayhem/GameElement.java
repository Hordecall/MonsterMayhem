package monsterMayhem;

import java.awt.Polygon;

import javax.swing.JComponent;
/**
 * Classe abstraite pour tous les sprites du jeu, étend JComponent pour l'affichage.
 * 
 * 
 * @author hero
 *
 */
public abstract class GameElement extends JComponent {
	

	private static final long serialVersionUID = 5486905513863552930L;
	private double angle;
	private double deltaAngle;
	private double dx, dy;
	private double currentX, currentY;
	private double deltaX, deltaY;
	private boolean rotatingLeft, rotatingRight;
	private Polygon originalShape;
	private Polygon newShape;
	private boolean toRemove;
	private boolean hasCollided;
	
	  /**
	   * Rendu du déplacement. Cette méthode calcule un nouveau polygone à partir de l'ancien en appliquant les règles de rotation et de translation.
	   * @author Mike Hall
	   */
	public void render(){
		
		this.setNewShape(new Polygon());
		
		for (int i = 0; i < this.getOriginalShape().npoints; i++)
		{
			this.getNewShape().addPoint((int) Math.round(this.getOriginalShape().xpoints[i] * Math.cos(this.getAngle()) + this.getOriginalShape().ypoints[i] * Math.sin(this.getAngle())) + (int) Math.round(this.getCurrentX()),
			(int) Math.round(this.getOriginalShape().ypoints[i] * Math.cos(this.getAngle()) - this.getOriginalShape().xpoints[i] * Math.sin(this.getAngle())) + (int) Math.round(this.getCurrentY()));
		}
	}
	
	/**
	 * Calcul des déplacements. Dépend de chaque élément.
	 * @Author Mike Hall
	 */
	public void advance(){
		
	}
	
	/**
	 * Fonction appellée par l'update de la classe principale, pour la mise à jour des éléments.
	 */
	public void update(){
		
	}
	
	public void setRemove(boolean state)
	{
		this.toRemove = state;
	}
	
	public boolean toRemove()
	{
		return this.toRemove;
	}
	
	public boolean isRotatingLeft()
	{
		return this.rotatingLeft;
	}
	
	public void rotatingLeft(boolean state)
	{
		this.rotatingLeft = state;
	}
	
	public boolean isRotatingRight()
	{
		return this.rotatingRight;
	}
	
	public void rotatingRight(boolean state)
	{
		this.rotatingRight = state;
	}
	
	public double getDeltaAngle() {
		return deltaAngle;
	}
	public void setDeltaAngle(double deltaAngle) {
		this.deltaAngle = deltaAngle;
	}
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public double getCurrentX() {
		return currentX;
	}
	public void setCurrentX(double currentX) {
		this.currentX = currentX;
	}
	public double getDeltaX() {
		return deltaX;
	}
	public void setDeltaX(double deltaX) {
		this.deltaX = deltaX;
	}
	public double getCurrentY() {
		return currentY;
	}
	public void setCurrentY(double currentY) {
		this.currentY = currentY;
	}
	public double getDeltaY() {
		return deltaY;
	}
	public void setDeltaY(double deltaY) {
		this.deltaY = deltaY;
	}
	public double getDx() {
		return dx;
	}
	public void setDx(double dx) {
		this.dx = dx;
	}
	public double getDy() {
		return dy;
	}
	public void setDy(double dy) {
		this.dy = dy;
	}
	public Polygon getOriginalShape() {
		return originalShape;
	}
	public void setOriginalShape(Polygon originalShape) {
		this.originalShape = originalShape;
	}
	public Polygon getNewShape() {
		return newShape;
	}
	public void setNewShape(Polygon newShape) {
		this.newShape = newShape;
	}

	public boolean hasCollided() {
		return hasCollided;
	}

	public void hasCollided(boolean state) {
		this.hasCollided = state;
	}

}
