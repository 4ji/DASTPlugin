package dast.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.VMDisconnectEvent;

@SuppressWarnings("restriction")
public class ObjectManager {	
	private List<ReferenceType> preparedClass = new ArrayList<ReferenceType>();
	private List<ClassDefinition> targetClass = new ArrayList<ClassDefinition>();
	private List<ObjectReference> targetObject = new ArrayList<ObjectReference>();
	protected List<ObjectInfo> objectInfo = new ArrayList<ObjectInfo>();
	//private List<List<ObjectInfo>> objectInfoMemory = new ArrayList<List<ObjectInfo>>();
	private List<ArrayInfo> arrayInfo = new ArrayList<ArrayInfo>();
	private List<ObjectInfo> drawTarget;
	private ReadDast rf;
	private Visualize visualize;
	
	private int time = 0;
	
	
	
	public ObjectManager(List<ClassDefinition> cld){
		targetClass = cld;
	}

	
	
	public void fieldWrite(ModificationWatchpointEvent e){
		incTime();
		ObjectReference object = e.object();
		ObjectInfo oin = searchObjectInfo(object);
		if(oin == null){
			oin = createObjectInfo(object);
		}
		
		if(e.valueToBe() instanceof ArrayReference){
			ArrayReference value = (ArrayReference)e.valueToBe();
			ArrayInfo valueArr = searchArrayInfo(value);
			if(valueArr == null){
				valueArr = createArrayInfo((ArrayReference)value, object, e.field());
			}
			
		}else if(e.valueToBe() instanceof ObjectReference){
			ObjectReference value = (ObjectReference)e.valueToBe();
			if(isDefinedClass(value.referenceType()) != null){
				ObjectInfo valueOin = searchObjectInfo(value);
				if(valueOin == null){
					valueOin = createObjectInfo(value);
				}
			}
		}
				
		
		try {
			oin.setLink(time, e.field(), e.valueToBe());
		} catch (IllegalArgumentException ex1) {
			// TODO Auto-generated catch block
			ex1.printStackTrace();
		} catch (IllegalAccessException ex1) {
			// TODO Auto-generated catch block
			ex1.printStackTrace();
		}
		
		//refreshAllObjectField();
	}
	
	public void staticFieldWrite(ModificationWatchpointEvent e){
		incTime();
		String ClsName = e.field().declaringType().name();
		ClassDefinition cld = null;
		for(int i = 0; i < targetClass.size();i++ ){
			if(ClsName.equals(targetClass.get(i).getName())){
				cld = targetClass.get(i);
			}
		}
		if(cld != null){
			for(int i = 0; i < objectInfo.size(); i++){
				ObjectInfo obi = objectInfo.get(i);
				if(obi.getDef() == cld){
					try {
						obi.setLink(time, e.field(), e.valueToBe());
					} catch (IllegalArgumentException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

		}
	}
	
	public void arrayWrite(AccessWatchpointEvent e){
		incTime();
		ObjectReference object = e.object();
		ObjectInfo oin = searchObjectInfo(object);
		if(oin == null){
			return;
		}
		ArrayReference targetArray = (ArrayReference)object.getValue(e.field());
		ArrayInfo targetArrayInfo = searchArrayInfo(targetArray);
		if(targetArrayInfo != null){
			targetArrayInfo.setArrayValue(time);
		}
		
	}
	
	public ObjectInfo createObjectInfo(ObjectReference tar){
			ClassDefinition cld = isDefinedClass(tar.referenceType());				
			getTargetObject().add(tar);
			ObjectInfo object = new ObjectInfo(tar, cld, this);
			objectInfo.add(object);
			object.checkStatic(time);
			return object;
	}
	
	private ArrayInfo createArrayInfo(ArrayReference tar, ObjectReference from, Field field){
		ClassDefinition cld = isDefinedClass((ClassType)from.referenceType());
		int directed = cld.getDirectionByName(field.name());
		ArrayInfo array = new ArrayInfo(tar, null, this, directed, field.name());
		objectInfo.add(array);
		arrayInfo.add(array);
		return array;
	}


	public ObjectInfo searchObjectInfo(ObjectReference tar){
		for(Iterator<ObjectInfo> it = objectInfo.iterator(); it.hasNext();){
			ObjectInfo oin = it.next();
			
			if(tar != null && oin.sameObject(tar)){
				return oin;
			}
		}
		return null;
	}
	
	public ArrayInfo searchArrayInfo(ArrayReference tar){
		if(tar == null){
			return null;
		}
		for(Iterator<ArrayInfo> it = arrayInfo.iterator(); it.hasNext();){
			ArrayInfo ain = it.next();
			if(ain.sameObject(tar)){
				return ain;
			}
		}
		return null;
	}
	
	ObjectInfo isMadeObjectInfo(ObjectReference tar){
		for(Iterator<ObjectInfo> it = objectInfo.iterator(); it.hasNext();){
			ObjectInfo obInfo = (ObjectInfo)it.next();
			if(obInfo.getObject().equals(tar)){
				return obInfo;
			}
		}
		return null;
	}
	
	/*private void refreshAllObjectField(){
		for(Iterator<ObjectInfo> it = objectInfo.iterator(); it.hasNext();){
			ObjectInfo tar = it.next();
			tar.setLi
		}
	}*/
	
	public synchronized void draw(){
		
		if(objectInfo.size() > 0){
		
		if(visualize == null){
			setPosion(objectInfo);
			//shortSpace(tar);
			visualize = new Visualize(objectInfo);
		}else{	
			setPosion(objectInfo);
			//shortSpace(tar);
			visualize.reDraw(objectInfo);
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
	public void setPosion(List<ObjectInfo> tar) {	
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
			if(tar.name().equals(cld.getName())){
				return cld;
			}
		}
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


	/*
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
	}*/

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



	public void VMDisconnect(VMDisconnectEvent event) {
		VirtualMachine vm = event.virtualMachine();
		List<ReferenceType> allClasses = vm.allClasses();
		for(Iterator<ReferenceType> it = allClasses.iterator(); it.hasNext();){
			ReferenceType ref = it.next();
			if(isDefinedClass(ref) != null){
			}
		}
	}
	

}
