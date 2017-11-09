package data;

public class FrameBuffer {
	
	private boolean displayed = true;	
	private FrameData data;
	
	//this is a singleton
	private final static FrameBuffer displayBuffer = new FrameBuffer();
	private FrameBuffer() {}
	public static FrameBuffer getInstance() {
		System.out.println("DisplayBuffer getInstance()");
		return displayBuffer;
	}
	
	synchronized public void setData(FrameData data) {
		//System.out.println("DisplayBuffer setData()");
		displayed = false;
		this.data = data;
	}
	
	synchronized public FrameData getData() {
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
