package dast.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class BasePane extends JLayeredPane implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2426900381722536632L;
	private int cellWidth = 0;
	private int cellLength = 0;
	private static final int LineLength = 17;	
	private int bsWidth = 0;
	private int bsLength = 0;
	private Visualize frame;
	
	
	int drawMode = 2;
	
	
	private Map<ObjectInfo, CellPanel> cellpanel = new HashMap<ObjectInfo, CellPanel>();
	private List<ObjectInfo> targetObject;
	private HashMap<Integer, Integer> columnWidth;
	private HashMap<Integer, Integer> rowLength;
	
	private boolean mode = false;
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		//connect(g2);
	}
	
	BasePane(List<ObjectInfo> tar, Visualize frame, boolean mode){
		super();
		this.frame = frame;
		this.mode = mode;
		setLayout(null);
		setBackground(Color.WHITE);
				
		setPanel(tar);
	
		this.setPreferredSize(new Dimension(4000,4000));
		
		setLinePanel();
		this.setOpaque(true);
		revalidate();
		//setButton();
		this.setVisible(true);
	}
	
	private void setPanel(List<ObjectInfo> tar){
		this.targetObject = tar;
		columnWidth = new HashMap<Integer, Integer>();
		rowLength = new HashMap<Integer, Integer>();
		cellpanel.clear();

		for(Iterator<ObjectInfo> it = tar.iterator(); it.hasNext();){
			ObjectInfo oin = (ObjectInfo)it.next();
			/*System.out.println(oin.object.type());
			System.out.println(oin.isLinked() + " "+ oin.hasLink() + " " + oin.set);*/
			if((oin.isLinked() == true || oin.hasLink() == true) && oin.set == true){
				
				if(!mode){
					cellpanel.put(oin ,new CellPanel(oin));
				}/*else{
					cellpanel.put(oin ,new CellPanelVerJive(oin));
				}*/
				/*if(cellpanel.get(oin).getPreferredSize().width > cellWidth && oin.isArray() == false){
					cellWidth = cellpanel.get(oin).getPreferredSize().width;
				}
				if(cellpanel.get(oin).getPreferredSize().height > cellLength && oin.isArray() == false){
					cellLength = cellpanel.get(oin).getPreferredSize().height;

				}*/
				Integer length = rowLength.get(oin.getPy());
				Integer width = columnWidth.get(oin.getPx());
				if(length == null || cellpanel.get(oin).getPreferredSize().height > length){
					rowLength.put(new Integer(oin.getPy()), new Integer(cellpanel.get(oin).getPreferredSize().height));
				}
				if(width == null || cellpanel.get(oin).getPreferredSize().width > width){
					columnWidth.put(new Integer(oin.getPx()), new Integer(cellpanel.get(oin).getPreferredSize().width));
				}
				
				
			}
		}
		
		for(Iterator<Entry<ObjectInfo, CellPanel>> it = cellpanel.entrySet().iterator(); it.hasNext();){
			Entry<ObjectInfo, CellPanel> entry = it.next();
			ObjectInfo object = entry.getKey();
			CellPanel panel = entry.getValue();
			try{
			if(panel != null ){
				/*int px = 0;
				int py = 0;
				for(int i = 0; i < object.getPx(); i++){
					px += rowLength.get(i);
				}
				for(int i = 0; i < object.getPy(); i++){
					py += columnWidth.get(i);
				}
				if(panel.getCellLength() > cellLength /2){
					panel.setBounds(object.getPx() * (columnWidth.get(object.getPx())+ 40) + 25  , object.getPy() * (rowLength.get(object.getPy())+ 40) + 20, panel.getMaximumSize().width,panel.getMaximumSize().height );
				}else{
					panel.setBounds(object.getPx() * (columnWidth.get(object.getPx())+ 40) + 25  , object.getPy() * (rowLength.get(object.getPy())+ 40) +(rowLength.get(object.getPy()) /2) - panel.getCellLength()/2, panel.getMaximumSize().width,panel.getMaximumSize().height );
				}
				add(panel, JLayeredPane.DEFAULT_LAYER);*/
				if(panel != null ){
					int px = 0;
					int py = 0;
					for(int i = 0; i < object.getPy(); i++){
						if(rowLength.get(i) != null){
						py += rowLength.get(i);
						}
						py += 60;
					}
					for(int i = 0; i < object.getPx(); i++){
						if(columnWidth.get(i) != null){
						px += columnWidth.get(i);
						}
						px += 60;
					}
						panel.setBounds(px +  25  , py + 40, panel.getMaximumSize().width,panel.getMaximumSize().height );
		
					add(panel, JLayeredPane.DEFAULT_LAYER);
				}
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void setLinePanel(){
		LinePanel line;
		//if(!mode){
			line = new LinePanel(targetObject, cellpanel, this);
	/*	}else{
			line = new LinePanelVerJive(targetObject, cellpanel, this);
		}*/
		this.add(line, JLayeredPane.PALETTE_LAYER);
		line.setSize(this.getPreferredSize());
	}
	
	private void setButton(){
		JPanel buttonSpace = new JPanel();
		
		JButton back = new JButton("<");
		JButton next = new JButton(">");
		JButton first = new JButton("<<");
		JButton last = new JButton(">>");
		
		buttonSpace.setOpaque(true);
		buttonSpace.setBackground(Color.GRAY);
		buttonSpace.setLayout(new BoxLayout(buttonSpace, BoxLayout.X_AXIS));
		buttonSpace.setPreferredSize(new Dimension(this.getPreferredSize().width, 20));
		bsWidth = this.getPreferredSize().width;
		bsLength = 20;
		buttonSpace.setBounds(0, 0, bsWidth, bsLength);
		buttonSpace.setVisible(true);
		this.add(buttonSpace, JLayeredPane.DEFAULT_LAYER);
		buttonSpace.setOpaque(true);
		
		buttonSpace.add(first);
		buttonSpace.add(back);
		buttonSpace.add(next);
		buttonSpace.add(last);
		
		first.setVisible(true);
		back.setVisible(true);
		next.setVisible(true);
		last.setVisible(true);
		
		first.addActionListener(this);
		back.addActionListener(this);
		next.addActionListener(this);
		last.addActionListener(this);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals("<<")){
			frame.showFirst();
		}else if(cmd.equals("<")){
			frame.showPrev();
		}else if(cmd.equals(">")){
			frame.showNext();
		}else if(cmd.equals(">>")){
			frame.showLast();
		}
		
	}
	
	
	public CellPanel searchAroundPanel(ObjectInfo tar){
		for(int i = 0; i < targetObject.size(); i++){
			if(targetObject.get(i) == tar){
				return cellpanel.get(tar);
			}
		}
		return null;
	}
	
	
	public Map<ObjectInfo, CellPanel> getCellPanel(){
		return cellpanel;
	}

	
	public int getRowLength(int x){
		if(rowLength.get(x) != null){
			return rowLength.get(x);
		}else{
			return 0;
		}
	}
	
	public int getColumnWidth(int y){
		if(columnWidth.get(y) != null){
			return columnWidth.get(y);
		}else{
			return 0;
		}
	}
	
	public void setJiveMode(boolean m){
		
		this.mode = m;
		System.out.println(mode);
	}
	
}
