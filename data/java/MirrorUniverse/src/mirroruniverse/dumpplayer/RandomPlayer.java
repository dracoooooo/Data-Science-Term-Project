package mirroruniverse.dumpplayer;

import java.util.Random;

import mirroruniverse.sim.MUMap;
import mirroruniverse.sim.Player;

public class RandomPlayer implements Player
{
	boolean blnLOver = false;
	boolean blnROver = false;
	
	public int lookAndMove( int[][] aintViewL, int[][] aintViewR )
	{
		int[][] aintLocalViewL = new int[ 3 ][ 3 ];
		int intMid = aintViewL.length / 2;
		for ( int i = -1; i <= 1; i ++ )
		{
			for ( int j = -1; j <= 1; j ++ )
			{
				aintLocalViewL[ 1 + j ][ 1 + i ] = aintViewL[ intMid + j ][ intMid + i ];
				if ( aintLocalViewL[ 1 + j ][ 1 + i ] == -1 )
				{
					if ( i == 0 && j == 0 )
						continue;
					blnLOver = true;
					return MUMap.aintMToD[ j + 1 ][ i + 1 ];
				}
			}
		}
		
		int[][] aintLocalViewR = new int[ 3 ][ 3 ];
		for ( int i = -1; i <= 1; i ++ )
		{
			for ( int j = -1; j <= 1; j ++ )
			{
				aintLocalViewR[ 1 + j ][ 1 + i ] = aintViewR[ intMid + j ][ intMid + i ];
				if ( aintViewR[ intMid + j ][ intMid + i ] == -1 )
				{
					if ( i == 0 && j == 0 )
						continue;
					blnROver = true;
					return MUMap.aintMToD[ j + 1 ][ i + 1 ];
				}
			}
		}
		
		Random rdmTemp = new Random();
		int intD;
		int intDeltaX;
		int intDeltaY;
		if ( !blnLOver )
		{
			do
			{
				intD = rdmTemp.nextInt( 8 ) + 1;
				intDeltaX = MUMap.aintDToM[ intD ][ 0 ];
				intDeltaY = MUMap.aintDToM[ intD ][ 1 ];
			} while ( aintLocalViewL[ 1 + intDeltaY ][ 1 + intDeltaX ] == 1 );
		}
		else
		{
			do
			{
				intD = rdmTemp.nextInt( 8 ) + 1;
				intDeltaX = MUMap.aintDToM[ intD ][ 0 ];
				intDeltaY = MUMap.aintDToM[ intD ][ 1 ];
			} while ( aintLocalViewR[ 1 + intDeltaY ][ 1 + intDeltaX ] == 1 );
		}
		return intD;
	}
}
