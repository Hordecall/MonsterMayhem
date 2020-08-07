package monsterMayhem;

import javax.swing.JOptionPane;

/**
 * Une classe d'exception, qui ne sert à rien, vu qu'il n'y a pas d'exceptions.
 * @author hero
 *
 */
public class TerribleMistake extends Exception {

	private static final long serialVersionUID = -5683978693595980660L;

	TerribleMistake(String message)
	{
		JOptionPane.showMessageDialog(null, message, "Uh oh !", JOptionPane.ERROR_MESSAGE);
		System.out.println(message);
	}
}
