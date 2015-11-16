package dast.ui;

import java.util.LinkedHashMap;
import java.util.Map;





public class ClassDefinition {
	private String name;
	private int numField = 0;
	private int numObject = 0;
	private LinkedHashMap<String, Integer> fieldDirection = new LinkedHashMap(); 
	
	ClassDefinition(String name){
		this.name = name;
	}
	
	public void setField(String fieldName, String dir){
		fieldDirection.put(fieldName, numDirection(dir));
		
		numField++;
	}
	
	private int numDirection(String dir){
		int direction = 9;
		switch (dir){
		case "^<":
			direction = 0;
			break;
		case "<^":
			direction = 0;
			break;
		case "^":
			direction = 1;
			break;
		case "^>":
			direction = 2;
			break;
		case ">^":
			direction = 2;
			break;
		case "<":
			direction = 3;
			break;
		case ">":
			direction = 4;
			break;
		case "v<":
			direction = 5;
			break;
		case "<v":
			direction = 5;
			break;
		case "v":
			direction = 6;
			break;
		case "v>":
			direction = 7;
			break;
		case ">v":
			direction = 7;
			break;
		case "*":
			direction = 8;
		
			break;
			default:
				System.out.println("Error in ClassDifinition.numDirection");
				break;
		}
		return direction;
	}
	
	/*public void setArray(String fieldName, String direction){
		setField(fieldName, direction);
	}*/
	
	public String getName(){
		return name;
	}
	
	public LinkedHashMap<String, Integer> getFields(){
		return fieldDirection;
	}
	
	public void addObject(){
		numObject++;
	}
	
	public int getNumObject(){
		return numObject;
	}

	public int getDirectionbyName(String name){
		if(fieldDirection.get(name) != null){
			return fieldDirection.get(name);
		}else if(fieldDirection.get(name + "[]") != null){
			return fieldDirection.get(name + "[]");
		}else{
			return -1;
		}
	}
	

}