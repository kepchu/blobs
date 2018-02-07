package app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.naming.Context;

import data.FrameData;

public class DiscAccess {
	
	private FrameData f;
	private static final String PATH = "frame.ser";
	
	public static void saveFrame(FrameData f) {
		Context c;
		
		//ObjectOutputStream oos;
		try (ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(PATH))) {
			
			oos.writeObject(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public static void loadFrame() {
		loadFrame(PATH);
	}
	
	public static void loadFrame (String path) {
		File f = new File(path);
		System.out.println("file " + path + (f.exists()? " is being loaded" : " do not exists"));
		
	}
}
