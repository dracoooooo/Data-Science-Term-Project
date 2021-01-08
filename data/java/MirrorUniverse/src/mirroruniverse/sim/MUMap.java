package mirroruniverse.sim;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MUMap 
{
	private int[][] aintMap;
	private int intXLength;
	private int intYLength;
	private double dblThresh;
	Random rdmGen;
	private int intPositionX = -1;
	private int intPositionY = -1;
	private int intSightRadius;
	private int intExitX = -1;
	private int intExitY = -1;
	private boolean blnMapOver = false;
	private long lngMapSeed;
	private long lngInitialSeed;

	private String strMapPath = null;
	
	private int intStep = 0;
	
	public static final int[][] aintDToM = { { 0, 0 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 },  { -1, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 } };
	public static final int[][] aintMToD = { { 4, 3, 2 }, { 5, 0, 1 }, { 6, 7, 8 } };
	
//	protected MUMap( int intXLength, int intYLength, long lngMapSeed, double dblThresh, long lngInitialSeed, int intSightRadius )
//	{
//		this.intXLength = intXLength;
//		this.intYLength = intYLength;
//		this.dblThresh = dblThresh;
//		rdmGen = new Random( lngMapSeed );
//		this.intSightRadius = intSightRadius;
////		boolean blnMapValid = false;
////		while ( !blnMapValid )
////		{
////			aintMap = new int[ intYLength ][ intXLength ];
////			generateRandomMap();
////			blnMapValid = checkMapConnection();
////		}
////		rdmGen = new Random( lngInitialSeed );
////		while ( !inMap( intExitX, intExitY ) || aintMap[ intExitY ][intExitX ] == 1 )
////		{
////			intExitX = rdmGen.nextInt( intXLength );
////			intExitY = rdmGen.nextInt( intYLength );
////		}
////		aintMap[ intExitY ][ intExitX ] = -1;
////		while ( !inMap( intPositionX, intPositionY ) || aintMap[ intPositionY ][intPositionX ] != 0 )
////		{
////			intPositionX = rdmGen.nextInt( intXLength );
////			intPositionY = rdmGen.nextInt( intYLength );
////		}
//		if ( lngMapSeed >= 0 )
//		{
//			rdmGen = new Random( lngMapSeed );
//			boolean blnMapValid = false;
//			while ( !blnMapValid )
//			{
//				aintMap = new int[ intYLength ][ intXLength ];
//				generateRandomMap();
//				blnMapValid = checkMapConnection();
//			}
//			rdmGen = new Random( lngInitialSeed );
//			while ( !inMap( intExitX, intExitY ) || aintMap[ intExitY ][intExitX ] == 1 )
//			{
//				intExitX = rdmGen.nextInt( intXLength );
//				intExitY = rdmGen.nextInt( intYLength );
//			}
//			aintMap[ intExitY ][ intExitX ] = -1;
//			while ( !inMap( intPositionX, intPositionY ) || aintMap[ intPositionY ][intPositionX ] != 0 )
//			{
//				intPositionX = rdmGen.nextInt( intXLength );
//				intPositionY = rdmGen.nextInt( intYLength );
//			}
//		}
//		else
//		{
//			Scanner scnMapPath = new Scanner( System.in );
//			System.out.println( "Going to use appointed map, input the path:" );
//			String strPath = scnMapPath.nextLine();
//			aintMap = new int[ intYLength ][ intXLength ];
//			generateAppointedMap( strPath );
//		}		
//	}
	
	protected MUMap( MUMapConfig mmcConfig )
	{
		int intXLength = mmcConfig.getIntXLength();
		int intYLength = mmcConfig.getIntYLength();
		long lngMapSeed = mmcConfig.getLngMapSeed();
		double dblThresh = mmcConfig.getDblThresh();
		long lngInitialSeed = mmcConfig.getLngInitialSeed();
		int intSightRadius = mmcConfig.getIntSightRadius();
		String strMapPath = mmcConfig.getMapPath();
		
		this.intXLength = intXLength;
		this.intYLength = intYLength;
		this.dblThresh = dblThresh;
		this.intSightRadius = intSightRadius;
		this.lngMapSeed = lngMapSeed;
		this.lngInitialSeed = lngInitialSeed;
		if ( lngMapSeed >= 0 )
		{
			rdmGen = new Random( lngMapSeed );
			boolean blnMapValid = false;
			while ( !blnMapValid )
			{
				aintMap = new int[ intYLength ][ intXLength ];
				generateRandomMap();
				blnMapValid = checkMapConnection();
			}
			rdmGen = new Random( lngInitialSeed );
			while ( !inMap( intExitX, intExitY ) || aintMap[ intExitY ][intExitX ] == 1 )
			{
				intExitX = rdmGen.nextInt( intXLength );
				intExitY = rdmGen.nextInt( intYLength );
			}
			aintMap[ intExitY ][ intExitX ] = 2;
			while ( !inMap( intPositionX, intPositionY ) || aintMap[ intPositionY ][intPositionX ] != 0 )
			{
				intPositionX = rdmGen.nextInt( intXLength );
				intPositionY = rdmGen.nextInt( intYLength );
			}
		}
		else
		{
			this.strMapPath = strMapPath;
			generateAppointedMap( strMapPath );
		}
		
	}
	
 	private void generateAppointedMap( String strPath ) 
 	{
		Scanner scnMap = null;
		try {
			scnMap = new Scanner( new File( strPath ) );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList< String > alMap = new ArrayList< String >();
		int intLineCount = 0;
		while ( scnMap.hasNextLine() )
		{
			alMap.add( scnMap.nextLine() );
			intLineCount ++;
		}
//		if ( intLineCount != intYLength )
//		{
//			System.out.println( "Inconsistent map height" );
//		}
		intYLength = intLineCount;
		for ( int j = 0; j < intYLength; j ++ )
		{
			String strMapRow = alMap.get( j ).trim();
			String[] astrMapRow = strMapRow.split( " " );
//			if ( astrMapRow.length != intXLength )
//			{
//				System.out.println( "Inconsistent map breadth" );
//			}
			if ( j == 0 )
			{
				intXLength = astrMapRow.length;
				aintMap = new int[ intYLength ][ intXLength ];
			}
			for ( int i = 0; i < intXLength; i ++ )
			{
				int intMapGrid = Integer.parseInt( astrMapRow[ i ] );
				if ( intMapGrid >= 0 && intMapGrid <= 1 )
				{
					aintMap[ j ][ i ] = intMapGrid;	
				}
				if ( intMapGrid == 2 )
				{
					intExitX = i;
					intExitY = j;
					aintMap[ j ][ i ] = intMapGrid;
				}
				if ( intMapGrid == 3 )
				{
					intPositionX = i;
					intPositionY = j;
				}
			}
		}
	}
 	
 	public int getSightRadius()
 	{
 		return intSightRadius;
 	}
	
	public String getConfigForGui()
	{
		String strReturn = "";
		strReturn += intXLength + ", " + intYLength + ", " + intSightRadius + ", " + lngMapSeed + ", ";
		if ( lngMapSeed >= 0 )
		{
			strReturn += dblThresh + ", " + lngInitialSeed;
		}
		else
		{
			strReturn += strMapPath;
		}
		return strReturn;
	}

	public int[][] getView()
	{
		int[][] aintView = new int[ intSightRadius * 2 + 1 ][ intSightRadius * 2 + 1 ];
		for ( int y = intPositionY - intSightRadius; y <= intPositionY + intSightRadius; y ++ )
		{
			for ( int x = intPositionX - intSightRadius; x <= intPositionX + intSightRadius; x ++ )
			{
				if ( !inMap( x, y ) )
				{
					aintView[ y - intPositionY + intSightRadius ][ x - intPositionX + intSightRadius ] = 1;
				}
				else
				{
					aintView[ y - intPositionY + intSightRadius ][ x - intPositionX + intSightRadius ] = aintMap[ y ][ x ];
				}
			}
		}
		return aintView;
	}
	
	public boolean getMapOver()
	{
		return blnMapOver;
	}
	
	protected int[][] getMap()
	{
		return aintMap;
	}
	
	protected int[] getPosition()
	{
		int[] aintPosition = { intPositionX, intPositionY };
		return aintPosition;
	}
	
	protected int[] getExit()
	{
		int[] aintExit= { intExitX, intExitY };
		return aintExit;
	}
	
	protected boolean move( int intDeltaX, int intDeltaY )
	{
		if ( !blnMapOver )
		{
			if ( intDeltaX < -1 || intDeltaX > 1 || intDeltaY < -1 || intDeltaY > 1 )
				return false;
			int x = intPositionX + intDeltaX;
			int y = intPositionY + intDeltaY;
			boolean moved = false;
			if ( inMap( x, y ) && aintMap[ y ][ x ] != 1 )
			{
				intPositionX = x;
				intPositionY = y;
				moved = true;
			}
			blnMapOver = checkExit();
			intStep ++;
			return moved;
		}
		return false;
	}
	
	public int getStep()
	{
		return intStep;
	}
	
	private boolean checkExit()
	{
		if ( intPositionX == intExitX && intPositionY == intExitY )
		{
			return true;
		}
		return false;
	}
	
	protected boolean move( int intDirection )
	{
		int[] aintMove = aintDToM[ intDirection ];
		int intDeltaX = aintMove[ 0 ];
		int intDeltaY = aintMove[ 1 ];
		return move( intDeltaX, intDeltaY );
	}
	
	protected int[] getLocation()
	{
		int[] aintLocation = new int[ 2 ];
		aintLocation[ 0 ] = intPositionX;
		aintLocation[ 1 ] = intPositionY;
		return aintLocation;
	}
	
	protected int getXLength()
	{
		return intXLength;
	}
	
	protected int getYLength()
	{
		return intYLength;
	}
	
	private boolean inMap( int x, int y )
	{
		if ( x < 0 || y < 0 || x >= intXLength || y >= intYLength )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	private void generateRandomMap()
	{
		for ( int y = 0; y < intYLength; y ++ )
		{
			for ( int x = 0; x < intXLength; x ++ )
			{
				double dblRandom = rdmGen.nextDouble();
				if ( dblRandom < dblThresh )
				{
					aintMap[ y ][ x ] = 1;
				}
			}
		}
	}
	
	private boolean checkMapConnection()
	{
		int[][] aintReached = new int[ intYLength ][ intXLength ];
		int intSubRootX = -1;
		int intSubRootY = -1;
		
		for ( int y = 0; y < intYLength; y ++ )
		{
			for ( int x = 0; x < intXLength; x ++ )
			{
				aintReached[ y ][ x ] = aintMap[ y ][ x ];
				if ( aintMap[ y ][ x ] == 1 )
				{
					intSubRootX = x;
					intSubRootY = y;
				}
			}
		}
		
		if ( intSubRootX == -1 || intSubRootY == - 1 )
		{
			return false;
		}
		
		bfsMap( aintReached, intSubRootX, intSubRootY );
		
		for ( int y = 0; y < intYLength; y ++ )
		{
			for ( int x = 0; x < intXLength; x ++ )
			{
				if ( aintReached[ y ][ x ] == 0 )
				{
					return false;
				}
			}
		}
		return true;
	}
	
	private void bfsMap( int[][] aintReached, int intSubRootX, int intSubRootY )
	{
		Coordinate cdnTemp = new Coordinate();
		cdnTemp.intX = intSubRootX;
		cdnTemp.intY = intSubRootY;
		ArrayDeque< Coordinate > adBFS = new ArrayDeque< Coordinate >();
		adBFS.add( cdnTemp );
		aintReached[ intSubRootY ][ intSubRootX ] = 1;
		while ( !adBFS.isEmpty() )
		{
			Coordinate cdnCurrent = adBFS.poll();
//			System.out.print( "\\\t" );
//			for ( int j = 0; j < intYLength; j ++ )
//				System.out.print( j + "\t" );
//			System.out.println();
//			for ( int j = 0; j < intXLength; j ++ )
//			{
//				System.out.print( j + "\t" );
//				for ( int i = 0; i < intYLength; i ++ )
//				{
//					System.out.print( aintReached[ j ][ i ] + "\t" );
//				}
//				System.out.println();
//			}
//			System.out.println();
			for ( int deltaX = -1; deltaX <= 1; deltaX ++ )
			{
				for ( int deltaY = -1; deltaY <= 1; deltaY ++ )
				{
					int X = cdnCurrent.intX + deltaX;
					int Y = cdnCurrent.intY + deltaY;
					if ( X < 0 || Y < 0 || X >= intXLength || Y >= intYLength )
					{
						continue;
					}
					if ( aintReached[ Y ][ X ] == 1 )
					{
						continue;
					}
					Coordinate cdnNew = new Coordinate();
					cdnNew.intX = X;
					cdnNew.intY = Y;
					adBFS.add( cdnNew );
					aintReached[ Y ][ X ] = 1;
					
				}
			}
		}
	}
	
	private class Coordinate
	{
		int intX;
		int intY;
		
	}
	
	@SuppressWarnings("unused")
	private void dfsMap( int[][] aintReached, int intSubRootX, int intSubRootY )
	{
		aintReached[ intSubRootY ][ intSubRootX ] = 1;
		for ( int deltaX = -1; deltaX <= 1; deltaX ++ )
		{
			for ( int deltaY = -1; deltaY <= 1; deltaY ++ )
			{
				int X = intSubRootX + deltaX;
				int Y = intSubRootY + deltaY;
				if ( X < 0 || Y < 0 || X >= intXLength || Y >= intYLength )
				{
					continue;
				}
				if ( aintReached[ Y ][ X ] == 1 )
				{
					continue;
				}
				dfsMap( aintReached, X, Y );
			}
		}
	}
}
