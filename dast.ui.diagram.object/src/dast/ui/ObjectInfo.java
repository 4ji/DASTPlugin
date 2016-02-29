package dast.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;






import java.util.Map.Entry;

import com.sun.jdi.ArrayReference;
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
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StringReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.event.ModificationWatchpointEvent;

//import dast.ui.ObjectInfo.ArrayPair;

@SuppressWarnings("restriction")
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

	
	private ObjectInfo[] aroundObject = new ObjectInfo[8];
	private HashMap<String,ObjectInfo> noDirectionField = new HashMap<String, ObjectInfo>();
	
	public boolean copy = false;
	public boolean copied = false;
	
	private ArrayInfo[] aroundArray = new ArrayInfo[8]; 
	private String[] aroundArrayName = new String[8];
	protected int with = -1;
	private String[] aroundFieldName = new String[8];
	private int[] aroundFieldWriteTime = {0, 0, 0, 0, 0, 0, 0, 0};
	private int[] aroundArrayWriteTime = {0, 0, 0, 0, 0, 0, 0, 0};
	private Map<String, Value> anotherField = new HashMap<String,Value>();
	private List<Field> linkedField = new ArrayList<Field>();
	private List<ArrayPair> linkedArray = new ArrayList<ArrayPair>();
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
	
	private boolean head = false;


	
	ObjectInfo(ObjectReference tar, ClassDefinition def, ObjectManager om){
		object = tar;
		this.om = om;
		this.type = (ReferenceType)tar.referenceType();
		this.def = def;
		if(def != null){
			def.addObject();
			index = def.getNumObject();
		}
		this.copy = false;
		setFieldName();
		
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
		
		om.addObject(this);

	}
	
	ObjectInfo(Value v){
		isPrimitive = true;
		if(v instanceof IntegerValue){
			i = ((IntegerValue)v).value();
			primitiveType = "int";
		}

	}
	
	void setFieldName(){
		if(def == null){
			return;
		}
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
				setAroundArrayName(direction, tar);
			}else if(direction != null && direction == 8){
				noDirectionField.put(tar.name(),null);
			}else{
				setAnotherField(tar, object.getValue(tar));
			}
		}
	}

	
	private void setAroundArrayName(Integer direction,  Field tar){
		try {
			if(tar.type() instanceof ArrayType &&
					((((ArrayType)tar.type()).componentType() instanceof PrimitiveType) || ((ArrayType)tar.type()).componentType().toString().indexOf("java.lang.Integer") != -1)){
				aroundArrayName[direction] = tar.name();
			}else{
				 aroundArrayName[direction] = aroundFieldName[direction];
				 aroundFieldName[direction] = tar.name();
			}
		} catch (ClassNotLoadedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setAnotherField(Field field, Value value){
		try {
			if(field.type() instanceof IntegerType){
				anotherField.put("int " + field.name(), value);
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

	public void checkStatic(int time){
		List<Field> fields = object.referenceType().allFields();
		for(int i = 0; i < fields.size(); i++){
			Field tar = fields.get(i);
			if(tar.isStatic() && def.getDirectionByName(tar.name()) != -1){
				try {
					setLink(time, tar, object.getValue(tar));
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
	
	public void setLink(int time, Field field, Value v) throws IllegalArgumentException, IllegalAccessException{
		Map<String, Integer> fieldDirection = def.getFields();
			Integer direction = fieldDirection.get(field.name());
			if(direction == null){
				 direction = fieldDirection.get(field.name() + "[]");
			}
			
		
			if(direction != null && v != null){
				ObjectInfo obin = null;
				ObjectReference value = (ObjectReference) v;
				if(value != null){
					obin = om.searchObjectInfo(value);
				}
				if(direction <= 7 && aroundFieldName[direction] == field.name()){
					if(aroundObject[direction] != null && aroundObject[direction] != obin){
						aroundObject[direction].removeLink(field);
					}
					aroundObject[direction] = obin;
					aroundFieldWriteTime[direction] = time;
				}else if(direction <= 7 && aroundArrayName[direction] == field.name()){
					if(aroundArray[direction] != null && aroundArray[direction] != obin){
						aroundArray[direction].removeLink(field);
					}
					aroundArray[direction] = (ArrayInfo)obin;
					aroundArrayWriteTime[direction] = time;
				}else if(direction == 8){
					noDirectionField.put(field.name(), obin);
				}
				if(obin != null){
					obin.linked(this, field);
				}
			}else if(direction == null){
				setAnotherField(field, object.getValue(field) );
			}
	}

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
				if (aroundObject[direction] != null && aroundObject[direction].calculated == false ) {
					aroundObject[direction].calculateSize();
					if (around_size[up_length] <= aroundObject[direction].getLength()) {
						around_size[up_length] = aroundObject[direction].getLength();
					}
					around_size[direction + 3] += aroundObject[direction].getWidth();
				}else if(aroundObject[direction] != null && aroundObject[direction].getTime() < aroundFieldWriteTime[direction]){
					if (around_size[up_length] <= aroundObject[direction].getLength()) {
						around_size[up_length] = aroundObject[direction].getLength();
					}
					around_size[direction + 3] += aroundObject[direction].getWidth();
				}
			}
			
			if(direction >= 5 && direction <= 7){
				if(aroundObject[direction] != null && aroundObject[direction].calculated == false) {
					aroundObject[direction].calculateSize();
					if(around_size[down_length] <= aroundObject[direction].getLength()){
						around_size[down_length] = aroundObject[direction].getLength();
					}
					around_size[direction + 3] += aroundObject[direction].getWidth();
				}else if(aroundObject[direction] != null && aroundObject[direction].getTime() < aroundFieldWriteTime[direction]){
					if(around_size[down_length] <= aroundObject[direction].getLength()){
						around_size[down_length] = aroundObject[direction].getLength();
					}
					around_size[direction + 3] += aroundObject[direction].getWidth();
				}
					
			}
			if(direction == 3 || direction == 4) {
				if (aroundObject[direction] != null && aroundObject[direction].calculated == false) {
					aroundObject[direction].calculateSize();
					if (around_size[cent_length] < aroundObject[direction].getLength()) {
						around_size[cent_length] = aroundObject[direction].getLength();
					}
					around_size[direction + 3] = aroundObject[direction].getWidth();
				}else if(aroundObject[direction] != null && aroundObject[direction].getTime() < aroundFieldWriteTime[direction]){
					if (around_size[cent_length] < aroundObject[direction].getLength()) {
						around_size[cent_length] = aroundObject[direction].getLength();
					}
					around_size[direction + 3] = aroundObject[direction].getWidth();
				}
			}
			if(direction <= 7 && aroundObject[direction] != null){
				aroundObject[direction].setTime(aroundFieldWriteTime[direction]);
			}
		}	
		
		if(getAroundArray()[5] != null || getAroundArray()[6] != null || getAroundArray()[7] != null){
			around_size[down_length] ++;
		}
		
		if((aroundObject[5] != null || aroundObject[7] != null) &&aroundObject[6] == null){
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
		
		if ((aroundObject[0] != null || aroundObject[2] != null) && aroundObject[1] == null)  {
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
		if(aroundObject[1] != null){
			cent_left = aroundObject[1].getLeftHalf();
			cent_right = aroundObject[1].getRightHalf();
		}
		if(aroundObject[6] != null){
			if(aroundObject[6].getLeftHalf() > cent_left){
				cent_left = aroundObject[6].getLeftHalf();
			}
			if(aroundObject[6].getRightHalf() > cent_right){
				cent_right = aroundObject[6].getRightHalf();
			}
		}
		left_half = left + cent_left;
		right_half = right + cent_right;
		
		length = around_size[up_length] + around_size[cent_length] + around_size[down_length] ;

		
		int cent_up = 0;
		int cent_bottom = 0;
		if(aroundObject[3] != null){
			cent_up = aroundObject[3].getUpHalf();
			cent_bottom = aroundObject[3].getBottomHalf();
		}
		if(aroundObject[4] != null){
			if(aroundObject[4].getUpHalf() > cent_up){
				cent_up = aroundObject[4].getUpHalf();
			}
			if(aroundObject[4].getBottomHalf() > cent_bottom){
				cent_bottom = aroundObject[4].getBottomHalf();
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
		if(set == true && (this.time > time|| this.h <= 1 ||(parent.size() != 0 &&  parent.get(parent.size()-1).isLinked() == false))){
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
			if(direction <= 7 && aroundObject[direction] != null){
				if(direction == 0){
					int cw = 0;
					int with = 0;
					if(aroundArray[0] != null){
						with = 1;
					}
					if(aroundObject[1] != null){
						cw = aroundObject[1].getLeftHalf();
					}
					if(aroundObject[6] != null && aroundObject[6].getLeftHalf() > cw){
						cw = aroundObject[6].getLeftHalf();
					}
					if(aroundObject[3] == null){
						aroundObject[0].setPosion(px - aroundObject[0].getWidth() - cw, py - aroundObject[0].getLength() - with,aroundFieldWriteTime[0], h + 1, newParent);
					}else{
						aroundObject[0].setPosion(px - aroundObject[0].getWidth() - cw , py - aroundObject[0].getLength() - aroundObject[3].getUpHalf() - with, aroundFieldWriteTime[0], h + 1, newParent);
					}
					if(aroundArray[0] != null){
						aroundArray[0].setPxy(aroundObject[0].getPx(), py + 1);
					
					}
				}
				
				if(direction == 1){
					int with = 0;
					if(aroundArray[1] != null){
						with = 1;
					}
					aroundObject[1].setPosion(px - aroundObject[1].getLeftHalf(), py - aroundObject[1].getLength() - with, aroundFieldWriteTime[1], h + 1, newParent);
					if(aroundArray[1] != null){
						aroundArray[1].setPxy(aroundObject[1].getPx(), py - 1);
					
					}
				}
				
				if(direction == 2){
					int cw = 0;
					int with = 0;
					if(aroundArray[2] != null){
						with = 1;
					}
					if(aroundObject[1] != null){
						cw = aroundObject[1].getRightHalf();
					}
					if(aroundObject[6] != null && aroundObject[6].getRightHalf() > cw){
						cw = aroundObject[6].getRightHalf();
					}
					if(aroundObject[4] == null){
						aroundObject[2].setPosion(px + 1 + cw, py - aroundObject[2].getLength() - with, aroundFieldWriteTime[2], h + 1, newParent);
					}else{
						aroundObject[2].setPosion(px + 1 + cw, py - aroundObject[4].getUpHalf() - aroundObject[2].getLength()-with, aroundFieldWriteTime[2], h + 1, newParent);
					}
					if(aroundArray[2] != null){
						aroundArray[2].setPxy(aroundObject[2].getPx(), py - 1);
					}
				}
				
				if(direction == 3){
					int with = 0;
					if(aroundArray[3] != null){
						with = 1;
					}
					int cw = 0;
					if(aroundObject[1] != null && aroundObject[3].up_half > 0){
						cw = aroundObject[1].getLeftHalf();
					}
					if(aroundObject[6] != null && aroundObject[6].getLeftHalf() > cw){
						cw = aroundObject[6].getLeftHalf();
					}		
					if(aroundArray[3] != null){
						aroundObject[3].setPosion(px - aroundObject[3].getWidth() - cw - with, py - aroundObject[3].getUpHalf(), aroundFieldWriteTime[3], h + 1, newParent);			
						aroundArray[3].setPxy(px - 1 , aroundObject[3].getPy());
					}else{	
						aroundObject[3].setPosion(px - aroundObject[3].getWidth() - cw, py - aroundObject[3].getUpHalf(), aroundFieldWriteTime[3], h + 1, newParent);	
					}
				}
				
				if(direction == 4){
					int cw = 0;
					int with = 0;
					if(aroundArray[4] != null){
						with = 1;
					}
					
					if(aroundObject[1] != null && aroundObject[4].up_half > 0){
						cw = aroundObject[1].getRightHalf();
					}
					if(aroundObject[6] != null && aroundObject[6].getRightHalf() > cw && aroundObject[4].bottom_half > 0){
						cw = aroundObject[6].getRightHalf();
					}
					//cw = 0; //ˆêŽž—lŽqŒ©
					aroundObject[4].setPosion(px + 1 + cw + with, py - aroundObject[4].getUpHalf(), aroundFieldWriteTime[4], h + 1, newParent);
					
					if(aroundArray[4] != null){
						aroundArray[4].setPxy(px + 1, aroundObject[4].getPy());	
					}
				}
				
				if(direction == 5){
					int cw = 0;
					int with = 0;
					if(aroundArray[5] != null){
						with  = 1;
					}
					if(aroundObject[1] != null){
						cw = aroundObject[1].getLeftHalf();
					}
					if(aroundObject[6] != null && aroundObject[6].getLeftHalf() > cw){
						cw = aroundObject[6].getLeftHalf();
					}
					if(aroundObject[3] == null){
						aroundObject[5].setPosion(px - aroundObject[5].getWidth() - cw, py + ownLength + with, aroundFieldWriteTime[5], h + 1, newParent);
					}else{
						aroundObject[5].setPosion(px - aroundObject[5].getWidth() - cw, py + aroundObject[3].getBottomHalf() + ownLength + with, aroundFieldWriteTime[5], h + 1, newParent);
					}
					
					if(aroundArray[5] != null){
						aroundArray[5].setPxy(aroundObject[5].getPx(), py + 1);
					}
				}
				
				if(direction == 6){
					int with = 0;
					if(aroundArray[6] != null){
						with = 1;
					}
					aroundObject[6].setPosion(px - aroundObject[6].getLeftHalf() , py + ownLength + with, aroundFieldWriteTime[6], h + 1, newParent);
					
					if(aroundArray[6] != null){
						aroundArray[6].setPxy(aroundObject[6].getPx(), py + 1);
						
					}	
				}
				
				if(direction == 7){
					int cw = 0;
					int with = 0;
					if(aroundArray[7] != null){
						with = 1;
					}
					
					if(aroundObject[1] != null){
						cw = aroundObject[1].getRightHalf();
					}
					if(aroundObject[6] != null && aroundObject[6].getRightHalf() > cw){
						cw = aroundObject[6].getRightHalf();
					}
					
					if(aroundObject[4] == null){
						aroundObject[7].setPosion( px + 1 + cw, py + ownLength + with, aroundFieldWriteTime[7], h + 1, newParent);
					}else{
						aroundObject[7].setPosion(px + 1  + cw, py + aroundObject[4].getBottomHalf() + ownLength + with, aroundFieldWriteTime[7], h + 1, newParent);
					}
					
					if(aroundArray[7] != null){
						aroundArray[7].setPxy(aroundObject[7].getPx(), py + 1);
					
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

	public Map<String, Value> getAnotherField() {
		return anotherField;
	}

	public void setAnotherField() {
		
	}

	public ObjectInfo[] getAround() {
		return aroundObject;
	}

	public void setAround(ObjectInfo[] around) {
		this.aroundObject = around;
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
	
	public void linked(ObjectInfo from, Field field){
		if(from != this){
			//this.lastLinked = from;
			linkedField.add(field);
		};
	}
	
	public void linkedFromArray(ArrayInfo from, int index){
		if(from != this){
			//this.lastLinked = from;
			linkedArray.add(new ArrayPair(from, index));
		}
	}
	
	
	public boolean isLinked(){
		if(linkedField.isEmpty() && linkedArray.isEmpty()){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean hasLink(){
		if(!anotherField.isEmpty()){
			return true;
		}
		for(int i = 0; i < 8; i++){
			if(aroundObject[i] != null || aroundArray[i] != null){
				return true;
			}
		}
		return false;
	}
	public void removeLink(Field field){
		linkedField.remove(field);
	}
	
	public void removeLinkArray(ArrayInfo from, int index){
		for(Iterator<ArrayPair> it = linkedArray.iterator(); it.hasNext();){
			ArrayPair pair = it.next();
			if(pair.array == from && pair.index == index){
				linkedArray.remove(pair);
				break;
			}
		}
	}
	class ArrayPair{
		ArrayInfo array;
		int index;
		
		ArrayPair(ArrayInfo array, int index){
			this.array = array;
			this.index = index;
		}
		
		public boolean equals(ArrayPair arr){
			return this.array == arr.array && this.index == arr.index;
		}
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
		return noDirectionField;
	}
	
	public int[] getAroundTime(){
		return aroundFieldWriteTime;
	}
	
	
	public boolean isHead(){
		return head;
	}
	
	public void setHead(){
		this.head = true;
	}

}

