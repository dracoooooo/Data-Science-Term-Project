/* 
 * 	$Id: BoardPanel.java,v 1.1 2007/09/06 14:51:49 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package mirroruniverse.sim;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public final class MUBoardPanel extends JPanel implements MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Point2D MouseCoords;

	private MUBoard board;
	private MUMap mumMap;
	private int intXLength;
	private int intYLength;
	private int[][] aintFog;

	private static int cacheWidth = 0;
	Cursor curCursor;
	
	private double dblPixelsPerGrid;
	private boolean blnFog = false;

	public MUBoardPanel() {
		this.setPreferredSize(new Dimension(60, 60));
		this.setBackground(Color.white);
		addMouseMotionListener(this);

	}

	Rectangle2D boardBox = null;
	public static Line2D debugLine = null;

	final Color default_color = new Color(0, 65, 18);
	final Color default_danger_color = new Color(255, 0, 0);
	final Color ocrean_color = new Color(205, 221, 229);
	final Color visible_color = new Color(205,0,229);
	
	@Override
	public void paint(Graphics g) {
		if (board == null)
			return;
		int w = (int) MUBoard.toScreenSpaceNoOffset(1);
		if (w != cacheWidth)
			cacheWidth = w;
		int[][] counts = new int[MUGameConfig.d * 2 + 1][MUGameConfig.d * 2 + 1];
		for (int i = 0; i < counts.length; i++) {
			for (int j = 0; j < counts.length; j++)
				counts[i][j] = 0;
		}
		Graphics2D g2D = (Graphics2D) g;
		g2D.setColor(ocrean_color);
		boardBox = new Rectangle2D.Double(
				MUBoard.toScreenSpace(MUGameConfig.d * -1),
				MUBoard.toScreenSpace(MUGameConfig.d * -1),
				MUBoard.toScreenSpace(MUGameConfig.d + 1),
				MUBoard.toScreenSpace(MUGameConfig.d + 1));
		g2D.fill(boardBox);
		g2D.setColor(Color.black);
		g2D.fillOval((int) MUBoard.toScreenSpace(0),
				(int) MUBoard.toScreenSpace(0),
				(int) MUBoard.toScreenSpaceNoOffset(1),
				(int) MUBoard.toScreenSpaceNoOffset(1));
		
		if(onPlayer)
		{
			g2D.setColor(visible_color);
			double tx = MUBoard.toScreenSpace(MouseCoords.getX() - MUGameConfig.r);
			double ty = MUBoard.toScreenSpace(MouseCoords.getY()- MUGameConfig.r );
			Ellipse2D visibleBox = new Ellipse2D.Double(tx,ty, MUBoard.toScreenSpaceNoOffset(MUGameConfig.r*2 + 1) , MUBoard.toScreenSpaceNoOffset(MUGameConfig.r*2 + 1) );
			g2D.fill(visibleBox);
		}
		g2D.setColor(Color.black);
		
		drawObstacles( g2D );
		drawPlayer( g2D );
		drawExit( g2D );
		drawGrid(g2D);
		
		if ( blnFog )
		{
			drawFog( g2D );
		}

	}
	
	public void setFog( boolean blnFogOn )
	{
		this.blnFog = blnFogOn;
	}
	
	private void drawPlayer( Graphics2D g2D )
	{
		int[] aintPlayerP = mumMap.getPosition();
		g2D.setPaint( Color.orange );
	    Rectangle2D rect = new Rectangle2D.Double( toScreenSpaceNoOffset( aintPlayerP[ 0 ] ), toScreenSpaceNoOffset( aintPlayerP[ 1 ] ), toScreenSpaceNoOffset( 1 ), toScreenSpaceNoOffset( 1 ) );
        g2D.draw(rect);
        g2D.fill(rect);
	}
	
	private void drawExit( Graphics2D g2D )
	{
		int[] aintExitP = mumMap.getExit();
		g2D.setPaint( Color.green );
	    Rectangle2D rect = new Rectangle2D.Double( toScreenSpaceNoOffset( aintExitP[ 0 ] ), toScreenSpaceNoOffset( aintExitP[ 1 ] ), toScreenSpaceNoOffset( 1 ), toScreenSpaceNoOffset( 1 ) );
        g2D.draw(rect);
        g2D.fill(rect);
	}
	
	private void drawObstacles( Graphics2D g2D )
	{
		int[][] aintMap = mumMap.getMap();
		g2D.setPaint( Color.gray );
	       
		for ( int x = 0; x < intXLength; x ++ )
		{
			for ( int y = 0; y < intYLength; y ++ )
			{
				if ( aintMap[ y ][ x ] == 1 )
				{
					Rectangle2D rect = new Rectangle2D.Double( toScreenSpaceNoOffset( x ), toScreenSpaceNoOffset( y ), toScreenSpaceNoOffset( 1 ), toScreenSpaceNoOffset( 1 ) );
			        g2D.draw(rect);
			        g2D.fill(rect);
				}
			}
		}
		
	}
	
	private void drawFog(Graphics2D g2D) 
	{
		g2D.setPaint( Color.BLACK );
	       
		for ( int x = 0; x < intXLength; x ++ )
		{
			for ( int y = 0; y < intYLength; y ++ )
			{
				if ( aintFog[ y ][ x ] == 1 )
				{
					Rectangle2D rect = new Rectangle2D.Double( toScreenSpaceNoOffset( x ), toScreenSpaceNoOffset( y ), toScreenSpaceNoOffset( 1 ), toScreenSpaceNoOffset( 1 ) );
			        g2D.draw(rect);
			        g2D.fill(rect);
				}
			}
		}
	}
	
	private void drawGrid(Graphics2D g2D) {
		g2D.setColor(Color.black);
//		for (int i = -intXD; i <= intXD + 1; i++) 
		for (int i = 0; i <= intYLength; i++)
		{
			Line2D line = new Line2D.Double(0, toScreenSpaceNoOffset(i),
					toScreenSpaceNoOffset(mumMap.getXLength() ),
					toScreenSpaceNoOffset(i));
			g2D.draw(line);
		}
		for ( int i = 0; i <= intXLength; i ++ )
		{
			Line2D line = new Line2D.Double(toScreenSpaceNoOffset(i), 0,
					toScreenSpaceNoOffset(i),
					toScreenSpaceNoOffset(mumMap.getYLength()));
			g2D.draw(line);
		}
	}

	public void setBoard( String strBoardConfig ) 
	{
//		String[] astrBoardConfig = strBoardConfig.split( ", " );
//		int intXLength = Integer.parseInt( astrBoardConfig[ 0 ] );
//		int intYLength = Integer.parseInt( astrBoardConfig[ 1 ] );
//		long lngMapSeed = Long.parseLong( astrBoardConfig[ 2 ] );
//		double dblThresh = java.lang.Double.parseDouble( astrBoardConfig[ 3 ] );
//		long lngInitialSeed = Long.parseLong( astrBoardConfig[ 4 ] );
//		int intSightRadius = Integer.parseInt( astrBoardConfig[ 5 ] );
		MUMapConfig mmcMap = new MUMapConfig( strBoardConfig );
		mumMap = new MUMap( mmcMap );
		this.intXLength = mmcMap.getIntXLength();
		this.intYLength = mmcMap.getIntYLength();
		board = new MUBoard( 10, 10 );
		aintFog = new int[ intYLength ][ intXLength ];
		for ( int i = 0; i < this.intXLength; i ++ )
		{
			for ( int j = 0; j < this.intYLength; j ++ )
			{
				aintFog[ j ][ i ] = 1;
			}
		}
		updateFog( mumMap.getLocation() );
		this.repaint();
	}

	public int[] getBoardLocation()
	{
		return mumMap.getLocation();
	}
	
	public boolean getFinished()
	{
		return mumMap.getMapOver();
	}
	
	public boolean move( int intDirection )
	{
		boolean moved = mumMap.move( intDirection );
		int[] aintLocation = mumMap.getLocation();
		updateFog( aintLocation );
		this.repaint();
		return moved;
	}

	private void updateFog(int[] aintLocation) 
	{
		int intSightRadius = mumMap.getSightRadius();
		for ( int i = aintLocation[ 0 ] - intSightRadius; i <= aintLocation[ 0 ] + intSightRadius; i ++ )
		{
			for ( int j = aintLocation[ 1 ] - intSightRadius; j <= aintLocation[ 1 ] + intSightRadius; j ++ )
			{
				if ( i >= 0 && j >= 0 && i < intXLength && j < intYLength )
				{
					aintFog[ j ][ i ] = 0;
				}
			}
		}
	}

	Line2D selectedLine = null;

	private int floorAbs(double v) {
		int r = (int) Math.abs(v);
		if (v < 0)
			r = 0 - r - 1;
		return r;
	}
	boolean onPlayer = false;
	Point mouseLoc = null;
	public void mouseMoved(MouseEvent e) {
		MouseCoords = MUBoard.fromScreenSpace(e.getPoint());
		MouseCoords = new Point2D.Double(floorAbs(MouseCoords.getX()),
				floorAbs(MouseCoords.getY()));
//		if (engine != null)
//			engine.mouseChanged();
//		String tip = "<html><b>Location:</b> (" + MouseCoords.getX() + ", " + MouseCoords.getY() + ")"+(MouseCoords.getX()==MouseCoords.getY() && MouseCoords.getX()==0 ? " <b>BOAT</B>" : "")+"<br><b>Sealife here:</b><br>";
//		int n=0;
//		if(engine != null && engine.getConfig() != null)
//		{
//			for(SeaLife s : engine.getConfig().creatures)
//			{
//				if(s.getLocation().equals(MouseCoords))
//				{
//					tip+=s.toString()+"<br>";
//					n++;
//				}
//			}
//			if(n==0)
//				tip+="<i>No sealife here</i><br>";
//			n =0;
//			tip +="<b>Players here:</b><br>";
//			onPlayer = false;
//			for(Player p : engine.players)
//			{
//				if(p.location.equals(MouseCoords))
//				{
//					onPlayer = true;
//					tip+=p.toString()+"<br>";
//					n++;
//				}
//			}
//			if(n==0)
//				tip+="<i>No players here</i><br>";
//		}
//		tip += "</html>";
//		this.setToolTipText(tip);
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void recalculateDimensions() {
		int my_w = this.getWidth();
		int my_h = this.getHeight();
		int d = Math.min(my_w, my_h);
		d -= 10;
		if (d > 0)
			dblPixelsPerGrid = d / ((double) ( intXLength > intYLength ? intXLength : intYLength ) );
		repaint();
	}
	
	private double toScreenSpaceNoOffset(double v) {
		return (v) * dblPixelsPerGrid;
	}
}
