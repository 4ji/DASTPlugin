package dast.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;






import java.util.Map.Entry;

import com.sun.jdi.ArrayType;
import com.sun.jdi.BooleanType;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.DoubleType;
import com.sun.jdi.Field;
import com.sun.jdi.IntegerType;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StringReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.event.ModificationWatchpointEvent;


public class ObjectInfo {
	
	static final int up_length = 0;
	static final int cent_length = 1;
	static final int down_length = 2;
	static final int up_left_width = 3;
	static final int up_cent_width = 4;
	static final int up_right_width = 5;
	static final int cent_left_width = 6;
	static final int cent_right_width = 7;
	static final int down_left_width = 8;
	static final int down_cent_width = 9;
	static final int down_right_width = 10;
	
	protected ObjectReference object;
	protected boolean isPrimitive = false;
	protected ReferenceType  type;
	private ClassDefinition def;
	protected int index;
	//private int num_linked = 0;
	//private int num_array = 0;
	
	protected boolean set = false;
	protected boolean calculated = false;
	protected boolean linked = false;
	private boolean link = false;
	
	private ObjectInfo[] around = new ObjectInfo[8];
	private HashMap<String,ObjectInfo> another = new HashMap<String, ObjectInfo>();
	
	public boolean copy = false;
	public boolean copied = false;
	
	private ArrayInfo[] aroundArray = new ArrayInfo[8]; 
	private String[] aroundArrayName = new String[8];
	protected int with = -1;
	private String[] aroundFieldName = new String[8];
	private int[] aroundTime = new int[8];
	private Map<String, Object> anotherField = new HashMap<String,Object>();
	protected int around_size[]  = { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	private int width = 0;
	private int length = 0;
	protected int ownLength = 1;
	protected int up_half = 0;
	protected int bottom_half = 0;
	private int left_half= 0;
	private int right_half = 0;
	protected int group = 0;

	
	protected int px;
	protected int py;
	
	protected int time=0;
	protected int h;

	protected ObjectManager om;
	private int i;
	private String primitiveType;
	private ObjectInfo lastLinked;
	private int headPoint = 0;


	
	ObjectInfo(ObjectReference tar,Type type, ClassDefinition def, ObjectManager om){
		object = tar;
		this.om = om;
		this.type = (ReferenceType)type;
		this.def = def;
		if(def != null){
		def.addObject();
		index = def.getNumObject();
		}
		this.copy = false;
		//System.out.println("make " + tar);

		
	}
	
	ObjectInfo(ObjectInfo source){
		object = source.object;
		this.om = source.om;
		this.type = source.type;
		this.def = source.def;
		index = source.index;
		this.copy = true;
		this.aroundFieldName = source.aroundFieldName;
		this.anotherField = source.anotherField;
		this.linked = source.isLinked();
		om.addObject(this);

	}
	
	ObjectInfo(Value v){
		isPrimitive = true;
		if(v instanceof IntegerValue){
			i = ((IntegerValue)v).value();
			primitiveType = "int";
		}

	}
	
	public void setField(){
		Map<String, Integer> fieldDirection = def.getFields();
		List<Field> fields = type.fields();
		for(Iterator<Field> it = fields.iterator(); it.hasNext();){
			
			Field tar = (Field)it.next();
			Integer direction = fieldDirection.get(tar.name());
			if(direction == null){
				direction = fieldDirection.get(tar.name() + "[]");
			}
			if(direction != null && direction <= 7 && aroundFieldName[direction] == null){
				aroundFieldName[direction] = tar.name();
			}else if(direction != null && direction <= 7 && aroundFieldName[direction] != null && aroundFieldName[direction] != tar.name() &&  getAroundArrayName()[direction] == null){
				getAroundArrayName()[direction] = tar.name();
			}else if(direction != null && direction == 8){
				another.put(tar.name(),null);
			}else{
				setAnotherField(tar, object.getValue(tar));
			}
		}
	}


	public void setAnotherField(Field field, Value value){
		try {
			if(field.type() instanceof IntegerType){
				anotherField.put("int " + field.name(), ((IntegerValue)value).value());
			}else if(field.type() instanceof DoubleType){
				anotherField.put("double " + field.name(), value);
			}else if(field.type() instanceof BooleanType){
				anotherField.put("boolean " + field.name(), value);
			}else if(field.typeName().equals("java.lang.String")){
				anotherField.put("String " + field.name(), value);
			}else if(field.typeName().equals("java.lang.Object")){
				anotherField.put("Object " + field.name(), value);
			}else if(value instanceof ObjectReference){
				if(((ObjectReference) value).referenceType().name().equals("java.lang.Integer")){
					anotherField.put("Integer " + field.name(),  (ObjectReference)value);
				}else{
					anotherField.put(field.type().name() + " " + field.name(), value);
				}
			}else{
				anotherField.put(field.type().name() + " " + field.name(), value);
				
			}
				
		} catch (ClassNotLoadedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int isAroundField(Field field){
		for(int i = 0; i <= 7; i++){
			if(aroundFieldName[i] == field.name()){
				return i;
			}
		}
		/*for(int i = 0; i <= 7; i++){
			if(aroundFieldName[i] != null && aroundFieldName[i].equals(field.name() + "[]")){
				return i;
			}
		}*/
		return -1;
	}
	
	public void setLink(int time, Field field) throws IllegalArgumentException, IllegalAccessException{
		Map<String, Integer> fieldDirection = def.getFields();
			Integer direction = fieldDirection.get(field.name());
			if(direction == null){
				 direction = fieldDirection.get(field.name() + "[]");
			}
			
			if(direction != null && object.getValue(field) != null){
				ObjectInfo obin = om.searchObjectInfo((ObjectReference)object.getValue(field));
				if(direction <= 7 && obin!=null && obin != around[direction]){
					aroundTime[direction] = time;
				}
				if(direction <= 7 && around[direction] == null){
					around[direction] = obin;
					if(field.name() != aroundFieldName[direction]){
						String copy = getAroundArrayName()[direction];
						getAroundArrayName()[direction] = aroundFieldName[direction];
						aroundFieldName[direction] = copy;							
					}
				}else if(direction == 8){
					another.put(field.name(), obin);
				}else{
					if(obin != null && obin.isArray() == false){
						if(direction <= 7){
							around[direction] = obin;
						}else{
							another.put(field.name(),  obin);
						}
					}else if(obin != null && obin.isArray() && !((ArrayInfo)obin).isPrimitive() && obin != around[direction]){
						around[direction] = aroundArray[direction];
						getAroundArray()[direction] = (ArrayInfo)obin;
						if(field.name() != getAroundArrayName()[direction]){
							String copy = getAroundArrayName()[direction];
							getAroundArrayName()[direction] = aroundFieldName[direction];
							aroundFieldName[direction] = copy;							
						}
					}else if(obin != null && around[direction].isArray() && ((ArrayInfo)obin).isPrimitive()){
						aroundArray[direction] = (ArrayInfo)obin;
						if(field.name() != aroundArrayName[direction]){
							String copy = getAroundArrayName()[direction];
							getAroundArrayName()[direction] = aroundFieldName[direction];
							aroundFieldName[direction] = copy;							
						}

					}
				}
				if(direction <= 7 && around[direction] != null){
					around[direction].linked(this);
					this.link();
				}
				if(direction <= 7 && getAroundArray()[direction] != null){
					getAroundArray()[direction].linked(this);
					this.link();
				}
			}else if(direction != null && direction <= 7 && object.getValue(field) == null){
				around[direction] = null;
			}else if(direction == null){
				setAnotherField(field, object.getValue(field) );
			}
	}
	
	public void setLink(int time, Field field, Value value){
		try {
			setLink(time, field);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, Integer> fieldDirection = def.getFields();
		Integer direction = fieldDirection.get(field.name());
		if(direction == null){
			 direction = fieldDirection.get(field.name() + "[]");
		}
		
		if(direction == null){
			setAnotherField(field, value);
		}
	}
	/*public void setLink(Field field) throws IllegalArgumentException, IllegalAccessException{
		//List<Field> fields = object.referenceType().fields();
		Map<String, Integer> fieldDirection = def.getFields();
		//for(Iterator<Field> it = fields.iterator(); it.hasNext(); ){
			//Field field = (Field)it.next();
			Integer direction = fieldDirection.get(field.name());
			if(direction == null){
				 direction = fieldDirection.get(field.name() + "[]");
			}
			if(direction != null && object.getValue(field) != null){
				if(around[direction] == null){
					around[direction] = om.searchObjectInfo((ObjectReference)object.getValue(field));
					if(field.name() != aroundFieldName[direction]){
						String copy = getAroundArrayName()[direction];
						getAroundArrayName()[direction] = aroundFieldName[direction];
						aroundFieldName[direction] = copy;							
					}
				}else{
					ObjectInfo obim = om.searchObjectInfo((ObjectReference)object.getValue(field));
					if(obim != null && obim.isArray() && ((ArrayInfo)obim).isPrimitive()){
						getAroundArray()[direction] = (ArrayInfo)obim;
						if(field.name() != getAroundArrayName()[direction]){
							String copy = getAroundArrayName()[direction];
							getAroundArrayName()[direction] = aroundFieldName[direction];
							aroundFieldName[direction] = copy;							
						}
					}else if(around[direction].isArray() && ((ArrayInfo)around[direction]).isPrimitive()){
						getAroundArray()[direction] = (ArrayInfo)around[direction];
						around[direction] = obim;
						if(field.name() != aroundFieldName[direction]){
							String copy = getAroundArrayName()[direction];
							getAroundArrayName()[direction] = aroundFieldName[direction];
							aroundFieldName[direction] = copy;							
						}
					}
				}
				if(around[direction] != null){
					around[direction].Linked();
					this.Link();
				}
				if(getAroundArray()[direction] != null){
					getAroundArray()[direction].Linked();
					this.Link();
				}
			}else if(direction != null && object.getValue(field) == null){
				around[direction] = null;
			}
		//}
	}*/
	boolean checkObject(Object obj){
		if(obj == object){
			return true;
		}
		return false;
	}
	
	public boolean sameObject(ObjectReference tar){
		return tar.equals(object);
	}	
	

	public int calculateSize(int totalHeight){
		calculateSize();
		headPoint = totalHeight;
		return this.length;
	}
	
	public void calculateSize(){

		calculated = true;
		around_size[cent_length] = ownLength; 
		LinkedHashMap<String, Integer> fields = def.getFields();
		
		for(Iterator<Integer> it = fields.values().iterator(); it.hasNext();){
			int direction = it.next();
			if (direction >= 0 && direction <= 2) {
				if (around[direction] != null && around[direction].calculated == false ) {
					around[direction].calculateSize();
					if (around_size[up_length] <= around[direction].getLength()) {
						around_size[up_length] = around[direction].getLength();
					}
					around_size[direction + 3] += around[direction].getWidth();
				}else if(around[direction] != null && around[direction].getTime() < aroundTime[direction]){
					if (around_size[up_length] <= around[direction].getLength()) {
						around_size[up_length] = around[direction].getLength();
					}
					around_size[direction + 3] += around[direction].getWidth();
				}
			}
			
			if(direction >= 5 && direction <= 7){
				if(around[direction] != null && around[direction].calculated == false) {
					around[direction].calculateSize();
					if(around_size[down_length] <= around[direction].getLength()){
						around_size[down_length] = around[direction].getLength();
					}
					around_size[direction + 3] += around[direction].getWidth();
				}else if(around[direction] != null && around[direction].getTime() < aroundTime[direction]){
					if(around_size[down_length] <= around[direction].getLength()){
						around_size[down_length] = around[direction].getLength();
					}
					around_size[direction + 3] += around[direction].getWidth();
				}
					
			}
			if(direction == 3 || direction == 4) {
				if (around[direction] != null && around[direction].calculated == false) {
					around[direction].calculateSize();
					if (around_size[cent_length] < around[direction].getLength()) {
						around_size[cent_length] = around[direction].getLength();
					}
					around_size[direction + 3] = around[direction].getWidth();
				}else if(around[direction] != null && around[direction].getTime() < aroundTime[direction]){
					if (around_size[cent_length] < around[direction].getLength()) {
						around_size[cent_length] = around[direction].getLength();
					}
					around_size[direction + 3] = around[direction].getWidth();
				}
			}
			if(direction <= 7 && around[direction] != null){
				around[direction].setTime(aroundTime[direction]);
			}
		}	
		
		if(getAroundArray()[5] != null || getAroundArray()[6] != null || getAroundArray()[7] != null){
			around_size[down_length] ++;
		}
		
		if((around[5] != null || around[7] != null) &&around[6] == null){
			around_size[down_cent_width] += 1;
		}
		
		if(getAroundArray()[3] != null){
			around_size[cent_left_width]++;
		}
		if(getAroundArray()[4] != null){
			around_size[cent_right_width]++;
		}
		
		if(getAroundArray()[0] != null || getAroundArray()[1] != null || getAroundArray()[2] != null){
			around_size[up_length] ++;
		}
		
		if ((around[0] != null || around[2] != null) && around[1] == null)  {
			around_size[up_cent_width] += 1;
		}

		
		setSize();
		
		return;
	}
	
	void setSize(){
		int left = Math.max(Math.max(around_size[up_left_width], around_size[down_left_width]), around_size[cent_left_width]);
		int right =  Math.max(Math.max(around_size[up_right_width], around_size[down_right_width]), around_size[cent_right_width]);
		width = left + right + Math.max(Math.max(around_size[up_cent_width], around_size[down_cent_width]), 1);
		
		
		int cent_left = 0;
		int cent_right = 0;
		if(around[1] != null){
			cent_left = around[1].getLeftHalf();
			cent_right = around[1].getRightHalf();
		}
		if(around[6] != null){
			if(around[6].getLeftHalf() > cent_left){
				cent_left = around[6].getLeftHalf();
			}
			if(around[6].getRightHalf() > cent_right){
				cent_right = around[6].getRightHalf();
			}
		}
		left_half = left + cent_left;
		right_half = right + cent_right;
		
		length = around_size[up_length] + around_size[cent_length] + around_size[down_length] ;

		
		int cent_up = 0;
		int cent_bottom = 0;
		if(around[3] != null){
			cent_up = around[3].getUpHalf();
			cent_bottom = around[3].getBottomHalf();
		}
		if(around[4] != null){
			if(around[4].getUpHalf() > cent_up){
				cent_up = around[4].getUpHalf();
			}
			if(around[4].getBottomHalf() > cent_bottom){
				cent_bottom = around[4].getBottomHalf();
			}
		}
		up_half = around_size[up_length] + cent_up;
		bottom_half = around_size[down_length] + cent_bottom;
		

}
	
	int setPosion(int totalHeight){
		ArrayList<ObjectInfo> parent = new ArrayList<ObjectInfo>(); 
		setPosion(0, totalHeight, 0,0,parent);
		return length;
	}
	
	
	
	void setPosion(int ulx, int uly,int time, int h, ArrayList<ObjectInfo> parent){
		if(set == true && (this.time > time|| this.h <= 1 || parent.get(parent.size()-1).linked == false)){
			return;
		}
		ArrayList<ObjectInfo> newParent = new ArrayList<ObjectInfo>(); 
		for(Iterator<ObjectInfo> it = parent.iterator(); it.hasNext();){
			ObjectInfo next = it.next();
			if(next.equals(this)){
				return;
			}
			newParent.add(next);
		}
		
		newParent.add(this);
		
		set = true;
		this.h = h;
		
		int x;
		int y;
		if(with >= 0){
			if(0 <= with && with <= 2){
				x = ulx + getLeftHalf();
				y = uly + getUpHalf() - 1;
			}else if(with == 3){
				x = ulx + getLeftHalf();
				y = uly + getUpHalf();
			}else if(with == 4){
				x = ulx + getLeftHalf() + 1;
				y = uly + getUpHalf();
			}else{
				x = ulx + getLeftHalf();
				y = uly + getUpHalf() + 1;
			}
		}else{

		x = ulx + getLeftHalf();
		y = uly + getUpHalf();
		

		}
		List<ObjectInfo> list = om.getObjectInfo();
		if((x < 0 || y < 0) && set){
			return;
		}else if((x < 0 || y < 0) && set != true){
			while(x < 0){
			for(Iterator<ObjectInfo> it = list.iterator(); it.hasNext();){
				ObjectInfo obji = it.next();
				if(obji == this){
					x++;
				}else{
					obji.px++;
				}
			}
			}
			while(y < 0){
				for(Iterator<ObjectInfo> it = list.iterator(); it.hasNext();){
					ObjectInfo obji = it.next();
					if(obji == this){
						y++;
					}else{
						obji.py++;
					}
				}
				}
			
		}
		
		/*for(Iterator<ObjectInfo> it = list.iterator(); it.hasNext();){
			ObjectInfo obji = it.next();
			if(obji.set == true && obji.getPx() == x && obji.getPy() == y && obji != this){
				return;
			}
		}*/
		
		px = x;
		py = y;
		/*System.out.println("--");
		System.out.println(type.name() + index + ":(" + px + "," + py + ") ");
		System.out.println("l:" + getLeftHalf() + " r:"+ getRightHalf() + " u:" + getUpHalf() + " d:" + getBottomHalf());
		System.out.println("ulx:" + ulx + " uly:" + uly);
		System.out.println("width:" + getWidth() + " length:" + getLength());
		System.out.println();
		*/

		LinkedHashMap<String, Integer> fields = def.getFields();

		for(Iterator<Integer> it = fields.values().iterator(); it.hasNext();){
			int direction = it.next();
			if(direction <= 7 && around[direction] != null){
				if(direction == 0){
					int cw = 0;
					int with = 0;
					if(aroundArray[0] != null){
						with = 1;
					}
					if(around[1] != null){
						cw = around[1].getLeftHalf();
					}
					if(around[6] != null && around[6].getLeftHalf() > cw){
						cw = around[6].getLeftHalf();
					}
					if(around[3] == null){
						around[0].setPosion(px - around[0].getWidth() - cw, py - around[0].getLength() - with,aroundTime[0], h + 1, newParent);
					}else{
						around[0].setPosion(px - around[0].getWidth() - cw , py - around[0].getLength() - around[3].getUpHalf() - with, aroundTime[0], h + 1, newParent);
					}
					if(aroundArray[0] != null){
						aroundArray[0].setPxy(around[0].getPx(), py + 1);
					
					}
				}
				
				if(direction == 1){
					int with = 0;
					if(aroundArray[1] != null){
						with = 1;
					}
					around[1].setPosion(px - around[1].getLeftHalf(), py - around[1].getLength() - with, aroundTime[1], h + 1, newParent);
					if(aroundArray[1] != null){
						aroundArray[1].setPxy(around[1].getPx(), py - 1);
					
					}
				}
				
				if(direction == 2){
					int cw = 0;
					int with = 0;
					if(aroundArray[2] != null){
						with = 1;
					}
					if(around[1] != null){
						cw = around[1].getRightHalf();
					}
					if(around[6] != null && around[6].getRightHalf() > cw){
						cw = around[6].getRightHalf();
					}
					if(around[4] == null){
						around[2].setPosion(px + 1 + cw, py - around[2].getLength() - with, aroundTime[2], h + 1, newParent);
					}else{
						around[2].setPosion(px + 1 + cw, py - around[4].getUpHalf() - around[2].getLength()-with, aroundTime[2], h + 1, newParent);
					}
					if(aroundArray[2] != null){
						aroundArray[2].setPxy(around[2].getPx(), py - 1);
					}
				}
				
				if(direction == 3){
					int with = 0;
					if(aroundArray[3] != null){
						with = 1;
					}
					int cw = 0;
					if(around[1] != null && around[3].up_half > 0){
						cw = around[1].getLeftHalf();
					}
					if(around[6] != null && around[6].getLeftHalf() > cw){
						cw = around[6].getLeftHalf();
					}		
					if(aroundArray[3] != null){
						around[3].setPosion(px - around[3].getWidth() - cw - with, py - around[3].getUpHalf(), aroundTime[3], h + 1, newParent);			
						aroundArray[3].setPxy(px - 1 , around[3].getPy());
					}else{	
						around[3].setPosion(px - around[3].getWidth() - cw, py - around[3].getUpHalf(), aroundTime[3], h + 1, newParent);	
					}
				}
				
				if(direction == 4){
					int cw = 0;
					int with = 0;
					if(aroundArray[4] != null){
						with = 1;
					}
					
					if(around[1] != null && around[4].up_half > 0){
						cw = around[1].getRightHalf();
					}
					if(around[6] != null && around[6].getRightHalf() > cw && around[4].bottom_half > 0){
						cw = around[6].getRightHalf();
					}
					//cw = 0; //ˆêŽž—lŽqŒ©
					around[4].setPosion(px + 1 + cw + with, py - around[4].getUpHalf(), aroundTime[4], h + 1, newParent);
					
					if(aroundArray[4] != null){
						aroundArray[4].setPxy(px + 1, around[4].getPy());	
					}
				}
				
				if(direction == 5){
					int cw = 0;
					int with = 0;
					if(aroundArray[5] != null){
						with  = 1;
					}
					if(around[1] != null){
						cw = around[1].getLeftHalf();
					}
					if(around[6] != null && around[6].getLeftHalf() > cw){
						cw = around[6].getLeftHalf();
					}
					if(around[3] == null){
						around[5].setPosion(px - around[5].getWidth() - cw, py + ownLength + with, aroundTime[5], h + 1, newParent);
					}else{
						around[5].setPosion(px - around[5].getWidth() - cw, py + around[3].getBottomHalf() + ownLength + with, aroundTime[5], h + 1, newParent);
					}
					
					if(aroundArray[5] != null){
						aroundArray[5].setPxy(around[5].getPx(), py + 1);
					}
				}
				
				if(direction == 6){
					int with = 0;
					if(aroundArray[6] != null){
						with = 1;
					}
					around[6].setPosion(px - around[6].getLeftHalf() , py + ownLength + with, aroundTime[6], h + 1, newParent);
					
					if(aroundArray[6] != null){
						aroundArray[6].setPxy(around[6].getPx(), py + 1);
						
					}	
				}
				
				if(direction == 7){
					int cw = 0;
					int with = 0;
					if(aroundArray[7] != null){
						with = 1;
					}
					
					if(around[1] != null){
						cw = around[1].getRightHalf();
					}
					if(around[6] != null && around[6].getRightHalf() > cw){
						cw = around[6].getRightHalf();
					}
					
					if(around[4] == null){
						around[7].setPosion( px + 1 + cw, py + ownLength + with, aroundTime[7], h + 1, newParent);
					}else{
						around[7].setPosion(px + 1  + cw, py + around[4].getBottomHalf() + ownLength + with, aroundTime[7], h + 1, newParent);
					}
					
					if(aroundArray[7] != null){
						aroundArray[7].setPxy(around[7].getPx(), py + 1);
					
					}
				}
			}
		}		
	}

	void setAnotherPosion(int x, int y){
		px = x;
		py = y;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getOwnLength(){
		return ownLength;
	}

	public Map<String, Object> getAnotherField() {
		return anotherField;
	}

	public void setAnotherField() {
		
	}

	public ObjectInfo[] getAround() {
		return around;
	}

	public void setAround(ObjectInfo[] around) {
		this.around = around;
	}

	public String[] getAroundFieldName() {
		return aroundFieldName;
	}

	public void setAroundFieldName(String[] aroundFieldName) {
		this.aroundFieldName = aroundFieldName;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getUpHalf(){
		return this.up_half;
	}
	
	public int getBottomHalf(){
		return this.bottom_half;
	}
	
	public int getLeftHalf(){
		return this.left_half;
	}
	
	public int getRightHalf(){
		return this.right_half;
	}
	
	public boolean isCalculated(){
		return this.calculated;
	}
	
	public boolean isArray(){
		return false;
	}
	

	
	public int getPx(){
		return px;
	}
	public int getPy(){
		return py;
	}
	
	public ObjectReference getObject(){
		return object;
	}

	public void slideUp(){
		this.py--;
	}
	
	public void slideLeft(){
		this.px--;
	}

	public void setWith(int d){
		with = d;
	}

	/*private void setAroundFieldName(){
		ClassDefinition.Value[] value = type.getValue();
		for(int i =0; i< value.length; i++){
			if(value[i] != null){
				aroundFieldName[value[i].direction] = value[i].name;
			}
		}

	}*/
	
	public void linked(ObjectInfo from){
		if(from != this){
			this.lastLinked = from;
				this.linked = true;
		};
	}
	
	public void link() {
		this.link = true;
	}
	
	public boolean isLinked(){
		return this.linked;
	}
	
	public boolean hasLink(){
		return this.link;
	}
	
	/*public void resetAround(){
		for(int i = 0 ; i <= 7;i++){
			around[i] = null;
			aroundArray[i] = null;
		}
	}*/

	public int getIndex(){
		return index;
	}
	
	public ObjectInfo deepCopy(){
		ObjectInfo tar = new ObjectInfo(this);
		return tar;
		
	}

	public String[] getAroundArrayName() {
		return aroundArrayName;
	}

	public void setAroundArrayName(String[] aroundArrayName) {
		this.aroundArrayName = aroundArrayName;
	}

	public ArrayInfo[] getAroundArray() {
		return aroundArray;
	}

	public void setAroundArray(ArrayInfo[] aroundArray) {
		this.aroundArray = aroundArray;
	}
	
	public void reset(){
		set = false;
		calculated = false;
		/*
		linked = false;
		link = false;*/
		
		/*for(int i = 0; i < 8; i++){
			around[i] = null;
			aroundArray[i] = null;
		}*/
		
		
		for(int i = 0; i < around_size.length; i++){
			if(i == 1){
				around_size[i] = 1;
			}else{
				around_size[i] = 0;
			}
		}
		with = -1;
		width = 0;
		length = 0;
		ownLength = 1;
		up_half = 0;
		bottom_half = 0;
		left_half= 0;
		right_half = 0;
	 	
		px = 0;
		py = 0;
	}

	

	public int getTime(){
		return time;
	}
	
	public void setTime(int time){
		this.time = time;
	}
	
	public ClassDefinition getDef(){
		return def;
	}
	
	public HashMap<String, ObjectInfo> getAnother(){
		return another;
	}
	
	public int[] getAroundTime(){
		return aroundTime;
	}

}
