package dast.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class CellParts extends JLabel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//private int CellWidth ;
	//private int CellLength;

	CellParts(String str, Color bg){
		super(str);
		setBackground(bg);
		setFont(new Font("Arial", Font.PLAIN, 12));
		LineBorder border = new LineBorder(Color.BLACK);
		setOpaque(true);
		setBorder(border);
		setVisible(true);
	}
	
	CellParts(String str){
		super(str);
		setFont(new Font("Arial", Font.PLAIN, 12));
		LineBorder border = new LineBorder(Color.BLACK);
		setOpaque(true);
		setBorder(border);
		setVisible(true);
	}
	
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
	}
	

}
