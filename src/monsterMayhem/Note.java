package monsterMayhem;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

/**
 * Tentative d'implémentation d'une classe pour les notes jouées lors d'un tir, chaque note étant gérée par un Thread qui s'occuperait d'ouvrir et de fermer les AudioInput.
 * Au final, cette classe n'a pas été implémentée, étant beaucoup plus lourde pour le jeu que la solution actuelle.
 * @deprecated
 * @author hero
 *
 */
public class Note implements Runnable
{
    private boolean running = false;
    private Thread thread;
    private boolean playSong = false;
    private AudioInputStream inputStream;
//    private String url;
    private Clip clip;
    private File file;
    private String mod;
    
    public Note(int note, String mod)
    {
    	this.mod = mod;
    	
    	switch (mod)
    	{
    	case "od":
    		file = new File("snd/od/"+note+".wav");
    		break;
    		
    	case "clean":
    		file = new File("snd/cln/"+note+".wav");
    		break;
    		
    	default:
    		file = null;
    		break;  			
    	}
    	
        this.start();
    }

    public void start()
    {
        if(running)
            return;
        this.thread = new Thread(this);
        this.running = true;
        this.thread.start();
    }

    @Override
    public void run()
    {
        while(running)
        {
            if(inputStream == null && playSong)
            {
                this.playSong = false;
                try
                {
                    this.inputStream = AudioSystem.getAudioInputStream(file);
                    this.clip.open(inputStream);
                    this.clip.loop(0);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void play()
    {
        if (this.clip != null)
        {
            this.clip.stop();
            this.clip.close();
        }
        try
        {
            this.clip = AudioSystem.getClip();
        }
        catch(LineUnavailableException e)
        {
            e.printStackTrace();
        }
        this.playSong = true;
        this.inputStream = null;
    }

    public void disposeSound()
    {
        if(this.clip != null)
        {
            this.clip.stop();
            this.clip.close();
        }
        this.clip = null;
        this.playSong = false;
        this.inputStream = null;
    }
}