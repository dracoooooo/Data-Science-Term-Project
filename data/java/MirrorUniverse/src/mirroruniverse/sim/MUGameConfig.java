/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mirroruniverse.sim;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Random;


public class MUGameConfig {

	int gameDelay = 500;
	int number_of_rounds;
	int current_round;
	int penalty = 0;
	
	public long getRandomSeed() {
		return randomSeed;
	}
	public void setRandomSeed(long randomSeed) {
		this.randomSeed = randomSeed;
		random = new Random(randomSeed);
	}
	public static int d = 20;
	public static int r = 2;
	int num_divers = 20;
	long randomSeed = System.currentTimeMillis();
	
	public int getNumDivers() {
		return num_divers;
	}
	public void setNumDivers(int num_divers) {
		this.num_divers = num_divers;
	}
	public void setD(int d) {
		
		MUGameConfig.d = d;
	}

	public int getD() {
		return d;
	}

	public void setR(int r) {
		MUGameConfig.r = r;
	}

	public int getR() {
		return r;
	}

	String selectedBoard = null;
	int max_rounds = -1;
	public static Random random;
	private ArrayList<File> availableBoards;

//	private Logger log = Logger.getLogger(this.getClass());
	int num_lights = 5;

	public static final int max_rounds_max = 4000;

	/**
	 * Obtain the list of all valid boards in the location specified by the xml
	 * configuration file.
	 * 
	 * @return An array of valid board files.
	 */
	public File[] getBoardList() {
		File[] ret = new File[availableBoards.size()];
		return availableBoards.toArray(ret);
	}

	public void setMaxRounds(int v) {
		this.max_rounds = v;
	}

	public int getMaxRounds() {
		return max_rounds;
	}

	public MUGameConfig() 
	{
	}

	/**
	 * Read all xml files from the board directory. Accept them only if valid.
	 * 
	 */
	public void readBoards() {
		availableBoards.clear();
		String s = "boards";

		File dir = new File(s);

		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().endsWith(".xml");
			}
		});
		/* Board b = new Board(1,1); */
		for (int i = 0; i < files.length; i++) {
			/*
			 * try{ b.load(files[i]); availableBoards.add(files[i]);
			 * }catch(IOException e){ log.error("Problem loading board file " +
			 * files[i]); } catch(BoardSanityException e){
			 * log.error("Sanity problem loading board file " +files[i]+". " +
			 * e); }
			 */
			availableBoards.add(files[i]);
		}
	}

}
