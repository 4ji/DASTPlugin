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

			Map<String, Object> field = cell.getAnotherField();
			for (Iterator<Entry<String, Object>> it = field.entrySet().iterator(); it.hasNext();) {
				Entry<String, Object> target = it.next();
				if(target.getKey().matches(".*" + "Integer" + ".*")){
					//System.out.println(((ObjectReference) target.getValue()).referenceType());
					str = target.getKey() + " : " + ((Integer)(target.getValue())).intValue();
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
	
				/*if (f.get(i).type() instanceof PrimitiveType) {
					str = f.get(i).typeName() + " " + f.get(i).name()
							+ " : " + tar.getValue(f.get(i));
					cp[cp_num] = new CellParts(str);
					cp_num++;
				} else if (f.get(i).typeName().equals("String")
						|| f.get(i).typeName().equals("Integer")) {
					str = f.get(i).typeName() + " " + f.get(i).name()
							+ " : " + tar.getValue(f.get(i));
					cp[cp_num] = new CellParts(str);
					cp_num++;
				} else if(f.get(i).get(tar) instanceof Integer){
					str = "Integer" + " " + f.get(i).getName()
							+ " : " + f.get(i).get(tar);
					cp[cp_num] = new CellParts(str);
					cp_num++;
				}else if(f.get(i).get(tar) instanceof String){
					str = "String" + " " + f.get(i).getName()
							+ " : " + f.get(i).get(tar);
					cp[cp_num] = new CellParts(str);
					cp_num++;*/
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
			/*
			for (int i = 0; i < 8; i++) {
				if (cell.getAroundFieldName()[i] != null) {
					if (cell.getAround()[i] != null && cell.getAround()[i].isArray() == false) {
						if (cell.getAround()[i] != null) {
							str = cell.getAroundFieldName()[i]
									+ " "
									+ cell.getAround()[i].getObject().referenceType().name() + ":"
									+ (cell.getAround()[i].getIndex());
						
						cp.add(new CellParts(str, Color.ORANGE));
						cp_num++;
						}
					} else if(cell.getAround()[i] != null && cell.getAround()[i].isArray() == true){
						str = cell.getAroundFieldName()[i]
								+ " "
								+ cell.getAround()[i].getObject().referenceType().name();
								//+ ((ArrayInfo) (cell.getAround()[i])).getIndex();
						cp.add(new CellParts(str, Color.ORANGE));
						
						cp_num++;
					} else {

						str = cell.getAroundFieldName()[i] + " " + "null";
						cp.add(new CellParts(str, Color.ORANGE));
						cp_num++;
					}
				}*/
				/*if(cell.getAroundArrayName()[i] != null ){
					if(cell.getAroundArray()[i] != null){
						str = cell.getAroundArrayName()[i] + " " 
								+ cell.getAround()[i].getobject().referenceType().name();
						cp.add(new CellParts(str, Color.ORANGE));
						cp_num++;
						
					}else{
						str = cell.getAroundArrayName()[i] + " " + "null";
						cp.add(new CellParts(str, Color.ORANGE));
						cp_num++;
					}
				}*/
			//}
			

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
			ll[i] = new CellParts(cell.getValue(i).toString());
			//System.out.println(cell.getValue(i).toString());
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
		//return this.length * 17 ; ‚È‚ñ‚Å‰‚ß‚±‚Á‚¿‚Å‘‚¢‚½H
	}

}
