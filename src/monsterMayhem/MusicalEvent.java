package monsterMayhem;

/**
 * Classe fille d'AbstractEvent qui s'occupe de gérer les évènements musicaux au cours de la chanson (changements de gamme et d'effets).
 * @see AbstractEvent
 * @author hero
 *
 */
public class MusicalEvent extends AbstractEvent {
	
	private boolean od, twin; // les effets à activer
	private int[] range; // la gamme

	MusicalEvent(boolean overdrive, boolean twinNote, long timeOfStart, int[] rangeOfPart, String name) {
		
		super(timeOfStart, name);
		this.od = overdrive;
		this.twin = twinNote;
		this.range = rangeOfPart;
				
	}
	
	public int[] getRange()
	{
		return range;
	}

	public boolean getOverdrive() {
		return od;
	}

	public boolean getTwinNotes() {
		return twin;
	}
	
	@Override
	public String getType()
	{
		return "Musical Event";
	}
}
