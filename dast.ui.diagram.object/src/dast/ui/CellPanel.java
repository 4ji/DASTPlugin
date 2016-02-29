package dast.ui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;

public class CellPanel extends JPanel {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private static int nextColor = 0;
	private static Map<ObjectReference, Color> panelColor = new HashMap<ObjectReference, Color>();
	protected List<CellParts> cp = new ArrayList<CellParts>();
	protected int cp_num = 0;
	protected ObjectReference tar;
	private int length = 0;
	
	Dimension maxDim = null;

	CellPanel(ObjectInfo cell) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setOpaque(true);
		setBackground(Color.WHITE);
		setVisible(true);
		LineBorder border = new LineBorder(Color.BLACK);
		setBorder(border);

		tar = cell.getObject();
			if (!(cell.isArray())) {
				makeObjectCell(cell);
				length = cp_num;
			} else if(((ArrayInfo)cell).isPrimitive() == false){
				makeArrayCell((ArrayInfo) cell);
			}else{
				makePrimitiveArray((ArrayInfo) cell);
			}


		setVisible(true);

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public void makeObjectCell(ObjectInfo cell) {
		Color color;
		try {
			String str = tar.referenceType().name() + ":"
					+ cell.getIndex() + "(id=" + tar.hashCode() + ")";
			if(cell.copied == true || cell.copy == true){
				color = setColor(tar);
				
			}else{
				color = Color.yellow;
			}
			cp.add(new CellParts(str,color));
			cp_num++;

			Map<String, Value> field = cell.getAnotherField();
			for (Iterator<Entry<String, Value>> it = field.entrySet().iterator(); it.hasNext();) {
				Entry<String,Value> target = it.next();
				
				if(target.getValue() != null &&(target.getValue().type().toString().equals("java.lang.Integer")|| target.getValue().type().toString().equals("class java.lang.Integer (no class loader)"))){
					
					if(target.getValue().type() instanceof ReferenceType){
						Value v = target.getValue();
						str = target.getKey() + " : " + ((ObjectReference)v).getValue(((ReferenceType)v.type()).fieldByName("value"));
						cp.add(new CellParts(str));
						cp_num++;
					}
					
				}else{
				if(target.getValue() == null){	
				str = target.getKey() + " : " + target.getValue(); 
				cp.add(new CellParts(str));
				cp_num++;
				}else{
					str = target.getKey() + " : " + target.getValue().toString().replace("instance of ", ""); 
					cp.add(new CellParts(str));
					cp_num++;
				}
				}
	
				
				}
			
			for(Iterator<Entry<String, Integer>> it = cell.getDef().getFields().entrySet().iterator();
					it.hasNext();){
				Entry<String, Integer> ent = it.next();
				if(ent.getValue() <= 7 && cell.getAround()[ent.getValue()] != null ){
					if(cell.getAround()[ent.getValue()].isArray() == false){
						str = ent.getKey() 
								+ " " 
								+ cell.getAround()[ent.getValue()].getObject().referenceType().name() 
								+ " : "
								+ cell.getAround()[ent.getValue()].getIndex();
						cp.add(new CellParts(str, Color.ORANGE));
						cp_num++;
					}else if(cell.getAround()[ent.getValue()].isArray()){
						if(cell.getAroundFieldName()[ent.getValue()] != null && cell.getAroundFieldName()[ent.getValue()].equals(ent.getKey().replace("[]", "")) ){
						str = cell.getAround()[ent.getValue()].getObject().referenceType().name()
								+ " "
								+ent.getKey().replace("[]", "") 
								;
						
						}else if(cell.getAroundArray()[ent.getValue()] != null){
							str = cell.getAroundArray()[ent.getValue()].getObject().referenceType().name()
									+ " "
									+ent.getKey().replace("[]", "") 
									;
						}
						cp.add(new CellParts(str, Color.ORANGE));
						cp_num++;
					}
				}else if(ent.getValue() == 8 && cell.getAnother().get(ent.getKey()) != null ){
					str = ent.getKey()
							+ " "
							+ cell.getAnother().get(ent.getKey()).getObject().referenceType().name()
							+ " : "
							+ cell.getAnother().get(ent.getKey()).getIndex();
					cp.add(new CellParts(str, Color.ORANGE));
					cp_num++;
				}else{
					str = ent.getKey()
							+ " "
							+ "null";
					cp.add(new CellParts(str, Color.ORANGE));
					cp_num++;
				}
			}
			
			

		} catch (Exception e) {
			System.out.println(e + " makeObjectCell");
			e.printStackTrace();
		}

		adjustSize();
		for (int i = 0; i < cp.size(); i++) {
			add(cp.get(i));
		}

	}
	
	Color setColor(ObjectReference tar){
		if(panelColor.get(tar) != null){
			return panelColor.get(tar);
		}else{
			if(nextColor == 0){
				nextColor++;
				panelColor.put(tar, Color.cyan);
				return Color.cyan;
			}else if(nextColor == 1){
				nextColor++;
				panelColor.put(tar, Color.red);
				return Color.magenta;
			}else if(nextColor == 2){
				nextColor++;
				panelColor.put(tar, Color.green);
				return Color.green;
			}else if(nextColor == 3){
				nextColor++;
				panelColor.put(tar, Color.pink);
				return Color.pink;
			}else{
				return Color.white;
			}
		}
	}

	private void makeArrayCell(ArrayInfo cell) {
		String str = cell.getArray().referenceType().name();
		cp.add(new CellParts(str));
		cp.get(cp_num).setBackground(Color.YELLOW);
		cp_num++;

		CellParts[] idl = new CellParts[cell.getSize()];
		CellParts[] ll = new CellParts[cell.getSize()];

		JPanel IDL = new JPanel();
		JPanel LL = new JPanel();

		if (cell.getDirection() != 3 && cell.getDirection() != 4) {
			IDL.setLayout(new BoxLayout(IDL, BoxLayout.LINE_AXIS));
			LL.setLayout(new BoxLayout(LL, BoxLayout.LINE_AXIS));
		} else {
			IDL.setLayout(new BoxLayout(IDL, BoxLayout.PAGE_AXIS));
			LL.setLayout(new BoxLayout(LL, BoxLayout.PAGE_AXIS));
		}
		IDL.setOpaque(true);
		IDL.setBackground(Color.WHITE);
		for (int i = 0; i < cell.getSize(); i++) {
			idl[i] = new CellParts(Integer.toString(i));
			idl[i].setBackground(Color.ORANGE);
		}

		maxDim = idl[0].getPreferredSize();

		LL.setOpaque(true);
		LL.setBackground(Color.WHITE);
		for (int i = 0; i < cell.getSize(); i++) {
			if (cell.getArray().getValue(i) != null) {
				if (cell.getArrayValue(i) != null) {
					String str2 = cell.getArray().getValue(i).type().name()
							+ ":" + (cell.getArrayValue(i).index);
					ll[i] = new CellParts(str2);
				} else {
					String str2 = "null";
					ll[i] = new CellParts(str2);

				}

			} else {
				ll[i] = new CellParts("null");
			}

			if (ll[i].getPreferredSize().width > maxDim.width) {
				maxDim = ll[i].getPreferredSize();
			}
			ll[i].setBackground(Color.ORANGE);
		}

		for (int i = 0; i < idl.length; i++) {
			idl[i].setMaximumSize(maxDim);
			ll[i].setMaximumSize(maxDim);
			IDL.add(idl[i]);
			LL.add(ll[i]);
		}

		maxDim = IDL.getMaximumSize();
		if (maxDim.width < LL.getMaximumSize().width) {
			maxDim = LL.getMaximumSize();
		}

		IDL.setAlignmentX(0.0f);
		LL.setAlignmentX(0.0f);



		IDL.setVisible(true);
		LL.setVisible(true);

		if (cell.getDirection() >= 0 && cell.getDirection() <= 2) {
			add(IDL);
			add(LL);
			for (int i = 0; i < cp_num; i++) {
				cp.get(i).setAlignmentX(0.0f);
				cp.get(i).setMaximumSize(maxDim);
				add(cp.get(i));
			}
			length = 3;

		} else if (cell.getDirection() >= 6 && cell.getDirection() <= 7) {

			for (int i = 0; i < cp_num; i++) {
				cp.get(i).setAlignmentX(0.0f);
				cp.get(i).setMaximumSize(maxDim);
				add(cp.get(i));
			}
			add(IDL);
			add(LL);
			length = 3;
		} else {
			for (int i = 0; i < cp_num; i++) {
				cp.get(i).setAlignmentX(0.0f);
				cp.get(i).setMaximumSize(maxDim);
				add(cp.get(i));
			}

			JPanel subPanel = new JPanel();
			subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
			subPanel.setOpaque(true);
			subPanel.add(IDL);
			subPanel.add(LL);
			subPanel.setVisible(true);
			add(subPanel);

			cp.get(0).setAlignmentX(0.5f);
			cp.get(0).setMaximumSize(new Dimension(IDL.getMaximumSize().width
					+ LL.getMaximumSize().width,
					cp.get(0).getPreferredSize().height));
			subPanel.setMaximumSize(new Dimension(80, 18*(cell.getSize()+1)));
			this.setMaximumSize(new Dimension(80, 18*(cell.getSize()+1)+2));
			this.setPreferredSize(new Dimension(80, 18*(cell.getSize()+1)+2));
			
			length = cell.getSize();

			
		}

	}
	
	private void makePrimitiveArray(ArrayInfo cell){
		String str = cell.getFieldName();
		cp.add(new CellParts(str));
		cp.get(cp_num).setBackground(Color.YELLOW);
		cp_num++;

		CellParts[] idl = new CellParts[cell.getSize()];
		CellParts[] ll = new CellParts[cell.getSize()];

		JPanel IDL = new JPanel();
		JPanel LL = new JPanel();

		if (cell.getDirection() != 3 && cell.getDirection() != 4) {
			IDL.setLayout(new BoxLayout(IDL, BoxLayout.LINE_AXIS));
			LL.setLayout(new BoxLayout(LL, BoxLayout.LINE_AXIS));
		} else {
			IDL.setLayout(new BoxLayout(IDL, BoxLayout.PAGE_AXIS));
			LL.setLayout(new BoxLayout(LL, BoxLayout.PAGE_AXIS));
		}
		IDL.setOpaque(true);
		IDL.setBackground(Color.WHITE);
		for (int i = 0; i < cell.getSize(); i++) {
			idl[i] = new CellParts(Integer.toString(i));
			idl[i].setBackground(Color.white);
		}


		LL.setOpaque(true);
		LL.setBackground(Color.WHITE);
		for (int i = 0; i < cell.getSize(); i++) {
			if(cell.getValue(i) != null){
				if((cell.array.getValue(i).type().equals("java.lang.Integer")) || cell.array.getValue(i).type().toString().equals("class java.lang.Integer (no class loader)")){
					ObjectReference or = ((ObjectReference)cell.array.getValue(i));
					if(or != null){
						ll[i] = new CellParts(or.getValue(((ReferenceType) or.type()).fieldByName("value")).toString());
					}
				}else{
					ll[i] = new CellParts(cell.getValue(i).toString());
				}
			}else{
				ll[i] = new CellParts("null");
			}


			ll[i].setBackground(Color.white);
		}

		for (int i = 0; i < idl.length; i++) {
			idl[i].setMaximumSize(new Dimension(30,18));
			ll[i].setMaximumSize(new Dimension(30,18));
			IDL.add(idl[i]);
			LL.add(ll[i]);
		}



		IDL.setAlignmentX(0.0f);
		LL.setAlignmentX(0.0f);

	

		IDL.setVisible(true);
		LL.setVisible(true);

		if (cell.getDirection() >= 0 && cell.getDirection() <= 2) {
			IDL.setMaximumSize(new Dimension(30 * cell.getSize(),18));
			LL.setMaximumSize(new Dimension(30*cell.getSize(), 18));
			add(IDL);
			add(LL);
			for (int i = 0; i < cp_num; i++) {
				cp.get(i).setAlignmentX(0.0f);
				cp.get(i).setMaximumSize(new Dimension(cell.getSize() * 30,18));
				add(cp.get(i));
			}
			
			length = 3;

		} else if (cell.getDirection() >= 6 && cell.getDirection() <= 7) {
			IDL.setMaximumSize(new Dimension(30 * cell.getSize(),18));
			LL.setMaximumSize(new Dimension(30*cell.getSize(), 18));

			for (int i = 0; i < cp_num; i++) {
				cp.get(i).setAlignmentX(0.0f);
				cp.get(i).setMaximumSize(new Dimension(cell.getSize() * 30,18));
				add(cp.get(i));
			}
			add(IDL);
			add(LL);
			length = 3;
		} else {
			for (int i = 0; i < cp_num; i++) {
				cp.get(i).setAlignmentX(0.0f);
				cp.get(i).setMaximumSize(new Dimension(60,18));
				add(cp.get(i));
			}

			JPanel subPanel = new JPanel();
			subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
			subPanel.setOpaque(true);
			subPanel.add(IDL);
			subPanel.add(LL);
			subPanel.setVisible(true);
			add(subPanel);

			cp.get(0).setAlignmentX(0.5f);
			IDL.setMaximumSize(new Dimension(30 ,18*cell.getSize()));
			LL.setMaximumSize(new Dimension(30, 18*cell.getSize()));
			subPanel.setMaximumSize(new Dimension(60, 18*cell.getSize()));
			this.setMaximumSize(new Dimension(60, 18*(cell.getSize() +1)+5));
			this.setPreferredSize(new Dimension(60, 18*(cell.getSize() +1)+5));

			length = cell.getSize();
		}
	}

	protected void adjustSize() {
		Dimension dim;
		for (int i = 0; i < cp_num; i++) {
			dim = cp.get(i).getPreferredSize();

			if (maxDim == null || maxDim.width < dim.width) {
				maxDim = dim;
			}
		}

		for (int i = 0; i < cp_num; i++) {
			cp.get(i).setMaximumSize(maxDim);
		}
	}

	public int getCellWidth() {
		return this.getPreferredSize().width;
	}

	public int getCellLength() {
		return this.getPreferredSize().height ;
	}

}
