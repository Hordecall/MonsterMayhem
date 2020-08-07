package monsterMayhem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioSystem;
import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.midi.MidiDevice.Info;
import javax.swing.Timer;

/**
 * La version MIDI du système de son, et la première version du système audio.
 * Malheureusement, cette implémenation requiert que l'utilisateur ait une interface MIDI installée et activée, et a donc été abandonnée.
 * @deprecated
 * @author hero
 *
 */
public class AsteroidAudioSystem {

	static Sequencer sequencer;
	static Synthesizer synth;
	static Receiver receiver;
	static Transmitter transmitter;
	MidiDevice device;
	MidiChannel[] sounds;
	Instrument[] insts;
	
	int[] timbre ={42, 43, 49, 50};
	int timbreIterator = 0;
	int musicIterator = 0;
	int prog = 0;
	private Timer[] musicTime;

	private boolean midiEnabled, music4on, music8on, music16on, music32on;
	
	private AudioInputStream audio;
	
	
	private Clip song;

	AsteroidAudioSystem() throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		
		music4on = false;
		music8on = false;
		music16on = false;
		music32on = false;
				
		try {
			addAudio();
			System.out.println("Audio has been created.");
			midiEnabled = true;
			
		} catch (MidiUnavailableException | InvalidMidiDataException e1) {
			System.out.println("Audio loading has failed. This might happen if you do not have a MIDI system enabled.");
			midiEnabled = false;
		}
		
		if (midiEnabled)
		{
			ActionListener music1 = new ActionListener(){
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
								
					sounds[15].allNotesOff();
					sounds[15].noteOn(timbre[timbreIterator], 80);
					timbreIterator++;
					
					if (timbreIterator == timbre.length)
					{
						timbreIterator = 0;
					}
					
					
				}
				
			};
			
			ActionListener music4 = new ActionListener(){
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if (music4on)
					{
						sounds[11].allNotesOff();
						int[] melody = {64,66,67,73,74};					
						int s = melody[(int) (Math.random()*melody.length)];
						sounds[11].noteOn(s, 40);			
					}				
				}
				
			};
			

			
			ActionListener music8 = new ActionListener(){
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if (music8on)
					{
						sounds[14].allNotesOff();
						int[] melody = {52,54,55,61,62,64,66,67};
						int s = melody[(int) (Math.random()*melody.length)];
						sounds[14].noteOn(s, 60);			
					}				
				}
				
			};
			
			ActionListener music16 = new ActionListener(){
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if (music16on)
					{
						sounds[13].allNotesOff();
						int[] melody = {52,54,55,61,62,64,66,67};
						int s = melody[(int) (Math.random()*melody.length)];
						sounds[13].noteOn(s, 60);			
					}				
				}
				
			};
			
			ActionListener music32 = new ActionListener(){
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if (music32on)
					{
						sounds[1].allNotesOff();
						int[] melody = {64,66,67,73,74,76,78,79};
						int s = melody[(int) (Math.random()*melody.length)];
						sounds[1].noteOn(s, 40);			
					}				
				}
				
			};
			
			musicTime = new Timer[5];	
			
			musicTime[0] = new Timer(1000, music4);
			musicTime[1] = new Timer(4000, music1);
			musicTime[2] = new Timer(500, music8);
			musicTime[3] = new Timer(250, music16);
			musicTime[4] = new Timer(125, music32);
			musicTime[0].start();
			musicTime[1].start();
			musicTime[2].start();
			musicTime[3].start();
			musicTime[4].start();
		}
	
	}
	
	/**
	 * Initialise l'audio à être implémenté au conteneur de jeu.
	 * <p>Vous devez avoir un driver MIDI installé sans quoi cela ne marchera pas.</p>
	 * @throws MidiUnavailableException, InvalidMidiDataException
	 * @throws IOException 
	 * @throws UnsupportedAudioFileException 
	 * @throws LineUnavailableException 
	 */
	private void addAudio() throws MidiUnavailableException, InvalidMidiDataException, UnsupportedAudioFileException, IOException, LineUnavailableException {
	
		sequencer = MidiSystem.getSequencer();
		synth = MidiSystem.getSynthesizer();
		transmitter = MidiSystem.getTransmitter();
		receiver = MidiSystem.getReceiver();
		
		Info[] midiDevices = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < midiDevices.length; i++)
		{
			System.out.println(midiDevices[i].getDescription());
		}
		
		insts = synth.getAvailableInstruments();
		sounds = synth.getChannels();
		transmitter.setReceiver(receiver);		
		synth.loadInstrument(insts[0]);
		synth.open();
		
		sounds[15].programChange(53);
		sounds[14].programChange(123);
		sounds[13].programChange(20);
		sounds[1].programChange(90);
		sounds[11].programChange(41);
		
		URL url = this.getClass().getClassLoader().getResource("snd/mayhem.wav");
		audio = AudioSystem.getAudioInputStream(url);
		song.open(audio);
		song.start();
		

	}
}
