package monsterMayhem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * Classe de fenêtre principale, conteneur du jeu et de tous les éléments dont elle gère les mises à jour.
 * 
 * Cette classe recoit les entrées claviers.
 * @author Hordecall
 */
public class MonsterFrame implements KeyListener, Pause, ActionListener {

	private static final int starting_size = 20;
	private JFrame frame;
	private JLabel console, customImage;
	private Ship ship;
	private Timer time;
	
	private int score;
	private HighScores highScores;
	
	private ArrayList<Asteroid> asteroids;
	private ArrayList<Shot> shots;
	private ArrayList<Explosion> explosions;
	private ArrayList<GameEvent> events;

	private Background background;
	private JLabel start;
	private ScoreEntry scoreEntry;
	private MonsterAudioSystem audio;
	private int respawnTimer, invincibilityTimer;
	private boolean isPaused, scoresShown, gameStarted;
	private boolean funkyTime, twinShot, pierceShot, bounceShot;


	
	MonsterFrame() throws FontFormatException, IOException, UnsupportedAudioFileException, LineUnavailableException
	{
		frame = new JFrame("Monster Mayhem");

		frame.getContentPane().setBackground(Color.black);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		System.out.println("Frame created.");
		
		asteroids = new ArrayList<Asteroid>();
			
		frame.addKeyListener(this);
		
		addStartupMenu();
		addBackground();
		addConsole();
		addAudio();
		addIntro();
		addShip();
		addScores();
		
		shots = new ArrayList<Shot>();
		explosions = new ArrayList<Explosion>();
		events = new ArrayList<GameEvent>();
		
		addEvents();

		frame.validate();
		
		isPaused = false;
		scoresShown = false;
		funkyTime = false;
		twinShot = false;
		pierceShot = false;
		bounceShot = false;
		
		pause();
		gameStarted = false;
		
		frame.setSize(1024, 768);
		frame.setVisible(true);
								
		time = new Timer(20, this);
		time.start();
				
		score = 0;
		
	}
	
	/**
	 * La fonction "update" du programme. Déclenchée toutes les 20ms par le Timer time, elle appelle tour à tour les fonctions update de l'audio, des asteroids, des tirs et du vaisseau, et calcul les collisions.
	 */
	@Override
	public void actionPerformed(ActionEvent evt)
	{
				
			if (!isPaused)
			{
				audio.update();				
				long sp = audio.getSP();		
				eventManager(sp);									
				ship.update();
				shipManager();
				asteroidManager();
				shotManager();
			
				try
				{
					collisionListener();
				} catch (IndexOutOfBoundsException e)
				{
					System.out.println("Une erreur est survenue dans la manipulation des objets dans les ArrayList."); //cela arrivait parfois lorsque les objets étaient retirés au milieu des boucles d'itérations dans les collisions, mais a été réglé par cleanUp().
				}
				
				explosionManager();
				
				console.setText("Current Score : " + score);
				scoreManager();
				
				cleanUp();
									
				frame.getContentPane().setComponentZOrder(background, frame.getContentPane().getComponents().length-1);	//Pour une raison obscure, le background s'affiche toujours devant tous les autres objets sans cette fonction.	
				frame.revalidate();
				frame.repaint();
				
			}
					
		
	}

	/**
	 * Fonction qui assure le nettoyage des élements qui n'apparaissent plus ou qui ont été marqués à enlever.
	 */
	private void cleanUp() {

		Component[] comps = frame.getContentPane().getComponents();
		for (int i = 0; i < comps.length; i++)
		{
			if (comps[i] instanceof GameElement)
			{
				GameElement toClean = (GameElement) comps[i];
				if (toClean.toRemove())
				{
					frame.remove(toClean);
					
					if (toClean instanceof Shot)
					{
						shots.remove(toClean);
					}
					else if (toClean instanceof Asteroid)
					{
						asteroids.remove(toClean);
					}
				}

			}
		}
		
		shots.trimToSize();
		asteroids.trimToSize();
		
	}

/**
 * Surveillance des collisions. Appellée dans l'actionPerformed du timer du constructeur, elle s'éxécute toutes les 20ms, comme défini par le Timer time.
 * 
 */
	private void collisionListener(){
		
		
		if (ship.getCurrentX() >= 1004 || ship.getCurrentX() <= 0)
		{
			ship.setDeltaX(-ship.getDeltaX());
		}
		
		if (ship.getCurrentY() >= 738 || ship.getCurrentY() <= 0)
		{
			ship.setDeltaY(-ship.getDeltaY());
		}
		
		//Collision Ship/Asteroid, depuis Ship (calcul des points de la Shape de l'Asteroid contenu par la Shape du vaisseau, plus pratique dans le cas de petits asteroides)
		for (int i = 0; i < asteroids.size(); i++)
		{
			
			for (int l = 0; l < asteroids.get(i).getNewShape().npoints; l++)
				
			{
				if (ship.getNewShape().contains(asteroids.get(i).getNewShape().xpoints[l], asteroids.get(i).getNewShape().ypoints[l]) && ship.isAlive() && !ship.isInvincible())
				{
					console.setText("Hit by Asteroid #" + i + " at point : " + asteroids.get(i).getNewShape().xpoints[l]);
					explosions.add(new Explosion((int)ship.getCurrentX(), (int)ship.getCurrentY()));
					ship.kill();
					score -= 2000;
					ship.setRemove(true);
				}
				
				
			}			
		}
		
		//Collision Asteroid/Ship, depuis Asteroid (calcul des points de la Shape du vaisseau contenus dans la Shape de l'Asteroid, plus précis dans le cas de gros astéroides)
		for (int i = 0; i < asteroids.size(); i++)
		{
			for (int j = 0; j < ship.getNewShape().npoints; j++)
			{
				if (asteroids.get(i).getNewShape().contains(ship.getNewShape().xpoints[j], ship.getNewShape().ypoints[j]) && ship.isAlive() && !ship.isInvincible())
				{
					ship.kill();
					score -= 2000;
					ship.setRemove(true);
					
					explosions.add(new Explosion((int)ship.getCurrentX(), (int)ship.getCurrentY()));

				}
			}
		}
		
		//Collision Shot/Asteroid, depuis Asteroid (calcul des points de la shape des tirs contenus dans la Shape de l'Asteroid)
		if (!shots.isEmpty())
		{				
			for (int i = 0; i < asteroids.size(); i++)
			{
				for (int j = 0; j < shots.size(); j++)
				{
					for (int k = 0; k < shots.get(j).getNewShape().npoints; k++)
					{
						if (asteroids.get(i).getNewShape().contains(shots.get(j).getNewShape().xpoints[k], shots.get(j).getNewShape().ypoints[k]) && !shots.get(j).hasCollided())
						{
							if (!pierceShot)
								shots.get(j).hasCollided(true);
							
							if (funkyTime)
							{
								asteroids.get(i).setRandomColor();
							}
							
							int size = asteroids.get(i).getAsteroidSize();

							if (size >= 20)
							{
								score +=50;
								console.setText(String.valueOf(score));
							}
							else if (size < 20 && size > 1)
							{
								score +=200;
								console.setText(String.valueOf(score));
							}
							else if (size <= 1)
							{
								score +=500;
								console.setText(String.valueOf(score));
							}
							
							asteroids.get(i).hp -= 1;
							
							if (asteroids.get(i).hp == 0)
							{
//								System.out.println("Asteroid #" + k + " is down. Asteroids left : " + asteroids.size());
								breakUp(asteroids.get(i));
							}
							
//							frame.remove(shots.get(j));
//							shots.remove(j);
							if (!pierceShot)
								shots.get(j).setRemove(true);
						}
					}
				}
			}
		}
	}

	/**
	 * Cette fonction s'occupe des effets d'explosions, de leur mise à jour et de leur retrait.
	 */
	private void explosionManager() {
		
		if (!explosions.isEmpty())
		{
			for (int i = 0; i < explosions.size(); i++)
			{
				if (funkyTime)
				{
					explosions.get(i).setLifeFactor(1);
				}
				explosions.get(i).update();
				explosions.get(i).setVisible(true);
				frame.getContentPane().add(explosions.get(i));
				if (!explosions.get(i).isAlive())
				{
					frame.getContentPane().remove(explosions.get(i));
					explosions.remove(i);
				}
			}
		}
	}

	/**
	 * Cette fonction met à jour les objets Shot et les retire si ils sortent de l'aire de jeu.
	 */
	private void shotManager() 
	{		
		for (int i = 0; i < shots.size(); i++)
		{
			Shot s = shots.get(i);
			s.update();
			if (s.getCurrentX() > 1024 || s.getCurrentX() < 0 || s.getCurrentY()<0 || s.getCurrentY()>768)
			{
				if(!bounceShot)
				{
					frame.remove(s);
					shots.remove(i);
				}
				else
				{
					if (s.getCurrentX() > 1024 || s.getCurrentX() < 0)
					{
						s.setDeltaX(-shots.get(i).getDeltaX());
						s.setAngle(-s.getAngle());
					}
					if (s.getCurrentY() > 768 || s.getCurrentY() < 0)
					{
						s.setDeltaY(-shots.get(i).getDeltaY());
						s.setAngle(-s.getAngle());
					}
				}
			}
		}		
	}

	/**
	 * Cette fonction s'occupe de mettre à jour les asteroides.
	 */
	private void asteroidManager() {
		
		for (int i = 0; i < asteroids.size(); i++)
		{
			asteroids.get(i).update();
		}
		
	}

	/**
	 * Cette fonction s'occupe de remettre le vaisseau en vie et d'enlever l'invincibilité avec deux timers incrémentés par l'update.
	 */
	private void shipManager() {
		
		if (!ship.isAlive())
		{
			respawnTimer++;
			if (respawnTimer == 50)
			{
				System.out.println("Respawn !");
				ship = new Ship();
				ship.setVisible(true);
				frame.getContentPane().add(ship);
				frame.revalidate();
				frame.repaint();
				respawnTimer = 0;
			}
		}
		
		if (ship.isInvincible() && ship.isAlive())
		{
			invincibilityTimer++;
			if (invincibilityTimer == 100)
			{
				System.out.println("Vulnerable again !");
				ship.setInvincible(false);
				invincibilityTimer = 0;
			}
		}
		
		if (ship.getLocation().x > 1024 || ship.getLocation().x < 0 || ship.getLocation().y > 768 || ship.getLocation().y < 0)
		{
			ship.setLocation(200,200);
		}
		
	}

	/**
	 * Cette fonction s'occupe d'afficher les scores à la fin de la partie après l'entrée du nom de l'utilisateur.
	 */
	private void scoreManager() {

		if (scoreEntry != null)
		{
			if (scoreEntry.isDone())
			{
				System.out.println("Game is over !");
				scoreEntry.setVisible(false);

				frame.setVisible(true);
				try {
					toggleScores();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
	}

	/**
	 * Cette fonction s'occupe de faire la transition entre les Events et le moteur du jeu.
	 * Elle récupère les attributs des Event, qu'elle analyse et donne des instructions en fonction.
	 * @see GameEvent
	 * @see AbstractEvent
	 * @param sp la position dans le temps de la chanson principale.
	 */
	private void eventManager(long sp) {
	
		for (int i = 0; i < events.size(); i++)
		{
			if (sp >= events.get(i).getTime() && !events.get(i).hasLaunched())
			{
				events.get(i).setLaunched(true);
				if (events.get(i).getAction().equalsIgnoreCase("breakup"))
				{
					if(!asteroids.isEmpty())
					{
						for (int j = 0; j < asteroids.size(); j++)
						{
							breakUp(asteroids.get(j));
						}
					}
				}
				else if (events.get(i).getAction().equalsIgnoreCase("bomb"))
				{
					if (!asteroids.isEmpty())
						bomb();
				}
				else if (events.get(i).getAction().equalsIgnoreCase("dangerousBomb"))
				{
					if (!asteroids.isEmpty())
						absoluteBomb();
				}
				else if (events.get(i).getAction().contains("intro"))
				{
					introManager(events.get(i).getAction());
				}
				else if (events.get(i).getAction().contains("backgroundChange"))
				{
					this.background.toggleFunky();
				}
				else if (events.get(i).getAction().contains("asteroidAttack")) //change tous les asteroides en rouge et augmente leur vitesse
				{
					for (int j = 0; j < asteroids.size(); j++)
					{
						asteroids.get(j).setColor(Color.red);
						asteroids.get(j).setDeltaX(asteroids.get(j).getDeltaX()*4);
						asteroids.get(j).setDeltaY(asteroids.get(j).getDeltaY()*4);
						asteroids.get(j).setDx(asteroids.get(j).getDx()*2);
						asteroids.get(j).setDy(asteroids.get(j).getDy()*2);
						ship.setInvincible(true);
					}
				}
				else if (events.get(i).getAction().contains("slowDown")) // ralentis tous les asteroides et réduit leur vitesse
				{
					for (int j = 0; j < asteroids.size(); j++)
					{
						asteroids.get(j).setRandomColor();
						asteroids.get(j).setDeltaX(asteroids.get(j).getDeltaX()/2);
						asteroids.get(j).setDeltaY(asteroids.get(j).getDeltaY()/2);
						ship.setInvincible(true);
					}
				}
				else if (events.get(i).getAction().contains("playerAttack")) // active les tirs perçants et le double tir
				{
					this.twinShot = true;
					this.pierceShot = true;
					this.ship.setInvincible(true);
				}
				
				else if (events.get(i).getAction().equalsIgnoreCase("boss"))
				{
					absoluteBomb();
					addAsteroids(2, 80, Color.BLACK);
				}
				
				else if (events.get(i).getAction().equalsIgnoreCase("boss2"))
				{
					pierceShot = true;
					bounceShot = true;
					
					addAsteroids(1, 100, Color.black);
				}
				
				else if (events.get(i).getAction().equalsIgnoreCase("shotsExplode"))
				{
					for (int l = 0; l < shots.size(); l++)
					{
						Shot s = shots.get(l);
						Explosion x = new Explosion((int) s.getCurrentX(),(int) s.getCurrentY());
						x.setVisible(true);
						frame.getContentPane().add(x);
						s.setRemove(true);
						frame.validate();
						frame.repaint();
						
					}
				}
				else if (events.get(i).getAction().equalsIgnoreCase("scoreSheet"))
				{
					try {
						addScoreEntry();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (FontFormatException e) {
						e.printStackTrace();
					}
				}
				
				else
				{
					addAsteroids(events.get(i).getNumberOfAsteroids(), events.get(i).getSize(), events.get(i).getColor());
					ship.setInvincible(true);
				}
				
				if (events.get(i).getName().contains("reggae"))
				{
					System.out.println("Funky time !");
					funkyTime = true;
				}
				else
					funkyTime = false;
				
			}
		}
		
	}
	/**
	 * Ajoute l'interface de saisie de nom à l'écran.
	 * @see ScoreEntry 
	 * @throws IOException
	 * @throws FontFormatException
	 */
	private void addScoreEntry() throws IOException, FontFormatException {
	
		this.scoreEntry = new ScoreEntry(score, highScores);
		scoreEntry.setLocation(360,200);
		frame.add(scoreEntry, BorderLayout.CENTER);
	
	}

	/**
	 * Gère les évènements arrivant pendant l'intro (le changement d'image des JPanel).
	 * @param action
	 */
	private void introManager(String action) {
		
		if (action.equalsIgnoreCase("intro"))
		{
			customImage.setVisible(true);
		}
		else if (action.equalsIgnoreCase("introA"))
		{
			ImageIcon image = new ImageIcon("res/customnobody2.png");
			this.customImage.setIcon(image);
		}
		else if (action.equalsIgnoreCase("introB"))
		{
			ImageIcon image = new ImageIcon("res/customnobody3.png");
			this.customImage.setIcon(image);
		}
		else if (action.equalsIgnoreCase("introC"))
		{
			ImageIcon image = new ImageIcon("res/customnobody4.png");
			this.customImage.setIcon(image);
		}
		else if (action.equalsIgnoreCase("introD"))
		{
			ImageIcon image = new ImageIcon("res/customnobody5.png");
			this.customImage.setIcon(image);
		}
		else if (action.equalsIgnoreCase("introE"))
		{
			ImageIcon image = new ImageIcon("res/customnobody6.png");
			this.customImage.setIcon(image);
		}
		else if (action.equalsIgnoreCase("introF"))
		{
			ImageIcon image = new ImageIcon("res/customnobody7.png");
			this.customImage.setIcon(image);
		}
		else if (action.equalsIgnoreCase("introG"))
		{
			ImageIcon image = new ImageIcon("res/customnobody8.png");
			this.customImage.setIcon(image);
		}
		else if (action.equalsIgnoreCase("introH"))
		{
			customImage.setVisible(false);
		}
	
}

	/**
	 * Méthode appellée lorsqu'un Asteroide n'a plus de vie.
	 * Si l'asteroide est petit (de taille 1), il est juste marqué à détruire, sinon, il génère en plus 3 plus petits asteroides.
	 * @param l'objet de classe Asteroid qui n'a plus de vie
	 */
	private void breakUp(Asteroid asteroid) {
		
		Asteroid oldsteroid = asteroid;
		
		if ((int) (oldsteroid.size/4) == 1)
		{
			oldsteroid.setRemove(true);
		}
		
		else
		{
			addAsteroids(asteroid, 3);
			oldsteroid.setRemove(true);
		}
		
		Explosion exp = new Explosion((int)oldsteroid.getCurrentX(),(int) oldsteroid.getCurrentY());
		exp.validate();
		exp.setVisible(true);
		explosions.add(exp);
		frame.getContentPane().add(exp);
		
		frame.validate();
		frame.repaint();

	}

	/**
	 * Ajoute des asteroides.
	 * @param int nombre d'asteroids, int taille asteroids , Color la couleur
	 */
	private void addAsteroids(int number, int size, Color color) {
	
		int sx = 0; int sy = 0;
		Color asteroidColor = color;
		
		if (color == null)
		{
			asteroidColor = Color.ORANGE;
		}
				
		for (int i = 0; i < number; i++)
		{
			boolean rightfulPlaceX = false;
			boolean rightfulPlaceY = false;
			
			while(!rightfulPlaceX)
			{
				sx = (int) (Math.random()*1004+10);
				if (sx > 682 || sx < 341)
					rightfulPlaceX = true;
			}
			
			while(!rightfulPlaceY)
			{
				sy = (int) (Math.random()*748+10);
				if (sy < 768/3 || sy <768-(768/3))
					rightfulPlaceY = true;
			}
			
			double svx = Math.random();
			double svy = Math.random();
			
			if (Math.random()>0.5)
			{
				svx = -svx;
			}
			
			if (Math.random()>0.5)
			{
				svy = -svy;
			}
			
			if (asteroidColor == Color.GREEN)
			{
				svx/=4; svy/=4;
			}
			
			Asteroid a = new Asteroid(sx, sy, Math.random()*2, svx, svy, size, asteroidColor);
			asteroids.add(a);
			a.init();
			a.setSize(1024,768);
			a.validate();
			a.setVisible(true);
			frame.getContentPane().add(a);
			frame.revalidate();
			frame.repaint();
			
		}
		
	}

	/**
	 * Ajoute des asteroides, en fonction du père.
	 * @param father l'Asteroide où vont apparaitre les nouveaux asteroides.
	 * @param number le nombre d'Asteroides à faire apparaitre.
	 */
	private void addAsteroids(Asteroid father, int number)
	{
		Asteroid[] newAsteroids = new Asteroid[number];
		for (int i = 0; i < newAsteroids.length; i++)
		{
			newAsteroids[i] = new Asteroid((int) father.getCurrentX() , (int) father.getCurrentY(), Math.PI, father.getDeltaX()*1.5*getRandomNumber(), father.getDeltaY()*1.5*getRandomNumber(), father.size/2, father.getColor());
			newAsteroids[i].init();
			newAsteroids[i].validate();
			newAsteroids[i].setVisible(true);
			asteroids.add(newAsteroids[i]);
			frame.getContentPane().add(newAsteroids[i]);
			frame.getContentPane().validate();
			frame.getContentPane().repaint();
		}
		asteroids.remove(father);
		asteroids.trimToSize();
	}

	/**
	 * Ajoute un vaisseau (Ship) au conteneur de jeu. Le vaisseau est contrôlé par le clavier.
	 * @see Ship
	 */
	private void addShip()
	{
		ship = new Ship();
		invincibilityTimer = 0;
		ship.setSize(1024,746);
		frame.getContentPane().add(ship);
		System.out.println("Ship installed.");
	}

	/**
	 * Ajoute les Events.
	 * @see Event
	 * @see MusicalEvent
	 * @see GameEvent
	 */
	private void addEvents()
	{
		events.add(new GameEvent("intro", 70656, "intro"));
		events.add(new GameEvent("introA", 219648, "introA"));
		events.add(new GameEvent("introB", 381440, "introB"));
		events.add(new GameEvent("introC", 543232, "introC"));
		events.add(new GameEvent("introD", 574976, "introD"));
		events.add(new GameEvent("introE", 606720, "introE"));
		events.add(new GameEvent("introF", 638464, "introF"));
		events.add(new GameEvent("introG", 696320, "introG"));
		events.add(new GameEvent("introH", 745472, "introH"));
		events.add(new GameEvent(4, starting_size, null, 696320, "start"));
		events.add(new GameEvent(2, starting_size, null, 1329664, "modu1"));
		events.add(new GameEvent(1, starting_size, null, 1949184, "buildup1"));
		events.add(new GameEvent(1, starting_size, null, 2042080, "buildup2"));		
		events.add(new GameEvent(1, starting_size, null, 2113024, "buildup3"));
		events.add(new GameEvent(1, starting_size, null, 2194432, "buildup4"));
		events.add(new GameEvent(1, starting_size, null, 2267136, "buildup5"));
		events.add(new GameEvent(1, starting_size, null, 2341888, "buildup6"));
		events.add(new GameEvent(1, starting_size, null, 2420736, "buildup7"));
		events.add(new GameEvent("breakup", 2585088, "breakup1"));
		events.add(new GameEvent("bomb", 2727946, "bomb1"));
		events.add(new GameEvent("bomb", 2888704, "bomb2"));
		events.add(new GameEvent("bomb", 3046400, "bomb3"));
		events.add(new GameEvent(1, 50, Color.red, 3211264, "red1"));
		events.add(new GameEvent(1, 50, Color.red, 3833856, "red2"));
		events.add(new GameEvent(1, 50, Color.red, 4472320, "red3"));
		events.add(new GameEvent(1, 50, Color.red, 5080064, "redAnew"));
		events.add(new GameEvent(1, 50, Color.red, 5764096, "redAnew"));
		events.add(new GameEvent("dangerousBomb", 6031360, "endRed"));
		events.add(new GameEvent(1, 200, Color.GREEN, 6182400, "reggae1"));	
		events.add(new GameEvent("backgroundChange", 6182400, "reggae2"));
		events.add(new GameEvent("asteroidAttack", 10890752, "metal1"));
		events.add(new GameEvent(1, 50, Color.red, 11523584, "metal2"));
		events.add(new GameEvent("backgroundChange", 11836416, "reggae3"));
		events.add(new GameEvent("slowDown", 11836416, "reggae3"));
		events.add(new GameEvent(1, 20, Color.GREEN, 11836416, "reggae3"));
		events.add(new GameEvent(1, 20, Color.GREEN, 12467200, "reggae4"));
		events.add(new GameEvent(1, 20, Color.red, 13399552, "metal3"));
		events.add(new GameEvent("backgroundChange", 13399552, "metal3"));
		events.add(new GameEvent("asteroidAttack", 13399552, "metal3"));
		events.add(new GameEvent("playerAttack", 15282688, "solo1"));
		events.add(new GameEvent(3, 20, Color.red, 15908352, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 16220160, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 16379904, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 16532480, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 16842752, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 17004032, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 17158656, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 17490432, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 17790432, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 17942528, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 18105344, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 18265600, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 18412032, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 18574336, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 18728448, "final2"));
		events.add(new GameEvent(3, 20, Color.red, 18888704, "final2"));
		events.add(new GameEvent("boss", 19044352, "final1"));
		events.add(new GameEvent(1, 50, Color.BLACK, 19197952, "final2"));
		events.add(new GameEvent(1, 50, Color.BLACK, 19353088, "final3"));
		events.add(new GameEvent(1, 50, Color.BLACK, 19508224, "final3"));
		events.add(new GameEvent(10, 50, Color.BLACK, 19666944, "final3"));
		events.add(new GameEvent("boss2", 19676672, "final2"));
		events.add(new GameEvent(10, 20, Color.BLACK, 19988480, "final3"));
		events.add(new GameEvent(10, 20, Color.BLACK, 20142592, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20303360, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20333360, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20363360, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20393360, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20423360, "final3"));		
		events.add(new GameEvent(10, 20, Color.BLACK, 20456960, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20487168, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20517168, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20547168, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20577168, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20604928, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20634928, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20664928, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20694928, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20734928, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20764928, "final3"));	
		events.add(new GameEvent(3, 50, Color.BLACK, 20772864, "final3"));	
		events.add(new GameEvent(3, 50, Color.BLACK, 20802864, "final3"));	
		events.add(new GameEvent(3, 50, Color.BLACK, 20832864, "final3"));	
		events.add(new GameEvent(3, 50, Color.BLACK, 20862864, "final3"));	
		events.add(new GameEvent(3, 50, Color.BLACK, 20892864, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20922368, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20952368, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20982368, "final3"));	
		events.add(new GameEvent(10, 20, Color.BLACK, 20982368, "final3"));	
		
		events.add(new GameEvent("dangerousBomb", 21050368, "end"));
		events.add(new GameEvent("shotsExplode", 21050368, "end"));
		
		events.add(new GameEvent("scoreSheet", 21080368, "end"));
		
	}

	/**
	 * Ajoute un tableau des scores au conteneur de jeu.
	 * Le tableau de score est crée et caché immédiatement : il lit le fichier des scores et l'affiche.
	 * @see HighScores
	 * @throws FontFormatException
	 * @throws IOException
	 */
	private void addScores() throws FontFormatException, IOException {

		this.highScores = new HighScores();
		highScores.setSize(1024,768);
		frame.add(highScores);
		highScores.setVisible(false);
		
	}

	/**
	 * Fonction gérant l'implémentation du menu-titre.
	 * @throws FontFormatException
	 * @throws IOException
	 */
	private void addStartupMenu() throws FontFormatException, IOException {
			
			File f = new File("res/title.png");
			ImageIcon img = new ImageIcon(f.getAbsolutePath());
			start = new JLabel(img);
			start.setSize(1024,768);
			frame.getContentPane().add(start, BorderLayout.CENTER);
			start.setVisible(true);
			
		}

	/**
	 * Ajoute le JLabel pour l'intro une fois que le jeu est lancé.
	 */
	private void addIntro() {
	
		ImageIcon image = new ImageIcon(new File("res/customnobody.png").getPath());
		this.customImage = new JLabel(image);
		customImage.setVisible(false);
		customImage.setBounds(300,150, 456, 287);
		frame.getContentPane().add(customImage, BorderLayout.CENTER);
	
	}

	/**
	 * Initialise le fond étoilé et l'ajoute au conteneur de jeu.
	 * Pour une raison obscure, celui-ci se place toujours devant tout le monde : il y a donc une instruction qui le maintient au fond dans l'ActionPerformed.
	 */
	private void addBackground() {
	
			background = new Background();
			background.setSize(1024,768);
			frame.getContentPane().add(background);
		}

	/**
	 * Ajoute une console au conteneur de jeu.
	 * Devait à la base servir pour divers diagnostics, au final sert à afficher le score.
	 */
	private void addConsole()
	{	
		console = new JLabel();
		console.setBackground(Color.black);
		console.setForeground(Color.WHITE);
		console.setSize(1024, 20);
		console.setText("Monster Mayhem");
		console.validate();
		frame.getContentPane().add(console, BorderLayout.SOUTH);
		System.out.println("Console installed.");
	}
	

	
	/**
	 * Initialise l'audio à être implémenté au conteneur de jeu.
	 * 
	 * @throws MidiUnavailableException, InvalidMidiDataException
	 * @throws LineUnavailableException 
	 * @throws IOException 
	 * @throws UnsupportedAudioFileException 
	 */
	private void addAudio() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		
		System.out.println("Creating audio ...");
		audio = new MonsterAudioSystem();
	
	}

	/**
	* Méthode appellée lors de l'utilisation de la barre espace qui fait tirer le vaisseau : ajoute un objet Shot à l'array de tirs shots.
	* Envoie également une information au système audio pour qu'il joue un son ce qui justifie toutes les exceptions.
	* Pour une raison obscure, impossible d'afficher deux tirs en même temps, j'ai donc été obligé de contourner le problème en appelant deux fois la méthode sur une "ligne" différente.
	* @throws LineUnavailableException 
	* @throws IOException 
	* @throws UnsupportedAudioFileException 
	*/
	private void shoot(int line) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		
		int twinShotFactor = 0;
		if (twinShot) twinShotFactor = 5; // déplace les tirs en X en cas de double tir.
			
		if (line == 0)
		{
			if (ship.isAlive() && !isPaused)
			{
					
				Shot shoot = new Shot(ship.getAngle(), ship.getCurrentX()+twinShotFactor, ship.getCurrentY());
					
				if (pierceShot && bounceShot)
					shoot.ultimate(true); // anime les tirs pour la fin du jeu
					
				shots.add(shoot);
				frame.getContentPane().add(shoot);
				shoot.setVisible(true);
					
				frame.validate();
				frame.repaint();
					
				audio.play(); //joue l'audio
			}
		}
		else if (line == 1)
		{
			if (ship.isAlive() && !isPaused)
			{
					
				Shot shoot = new Shot(ship.getAngle(), ship.getCurrentX()-twinShotFactor, ship.getCurrentY());
				if (pierceShot && bounceShot)
					shoot.ultimate(true);
				shots.add(shoot);
				frame.getContentPane().add(shoot);
				shoot.setVisible(true);
					
				frame.validate();
				frame.repaint();
//				audio.play(); // la deuxième ligne ne fait pas de son, ceci est géré par le système audio
			}
		}
			
			
	}

	/**
	 * Casse tous les asteroides présents au moment de l'appel (pas ceux qui en seront générés).
	 * Un système d'enregistrement a été mis en place, les asteroides se cassant en créant d'autres, ce qui créait des problèmes dans la manipulation des ArrayList.
	 */
	private void bomb(){
		
		int[] registeredIDs = new int[asteroids.size()];
		for (int i = 0; i < asteroids.size(); i++)
		{
			registeredIDs[i] = asteroids.get(i).getID();
		}
		for (int i = 0; i < asteroids.size(); i++)
		{
			for (int j = 0; j < registeredIDs.length; j++)
			{
				if (asteroids.get(i).getID() == registeredIDs[j])
				{
					breakUp(asteroids.get(i));
				}
			}
		}
		
	}

	/**
	 * Détruit absolument tous les asteroides présents.
	 */
	private void absoluteBomb(){
		
		while (!asteroids.isEmpty())
		{
			bomb();
			cleanUp();
		}
	}

	/**
	 * Affiche les scores, en mettant pause au besoin.
	 * A leur affichage, les scores sont regénérés depuis le fichier.
	 * @throws IOException
	 */
	private void toggleScores() throws IOException {
		
		System.out.println("Scores on : "+scoresShown);
	
			if (!scoresShown)
			{
				System.out.println("Showing scores !");
				if(!isPaused && gameStarted) 
					pause();
				
				highScores.readFile();
				highScores.setVisible(true);
				start.setVisible(false);
				scoresShown = true;
				System.out.println("Done !");
			}
			
			else
			{
				if (isPaused && gameStarted) 
					pause();
				
				System.out.println("hiding scores");
				highScores.setVisible(false);
				
				if (!gameStarted)
					start.setVisible(true);
				
				scoresShown = false;
				
			}
		}

	/**
	 * Méthode utilisée pour générer un peu d'aléatoirité dans certaines fonctions.
	 * @return double un double compris entre -2 et 2.
	 */
	private double getRandomNumber() {
		
		double r = Math.random()*2;
		
		if (Math.random() > 0.5)
			r = -r;
		
		return r;
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	/**
	 * NOTE : beaucoup de clés ici servent au diagnostic et ne seront pas dans la version finale du projet.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
//		String s ="Key pressed : " + String.valueOf(keyCode);
//		System.out.println(s);
//		console.setText(s);
		
		if (keyCode == 72) // H : affiche les scores
		{
			try {
				toggleScores();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if (keyCode == 39) // fleche droite : tourne le vaisseau
		{
			ship.rotatingRight(true);
		}
		
		else if (keyCode == 37) // fleche gauche : tourne le vaisseau
		{
			ship.rotatingLeft(true);
		}
		
		else if (keyCode == 38) // fleche haut : augmente la vélocité
		{
			ship.isThrusting(true);
		}
	
		
		if (keyCode == 32) // espace : tire
		{
			try {
				shoot(0);
				if (twinShot)
				shoot(1);
			} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e1) {
				e1.printStackTrace();
			}
		}
		
		if (keyCode == 80) // P : met pause, et donne des informations quand aux évènements audio
		{
			System.out.println("Exact frame position : " +audio.getSongPosition());
			System.out.println("Event running : " + audio.getEvent().getName());
			System.out.println("SP : " + audio.getSP());
			pause();
		}
		
//		if (keyCode == 87)
//		{
//			absoluteBomb();
//		}
//		
//		if (keyCode == 88)
//		{
//			bomb();
//		}
		
//		if (keyCode == 65)
//		{
//			System.out.println("Asteroids Array Size : " + asteroids.size());
//			for (int i = 0; i < asteroids.size(); i++)
//			{
//				System.out.println("Asteroid #"+ i + " : " + asteroids.get(i).getID());
//			}
//		}
		
//		if (keyCode == 67)
//		{
//			Component[] frameComps = frame.getContentPane().getComponents();
//			System.out.println("Components Order : ");
//			for (int i = 0; i < frameComps.length; i++)
//			{
//				System.out.println("Comp #"+i+" : " + frameComps[i]);
//			}
//			System.out.println("Ship Z Order : " + frame.getContentPane().getComponentZOrder(ship));
//			System.out.println("Intro Z Order : " + frame.getContentPane().getComponentZOrder(customImage));
//			customImage.setVisible(true);		
//		}
		
//		if (keyCode == 84)
//		{
//			System.out.println(audio.getTime());
//			System.out.println(audio.getSongPosition());
//			System.out.println(audio.getEvent());
//			System.out.println("SP : "+ audio.getSP());
//			audio.toggleTwin();
//			System.out.println("Twin : " + audio.getTwin());
//		}
		
		
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
//		String s = "Key released : " + String.valueOf(keyCode);
//		console.setText(s);	
		
		if (keyCode == 39)
		{
			ship.rotatingRight(false);

		}
		
		else if (keyCode == 37)
		{
			ship.rotatingLeft(false);

		}
		
		else if (keyCode == 38)
		{
			ship.isThrusting(false);
		}
		
		
	}

	/**
	 * La pause de la classe principale s'occupe d'envoyer les instructions de pause à tout le monde.
	 * Généralement, cela rend l'update de chaque classe concernée inopérante.
	 */
	@Override
	public void pause() {

		if (!gameStarted) gameStarted = true;
		
		console.setText("Current Score : " + score);
		
		ship.pause();
		
		for (int i = 0; i < asteroids.size(); i++)
		{
			asteroids.get(i).pause();
		}
		
		for (int i = 0; i < shots.size(); i++)
		{
			shots.get(i).pause();
		}	
		
		audio.pause();
		isPaused = !isPaused;
		if (isPaused)
			start.setVisible(true);
		else
			start.setVisible(false);
	}
	
	public boolean getPause(){
		
		return isPaused;
	}

}
