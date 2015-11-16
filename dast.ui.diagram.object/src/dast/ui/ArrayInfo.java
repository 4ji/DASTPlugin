package dast.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Type;
import com.sun.jdi.Value;



public class ArrayInfo extends ObjectInfo{
	protected int size;
	protected ArrayReference array;
	protected boolean isPrimitive = false;
	protected ObjectInfo[] arrayValue;
	protected Value[] primitiveArray;
	protected int directed;
	private String fieldName;
	protected Type type;
	
	protected int[] changeTime;


	private String name;
	private int width = 0;
	private int length = 0;
	protected int ownLength = 1;
	private int ownWidth = 1;
	private int up_half = 0;
	private int bottom_half = 0;
	private int left_half = 0;
	private int right_half = 0;
	protected int with = -1;
	

	
	ArrayInfo(ObjectReference tar, Type type, ClassDefinition def,
			ObjectManager om, int directed, String fieldName) {
		//Å¶def = null
		super(tar, type, def, om);
		array = (ArrayReference)(this.object);
		this.directed = directed;
		size = ((ArrayReference) tar).length();
		this.fieldName = fieldName;
		this.type = type;
	}

	ArrayInfo(ObjectInfo source){
		super(source);
		array = (ArrayReference)(this.object);
		isPrimitive = ((ArrayInfo)source).isPrimitive;
		this.directed = ((ArrayInfo)source).getDirection();
		size = ((ArrayInfo)source).getSize();
		this.type = source.type;
		
		this.fieldName = ((ArrayInfo)source).getFieldName();
		this.px = source.getPx();
		this.py = source.getPy();
	}

	public void setLink(int time){
		try {
			boolean isRead = false;
			for(int i = 0; i < size; i++){
				if(array != null && array.getValue(i) != null){
					isRead = true;
					break;
				}else if(array == null){
					//System.out.println("E1");
				}
			}
			if(isRead){
	
				if((((ArrayType)type).componentType() instanceof PrimitiveType ) || ((((ArrayType)type).componentType().toString().matches(".*" + "java.lang.String" + ".*")))){
					this.isPrimitive = true;
				}
				if(((ArrayType) type).componentTypeName().equals("java.lang.String")){
					
					this.isPrimitive = true;
				}
			}
		} catch (ClassNotLoadedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(isPrimitive);
		if(isPrimitive){
			if(primitiveArray == null){
				primitiveArray = new Value[size];
			}
			for(int i = 0; i < size; i++){
				primitiveArray[i] = array.getValue(i);
			}
		}else{
			if(arrayValue == null){
				arrayValue = new ObjectInfo[size];
				changeTime = new int[size];
			}
			for(int i = 0; i < size; i++){
				ObjectInfo obin = om.searchObjectInfo((ObjectReference)array.getValue(i));
				if(obin != null && arrayValue[i] != obin){
					arrayValue[i] = obin;
					changeTime[i] = time;
				}
				if(arrayValue[i] != null){
					arrayValue[i].linked(this);
					this.link();
				}else{
					ObjectReference tar = (ObjectReference) array.getValue(i);
					if(tar != null){
						ClassDefinition cld = om.isDefinedClass(tar.referenceType());
						if(cld != null){
							om.getTargetObject().add(tar);
							ObjectInfo object = new ObjectInfo(tar, (ReferenceType)tar.referenceType(), cld, om);
							object.setField();
							om.getObjectInfo().add(object);
						}
					}
				}
				
			}
		}
	}
	
	public void calculateSize(){
	
		if(isPrimitive == true){
			calculatePrimitive();
			return;
		}
		calculated =true;
		if(directed == 3 || directed == 4){
			ownLength = size + 1;
			ownWidth = 1;
			
			for(int i =0; i < size; i++){
				if(arrayValue[i] != null && arrayValue[i].isCalculated() == false){
					arrayValue[i].calculateSize();
					length += arrayValue[i].getLength();
					if(arrayValue[i].getWidth() > width){
						width = arrayValue[i].getWidth();
					}
					arrayValue[i].setTime(arrayValue[i].getTime());
				}else if(arrayValue[i] != null && arrayValue[i].getTime() < changeTime[i]){
					length += arrayValue[i].getLength();
					if(arrayValue[i].getWidth() > width){
						width = arrayValue[i].getWidth();
					}
					arrayValue[i].setTime(arrayValue[i].getTime());
				}else{
					length ++;
				}
			}
			width += ownWidth;
		}else{

			ownWidth = size;
			
			for(int i =0; i < size; i++){
				if(arrayValue[i] != null && arrayValue[i].isCalculated() == false){

					arrayValue[i].calculateSize();

					width += arrayValue[i].getWidth();
					if(arrayValue[i].getLength() > length){
						length = arrayValue[i].getLength();
					}
					arrayValue[i].setTime(arrayValue[i].getTime());
				}else if(arrayValue[i] != null && arrayValue[i].getTime() < changeTime[i]){
					width += arrayValue[i].getWidth();
					if(arrayValue[i].getLength() > length){
						length = arrayValue[i].getLength();
					}
					arrayValue[i].setTime(arrayValue[i].getTime());
				}else{
					width++;
				}
				
			}
			length += ownLength;
		}

		setSize();
	
	}
	
	private void calculatePrimitive(){
		calculated =true;
		if(directed == 3 || directed == 4){
			ownLength = size + 1;
			ownWidth = 1;
			
			width = 1;
			length = size + 1;

		}else{

			ownWidth = size;
			ownLength = 1;
			
			width = size;
			length = 1;
					
			
		}
		setPrimitiveSize();
	}
	
	void setSize(){
		int cent = size / 2;
		//System.out.println(cent);
		if(directed ==3 || directed == 4){
			int child_width = 0;
			for(int i = 0; i < cent; i++){
				if(arrayValue[i] != null){
					up_half += arrayValue[i].getLength();
					if(arrayValue[i].getWidth() > child_width){
						child_width = arrayValue[i].getWidth();
					}
				}else{
					up_half++;
				}
			}
			if(arrayValue[cent] != null){
			up_half += arrayValue[cent].getUpHalf();
			if(arrayValue[cent].getWidth() > child_width){
				child_width = arrayValue[cent].getWidth();
			}
			}
			for(int i = cent + 1; i < size; i++){
				if(arrayValue[i] != null){
					bottom_half += arrayValue[i].getLength();
					if(arrayValue[i].getWidth() > child_width){
						child_width = arrayValue[i].getWidth();
					}
				}else{
					bottom_half++;
				}
			}
			if(arrayValue[cent] != null){
			bottom_half += arrayValue[cent].getBottomHalf();
			}
			if(directed == 3){
				left_half = child_width;
			}else if(directed == 4){
				right_half = child_width;
			}
			
		}else{
			int child_length = 0;
			for(int i = 0; i < cent; i++){
				if(arrayValue[i] != null){
					left_half += arrayValue[i].getWidth();
					if(child_length  > arrayValue[i].getLength()){
						child_length = arrayValue[i].getLength();
					}
				}else{
					//left_half++;
				}
			}
			if(arrayValue[cent] != null){
			left_half += arrayValue[cent].getLeftHalf();
			if(child_length  > arrayValue[cent].getLength()){
				child_length = arrayValue[cent].getLength();
			}
			}
			for(int i = cent + 1; i < size; i++){
				if(arrayValue[i] != null){
					right_half += arrayValue[i].getWidth();
					if(child_length  < arrayValue[i].getLength()){
						child_length = arrayValue[i].getLength();
					}
				}else{
					//right_half++;
				}
			}
			if(arrayValue[cent] != null){
			right_half += arrayValue[cent].getRightHalf();
			}
			
			if(directed >= 0 && directed <= 2){
				up_half = child_length;
			}else{
				bottom_half = child_length;
			}
		}
	}
	
	private void setPrimitiveSize(){
		if(directed ==3 || directed == 4){
			up_half = length / 2;
			bottom_half = length - up_half;
			right_half = 0;
			left_half = 0;
		}else{
			up_half = 0;
			bottom_half = 0;
			right_half = width / 2;
			left_half = width - right_half;
		}
	}
	
	
	void setPosion(int ulx, int uly,int time, int h, ArrayList<ObjectInfo> parent){
		if(isPrimitive == true ){
			setPrimitivePosion(ulx, uly);
			return;
		}
		if(set == true && (this.time != time || this.h <= 1)){
			return;
		}
		for(Iterator<ObjectInfo> it = parent.iterator(); it.hasNext();){
			if(((ObjectInfo)it.next()).equals(this)){
				return;
			}
		}
		parent.add(this);
		
		set = true;
		this.h = h;
		
		if(with >= 0){
			if(0 <= with && with <= 2){
				px = ulx + getLeftHalf();
				py = uly + getUpHalf() - 1;
			}else if(with == 3){
				px = ulx + getLeftHalf();
				py = uly + getUpHalf();
			}else if(with == 4){
				px = ulx + getLeftHalf() + 1;
				py = uly + getUpHalf();
			}else{
				px = ulx + getLeftHalf();
				py = uly + getUpHalf() + 1;
			}
		}else{

		px = ulx + getLeftHalf();
		py = uly + getUpHalf();
		}
		
		/*System.out.println(type.name() + index + ":(" + px + "," + py + ") ");
		System.out.println("l:" + getLeftHalf() + " r:"+ getRightHalf() + " u:" + getUpHalf() + " d:" + getBottomHalf());
		System.out.println("ulx:" + ulx + " uly:" + uly);
		System.out.println("width:" + getWidth() + " length:" + getLength());
		System.out.println();
		*/
		if(directed >= 0 && directed <= 2){
			int nx = 0;
			for(int i = 0; i < size; i++){
				if(arrayValue[i] != null){
					arrayValue[i].setPosion(ulx + nx, py - arrayValue[i].getLength(),changeTime[i], h + 1, parent);
					nx += arrayValue[i].getWidth();
				}else{
					//nx++;
				}
			}
		}else if(directed == 3){
			int ny = 0;

			for(int i = 0; i < size; i++){
				if(arrayValue[i] != null){
					arrayValue[i].setPosion(ulx  , uly + ny, changeTime[i],h + 1, parent);
					ny += arrayValue[i].getLength();
				}else{
					ny ++;
				}
			}
			
		}else if(directed == 4){
			int ny = 0;
			for(int i = 0; i < size; i++){
				if(arrayValue[i] != null){
					arrayValue[i].setPosion(px + 1, uly + ny, changeTime[i], h+1, parent);
					ny += arrayValue[i].getLength();
				}else{
					ny ++;
				}
			}
			
		}else{
			int nx = 0;
			for(int i = 0; i < size; i++){
				if(arrayValue[i] != null){
					arrayValue[i].setPosion(ulx + nx, py + ownLength, changeTime[i], h+ 1, parent);
					nx += arrayValue[i].getWidth();
				}else{
					//nx++;
				}
			}
		}
	}
	
	protected void setPrimitivePosion(int ulx, int uly){
		if(set == true){
			return;
		}
		
		set = true;
		
		if(with >= 0){
			if(0 <= with && with <= 2){
				px = ulx + getLeftHalf();
				py = uly + getUpHalf() - 1;
			}else if(with == 3){
				px = ulx + getLeftHalf();
				py = uly + getUpHalf();
			}else if(with == 4){
				px = ulx + getLeftHalf() + 1;
				py = uly + getUpHalf();
			}else{
				px = ulx + getLeftHalf();
				py = uly + getUpHalf() + 1;
			}
		}else{

		px = ulx + getLeftHalf();
		py = uly + getUpHalf();
		}
		
		/*System.out.println(type.toString() + index + ":(" + px + "," + py + ") ");
		System.out.println("l:" + getLeftHalf() + " r:"+ getRightHalf() + " u:" + getUpHalf() + " d:" + getBottomHalf());
		System.out.println("ulx:" + ulx + " uly:" + uly);
		System.out.println("width:" + getWidth() + " length:" + getLength());*/
	}
	boolean checkObject(Object obj){
		return checkArray(obj);
	}
	
	boolean checkArray(Object obj){
		if(obj.equals(array)){
			return true;
		}
		return false;
	}

	
	public int getSize(){
		return size;
	}

	public boolean isPrimitive() {
		// TODO Auto-generated method stub
		return isPrimitive;
	}
	
	public ArrayReference getArray(){
		return array;
	}

	public int getDirection() {		
		return directed;
	}
	
	public ObjectInfo[] getArrayValue(){
		return arrayValue;
	}
	
	public ObjectInfo getArrayValue(int i){
		return arrayValue[i];
		
	}
	
	public String getFieldName(){
		return fieldName;
	}
	
	public ObjectInfo deepCopy(){
		ObjectInfo tar = new ArrayInfo(this);
		return tar;
		
	}
	
	public boolean isArray(){
		return true;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getLength(){
		return length;
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

	public void setPxy(int x, int y) {
		// TODO Auto-generated method stub
		set = true;
		px = x;
		py = y;
	}
	
	public Value getValue(int i){
		return primitiveArray[i];
	}

	public boolean arrayUpdated() {
		
		for(int i = 0; i < size; i++){
			
			if(isPrimitive){
				if(primitiveArray == null || array == null){
					return false;
				}
				if(primitiveArray[i] != array.getValue(i)){
					return true;
				}
				/*
				if(primitiveArray[i]== null && array.getValue(i) == null){
					return false;
				}else if(primitiveArray[i] == null && array.getValue(i) != null){
					return true;
				}else if(primitiveArray[i] != null && array.getValue(i) == null){
					return true;
				}else if(primitiveArray[i] != array.getValue(i)){
					return true;
				}else{
					return false;
				}*/
			}else if(!(isPrimitive)){
				if(arrayValue == null || array == null){
					return false;
				}
				if(arrayValue[i] == null && array.getValue(i) != null){
					return true;
				}else if(arrayValue[i] != null && array.getValue(i) == null){
					return true;
				}else if(arrayValue[i] != null && array.getValue(i) != null){
					if(!(arrayValue[i].getObject().equals(array.getValue(i)))){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void reset(){
		super.reset();
	    width = 0;
		length = 0;
		ownLength = 1;
		ownWidth = 1;
		up_half = 0;
		bottom_half = 0;
		left_half = 0;
		right_half = 0;
	}

	public boolean checkArrayValue(int time) {
		boolean changed = false;
		if(isPrimitive){
			for(int i = 0; i < size; i++){
				if(array.getValue(i) != primitiveArray[i]){
					changed = true;
					break;
				}
			}
		}else{
			for(int i = 0; i < size; i++){
				if((array.getValue(i) != null && arrayValue[i] != null && array.getValue(i) != arrayValue[i].getObject())
						|| (array.getValue(i) != null && arrayValue[i] == null)
						||(array.getValue(i) == null && arrayValue[i] != null)){
					changed = true;
					break;
				}
			}
		}
		if(changed){
			setLink(time);
			return true;
		}else{
			return false;
		}
	}
	

}
