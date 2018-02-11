package app;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import data.FrameBuffer;
import data.World;
import utils.U;
import view.MAINViewAndInput;

public class BlobsD5 {

	private static void test() {
		System.out.println("test in main:");
		int reps = 500000;
		int max = 21, min = 1;

		int[] bb = new int[max + min + 1];
		while (reps-- > 0) {

			int b = U.rndIntUneven(min, max, 0.25);

			bb[min + b]++;
		}
		for (int j = 0; j < bb.length; j++) {
			
			StringBuilder sb = new StringBuilder();// j+": ");
			int div = 1000;
			//System.out.print(j - min + " x " + bb[j]);
			while (bb[j]-- > 0) {
				if (bb[j] % div == 0)
					sb.append('.');
			}
			System.out.println(j + " - " + sb.toString());
		}

	}

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
					new BlobsD5();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	    
	    try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    test();
	}
	
	
	
	public BlobsD5 () {	
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
