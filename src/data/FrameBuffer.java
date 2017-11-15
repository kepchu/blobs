package data;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FrameBuffer implements BufferableFrames {
	
	private Queue <FrameData> frameQ;//TODO
	
	//settings
	private int maxBufferSize = 3;
	private int minBufferSize = 2;
	
	//this is a singleton
	private final static FrameBuffer displayBuffer = new FrameBuffer();
	private FrameBuffer() {
		System.out.println("FrameBuffer() constr. thread: " + Thread.currentThread().getName());
		frameQ = new LinkedList<FrameData>();
	}
	public static FrameBuffer getInstance() {
		System.out.println("FrameBuffer getInstance()");
		return displayBuffer;
	}
	
	
	synchronized public void addFrame(FrameData data) {
			while (isFull()) {
				//System.out.print(".");
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			frameQ.add(data);
			notifyAll();
	}
	
	synchronized public FrameData getFrame() {	
		//System.out.println("DisplayBuffer.getData - " + Thread.currentThread().getName());
		if (frameQ.isEmpty()) return null;
		return frameQ.peek();
		
	}
	// - DATA MODYFICATION
	synchronized public void advanceFrame() {
		//System.out.println("DisplayBuffer.advanceFrame - " + Thread.currentThread().getName());
		if (!isFull()) notifyAll();
		if (!isEmpty()) frameQ.poll();	
	}
	
	public boolean isEmpty() {
		if(frameQ.size() < minBufferSize) return true;
		return false;
	}

	public boolean isFull() {
		if (frameQ.size() >= maxBufferSize) return true;
		return false;
	}

	public int maxSize() {
		return maxBufferSize;
	}
	
	public int currentSize() {
		return frameQ.size();
	}
		
}
