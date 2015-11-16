package dast.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;


public class Visualize extends JFrame{
	
	/**
	 * 
	 */
	
	Container contentPane;
	
	private static final long serialVersionUID = -944159544965143986L;
	
	final int CellWidth = 100;
	final int CellLength = 20;
	
	private BasePane panel;
	private JScrollPane mainPanel;
	private List<JScrollPane> panelList = new ArrayList<JScrollPane>();
	private List<ObjectInfo> lastCellList;

	Visualize(List<ObjectInfo> cell){
		this.setSize(900, 1000);
		this.setTitle("ver14");
		this.setBackground(Color.WHITE);
		this.setLocationRelativeTo(null);
		
		//this.setLayout(null);
		
		/*for(Iterator<ObjectInfo> it = cell.iterator(); it.hasNext();){
			ObjectInfo oi = it.next();
			System.out.println(oi);
		}
		System.out.println("--");*/
		
		panel = new BasePane(cell, this);
		lastCellList = cell;
		JScrollPane scrollpane = new JScrollPane(panel);
		
		contentPane = getContentPane();
		contentPane.add(scrollpane);
		
		
		scrollpane.setVisible(true);
		mainPanel = scrollpane;
		
		//line = new LinePanel(cell, panel.getCellPanel(), panel.getPreferredSize());
		//line.setVisible(true);
		//contentPane.add(line);
		//mainLine = line;
		
		panelList.add(scrollpane);
		this.setVisible(true);
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				contentPane.invalidate();
				contentPane.validate();
			}
		});
		
	}
	
	public void reDraw(List<ObjectInfo> cell){
		/*for(Iterator<ObjectInfo> it = cell.iterator(); it.hasNext();){
			ObjectInfo oi = it.next();
			System.out.println(oi);
		}
		System.out.println("--");*/
		
		panel = new BasePane(cell, this);
		JScrollPane scrollpane = new JScrollPane(panel);
		
		contentPane = getContentPane();
		contentPane.add(scrollpane);
		panelList.add(scrollpane);
		
		scrollpane.setVisible(true);
		mainPanel.setVisible(false);
		
		mainPanel = scrollpane;
		this.setVisible(true);
		
	}

	public void showFirst() {
		show(panelList.get(0));
	}
	
	public void showPrev(){
		int num = panelList.indexOf(mainPanel);
		if(num > 0){
			show(panelList.get(num - 1));
		}
	}
	
	public void showNext(){
		int num = panelList.indexOf(mainPanel);
		if(num < panelList.size() - 1){
			show(panelList.get(num + 1));
		}
	}
	
	public void showLast(){
		show(panelList.get(panelList.size() - 1));
	}
	
	private void show(JScrollPane tar){
		tar.setVisible(true);
		mainPanel.setVisible(false);
		mainPanel = tar;
		this.setVisible(true);
	}
	

}
