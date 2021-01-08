package mirroruniverse.sim;

public class MUMapConfig 
{
	String strMapConfig;
	int intXLength;
	int intYLength;
	long lngMapSeed;
	double dblThresh;
	long lngInitialSeed;
	int intSightRadius;
	String strMapPath = null;
	
	public MUMapConfig( String strMapConfig ) 
	{
		this.strMapConfig = strMapConfig;
		
		String[] astrMapConfig = strMapConfig.split( ", " );
		intXLength = Integer.parseInt( astrMapConfig[ 0 ] );
		intYLength = Integer.parseInt( astrMapConfig[ 1 ] );
		intSightRadius = Integer.parseInt( astrMapConfig[ 2 ] );
		lngMapSeed = Long.parseLong( astrMapConfig[ 3 ] );
		if ( lngMapSeed >= 0 )
		{
			dblThresh = java.lang.Double.parseDouble( astrMapConfig[ 4 ] );
			lngInitialSeed = Long.parseLong( astrMapConfig[ 5 ] );
		}
		else
		{
			strMapPath = astrMapConfig[ 4 ];
		}
	}
	
	public String getMapPath()
	{
		return strMapPath;
	}

	public String getStrMapConfig() {
		return strMapConfig;
	}

	public int getIntXLength() {
		return intXLength;
	}

	public int getIntYLength() {
		return intYLength;
	}

	public long getLngMapSeed() {
		return lngMapSeed;
	}

	public double getDblThresh() {
		return dblThresh;
	}

	public long getLngInitialSeed() {
		return lngInitialSeed;
	}

	public int getIntSightRadius() {
		return intSightRadius;
	}
}
