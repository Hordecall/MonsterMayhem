package monsterMayhem;

import static org.junit.Assert.*;


import org.junit.Test;

/**
 * Petite classe de test.
 * Je n'ai pas trouvé beaucoup de fonctions a tester via JUnit, tout étant principalement graphique.
 * @author hero
 *
 */
public class MonsterAudioSystemTest {

	@Test
	public void rangeTest() {
			
		int[] testRange = {0, 2, 4, 6, 10};
		int[] returnedRange = invertRange(testRange);
		
		int[] expectedRange = {10,6,4,2,0};
		
		for (int i = 0; i < testRange.length; i++)
		{
			assertEquals(returnedRange[i], expectedRange[i]);
		}
		
	}
	
	private int[] invertRange(int[] range) {
		
		int[] newRange = new int[range.length];
		int newRangeIterator = 0;
		for (int i = range.length-1; i >-1; i--)
		{
			newRange[newRangeIterator] = range[i];
			newRangeIterator++;
		}
		return newRange;
	}

}
