/* 
 * 	$Id: GUI.java,v 1.4 2007/11/14 22:02:59 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package mirroruniverse.sim.ui;

import java.awt.Dimension;
import java.awt.GridLayout;

import mirroruniverse.sim.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;


public final class MUGUI extends JFrame implements ActionListener, ChangeListener
{
	private static final long serialVersionUID = 6520596470885212032L;

	private MUControlPanel controlPanel;
	private MUBoardPanel boardPanelL;
	private MUBoardPanel boardPanelR;
	private JPanel mapPanel;
	private JPanel gamePanel;
	private MUDialPanel dialPanel;
	
	private volatile boolean fast;
	private int[] aintReplay;
	private int intStepIndex;
	
	private JFileChooser chooser = new JFileChooser();
	
	private int intTotalScore;
	private int intDiffScore;
	
	public MUGUI() {
		setTitle("My Empty Frame");
		setPreferredSize(new Dimension(1200, 900));
		setMinimumSize(new Dimension(1200, 700));
		setLocation(10,200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		whoMoved = new LinkedList <Integer> ();
		manualMove = new LinkedList <Integer> ();

		controlPanel = new MUControlPanel();
		controlPanel.load.setText("Load");

		gamePanel = new JPanel(new BorderLayout());
		gamePanel.setName("Game Play");
		gamePanel.add(controlPanel, BorderLayout.NORTH);

		mapPanel = new JPanel( new GridLayout( 1, 2 ) );
		boardPanelL = new MUBoardPanel();
		boardPanelR = new MUBoardPanel();

		mapPanel.add( boardPanelL );
		mapPanel.add( boardPanelR );

		gamePanel.add(mapPanel, BorderLayout.CENTER);

		dialPanel = new MUDialPanel();
		gamePanel.add( dialPanel, BorderLayout.SOUTH );
		dialPanel.setVisible( false );

		getContentPane().add( gamePanel );
		this.pack();
		this.setVisible(true);

		setupListeners();
	}
	
	private void setupListeners() {
		controlPanel.addListener(this);
		dialPanel.addListener(this);
	}

	public static void main(String[] args) {
		new MUGUI();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		String command = arg0.getActionCommand();
		if (command.compareToIgnoreCase("Load") == 0) {
			chooser.setCurrentDirectory( new File( "replays" ) );
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					Scanner replayScanner = new Scanner( new FileInputStream( chooser.getSelectedFile()) );
					String strMapConfigL = replayScanner.nextLine();
					String strMapConfigR = replayScanner.nextLine();
					String strReplay = replayScanner.nextLine();
					String strScore = replayScanner.nextLine();
					
					boardPanelL.setBoard( strMapConfigL );
					boardPanelL.recalculateDimensions();
					boardPanelR.setBoard( strMapConfigR );
					boardPanelR.recalculateDimensions();
					this.repaint();
					
					String[] astrReplay = strReplay.split( ", " );
					aintReplay = new int[ astrReplay.length ];
					for ( int i = 0; i < astrReplay.length; i ++ )
					{
						aintReplay[ i ] = Integer.parseInt( astrReplay[ i ] );
					}
					if ( !controlPanel.manual.isSelected() )
					{
						String[] astrScore = strScore.split( ", " );
						intDiffScore = Integer.parseInt( astrScore[ 0 ] );
						intTotalScore = Integer.parseInt( astrScore[ 1 ] );
						controlPanel.setScore( intTotalScore, intDiffScore );
					}
					intStepIndex = 0;
				} catch (IOException e) {
					System.err.println("Error reading replay file:"
							+ e.getMessage());
					e.printStackTrace();
					System.exit(-1);
				}

//				JOptionPane.showMessageDialog((Frame) c,
//						"File loaded successfully.", "Success",
//						JOptionPane.INFORMATION_MESSAGE);
				// }catch(IOException e){
				// // e.printStackTrace();
				// JOptionPane.showMessageDialog( (Frame)c, e, "Load Error",
				// JOptionPane.ERROR_MESSAGE);
				// loading = false;
				// }
				if ( !controlPanel.manual.isSelected() )
				{
					controlPanel.load.setEnabled(false);
					fast = false;
					controlPanel.stop.setEnabled(true);
					controlPanel.back.setEnabled(false);
					controlPanel.play.setEnabled(true);
					controlPanel.pause.setEnabled(false);
					controlPanel.step.setEnabled(true);
				}
			}
			// disable the begin button and the configPanel (freeze the config)
			
			this.repaint();
		} else if (command.compareToIgnoreCase("Step") == 0) {
			nextStep();
		} else if (command.compareToIgnoreCase("Back") == 0) {
			backStep();
		} else if (command.compareToIgnoreCase("Play") == 0) {
			controlPanel.step.setEnabled(false);
			controlPanel.play.setEnabled(false);
			controlPanel.pause.setEnabled(true);
			controlPanel.back.setEnabled(false);
			controlPanel.load.setEnabled(false);
			controlPanel.stop.setEnabled(true);
			fast = true;
			GameRunner runner = new GameRunner(controlPanel.getSpeedSlider());
			runner.setName("Game Runner");
			runner.start();
		} else if (command.equalsIgnoreCase("Pause")) {
			fast = false;
			controlPanel.step.setEnabled(true);
			controlPanel.play.setEnabled(true);
			controlPanel.back.setEnabled(true);
			controlPanel.pause.setEnabled(false);
			controlPanel.load.setEnabled(false);
			controlPanel.stop.setEnabled(true);
		} else if (command.compareToIgnoreCase("Stop") == 0) {
			fast = false;
			controlPanel.stop.setEnabled(false);
			controlPanel.play.setEnabled(false);
			controlPanel.back.setEnabled(false);
			controlPanel.pause.setEnabled(false);
			controlPanel.step.setEnabled(false);
			controlPanel.load.setEnabled(true);
			intStepIndex = 0;
		} else if (command.compareToIgnoreCase("ManualSwitch") == 0) {
			fast = false;
			if ( controlPanel.manual.isSelected() )
			{
				dialPanel.setVisible( true );
				controlPanel.stop.setEnabled(false);
				controlPanel.play.setEnabled(false);
				controlPanel.back.setEnabled(false);
				controlPanel.pause.setEnabled(false);
				controlPanel.step.setEnabled(false);
				controlPanel.load.setEnabled(true);
				controlPanel.totalScore.setText( "--" );
				controlPanel.diffScore.setText( "0" );
				intDiffScore = 0;
			}
			else
			{
				dialPanel.setVisible( false );
				controlPanel.stop.setEnabled(false);
				controlPanel.play.setEnabled(false);
				controlPanel.pause.setEnabled(false);
				controlPanel.back.setEnabled(false);
				controlPanel.step.setEnabled(false);
				controlPanel.load.setEnabled(true);
				intStepIndex = 0;
			}			
		} else if (command.compareToIgnoreCase("Fog") == 0) {
			if ( controlPanel.fog.isSelected() )
			{
				boardPanelL.setFog( true );
				boardPanelR.setFog( true );
			}
			else
			{
				boardPanelL.setFog( false );
				boardPanelR.setFog( false );
			}
			this.repaint();
		} else if (command.compareToIgnoreCase("LU") == 0) {
			nextStep( 4 );
		} else if (command.compareToIgnoreCase("U") == 0) {
			nextStep( 3 );
		} else if (command.compareToIgnoreCase("RU") == 0) {
			nextStep( 2 );
		} else if (command.compareToIgnoreCase("L") == 0) {
			nextStep( 5 );
		} else if (command.compareToIgnoreCase("R") == 0) {
			nextStep( 1 );
		} else if (command.compareToIgnoreCase("LD") == 0) {
			nextStep( 6 );
		} else if (command.compareToIgnoreCase("D") == 0) {
			nextStep( 7 );
		} else if (command.compareToIgnoreCase("RD") == 0) {
			nextStep( 8 );
		} else {
			throw new RuntimeException("Unknow Action Command: " + command);
		}
	}

	private static int reverse(int direction) {
		switch (direction) {
			case 4: return 8;
			case 3: return 7;
			case 2: return 6;
			case 5: return 1;
			case 1: return 5;
			case 6: return 2;
			case 7: return 3;
			case 8: return 4;
			case 0: return 0;
		}
		return -1;
	}
	
	private class GameRunner extends Thread implements ChangeListener {
		private JSlider slider;
		private int delay;

		public GameRunner(JSlider slider) {
			this.slider = slider;
		}

		public void run() {
			delay = slider.getValue();
			slider.addChangeListener(this);
			while (fast && intStepIndex < aintReplay.length ) {
				try {
					nextStep();
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					// this should not happen!
					e.printStackTrace();
				}
			}
			slider.removeChangeListener(this);
		}

		public void stateChanged(ChangeEvent arg0) {
			if (arg0.getSource().equals(slider)) {
				delay = ((JSlider) arg0.getSource()).getValue();
			}
		}
	}

	private LinkedList <Integer> whoMoved;
	private LinkedList <Integer> manualMove;
	
	private void nextStep( int intMoveTo) 
	{
		if ( intStepIndex < aintReplay.length )
		{
			manualMove.offerLast(intMoveTo);
			boolean m1 = boardPanelL.move( intMoveTo );
			boolean m2 = boardPanelR.move( intMoveTo );
			int whoMovedNow = m1 && m2 ? 0 : (m1 ? 1 : 2);
			System.out.println(whoMovedNow);
			whoMoved.offerLast(whoMovedNow);
			intStepIndex ++ ;
		}
		if ( boardPanelL.getFinished() ^ boardPanelR.getFinished() )
		{
			intDiffScore ++;
			controlPanel.diffScore.setText( Integer.toString( intDiffScore ) );
		}
		controlPanel.back.setEnabled(true);
		controlPanel.setStepText( intStepIndex );
	}

	private void nextStep() 
	{
		if ( intStepIndex < aintReplay.length )
		{
			boolean m1 = boardPanelL.move( aintReplay[ intStepIndex ] );
			boolean m2 = boardPanelR.move( aintReplay[ intStepIndex ] );
			int whoMovedNow = m1 && m2 ? 0 : (m1 ? 1 : 2);
			whoMoved.offerLast(whoMovedNow);
			intStepIndex ++ ;
		}
		if ( intStepIndex == aintReplay.length )
			controlPanel.step.setEnabled(false);
		controlPanel.back.setEnabled(true);
		controlPanel.setStepText( intStepIndex );
	}

	private void backStep()
	{
		if (intStepIndex != 0)
		{
			intStepIndex --;
			int movedTo;
			if (manualMove.isEmpty())
				movedTo = aintReplay [ intStepIndex ];
			else
				movedTo = manualMove.pollLast();
			int whoMovedNow = whoMoved.pollLast();
			boolean m1 = (whoMovedNow == 0 || whoMovedNow == 1);
			boolean m2 = (whoMovedNow == 0 || whoMovedNow == 2);
			if (m1) boardPanelL.move( reverse( movedTo ) );
			if (m2) boardPanelR.move( reverse( movedTo ) );
		}
		if (intStepIndex == 0)
			controlPanel.back.setEnabled(false);
		controlPanel.step.setEnabled(true);
		controlPanel.setStepText( intStepIndex );
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {}
}
