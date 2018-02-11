package app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.naming.Context;

import data.FrameData;

public class DiscAccess {
	
	private FrameData f;
	private static final String PATH = "frame.ser";
	
	public static void saveFrame(FrameData f) {
				
		//ObjectOutputStream oos;
		try (ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(PATH))) {
			
			oos.writeObject(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public static FrameData loadFrame() {
		return(loadFrame(PATH));
	}
	
	public static FrameData loadFrame (String path) {
		File f = new File(path);
		if (!f.exists()) {
			System.out.println("file " + path + " do not exists");
			return null;
		}
		
		System.out.println("file " + path + " is being loaded");
		
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));	
		ObjectInputStream ois = new ObjectInputStream(bis)){
			
			return (FrameData)ois.readObject();
			
		} catch (IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;		
	}
}
