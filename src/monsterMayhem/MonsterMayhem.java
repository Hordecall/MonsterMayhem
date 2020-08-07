package monsterMayhem;

import java.awt.FontFormatException;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * 
 * Classe de lancement de la fenêtre principale.
 */
public class MonsterMayhem {
	
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException, FontFormatException {
		
		new MonsterFrame();

		
	}



}
