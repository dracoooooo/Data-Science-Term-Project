/*
 * 	$Id: ControlPanel.java,v 1.2 2007/11/13 02:49:55 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package mirroruniverse.sim.ui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

public final class MUControlPanel extends JPanel{

	private static final long serialVersionUID = -1949617167769627252L;

	protected JCheckBox manual;
	protected JCheckBox fog;
	protected JButton load;
	protected JButton back;
	protected JButton step;
	protected JButton play;
	protected JButton pause; 
	protected JButton stop;
	protected JButton tournament;
	protected JTextField roundText;
	protected JButton boardPanel;
	protected JSlider speedSlider;
	protected JLabel totalScore;
	protected JLabel diffScore ;
	
	protected MUControlPanel(){
		manual = new JCheckBox("Manual");
//		CheckHandler chdManual = new CheckHandler();
//		manual.addItemListener( chdManual );
//		manual.addKeyListener( chdManual );
		manual.setName("Manual");
		manual.setActionCommand( "ManualSwitch" );
		
		fog = new JCheckBox("Fog");
		fog.setName("Fog");
		fog.setActionCommand( "Fog" );
		
		load = new JButton("Load");
		load.setName("Load");
		load.setActionCommand("Load");
		load.setEnabled(true);

		back = new JButton("Back");
		back.setName("Back");
		back.setEnabled(false);
		back.setActionCommand("Back");
		
		step = new JButton("Step");
		step.setName("Step");
		step.setEnabled(false);
		step.setActionCommand("Step");
		
		play = new JButton("Play");
		play.setName("Play");
		play.setEnabled(false);
		play.setActionCommand("Play");
		
		pause = new JButton("Pause");
		pause.setName("Pause");
		pause.setEnabled(false);
		pause.setActionCommand("Pause");
		
		stop = new JButton("Resign");
		stop.setName("Stop");
		stop.setEnabled(false);
		stop.setActionCommand("Stop");
		
		add(manual);
		add(fog);
		add(load);
		add(back);
		add(step);
		add(play);
		add(pause);
		add(stop);
		
		JLabel label = new JLabel("Step: ");
		roundText = new JTextField();
		roundText.setEnabled(false);
		roundText.setEditable(false);
		roundText.setPreferredSize(new Dimension(60, 25));
		add(label);
		add(roundText);
		
		totalScore = new JLabel("Total");
		diffScore = new JLabel("Diff");
		add( totalScore );
		add( diffScore );
		Dimension dmsLable = new Dimension( 60, 25 );
		totalScore.setPreferredSize( dmsLable );
		diffScore.setPreferredSize( dmsLable );
		
		speedSlider = new JSlider(0, 1000);
		speedSlider.setValue(0);
		add(new JLabel("Delay (0 - 1000ms):"));
		add(speedSlider);
		
		
	}
	protected void addListener(ActionListener a){
		manual.addActionListener(a);
		fog.addActionListener(a);
		load.addActionListener(a);
		pause.addActionListener(a);
		play.addActionListener(a);
		back.addActionListener(a);
		step.addActionListener(a);
		stop.addActionListener(a);
	}
	public JSlider getSpeedSlider() 
	{
		// TODO Auto-generated method stub
		return speedSlider;
	}
	
	public void setStepText( int intStep )
	{
		roundText.setText( Integer.toString( intStep ) );
	}
	
	public void setScore( int intTotalScore, int intDiffScore )
	{
		totalScore.setText( Integer.toString( intTotalScore ) );
		diffScore.setText( Integer.toString( intDiffScore ) );
	}

	@SuppressWarnings("unused")
	private class CheckHandler implements ItemListener, KeyListener
	{
		@Override
		public void itemStateChanged(ItemEvent arg0) 
		{
			if ( manual.isSelected() )
			{
				load.setEnabled(false);
				step.setEnabled(false);
				back.setEnabled(false);
				play.setEnabled(false);
				pause.setEnabled(false);
			}
			else
			{
				load.setEnabled(true);
				step.setEnabled(true);
				back.setEnabled(true);
				play.setEnabled(true);				
				pause.setEnabled(true);
			}
		}

		@Override
		public void keyPressed(KeyEvent arg0) {}

		@Override
		public void keyReleased(KeyEvent arg0) {}

		@Override
		public void keyTyped(KeyEvent key) {}
	}
}
