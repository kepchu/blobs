package app;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import data.FrameBuffer;
import data.World;
import view.MAINViewAndInput;

public class BlobsD4 {

	
	
	public static void main (String[] args) {
		 
	    try {
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	        //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	    } catch (UnsupportedLookAndFeelException ex) {
	        ex.printStackTrace();
	    } catch (IllegalAccessException ex) {
	        ex.printStackTrace();
	    } catch (InstantiationException ex) {
	        ex.printStackTrace();
	    } catch (ClassNotFoundException ex) {
	        ex.printStackTrace();
	    }
	    /* Turn off metal's use bold fonts */
	    UIManager.put("swing.boldMetal", Boolean.FALSE);
		
	    javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new BlobsD4();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	
	public BlobsD4 () {	
		System.out.println("Blobs01 thread - " + Thread.currentThread().getName());
		//MVC pattern variation
		World dc = new World();
		MAINViewAndInput v = new MAINViewAndInput(FrameBuffer.getInstance());
		new MainHub(dc, v);		
	}
}

//00: create square root alternative for VecMath and use it wherever possible &
//OK 0: wrap around, zoom by ctrl + mouse wheel or +/-, 
//	 make debris "rain-fireworks"
// !all the charge-point changes applied to mouse pointer.
// independent charge point created on click and undo button
