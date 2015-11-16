package dast.ui;

import java.io.InputStream;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReadDast extends StreamTokenizer {
	private final static char LCB = '{';
	private final static char RCB = '}';
	//private final static char COLON = ':';
	
	private int numClass = 0; 
	
	private List<ClassDefinition> classSet = new ArrayList<ClassDefinition>();
	
	@SuppressWarnings("deprecation")
	public	ReadDast(InputStream in){
		// TODO Auto-generated method stub
		
		super(in);

		try {
			int ret;

			wordChars('<', '>');
			wordChars('^', '^');
			wordChars('[', ']');
			wordChars('$','$');
			wordChars('*','*');
			eolIsSignificant(true);

			while ((ret = nextToken()) != TT_EOF) {
				switch (ret) {
				case TT_WORD:
					if (sval.equals("Class")) {
						while ((ret = nextToken()) != TT_EOL) {
							if (ret == TT_WORD) {
								setClass(sval);
							}
						}
					} else {
						int tar = searchClass(sval);
						setValue(tar);

					}
					break;
				case TT_NUMBER:
					//System.out.println(nval);
					break;
				}
			}
			
			int i = 0;
			for(ClassDefinition tar : classSet){
				if(tar == null){
					break;
				}
				//System.out.println("Class[" + i + "] : " + tar.getName());
				
				int j = 0;
				
				Map<String, Integer> fieldMap = classSet.get(i).getFields();
				
				for(String field : fieldMap.keySet()){
					if(field == null){
						break;
					}
					//System.out.println(" " + "Field[" + j + "]  " + field + ":" + fieldMap.get(field)); 
					j++;
				}
				i++;
			}
			//System.out.println("");
			
			
			
		} catch (Exception e) {
			System.err.println("Exception :" + e);
			e.printStackTrace();
		}
	}
	
	private void setClass(String name){
		classSet.add(new ClassDefinition(name));
		numClass++;
	}
	
	private int searchClass(String name){
		for (int i = 0; i < numClass; i++) {
			if (classSet.get(i).getName().equals(name)) {
				return i;
			}
		}
		return -1;		
	}
	
	private  void setValue(int tar) {
		if (tar < 0) {
			System.out.println(sval + "‚Í“o˜^‚³‚ê‚Ä‚¢‚È‚¢ƒNƒ‰ƒX‚Å‚·");
		} else {
			try {
				int ret = nextToken();
				if (ret != LCB) {
					System.out.println("‹Lq‚ÉŠÔˆá‚¢‚ª‚ ‚è‚Ü‚·");
				} else {
					while ((ret = nextToken()) != RCB) {
						if(ret == TT_WORD){
							String fieldName = sval;
							/*Pattern p = Pattern.compile("\\[\\]$");
							Matcher m = p.matcher(fieldName);
							if(m.find()){
								while((ret = nextToken()) != TT_WORD);
								classSet.get(tar).setArray(fieldName, sval);
							}else{
								while((ret = nextToken()) != TT_WORD);
								classSet.get(tar).setField(fieldName, sval);
							}*/	
							while((ret = nextToken()) != TT_WORD);
							classSet.get(tar).setField(fieldName, sval);
						}
					}
				}
			} catch (Exception e) {
				System.err.println("Exception :" + e);
			}
		}
	}
	
	public boolean isTarget(String name){
		if(searchClass(name) >= 0){
			return true;
		}else{
			return false;
		}
	}
	
	public ClassDefinition checkClass(String name){
		for(int i =0; i < numClass; i++){
			if (classSet.get(i).getName().equals(name)) {
				classSet.get(i).addObject();
				return classSet.get(i);
			}
		}
		return null;
	}
	
	public List<ClassDefinition> getClassDefinition(){
		return classSet;
	}

}