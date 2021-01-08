package mirroruniverse.sim.ui;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

public class MUDialPanel extends JPanel
{
	private static final long serialVersionUID = 1771335955348728767L;

	protected JButton LU;
	protected JButton U;
	protected JButton RU;
	protected JButton L; 
	protected JButton O_O;
	protected JButton R;
	protected JButton LD;
	protected JButton D;
	protected JButton RD; 
	
	public MUDialPanel() 
	{
		setLayout(new GridLayout(3,3));
		LU = new JButton( "LU" );
		LU.setName("LU");
		LU.setActionCommand( "LU" );
		U = new JButton( "U" );
		U.setActionCommand( "U" );
		RU = new JButton( "RU" );
		RU.setActionCommand( "RU" );
		L = new JButton( "L" );
		L.setActionCommand( "L" );
		O_O = new JButton( "O_O" );
		R = new JButton( "R" );
		R.setActionCommand( "R" );
		LD = new JButton( "LD" );
		LD.setActionCommand( "LD" );
		D = new JButton( "D" );
		D.setActionCommand( "D" );
		RD = new JButton( "RD" );
		RD.setActionCommand( "RD" );
		add( LU );
		add( U );
		add( RU );
		add( L );
		add( O_O );
		add( R );
		add( LD );
		add( D );
		add( RD );
	}
	protected void addListener(ActionListener a){
		LU.addActionListener(a);
		U.addActionListener(a);
		RU.addActionListener(a);
		L.addActionListener(a);
		R.addActionListener(a);
		LD.addActionListener(a);
		D.addActionListener(a);
		RD.addActionListener(a);
	}
}
