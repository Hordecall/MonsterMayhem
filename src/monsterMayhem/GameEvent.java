package monsterMayhem;

import java.awt.Color;

/**
 * Classe fille d'AbsractEvent qui sert à gérer des évènements qui surviennent au cours du jeu et selon la position dans la chanson : création ou destruction d'asteroides.
 * @see AbstractEvent
 * @author hero
 *
 */
public class GameEvent extends AbstractEvent{

	private int numberOfAsteroids;
	private Color color;
	private int size;
	private String action;
	
	/**
	 * Création d'un Event, avec un nombre d'asteroides, une voleur, une taille d'asteroide, et l'heure d'execution.
	 * @param numberOfAster
	 * @param col
	 * @param size
	 * @param timeOfEvent
	 */
	GameEvent (int numberOfAster, int size, Color col, long timeOfEvent, String name)	
	{
		super(timeOfEvent, name);
		this.numberOfAsteroids = numberOfAster;
		if (col == null)
			this.color = Color.orange;
		else
			color = col;
		this.size = size;
		this.action = "spawn";

	}
	
	/**
	 * Surcharge du constructeur, pour actions spéciales (gérées par la fonction EventManager() de la classe principale MonsterFrame.
	 * @param action un String pour l'EventManager qui décidera son action
	 * @param timeOfEvent
	 * @param name
	 */
	GameEvent (String action, long timeOfEvent, String name)
	{
		super(timeOfEvent, name);
		this.action = action;

	}
	
	public int getNumberOfAsteroids() {
		return numberOfAsteroids;
	}

	public Color getColor(){
		return this.color;
	}
	
	public int getSize(){
		return this.size;
	}
	
	public String getAction(){
		return action;
	}
	
	@Override
	public String getType()
	{
		return "Game Event";
	}
}
