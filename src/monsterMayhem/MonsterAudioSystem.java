package monsterMayhem;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;

/**
 * Classe qui gère le système audio du jeu.
 * En plus de la chanson principale, il y a 44 notes enregistrées, 22 avec un son clean et 22 avec un son en overdrive.
 * A chaque appel de la fonction play(), le système "pioche" dans une des notes de la gamme, définie par le tableau range, définie lors des évènements musicaux.
 * Les sons sont initialisés au lancement du jeu : je n'ai pas trouvé de meilleur solution pour pouvoir m'en servir régulièrement par la suite. Ils sont "bouclés" pour rester en mémoire.
 * Pendant une période j'avais trouvé une meilleure solution qui créait à chaque appel un stream audio, mais après quelques crash je me suis aperçu qu'ils ne se fermaient jamais malgré toutes mes tentatives (voir AudioBit et Note), ce qui donnait à mon application un nombre de Threads faramineux.
 * 
 * @see MusicalEvent
 * @author hero
 * 
 * @see AudioBit
 * @see Note
 *
 */
public class MonsterAudioSystem implements Pause{

	private int[] range = {0, 2, 3, 6, 7}; // la gamme du début : 0 = mi, 1 = fa, 2 = fa# ... etc
	private int rangeIterator; //sert parfois pour naviguer la gamme dans l'ordre.
	
	private int twinFactorUp; //pour l'harmonisation
	private int twinFactorDown;
	
	private boolean od; //overdrive on/off
	private boolean twin; //harmonie on/off
	
	private AudioInputStream mainsong; // les AudioInputStream de tout l'audio.
	private AudioInputStream[] odNotes;
	private AudioInputStream[] clnNotes;

	private File mainSongFile; //les fichiers de tout l'audio.
	private File[] cleangtr;
	private File[] odgtr;
	
	private Clip song; // les Clips de tout l'audio.
	private Clip[] odClips;
	private Clip[] clnClips;
	
	private ArrayList<MusicalEvent> events;
	
	private MusicalEvent currentEvent;
	private int timer;
	private long sp; // la position en frame de la chanson.

	private boolean isPaused;

	MonsterAudioSystem() throws UnsupportedAudioFileException, IOException, LineUnavailableException{
			
	
		timer = 0;
		mainSongFile = new File("snd/mayhem2.wav");
		cleangtr = new File[23];
		odgtr = new File[23];
		
		odNotes = new AudioInputStream[23];
		clnNotes = new AudioInputStream[23];
		
		odClips = new Clip[23];
		clnClips = new Clip[23];

		
		mainsong = AudioSystem.getAudioInputStream(mainSongFile);
		song = AudioSystem.getClip();
		od = false;

		
		for (int i = 0; i < cleangtr.length; i++)
		{
			cleangtr[i] = new File("snd/clean/" + i + ".wav");
			odgtr[i] = new File("snd/od/"+ i + ".wav");
			odNotes[i] = AudioSystem.getAudioInputStream(odgtr[i]);
			clnNotes[i] = AudioSystem.getAudioInputStream(cleangtr[i]);
			odClips[i] = AudioSystem.getClip();
			odClips[i].open(odNotes[i]);
			odClips[i].loop(0);

			clnClips[i] = AudioSystem.getClip();
			clnClips[i].open(clnNotes[i]);
			clnClips[i].loop(0); //ce qu'on entend au lancement.
		}
		System.out.println("Done.");
		
		events = new ArrayList<MusicalEvent>();
		addEvents();
			
		song.open(mainsong);
		
		currentEvent = events.get(0);
		sp = 0;
		rangeIterator = 0;
		twinFactorUp = 5;
		twinFactorDown = 7;
		
		isPaused = false;
		twin = false;

	}
	
	/**
	 * Ajoute les évènements musicaux.
	 * @see MusicalEvent
	 */
	private void addEvents() {
		
		int[] rangeA = {7,9,10,13,14};
		int[] rangeEm = {0, 1, 3, 4};
		int[] rangeEm5 = {7, 8, 10, 11};
		int[] rangeEm12 = {10,11,14,15};
		int[] reggaeRange = {0,2,3,5,7,8,10,12};
		int[] reggaeRange12 = {12,14,15,17,19,20,22};
		int[] metalE = {10, 12, 14, 15};
		int[] all = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22};

		events.add(new MusicalEvent(true, false, 696320, range, "ev0-odStart"));
		events.add(new MusicalEvent(true, false, 1329664, rangeA, "ev1-modA"));
		events.add(new MusicalEvent(false, false, 3204096, rangeEm, "ev2-Em"));
		events.add(new MusicalEvent(true, false, 3833856, rangeEm, "ev3-Em+"));
		events.add(new MusicalEvent(true, false, 4472320, rangeEm5, "ev4-Em+5"));
		events.add(new MusicalEvent(true, false, 5080064, rangeEm12, "ev5-Em+"));
		events.add(new MusicalEvent(false, false, 6182400, reggaeRange, "ev6-reggae1"));
		events.add(new MusicalEvent(false, false, 8701440, reggaeRange12, "ev7-reggae2"));
		events.add(new MusicalEvent(true, false, 10890752, metalE, "ev8-metal1"));
		events.add(new MusicalEvent(true, false, 11523584, metalE, "ev9-metal2"));
		events.add(new MusicalEvent(false, false, 11836416, reggaeRange, "ev10-reggae3"));
		events.add(new MusicalEvent(false, false, 12467200, reggaeRange, "ev11-reggae4"));
		events.add(new MusicalEvent(true, false, 13399552, metalE, "ev12-metal3"));
		events.add(new MusicalEvent(true, false, 14025216, metalE, "ev13-metal4"));
		events.add(new MusicalEvent(true, false, 14346752, metalE, "ev14-metal5"));
		events.add(new MusicalEvent(true, false, 14965248, metalE, "ev15-metal6"));
		events.add(new MusicalEvent(true, true, 15282688, reggaeRange, "ev16-metal7"));
		events.add(new MusicalEvent(true, true, 16537088, reggaeRange12, "ev17-metal8"));
		events.add(new MusicalEvent(true, true, 17792512, reggaeRange12, "ev18-metal9"));
		events.add(new MusicalEvent(true, true, 19044352, reggaeRange, "ev19-metal10"));
		events.add(new MusicalEvent(true, true, 19676672, reggaeRange12, "ev20-metal11"));
		events.add(new MusicalEvent(true, true, 21050368, all, "end"));
		


	}

	/**
	 * Toutes les 4 update (appelé par MonsterFrame.actionPerformed()), le système récupère la position en frame de la chanson.
	 * Ce n'était pas possible à chaque update, cela étant beaucoup trop gourmand en ressource.
	 */
	void update()
	{
		if (!isPaused)
		{
			timer++;
			if (timer%16 == 0) 
			{
				sp = getSongPosition();
				eventManager(sp);
			}
		}
	}
	
	/**
	 * Cette fonction s'occupe d'activer les évènements musicaux, les changements de gamme et l'activation des effets (twinNote et Overdrive).
	 * @param sp2 le temps de la chanson
	 */
	private void eventManager(long sp2) {

		for (Iterator<MusicalEvent> it = events.iterator(); it.hasNext();)
		{
			MusicalEvent e = it.next();
			if (sp2 >= e.getTime() && !e.hasLaunched())
			{
				e.setLaunched(true);
				this.od = e.getOverdrive();
				this.twin = e.getTwinNotes();
				this.range = e.getRange();
				this.currentEvent = e;
			}
		}
		
	}

	/**
	 * Retourne le dernier enregistrement de la position de la chanson (une fois tous les quatre update). Permet d'éviter l'utilisation de GetSongPosition(), trop gourmand en ressources.
	 * @return long la position de la chanson au moment de l'update.
	 */
	public long getSP() {
		return sp;
	}
	
	/**
	 * Joue une note de musique aléatoire ou non, dans la gamme définie par range.
	 * 
	 * @throws LineUnavailableException
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public void play() throws LineUnavailableException, IOException, UnsupportedAudioFileException
	{
		int note = (int)(Math.random()*range.length);
		int note2 = (int)(Math.random()*range.length);
		
		if(!isPaused)
		{
			if (currentEvent.getId() == 2)
			{
//				System.out.println("Linear progression activated.");
				if (rangeIterator == range.length-1)
				{
					rangeIterator = 0;
					invertRange();
				}
				else
				{
					rangeIterator++;
				}
				note = rangeIterator;
			}

			if (twin) //mode harmonique : joue une note supplémentaire dans la gamme + 5 ou -7 (tierce).
			{	
				if (range[note] + twinFactorUp < 22)
				{
					note2 = range[note]+twinFactorUp;
				}
				else if (range[note] - twinFactorDown > 0)
				{
					note2 = range[note]-twinFactorDown;
				}					
			}

			if (od) //mode overdrive
			{
				Clip clip = odClips[range[note]];
				clip.loop(1);
				if (twin)
				{
					Clip clip2 = odClips[note2];
					clip2.loop(1);
				}
			}
			else //mode clean
			{
				Clip clip = clnClips[range[note]];
				clip.loop(1);
				if (twin)
				{
					Clip clip2 = clnClips[note2];
					clip2.loop(1);
				}
			}
			
		}

	}

	/**
	 * Inverse la gamme mélodique.
	 */
	private void invertRange() {
		
		int[] newRange = new int[range.length];
		int newRangeIterator = 0;
		for (int i = range.length-1; i >-1; i--)
		{
			newRange[newRangeIterator] = range[i];
			newRangeIterator++;
		}
		range = newRange;
	}

	/**
	 * Retourne la position en frame de la chanson.
	 * @return long la position en frame de la chanson.
	 */
	public long getSongPosition()
	{
		return song.getLongFramePosition();
	}
	
	public MusicalEvent getEvent()
	{
		return currentEvent;
	}
	
	public int getTime(){
		return timer;
	}
	
	/**
	 * Pour tests mélodiques.
	 */
	public void toggleTwin()
	{
		twin = true;
//		if (twinFactorUp == 5 && twinFactorDown == 7)
//		{
//			twinFactorUp = 4;
//			twinFactorDown = 8;
//			System.out.println("up : Major 3rd");
//		}
//		else if (twinFactorUp == 4 && twinFactorDown == 8)
//		{
//			twinFactorUp = 7;
//			twinFactorDown = 2;
//			System.out.println("up : 5th");
//		}
//		else if (twinFactorUp == 7 && twinFactorDown == 2)
//		{
//			twinFactorUp = 3;
//			twinFactorDown = 9;
//			System.out.println("up : Minor 3rd");
//		}
//		else if (twinFactorUp == 3 && twinFactorDown == 9)
//		{
//			twinFactorUp = 5;
//			twinFactorDown = 7;
//			System.out.println("up : 4th");
//		}
	
	}


	@Override
	public void pause() {

		if (!isPaused)
		{
			song.stop();
			isPaused=true;
		}
		else
		{
			song.start();
			isPaused = false;
		}
	}

	public boolean getTwin() {
		return twin;
	}
	

}
