package app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import data.FrameData;

public class DiscAccess {
	
	private FrameData f;
	private static final String PATH = "frame.ser";
	
	public void save(FrameData data, Map<String, Object> state) {
				
		//ObjectOutputStream oos;
		try (ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(PATH))) {
			
			oos.writeObject(new SaveFile(data, state));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public SaveFile load() {
		return(load(PATH));
	}
	
	public SaveFile load (String path) {
		File f = new File(path);
		if (!f.exists()) {
			System.out.println("file " + path + " do not exists");
			return null;
		}
		
		System.out.println("file " + path + " is being loaded");
		
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));	
		ObjectInputStream ois = new ObjectInputStream(bis)){
			
			return (SaveFile)ois.readObject();
			
		} catch (IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;		
	}
	
}
