package monsterMayhem;

/**
 * Classe abstraite des évènements, qui possède juste les ID, le temps d'activation et leur statut d'activation.
 * @see GameEvent
 * @see MusicalEvent
 * @author hero
 *
 */
public abstract class AbstractEvent {

	private static int ID;
	private int pid;
	private long time;
	private boolean hasLaunched;
	private String name;
	
	AbstractEvent(long startTime, String name)
	{
		this.pid = ID;
		ID++;
		hasLaunched = false;
		this.time = startTime;
		this.name = name;
	}
	
	public long getTime(){
		return this.time;
	}
	
	public int getId(){
		return this.pid;
	}
	
	/**
	 * Retourne le nom de l'évènement (différent de l'action).
	 * @return
	 */
	public String getName(){
		return this.name;
	}
	
	public boolean hasLaunched(){
		return hasLaunched;
	}
	
	public String getType()
	{
		return "Abstract";
	}
	
	/**
	 * Activé par l'EventManager directement après son lancement, pour éviter les répétitions.
	 * @param state
	 */
	public void setLaunched(boolean state) {
		System.out.println("Event #"+this.getId()+ " '"+this.getName()+"' has launched. " + " (" +this.getType()+") ");
		System.out.println("SP : " + this.time);
		this.hasLaunched = state;
	}
}
