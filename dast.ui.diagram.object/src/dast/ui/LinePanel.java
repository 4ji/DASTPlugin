package dast.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JPanel;

public class LinePanel extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final int LineLength = 17;	
	
	private int drawMode = 2;
	protected int coordinateLine = 5;
	
	protected BasePane base;
	private Map<ObjectInfo, CellPanel> cellpanel;
	private List<ObjectInfo> targetObject;
	protected List<ConnectPoint> lineList = new ArrayList<ConnectPoint>();
	protected List<ConnectPoint> arrowList = new ArrayList<ConnectPoint>();
	protected List<StartPoint> pointList = new ArrayList<StartPoint>();
	protected Map<Integer, CellPanel> XLine = new HashMap<Integer, CellPanel>(); 
	protected Map<Integer, CellPanel> YLine = new HashMap<Integer, CellPanel>();
	
	LinePanel(List<ObjectInfo> tar,  Map<ObjectInfo, CellPanel> cellpanel, BasePane base){
		super();
		this.cellpanel = cellpanel;
		this.targetObject = tar;
		this.base = base;
		setLayout(null);
		super.setOpaque(false);
		//this.setPreferredSize(d);
		revalidate();
		this.setVisible(true);
		set();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		setOpaque(false);
		Graphics2D g2 = (Graphics2D)g;
		connect(g2);
	}
	
	class ConnectPoint{
		int sx;
		int sy;
		int gx;
		int gy;
		
		CellPanel tar;
		
		ConnectPoint(int ax, int ay, int bx, int by, CellPanel tar){
			this.sx = ax;
			this.sy = ay;
			this.gx = bx;
			this.gy = by;
			this.tar = tar;
		}
	}
	
	class StartPoint{
		int x,y;
		int direction;
		
		StartPoint(int x, int y, int direction){
			this.x = x;
			this.y = y;
			this.direction = direction;
		}
	}
	void set(){
		for(Iterator<Entry<ObjectInfo, CellPanel>> it = cellpanel.entrySet().iterator(); it.hasNext();){
			Entry<ObjectInfo, CellPanel> ent = it.next();
				setPoint(ent);
		}
	}
	
	void setPoint(Entry<ObjectInfo, CellPanel> ent){
		ObjectInfo oin = ent.getKey();
		CellPanel cell = ent.getValue();
		ObjectInfo around[] = oin.getAround();
		CellPanel tar;
		if(!(oin instanceof ArrayInfo)){
			if(drawMode == 1){
		for(int i = 0;i <= 7;i++ ){ 
			if(around[i] != null && ((tar = searchPanel(around[i]) )!= null)){
				
				if(i == 0 || i == 2){
					if(oin.getPy() > around[i].getPy()){
						arrowList.add(new ConnectPoint(cell.getX() + cell.getCellWidth() / 2, cell.getY(), tar.getX() + tar.getCellWidth() / 2, 
								tar.getY() + tar.getCellLength() + 3, tar));
				
					}else{
						int nx;
						if(i == 0){
							nx = cell.getX() -20;
						}else{
							nx = cell.getX() + cell.getCellWidth() + 20;
						}
						lineList.add(new ConnectPoint(cell.getX() + cell.getCellWidth() / 2, cell.getY(), nx, cell.getY() + cell.getCellLength()-10, tar));
						lineList.add(new ConnectPoint(nx, cell.getY() + cell.getCellLength()-10, nx, tar.getY() + 10, tar));
						lineList.add(new ConnectPoint(nx, tar.getY() + 10, tar.getX() + tar.getCellWidth() / 2, tar.getY() + 10,tar));
						arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() / 2, tar.getY() + 10, tar.getX() + tar.getCellWidth() / 2, tar.getY() + tar.getCellLength(), tar));
					}
				}else if(i == 1){
					if(oin.getPy() > around[i].getPy()){
						if(oin.getPx() == around[i].getPx()){
							arrowList.add(new ConnectPoint(cell.getX() + cell.getCellWidth() / 2, cell.getY(), cell.getX() + cell.getCellWidth() / 2,tar.getY() + tar.getCellLength(), tar));
						}else{
							lineList.add(new ConnectPoint(cell.getX() + cell.getCellWidth()/2, cell.getY(), cell.getX() + cell.getCellWidth() / 2, cell.getY() -10, tar));
							lineList.add(new ConnectPoint(cell.getX() + cell.getCellWidth() / 2, cell.getY() -10, tar.getX() + tar.getCellWidth() / 2,cell.getY() -10, tar));
							arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() / 2,cell.getY() -10, tar.getX() + tar.getCellWidth() / 2, tar.getY() + tar.getCellLength(), tar));
						}
					}else{
						int nx;
						lineList.add(new ConnectPoint(cell.getX() + cell.getCellWidth() / 2,cell.getY(), cell.getX() + cell.getCellWidth() / 2, cell.getY() - 10, tar));
						if(oin.getPx() > around[i].getPx()){
							nx = tar.getX() + tar.getCellWidth() + 10;
						}else{
							nx = tar.getX() - 10;
						}
						lineList.add(new ConnectPoint(cell.getX() + cell.getCellWidth() / 2, cell.getY() - 10, nx, cell.getY() - 10 , tar));
						lineList.add(new ConnectPoint(nx, cell.getY() - 10, nx, tar.getY() + tar.getCellLength() + 10, tar));
						lineList.add(new ConnectPoint( nx, tar.getY() + tar.getCellLength() + 10,tar.getX() + tar.getCellWidth() / 2, tar.getCellLength() + 10, tar));
						arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() / 2, tar.getCellLength() + 10, tar.getX() + tar.getCellWidth() / 2, tar.getY() + tar.getCellLength(), tar));
					}
						
				}else if(i == 3){
					if(oin.getPx() > around[i].getPx()){
						arrowList.add(new ConnectPoint(cell.getX(), cell.getY() + cell.getCellLength() / 2,  tar.getX() + tar.getCellWidth(), cell.getY() +  cell.getCellLength() / 2, tar));
					}else{
						lineList.add(new ConnectPoint(cell.getX(), cell.getY() + cell.getCellLength() / 2,cell.getX()-10 , cell.getY() + cell.getCellLength() / 2, tar));
						if(oin.getBottomHalf() > around[i].getBottomHalf()){
							lineList.add(new ConnectPoint(cell.getX()-10 , cell.getY() + cell.getCellLength() / 2,cell.getX()-10 ,cell.getY() + cell.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint(cell.getX()-10 ,cell.getY() + cell.getCellLength() + 10, tar.getX() + tar.getCellWidth() + 10, cell.getY() + cell.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint( tar.getX() + tar.getCellWidth() + 10, cell.getY() + cell.getCellLength() + 10 ,tar.getX() + tar.getCellWidth() + 10, tar.getY() + tar.getCellLength() / 2 , tar));
							arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() + 10, tar.getY() + tar.getCellLength() / 2 , tar.getX() + tar.getCellWidth(),tar.getY() + tar.getCellLength() / 2, tar ));
						}else{
							lineList.add(new ConnectPoint(cell.getX()-10 , cell.getY() + cell.getCellLength() / 2,cell.getX()-10 ,cell.getY() + tar.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint(cell.getX()-10 ,cell.getY() + tar.getCellLength() + 10, tar.getX() + tar.getCellWidth() + 10,cell.getY() + tar.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint( tar.getX() + tar.getCellWidth() + 10,cell.getY() + tar.getCellLength() + 10,tar.getX() + tar.getCellWidth() + 10, tar.getY() + tar.getCellLength() / 2 , tar));
							arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() + 10, tar.getY() + tar.getCellLength() / 2 , tar.getX() + tar.getCellWidth(),tar.getY() + tar.getCellLength() / 2, tar));
						}
															
					}
				}else if(i == 4){
					if(oin.getPx() < around[i].getPx()){
						arrowList.add(new ConnectPoint(cell.getX() + cell.getCellWidth(), cell.getY() + cell.getCellLength() / 2, tar.getX(), cell.getY() + cell.getCellLength() / 2, tar));
					}else{
						lineList.add(new ConnectPoint(cell.getX() + cell.getCellWidth(), cell.getY() + cell.getCellLength() / 2,cell.getX()+cell.getCellWidth() + 10 , cell.getY() + cell.getCellLength() / 2, tar));
						if(oin.getBottomHalf() >= around[i].getBottomHalf()){
							lineList.add(new ConnectPoint(cell.getX()+cell.getCellWidth() + 10 , cell.getY() + cell.getCellLength() / 2,cell.getX()+cell.getCellWidth() + 10 ,cell.getY() + cell.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint(cell.getX()-10 ,cell.getY() + cell.getCellLength() + 10, tar.getX()  - 10, cell.getY() + cell.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint( tar.getX() - 10, cell.getY() + cell.getCellLength() + 10,tar.getX() - 10, tar.getY() + tar.getCellLength() / 2 , tar));
							arrowList.add(new ConnectPoint(tar.getX() - 10, tar.getY() + tar.getCellLength() / 2, tar.getX(),tar.getY() + tar.getCellLength() / 2, tar));
						}else{
							lineList.add(new ConnectPoint(cell.getX()+cell.getCellWidth() + 10 , cell.getY() + cell.getCellLength() / 2,cell.getX()+cell.getCellWidth() + 10,cell.getY() + tar.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint(cell.getX()+10 ,cell.getY() + tar.getCellLength() + 10, tar.getX() - 10,cell.getY() + tar.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint(tar.getX() - 10,cell.getY() + tar.getCellLength() + 10,tar.getX()-10 , tar.getY() + tar.getCellLength() / 2 , tar));
							arrowList.add(new ConnectPoint(tar.getX()-10 , tar.getY() + tar.getCellLength() / 2 , tar.getX(),tar.getY() + tar.getCellLength() / 2, tar));
						}
					}
				}else if (i == 5 || i == 7){
					if(oin.getPy() < around[i].getPy()){
						arrowList.add(new ConnectPoint(cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength(), tar.getX() + tar.getCellWidth() / 2, tar.getY() -3, tar));
					}else{
						int nx;
						if(i == 5){
							nx = cell.getX() -10;
						}else{
							nx = cell.getX() + cell.getCellWidth() + 10;
						}
						lineList.add(new ConnectPoint(cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength(), nx, cell.getY() + cell.getCellLength()+10, tar));
						lineList.add(new ConnectPoint(nx, cell.getY() + cell.getCellLength()+10, nx, tar.getY() - 10, tar));
						lineList.add(new ConnectPoint(nx, tar.getY() - 10, tar.getX() + tar.getCellWidth() / 2, tar.getY() - 10, tar));
						arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() / 2, tar.getY() - 10, tar.getX() + tar.getCellWidth() / 2, tar.getY(), tar));
					}
				}else{
					if(oin.getPy() < around[i].getPy()){
						if(oin.getPx() == around[i].getPx()){
							arrowList.add(new ConnectPoint(cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength(), cell.getX() + cell.getCellWidth() / 2, tar.getY()-3, tar));
						}else{
							lineList.add(new ConnectPoint(cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength(), cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint(cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength() + 10, tar.getX() + tar.getCellWidth() / 2, cell.getY() + cell.getCellLength() + 10, tar));
							arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() / 2, cell.getY() + cell.getCellLength() + 10, tar.getX() + tar.getCellWidth() / 2 ,tar.getY(), tar));
						}
						
					}else{
						lineList.add(new ConnectPoint(cell.getX() + cell.getCellWidth() / 2,cell.getY() + cell.getCellLength(), cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength() + 10, tar));
						if(oin.getPx() > around[i].getPx()){
							lineList.add(new ConnectPoint(cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength() + 10,tar.getX() + tar.getCellWidth() + 10,cell.getY() + cell.getCellLength() + 10, tar)); 
							lineList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() + 10,cell.getY() + cell.getCellLength() + 10, tar.getX() + tar.getCellWidth() + 10, tar.getY() -10, tar));
							lineList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() + 10, tar.getY() -10, tar.getX() + tar.getCellWidth() / 2, tar.getY() -10, tar));
						}else{
							lineList.add(new ConnectPoint(cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength() + 10,tar.getX() - 10,cell.getY() + cell.getCellLength() + 10, tar)); 
							lineList.add(new ConnectPoint(tar.getX() - 10,cell.getY() + cell.getCellLength() + 10, tar.getX() - 10, tar.getY() -10, tar));
							lineList.add(new ConnectPoint(tar.getX() - 10, tar.getY() -10, tar.getX() + tar.getCellWidth() / 2, tar.getY() -10, tar));
						}
						arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() / 2, tar.getY() -10, tar.getX() + tar.getCellWidth() / 2, tar.getY(), tar));
					}
				}
			}
		}
			}else if(drawMode == 2){
				setPoint2(ent);
		}
		}else{
			if(!((ArrayInfo)oin).isPrimitive()){
			ArrayInfo array = (ArrayInfo)(oin);
			ObjectInfo[] arrayValue = array.getArrayValue();
			for(int i = 0; i < arrayValue.length; i++){
				if(arrayValue[i] != null && (tar = searchPanel(arrayValue[i]) )!= null){
					if(array.getDirection() >= 5 && array.getDirection() <= 7){
						arrowList.add(new ConnectPoint(cell.getX() +(cell.getWidth() / arrayValue.length /2) +(cell.getWidth() / arrayValue.length * i), cell.getY() + cell.getCellLength(), tar.getX() + tar.getCellWidth() /2, tar.getY(), tar));
					}else if(array.getDirection() >= 0 && array.getDirection() <= 2){
						arrowList.add(new ConnectPoint(cell.getX() +(cell.getWidth() / arrayValue.length /2) +(cell.getWidth() / arrayValue.length * i), cell.getY(), tar.getX() + tar.getCellWidth() /2, tar.getY() + tar.getCellLength(),  tar));
					}else if(array.getDirection() == 4){
						if(oin.getPx() < arrayValue[i].getPx()){
							arrowList.add(new ConnectPoint(cell.getX() +cell.getWidth(), cell.getY() + 27 + 18 *i , tar.getX(), tar.getY() + tar.getCellLength()/2, tar));
						}else{
							lineList.add(new ConnectPoint(cell.getX() +cell.getWidth(), cell.getY() + 27 + 18 *i , cell.getX() +cell.getWidth() + 10, cell.getY() + 27 + 18 *i , tar));
							lineList.add(new ConnectPoint(cell.getX() +cell.getWidth() + 10, cell.getY() + 27 + 18 *i , cell.getX() +cell.getWidth() + 10, cell.getY() + 27 + 18 *i + tar.getY() + tar.getCellLength() + 5, tar));
							lineList.add(new ConnectPoint(cell.getX() +cell.getWidth() + 10, cell.getY() + 27 + 18 *i + tar.getY() + tar.getCellLength() + 5, tar.getX() + tar.getCellWidth()/2, cell.getY() + 27 + 18 *i + tar.getY() + tar.getCellLength() + 5, tar));
							arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth()/2, cell.getY() + 27 + 18 *i + tar.getY() + tar.getCellLength() + 5, tar.getX() + tar.getCellWidth()/2, tar.getY() + tar.getCellLength(), tar));
						}
					}else{
						arrowList.add(new ConnectPoint(cell.getX(), cell.getY() + 27 + 18 *i , tar.getX() + tar.getCellWidth(), tar.getY() + tar.getCellLength()/2, tar));
					}
				}
					
			}
			}
		}
	}
	
	void setPoint2(Entry<ObjectInfo, CellPanel> ent){
		ObjectInfo oin = ent.getKey();
		CellPanel cell = ent.getValue();
		ObjectInfo around[] = oin.getAround();
		String aroundFieldName[] = oin.getAroundFieldName();
		ObjectInfo aroundArray[] = oin.getAroundArray();
		String[] aroundArrayName = oin.getAroundArrayName();
		CellPanel tar = null;
		int valueCount = 0;
		int valueNum = 0;
		
		
		for(int i = 0; i <= 7; i++){
			if(aroundFieldName[i] != null){
				valueNum++;
			}
		}
		HashMap<String, ObjectInfo> another = oin.getAnother();
		for(Iterator<Entry<String, ObjectInfo>> it = another.entrySet().iterator(); it.hasNext();){
			Entry<String, ObjectInfo> ei = it.next();
				valueNum++;
		}
		
		for(int i = 0; i < aroundArrayName.length; i++){
			if(aroundArrayName[i] != null){
				valueNum++;
			}
		}
		
		for(Iterator<Entry<String, Integer>> it = oin.getDef().getFields().entrySet().iterator();
				it.hasNext();){
			Entry<String, Integer> def = it.next();
			int i = def.getValue();
			String s = def.getKey();
			int startX = -1;
			int startY = -1;
			
		/*}
		for(int i = 0; i <= 7; i++){*/
			
			if(i <= 7 && s.equals(aroundArrayName[i])){
				tar = searchPanel(aroundArray[i]);
			}else if(i <= 7){
				tar = searchPanel(around[i]);
			}
			
			if((i <= 7 && oin == around[i]) || (i == 8 && oin == oin.getAnother().get(s))){
			//–îˆó‚È‚µ
				
			}else if(i <= 7 && around[i] != null && tar != null){
				
				
				if(i == 0 || i == 3 || i == 5){
					startX = cell.getX();
				}else if(i == 2 || i == 4 || i == 7){
					startX = cell.getX() + cell.getCellWidth();
				}else if( i == 1){
					if(cell.getCellWidth()/2 > tar.getCellWidth() ){
						startX = tar.getX() + tar.getCellWidth() / 2 - 5;
					}else{
						startX = cell.getX() + cell.getCellWidth()/2 -5;
					}
				}else{
					if(cell.getCellWidth()/2 > tar.getCellWidth()){
						startX = tar.getX() + tar.getCellWidth() / 2 + 5;
					}else{
						startX = cell.getX() + cell.getCellWidth()/2 + 5;
					}
				}
				
				if(i == 1){
					startY = cell.getY() + cell.getCellLength() + LineLength*(valueCount - valueNum);
				}else if(i == 6){
					startY = cell.getY() + cell.getCellLength() + LineLength*(valueCount - valueNum + 1);
				}else{
					startY = cell.getY() + cell.getCellLength() + LineLength*(valueCount - valueNum) + LineLength/2;
				}
				pointList.add(new StartPoint(startX, startY, i));
				if(i == 0 || i == 2){
					if(oin.getPy() > around[i].getPy()){
						arrowList.add(new ConnectPoint(startX, startY, tar.getX() + tar.getCellWidth() / 2, 
								tar.getY() + tar.getCellLength() + 3, tar));
				
					}else{
						int nx;
						if(i == 0){
							nx = startX -15;
							while(XLine.get(nx) != null && XLine.get(nx) != tar){
								nx -= 10;
							}
						}else{
							nx = startX + 15;
							while(XLine.get(nx) != null && XLine.get(nx) != tar){
								nx += 10;
							}
						}
						XLine.put(nx, tar);
						lineList.add(new ConnectPoint(startX, startY, nx, startY-10, tar));
						lineList.add(new ConnectPoint(nx, startY-10, nx, tar.getY() + tar.getCellLength() + 10, tar));
						lineList.add(new ConnectPoint(nx, tar.getY() + tar.getCellLength() + 10, tar.getX() + tar.getCellWidth() / 2, tar.getY() + tar.getCellLength() + 10, tar));
						arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() / 2, tar.getY() + tar.getCellLength() + 10, tar.getX() + tar.getCellWidth() / 2, tar.getY() + tar.getCellLength(), tar));
					}
				}else if(i == 1){
					if(oin.getPy() > around[i].getPy()){
						if(oin.getPx() == around[i].getPx()){
							arrowList.add(new ConnectPoint( startX, startY, startX, tar.getY() + tar.getCellLength(), tar));
						}else{
							lineList.add(new ConnectPoint(startX, startY, startX, cell.getY() -10, tar));
							lineList.add(new ConnectPoint(startX, cell.getY() -10, tar.getX() + tar.getCellWidth() / 2,cell.getY() -10, tar));
							arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() / 2,cell.getY() -10, tar.getX() + tar.getCellWidth() / 2, tar.getY() + tar.getCellLength(), tar));
						}
					}else{
						int nx;
						lineList.add(new ConnectPoint(startX,startY, startX, cell.getY() - 10, tar));
						if(oin.getPx() > around[i].getPx()){
							nx = tar.getX() + tar.getCellWidth() + 10;
							while(XLine.get(nx) != null && XLine.get(nx) != tar){
								nx += 10;
							}
						}else{
							nx = tar.getX() - 10;
							while(XLine.get(nx) != null && XLine.get(nx) != tar){
								nx -= 10;
							}
						}
						XLine.put(nx, tar);
						lineList.add(new ConnectPoint(startX, cell.getY() - 10, nx, cell.getY() - 10, tar));
						lineList.add(new ConnectPoint(nx, cell.getY() - 10, nx, tar.getY() + tar.getCellLength() + 10, tar));
						lineList.add(new ConnectPoint( nx, tar.getY() + tar.getCellLength() + 10,tar.getX() + tar.getCellWidth() / 2, tar.getY() + tar.getCellLength() + 10, tar));
						arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() / 2, tar.getY() + tar.getCellLength() + 10, tar.getX() + tar.getCellWidth() / 2, tar.getY() + tar.getCellLength(), tar));
					}
					
				}else if(i == 3){
					if(oin.getPx() > around[i].getPx()){
						if(oin.getPy() == around[i].getPy()){
							//‘ŠŽè‚ªƒŠƒ“ƒN‚ðŽ‚½‚È‚¢ê‡‘ŠŽè‚ð‚¸‚ç‚·
							if(tar.getY() + tar.getCellLength() >= startY){
								arrowList.add(new ConnectPoint(startX, startY,  tar.getX() + tar.getCellWidth(), startY, tar));
							}else if(tar.getY() + tar.getCellLength() < startY && !around[i].hasLink()){
								tar.setLocation(tar.getX(), startY - tar.getCellLength() / 2);
								arrowList.add(new ConnectPoint(startX, startY,  tar.getX() + tar.getCellWidth(), startY, tar));
							}else{
								arrowList.add(new ConnectPoint(startX, startY, tar.getX() + tar.getCellWidth(), tar.getY() + tar.getCellLength() / 2, tar));
							}
							
							//–îˆó‚ð‚¸‚ç‚·
							/*if(tar.getY() + tar.getCellLength() < startY){
								arrowList.add(new ConnectPoint(startX, startY, tar.getX() + tar.getCellWidth(), tar.getY() + tar.getCellLength() / 2, tar));
							}else{
								arrowList.add(new ConnectPoint(startX, startY,  tar.getX() + tar.getCellWidth(), startY, tar));
							}*/
							
						}else{
							lineList.add(new ConnectPoint(startX, startY, cell.getX() + base.getColumnWidth(oin.getPx()) - 5, startY, tar));
							lineList.add(new ConnectPoint(cell.getX() + base.getColumnWidth(oin.getPx()) - 5, startY, cell.getX() + base.getColumnWidth(oin.getPx()) - 5, tar.getY() + tar.getCellLength()/ 2, tar));
							arrowList.add(new ConnectPoint(cell.getX() + base.getColumnWidth(oin.getPx()) - 5, tar.getY() + tar.getCellLength()/ 2, tar.getX() + tar.getCellWidth(), tar.getY() + tar.getCellLength()/2, tar));
						}
					}else{
						lineList.add(new ConnectPoint(startX, startY,startX-10 , startY, tar));
						if(oin.getBottomHalf() > around[i].getBottomHalf()){
							lineList.add(new ConnectPoint(startX-10 , startY,startX-10 ,startY + 10, tar));
							lineList.add(new ConnectPoint(startX-10 ,startY + 10, tar.getX() + tar.getCellWidth() + 10, startY + 10, tar));
							lineList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() + 10, startY + 10,tar.getX() + tar.getCellWidth() + 10, tar.getY() + tar.getCellLength() / 2, tar ));
							arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() + 10, tar.getY() + tar.getCellLength() / 2 , tar.getX() + tar.getCellWidth(),tar.getY() + tar.getCellLength() / 2, tar ));
						}else{
							lineList.add(new ConnectPoint(startX-10 , startY,startX-10 ,cell.getY() + tar.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint(cell.getX()-10 ,cell.getY() + tar.getCellLength() + 10, tar.getX() + tar.getCellWidth() + 10,cell.getY() + tar.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint( tar.getX() + tar.getCellWidth() + 10,cell.getY() + tar.getCellLength() + 10,tar.getX() + tar.getCellWidth() + 10, tar.getY() + tar.getCellLength() / 2, tar ));
							arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() + 10, tar.getY() + tar.getCellLength() / 2 , tar.getX() + tar.getCellWidth(),tar.getY() + tar.getCellLength() / 2, tar));
						}
															
					}
				}else if(i == 4){
					if(oin.getPx() < around[i].getPx()){
						if(oin.getPy() == around[i].getPy()){
							
							if(tar.getY() + tar.getCellLength() >= startY){
								arrowList.add(new ConnectPoint(startX, startY, tar.getX(), startY, tar));
							}else if(tar.getY() + tar.getCellLength() < startY && !around[i].hasLink()){
								arrowList.add(new ConnectPoint(startX, startY, tar.getX(), startY, tar));
								tar.setLocation(tar.getX(), startY - tar.getCellLength() / 2);
							}else{
								arrowList.add(new ConnectPoint(startX, startY, tar.getX(), tar.getY() + (tar.getCellLength() / 2), tar));
							}
							/*
							if(tar.getY() + tar.getCellLength() < startY){
								arrowList.add(new ConnectPoint(startX, startY, tar.getX(), tar.getY() + (tar.getCellLength() / 4), tar));
								System.out.println(tar.getY() + " " + tar.getCellLength() / 2 + " " + tar.getY() + tar.getCellLength());
							}else{
								arrowList.add(new ConnectPoint(startX, startY, tar.getX(), startY, tar));
							}*/
							
						}else{
							lineList.add(new ConnectPoint(startX, startY, cell.getX() + base.getColumnWidth(oin.getPx()) + 5, startY, tar));
							lineList.add(new ConnectPoint(cell.getX() + base.getColumnWidth(oin.getPx()) + 5, startY, cell.getX() + base.getColumnWidth(oin.getPx()) + 5, tar.getY() + tar.getCellLength()/ 2, tar));
							arrowList.add(new ConnectPoint(cell.getX() + base.getColumnWidth(oin.getPx()) + 5, tar.getY() + tar.getCellLength()/ 2, tar.getX() + tar.getCellWidth(), tar.getY() + tar.getCellLength()/2, tar));
						}
					}else{
						lineList.add(new ConnectPoint(startX, startY ,startX + 10 , startY, tar));
						if(oin.getBottomHalf() >= around[i].getBottomHalf()){
							lineList.add(new ConnectPoint(startX + 10 , startY,startX + 10 ,cell.getY() + cell.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint(startX + 10 ,cell.getY() + cell.getCellLength() + 10, tar.getX()  - 10, cell.getY() + cell.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint( tar.getX() - 10, cell.getY() + cell.getCellLength() + 10,tar.getX() - 10, tar.getY() + tar.getCellLength() / 2 , tar));
							arrowList.add(new ConnectPoint(tar.getX() - 10, tar.getY() + tar.getCellLength() / 2, tar.getX(),tar.getY() + tar.getCellLength() / 2, tar) );
						}else{
							lineList.add(new ConnectPoint(startX + 10 , startY,startX + 10,cell.getY() + tar.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint(startX + 10,cell.getY() + tar.getCellLength() + 10, tar.getX() - 10,cell.getY() + tar.getCellLength() + 10, tar));
							lineList.add(new ConnectPoint(tar.getX() - 10,cell.getY() + tar.getCellLength() + 10,tar.getX()-10 , tar.getY() + tar.getCellLength() / 2 , tar));
							arrowList.add(new ConnectPoint(tar.getX()-10 , tar.getY() + tar.getCellLength() / 2 , tar.getX(),tar.getY() + tar.getCellLength() / 2, tar));
						}
					}
				}else if (i == 5 || i == 7){
					if(oin.getPy() < around[i].getPy()){
						arrowList.add(new ConnectPoint(startX, startY, tar.getX() + tar.getCellWidth() / 2, tar.getY() -3, tar));
					}else{
						int nx;
						if(i == 5){
							nx = startX -10;
							while(XLine.get(nx) != null && XLine.get(nx) != tar){
								nx -= 10;
							}
						}else{
							nx = startX + 10;
							while(XLine.get(nx) != null && XLine.get(nx) != tar){
								nx += 10;
							}
						}
						XLine.put(nx, tar);
						lineList.add(new ConnectPoint(startX, startY, nx, startY, tar));
						lineList.add(new ConnectPoint(nx, startY, nx, tar.getY() - 10, tar));
						lineList.add(new ConnectPoint(nx, tar.getY() - 10, tar.getX() + tar.getCellWidth() / 2, tar.getY() - 10, tar));
						arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() / 2, tar.getY() - 10, tar.getX() + tar.getCellWidth() / 2, tar.getY(), tar));
					}
				}else{
					if(oin.getPy() < around[i].getPy()){
						if(oin.getPx() == around[i].getPx()){
						arrowList.add(new ConnectPoint(startX, startY, startX, tar.getY(), tar));
						}else{
							lineList.add(new ConnectPoint(startX, startY, startX, startY + 10, tar));
							lineList.add(new ConnectPoint(startX, startY + 10, tar.getX() + tar.getCellWidth() / 2, startY + 10, tar));
							arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() / 2, startY + 10, tar.getX() + tar.getCellWidth() / 2 ,tar.getY(), tar));
						}
						
					}else{
						int nx,ny;
						
						if(oin.getPx() > around[i].getPx()){
							nx = tar.getX() + tar.getCellWidth() + 10;
							while(XLine.get(nx) != null && XLine.get(nx) != tar){
								nx += 10;
							}
						}else{
							nx = tar.getX() - 10;
							while(XLine.get(nx) != null && XLine.get(nx) != tar){
								nx -= 10;
							}
						}
						XLine.put(nx, tar);
						
						ny = cell.getY() + base.getRowLength(oin.getPy()) + 5;
						while(YLine.get(ny) != null && YLine.get(ny) != tar){
							ny += 10;
						}
						YLine.put(ny, tar);
						
						lineList.add(new ConnectPoint(startX,startY, startX, ny, tar));
						lineList.add(new ConnectPoint(startX, ny, nx,ny, tar)); 
						lineList.add(new ConnectPoint(nx,ny, nx, tar.getY() -10, tar));
						lineList.add(new ConnectPoint(nx, tar.getY() -10, tar.getX() + tar.getCellWidth() / 2, tar.getY() -10, tar));
						arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() / 2, tar.getY() -10, tar.getX() + tar.getCellWidth() / 2, tar.getY(), tar));
					}
					
				}
				
			}else if(i == 8 && oin.getAnother().get(s) != null && (tar = searchPanel(oin.getAnother().get(s) ))!= null){
				
				ObjectInfo toin = oin.getAnother().get(s); 
				if(oin.getPy() == toin.getPy()){
					if(oin.getPx() < toin.getPx()){
						startX = cell.getX() + cell.getCellWidth();
						startY = cell.getY() + cell.getHeight() + LineLength*(valueCount - valueNum) + LineLength/2 + 2;
						arrowList.add(new ConnectPoint(startX, startY, tar.getX(), startY, tar));
					}else if(oin.getPx() > toin.getPx()){
						startX = cell.getX();
						startY = cell.getY() + cell.getHeight()+ LineLength*(valueCount - valueNum) + LineLength/2 - 2;
						arrowList.add(new ConnectPoint(startX, startY, tar.getX(), startY, tar));
					}else{// oin.getPx() == toin.getPx()
						//‹N‚±‚ç‚È‚¢‚Í‚¸
					}
				}else{
					if(oin.getPx() < toin.getPx()){
						startX = cell.getX() + cell.getCellWidth();
						startY = cell.getY() + cell.getHeight() + LineLength*(valueCount - valueNum) + LineLength/2 + 2;
						lineList.add(new ConnectPoint(startX, startY, tar.getX() - coordinateLine, startY, tar));
						lineList.add(new ConnectPoint(tar.getX() - coordinateLine, startY, tar.getX() - coordinateLine, tar.getY() + tar.getCellLength()/2, tar));
						arrowList.add(new ConnectPoint(tar.getX() - coordinateLine, tar.getY() + tar.getCellLength()/2, tar.getX(), tar.getY() + tar.getCellLength()/2, tar));
						XLine.put(tar.getX() - coordinateLine, tar);
					}else if(oin.getPx() > toin.getPx()){
						startX = cell.getX();
						startY = cell.getY() + cell.getHeight() + LineLength*(valueCount - valueNum) + LineLength/2 - 2;
						lineList.add(new ConnectPoint(startX, startY, tar.getX() + tar.getCellWidth() + coordinateLine,startY, tar));
						lineList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() + coordinateLine,startY, tar.getX() + tar.getCellWidth() + coordinateLine, tar.getY() + tar.getCellLength()/2, tar));
						arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() +coordinateLine, tar.getY() + tar.getCellLength()/2, tar.getX() + tar.getCellWidth(),tar.getY() + tar.getCellLength()/2, tar));
						XLine.put(tar.getX() + tar.getCellWidth() + coordinateLine, tar);
					}else{ // oin.getPx() == toin.getPx()
						startX = cell.getX() + cell.getCellWidth();
						startY = cell.getY() + cell.getHeight() + LineLength*(valueCount - valueNum) + LineLength/2;
						
						if(oin.getPy() > toin.getPy()){
							lineList.add(new ConnectPoint(startX, startY, cell.getX() + tar.getCellWidth() + coordinateLine, startY, tar));
							lineList.add(new ConnectPoint(cell.getX() + tar.getCellWidth() + coordinateLine, startY,cell.getX() + tar.getCellWidth() + coordinateLine,tar.getY() + tar.getCellLength() + coordinateLine, tar));
							lineList.add(new ConnectPoint(cell.getX() + tar.getCellWidth() + coordinateLine,tar.getY() + tar.getCellLength() + coordinateLine, tar.getX() + tar.getCellWidth() - coordinateLine, tar.getY() + tar.getCellLength() + coordinateLine, tar));
							arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() - coordinateLine, tar.getY() + tar.getCellLength() + coordinateLine, tar.getX() + tar.getCellWidth() - coordinateLine, tar.getY() + tar.getCellLength(),tar));
							XLine.put(cell.getX() + tar.getCellWidth() + coordinateLine, tar);
						}else{
							lineList.add(new ConnectPoint(startX, startY, startX + 10 + coordinateLine, startY, tar));
							lineList.add(new ConnectPoint(startX + 10 + coordinateLine, startY,startX + 10 + coordinateLine,tar.getY() - coordinateLine, tar));
							lineList.add(new ConnectPoint(startX + 10 + coordinateLine,tar.getY() - coordinateLine, tar.getX() + tar.getCellWidth() - coordinateLine, tar.getY() - coordinateLine, tar));
							arrowList.add(new ConnectPoint(tar.getX() + tar.getCellWidth() - coordinateLine, tar.getY() - coordinateLine, tar.getX() + tar.getCellWidth() - coordinateLine, tar.getY(),tar));
							XLine.put(cell.getX() + tar.getCellWidth() + coordinateLine, tar);
						}
					}

				}
				coordinateLine +=3;
				if(coordinateLine > 35){
					coordinateLine = 5;
				}
				if(startX >= 0 && startY >= 0){
					pointList.add(new StartPoint(startX, startY, i));
				}
			}
			valueCount++;
		}
	}
	
	CellPanel searchPanel(ObjectInfo tar){
		for(int i = 0; i < targetObject.size(); i++){
			if(targetObject.get(i) == tar){
				return cellpanel.get(tar);
			}
		}
		return null;
	}
	
	void connect(Graphics2D g){
		g.setColor(Color.BLUE);
		if(lineList != null){
			for(Iterator<ConnectPoint> it = lineList.iterator(); it.hasNext();){
				ConnectPoint cp = it.next();
				g.drawLine(cp.sx, cp.sy, cp.gx, cp.gy);
			}
		}
		
		for(Iterator<ConnectPoint> it = arrowList.iterator(); it.hasNext();){
			ConnectPoint cp = it.next();
			
			if(cp.sx == cp.gx && cp.sy > cp.gy){
				drawArrow(g, cp.sx, cp.sy, cp.gx, cp.tar.getY() + cp.tar.getCellLength(), 5);
			}else if(cp.sx == cp.gx && cp.sy< cp.gy){
				drawArrow(g, cp.sx, cp.sy, cp.gx, cp.tar.getY(), 5);
			}else if(cp.sy == cp.gy && cp.sx < cp.gx){
				drawArrow(g, cp.sx, cp.sy, cp.tar.getX(), cp.gy, 5);
			}else if(cp.sy == cp.gy && cp.sx > cp.gx){
				drawArrow(g, cp.sx, cp.sy, cp.tar.getX() + cp.tar.getCellWidth(), cp.gy, 5);
			}else if(cp.sy > cp.gy){
				drawArrow(g, cp.sx, cp.sy, cp.gx, cp.tar.getY() + cp.tar.getCellLength(), 5);
			}else if(cp.sy < cp.gy){
				drawArrow(g, cp.sx, cp.sy, cp.gx, cp.tar.getY(), 5);
			}
		}
		
		g.setColor(Color.RED);
		for(Iterator<StartPoint> it = pointList.iterator(); it.hasNext();){
			StartPoint sp = it.next();
			if(sp.direction == 1){
				g.fillRect(sp.x-2, sp.y, 4, 4);
			}else if(sp.direction == 6){
				g.fillRect(sp.x-2 , sp.y-4, 4, 4);
			}else{
				g.fillRect(sp.x-2, sp.y-2, 4, 4);
			}
		}
		
		
	}
	
	/*http://www.asahi-net.or.jp/~QC8K-STU/java/ExGraphics.java*/
	public void drawArrow(Graphics2D g,int x0,int y0,int x1,int y1,int l){
	    double theta;
	    int x,y;
	    double dt = Math.PI / 6.0;
	   
	    
	   
	    theta = Math.atan2((double)(y1-y0),(double)(x1-x0));
	    g.drawLine(x0,y0,x1,y1);
	    x = x1-(int)(l*Math.cos(theta-dt));
	    y = y1-(int)(l*Math.sin(theta-dt));
	    g.drawLine(x1,y1,x,y);
	    x = x1-(int)(l*Math.cos(theta+dt));
	    y = y1-(int)(l*Math.sin(theta+dt));
	    g.drawLine(x1,y1,x,y);
	  }

	public void reDraw(List<ObjectInfo> cell){
		
	}
}
