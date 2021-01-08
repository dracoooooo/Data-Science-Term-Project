package mirroruniverse.sim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MirrorUniverse 
{
	static Player plrCurrent;
	
	public static void main( String[] args )
	{
		FileWriter frtReplay = null;
		try {
			frtReplay = new FileWriter("replays/" + "LastGame" + ".txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedWriter bfrReplay = new BufferedWriter( frtReplay );
		
		Scanner scnGame = null;
		try {
			scnGame = new Scanner( new File( "game/GameConfig.txt" ) );
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String strConfigL = scnGame.nextLine();
		String strConfigR = scnGame.nextLine();
//		String strConfigL = "5, 5, 2, -1, maps/testMap.txt, 5";
//		String strConfigR = "6, 6, 2, 10, 0.1, 2";
		
		MUMapConfig mmcL = new MUMapConfig( strConfigL );
		MUMapConfig mmcR = new MUMapConfig( strConfigR );
		MUMap mumMapL = new MUMap( mmcL );
		MUMap mumMapR = new MUMap( mmcR );
		
		try {
			bfrReplay.write( mumMapL.getConfigForGui() + "\n" );
			bfrReplay.write( mumMapR.getConfigForGui() + "\n" );
			bfrReplay.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File filPlayersClasses = new File( "player/player.txt" );
		Scanner scnPlayersClasses = null;
		try 
		{
			scnPlayersClasses = new Scanner( filPlayersClasses );
		} 
		catch (FileNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			System.out.println( "Failed to load Players Classes File" );
			e1.printStackTrace();
		}
		while ( scnPlayersClasses.hasNextLine()) 
		{
			String strPlayerClass = scnPlayersClasses.nextLine();
			
			if ( strPlayerClass.startsWith( "//" ) )
			{
				continue;
			}
			
			try 
			{
				plrCurrent = ( Player ) Class.forName( strPlayerClass ).newInstance();
//				mprNew.startNewMapping( intMappingLength );
			} 
			catch (ClassNotFoundException e) 
			{
				System.out.println( "Problem loading Players' classes" );
//				log.error("[Configuration] Class not found: " + t);
			} 
			catch (InstantiationException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IllegalAccessException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int intStep = 0;
		while ( !mumMapL.getMapOver() || !mumMapR.getMapOver() )
		{
			int[][] aintViewL = mumMapL.getView();
			int[][] aintViewR = mumMapR.getView();
			int intMove = -1;
			try {
				intMove = plrCurrent.lookAndMove( aintViewL, aintViewR );
			//	System.out.println( intStep + ":::::" );
				if ( intStep != 0 )
				{
					bfrReplay.write( ", " );
				}
				bfrReplay.write( "" + intMove );
				bfrReplay.flush();
			} catch (Exception e) {
				System.out.println("Write out!");
				try {
					bfrReplay.flush();
					bfrReplay.close();
					frtReplay.flush();
					frtReplay.close();
				} catch (Exception e2) {}
				e.printStackTrace();
			}
			mumMapL.move( intMove );
			mumMapR.move( intMove );
			intStep ++;
		}
		int intStepL = mumMapL.getStep();
		int intStepR = mumMapR.getStep();
		int intStepMax = intStepL > intStepR ? intStepL : intStepR;
		int intStepMin = intStepL > intStepR ? intStepR : intStepL;
		int intStepDiff = intStepMax - intStepMin;
		if ( intStepMax != intStep )
			System.out.println( "Step inconsistent" );
		
		try {
			bfrReplay.write( "\n" + intStepDiff + ", " + intStepMax );
			bfrReplay.close();
			frtReplay.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
