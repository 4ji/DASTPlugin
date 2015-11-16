package dast.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IntegerType;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.event.ModificationWatchpointEvent;

public class ObjectManager {	
	private List<ReferenceType> preparedClass = new ArrayList<ReferenceType>();
	private List<ClassDefinition> targetClass = new ArrayList<ClassDefinition>();
	private List<ObjectReference> targetObject = new ArrayList<ObjectReference>();
	private List<ObjectInfo> objectInfo = new ArrayList<ObjectInfo>();
	//private List<List<ObjectInfo>> objectInfoMemory = new ArrayList<List<ObjectInfo>>();
	private List<ArrayInfo> arrayInfo = new ArrayList<ArrayInfo>();
	private List<ObjectInfo> drawTarget;
	private ReadDast rf;
	private Visualize visualize;
	
	private int time = 0;
	
	
	
	public ObjectManager(List<ClassDefinition> cld){
		targetClass = cld;
	}
	
	
	/*public void setLink(){
		if(objectInfo.size() > 0){
		for(Iterator<ObjectInfo> it = objectInfo.iterator(); it.hasNext();){
			ObjectInfo oinfo = (ObjectInfo)it.next();
			try {
				oinfo.setLink(time);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
	}*/
	
	public ObjectInfo searchObjectInfo(ObjectReference tar){
		for(Iterator<ObjectInfo> it = objectInfo.iterator(); it.hasNext();){
			ObjectInfo oin = it.next();
			
			if(tar != null && oin.sameObject(tar)){
				return oin;
			}
		}
		return null;
	}
	
	public void draw(){
		
		if(objectInfo.size() > 0){
		List<ObjectInfo> tar = objectInfo;
		if(visualize == null){
			set(tar);
			//shortSpace(tar);
			visualize = new Visualize(tar);
		}else{	
			set(tar);
			//shortSpace(tar);
			visualize.reDraw(tar);
		}
		}
		paramReset();
		
		
	}
	
	private void shortSpace(List<ObjectInfo> tar){
		int maxX = 0, maxY = 0;
		for(Iterator<ObjectInfo> it = tar.iterator(); it.hasNext();){
			ObjectInfo oi = it.next();
			if(oi.getPx() > maxX){
				maxX = oi.getPx();
			}
			if(oi.getPy() > maxY){
				maxY = oi.getPy();
			}
		}

		for(int i = 0; i < maxX; i++){
			boolean fx = false;
			for(Iterator<ObjectInfo> it = tar.iterator(); it.hasNext();){
				ObjectInfo oi = it.next();
				if(oi.px == i){
					fx = true;
				}
				if(fx){
					break;
				}
			}
			if(!fx){
				for(Iterator<ObjectInfo> it = tar.iterator(); it.hasNext();){
					ObjectInfo oi2 = it.next();
					if(oi2.px > i){
						oi2.px--;
					}
				}
			}
		}

		for(int i = 0; i < maxY; i++){
			boolean fy = false;
			for(Iterator<ObjectInfo> it = tar.iterator(); it.hasNext();){
				ObjectInfo oi = it.next();
				if(oi.py == i){
					fy = true;
				}
			}
			if(!fy){
				for(Iterator<ObjectInfo> it = tar.iterator(); it.hasNext();){
					ObjectInfo oi2 = it.next();
					if(oi2.py > i){
						oi2.py--;
					}
				}
			}
		}
	}
	protected void paramReset(){
		for(Iterator<ObjectInfo> it = objectInfo.iterator(); it.hasNext();){
			ObjectInfo oin = it.next();
			oin.reset();
		}
			
	}
	public void set(List<ObjectInfo> tar) {	
		ArrayList<ObjectInfo> another = new ArrayList<ObjectInfo>();
		int totalHeight = 0;
		for(Iterator<ObjectInfo> it = tar.iterator(); it.hasNext();){
			ObjectInfo oin = it.next();
			if(oin.isLinked() == false && oin.hasLink() == true){
				oin.calculateSize();
				totalHeight += oin.setPosion(totalHeight);
				totalHeight++;
				
				for(int i = 0; i < oin.getWidth() || i < oin.getLength(); i++){
					boolean slideLeft = true;
					boolean slideUp = true;
					for(Iterator<ObjectInfo> iit = tar.iterator(); iit.hasNext();){
						ObjectInfo oi = iit.next();
						if(oi.getPx() == i){
							slideLeft = false;
						}
						if(oi.getPy() == i){
							slideUp = false;
						}
						if(slideUp == false && slideLeft == false){
							break;
						}
					}
					if(slideLeft && i <= oin.getWidth()){
						for(Iterator<ObjectInfo> iit = tar.iterator(); iit.hasNext();){
							ObjectInfo oi = iit.next();
							if(oi.getPx() > i){
								oi.slideLeft();
							}
						}
					}
					if(slideUp && i <= oin.getLength()){
						for(Iterator<ObjectInfo> iit = tar.iterator(); iit.hasNext();){
							ObjectInfo oi = iit.next();
							if(oi.getPy() > i){
								oi.slideUp();
							}
						}
					}
					
				}
			}else if(oin.isLinked() == false && oin.hasLink() == false){
				another.add(oin);
			}
		}
		if(another.size() > 0){
			for(int i = 0; i < another.size(); i++){
				another.get(i).setAnotherPosion(i, totalHeight);
			}
		}
	}
		
	public boolean classPrepare(ClassType tar){
		if(isDefinedClass(tar) != null){
			preparedClass.add(tar);
			return true;
		}
		return false;
	}
	
	public boolean classPrepare(ReferenceType tar) {
		if(isDefinedClass(tar) != null){
			preparedClass.add(tar);
			return true;
		}
		return false;
	}
	
	public ClassDefinition isDefinedClass(ReferenceType tar){
		
		for(Iterator<ClassDefinition> it = targetClass.iterator(); it.hasNext();){
			ClassDefinition cld = ((ClassDefinition) it.next());
			//System.out.println(tar.name() + " "+cld.getName());
			if(tar.name().matches(".*\\.*" + cld.getName())){
				//System.out.println(cld.getName() + " "+ tar.name() );
				return cld;
			}else if(tar.name().equals(cld.getName())){
				//System.out.println(cld.getName() + " "+ tar.name() );
				return cld;
			}
		}
		return null;
	}
	
	public void renew(ModificationWatchpointEvent event){
		time++;
		ObjectReference tar = event.object();
		ObjectInfo obInfo = isMadeObjectInfo(event.object());
		Value value = event.valueToBe();
		Field field = event.field();
		Type type = null;
		if(value != null){
			type = value.type();

		}
		
		if(value != null && value.type() instanceof ReferenceType){
			makeObjectInfo((ObjectReference)value, field);
		}
		
		if(obInfo != null){
			if(type != null && type instanceof ArrayType){
				addArray(tar, field, (ObjectReference)value);
				obInfo.link();
			}
			try {
				obInfo.setLink(time, event.field(), value);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			/*ClassDefinition cld = isDefinedClass((ReferenceType) tar.referenceType());
			if(cld != null){
				getTargetObject().add(tar);
				ObjectInfo object = new ObjectInfo(tar, (ReferenceType)tar.referenceType(), cld, this);
				object.setField();
				objectInfo.add(object);
				try {
					object.setLink(time, event.field());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				if(type != null && type instanceof ArrayType){
					addArray(tar, field, (ObjectReference)value);
					object.Link();
				}
				
			}*/
			makeObjectInfo(tar, event.field());			
		}
		
	}
	
	public void addArray(ObjectReference from,Field field, ObjectReference value){
		if(searchObjectInfo(value) == null){
			ClassDefinition cld = isDefinedClass((ClassType) from.referenceType());
			int directed = cld.getDirectionbyName(field.name());
			ArrayInfo array = new ArrayInfo(value, value.type(), null, this, directed, field.name());
			objectInfo.add(array);
			arrayInfo.add(array);
		}
	}


	public void renew(ObjectReference object, Field field, Value value){
		
		time++;
		
 		ObjectInfo obInfo = isMadeObjectInfo(object);
		Type type = null;
		if(value != null){
			type = value.type();
		}
		
		if(value != null && type instanceof ReferenceType){
			makeObjectInfo((ObjectReference)value, field);
		}
		if(obInfo != null){		
			if(type != null && type instanceof ArrayType){
				addArray(object, field, (ObjectReference)value);
			}
			try {
				obInfo.setLink(time, field, value);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}else{
			if(object != null){
			ClassDefinition cld = isDefinedClass((ClassType) object.referenceType());
			if(cld != null){
				targetObject.add(object);
				ObjectInfo obj = new ObjectInfo(object, (ClassType)object.referenceType(), cld, this);
				obj.setField();
				objectInfo.add(obj);
				
				if(type != null && type instanceof ArrayType){
					addArray(object, field, (ObjectReference)value);
					obj.link();
				}
				try {
					obj.setLink(time, field, value);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}
		}
		
		for(Iterator<ArrayInfo> it = arrayInfo.iterator(); it.hasNext();){
			ArrayInfo ai = it.next();
			ai.setLink(time);
		}
		
		if(object != null){
			check(object);
		}
		 
	}	
	

	
	ObjectInfo isMadeObjectInfo(ObjectReference tar){
		//System.out.println();
		//System.out.println("target:"+tar);
		for(Iterator<ObjectInfo> it = objectInfo.iterator(); it.hasNext();){
			ObjectInfo obInfo = (ObjectInfo)it.next();
			//System.out.println("next:"+obInfo.getobject());
			if(obInfo.getObject().equals(tar)){
				return obInfo;
			}
		}
		//System.out.println("false:" +  tar);
		return null;
	}
	

	public List<ObjectReference> getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(List<ObjectReference> targetObject) {
		this.targetObject = targetObject;
	}

	public List<ObjectInfo> getObjectInfo() {
		return objectInfo;
	}
	
	public void addObject(ObjectInfo tar){
		objectInfo.add(tar);
	}

	public void makeObjectInfo(ObjectReference tar, Field field){
		ClassDefinition cld =  isDefinedClass((ReferenceType)tar.type());
		if(cld != null){
			ObjectInfo obji = isMadeObjectInfo(tar);
			if(obji == null){
				ObjectReference objr = tar;
				getTargetObject().add(objr);
				ObjectInfo object = new ObjectInfo(objr, objr.referenceType(), cld, this);
				objectInfo.add(object);
				//System.out.println("add " + toBe);
				object.setField();
				try {
					object.setLink(time, field);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				if(tar.type() != null && tar.type() instanceof ArrayType){
					addArray(tar, field, objr);
					object.link();
				}
			}
			
		}
	}
	
	public void check(ObjectReference obj){
		ObjectInfo obin = isMadeObjectInfo(obj);
		for(Iterator<Field> fIt = obj.referenceType().allFields().iterator(); fIt.hasNext();){
			Field field = fIt.next();
			Value tar = obj.getValue(field);
			if(tar != null && tar instanceof ObjectReference && isMadeObjectInfo((ObjectReference)tar) != null){
				try {
					obin.setLink(time, field);
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

	public boolean checkArray() {
		boolean changed = false;
		for(Iterator<ArrayInfo> it = arrayInfo.iterator(); it.hasNext(); ){
			ArrayInfo tar = it.next();
			if(tar.checkArrayValue(time)){
				changed = true;
			}
		}
		
		return changed;
	}
	
	public int getTime(){
		return time;
	}
	
	public void incTime(){
		time++;
	}
	
	public List<ObjectReference> getTargetObjectList(){
		return targetObject;
	}
	
	public List<ObjectInfo> getObjectInfoList(){
		return objectInfo;
	}
	
	public List<ArrayInfo> getArrayInfoList(){
		return arrayInfo;
	}
	
	public Visualize getVisualize(){
		return visualize;
	}
	
	public void setVisualize(Visualize v){
		visualize = v;
	}
}
