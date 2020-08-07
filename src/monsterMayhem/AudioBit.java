package monsterMayhem;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

/**
 * Petit robot d'espionnage de ligne pour gérer certains évenements de la musique.
 * N'a finalement pas été implémenté, ne solutionnant pas le problème de l'époque, qui était de gérer l'ouverture et la fermeture des AudioInputStream.
 * @deprecated
 * @author hero
 *
 */
public class AudioBit implements LineListener {
	
	private long pos;
	private LineEvent event;
	
	AudioBit()
	{
		this.pos = 0;
	}

	@Override
	public void update(LineEvent event) {

		pos = event.getFramePosition();
		System.out.println(pos);
		this.event = event;
		if (this.event.getType().equals(Type.STOP))
		{
			System.out.println("Line has stopped.");
			AudioInputStream ais = (AudioInputStream) event.getLine();
			System.out.println("AIS : " + ais);
			try {
				ais.reset();
				System.out.println("Maybe : resetted AIS");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			Clip clip = (Clip) event.getSource();
////			System.out.println("Clip : " +clip);
//			clip.close();
//			clip.flush();
//			clip.removeLineListener(this);
//			clip = null;
//			System.out.println("Clip : " +clip);
			
		}

	}

}
