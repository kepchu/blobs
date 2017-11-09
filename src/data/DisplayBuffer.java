package data;

import java.util.List;

public class DisplayBuffer {
	
	private boolean displayed = true;	
	private BufferData data;
	
	//this is a singleton
	private final static DisplayBuffer displayBuffer = new DisplayBuffer();
	private DisplayBuffer() {}
	public static DisplayBuffer getInstance() {
		System.out.println("DisplayBuffer getInstance()");
		return displayBuffer;
	}
	
	synchronized public void setData(BufferData data) {
		//System.out.println("DisplayBuffer setData()");
		displayed = false;
		this.data = data;
	}
	
	synchronized public BufferData getData() {
		//System.out.println("DisplayBuffer getData()");
		displayed = true;	
		return data;
	}
	
	public boolean isDisplayed() {
		return displayed;
	}

	public boolean isUpdated() {
		if (data != null && !displayed) {return true;}
		return false;
	}

}
