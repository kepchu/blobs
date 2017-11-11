package data;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FrameBuffer implements BufferedFrames {
	
	private Queue <FrameData> frameQ;//TODO
	
	//settings
	private int maxBufferSize = 3;
	private int minBufferSize = 1;
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
			while (frameQ.size() >= maxBufferSize) {
				//System.out.println("addFrame: no free space");
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			frameQ.add(data);		
	}
	
	public FrameData getFrame() {
		//System.out.println("DisplayBuffer.getData - " + Thread.currentThread().getName());
		return frameQ.peek();
	}
	// - DATA MODYFICATION
	synchronized public void advanceFrame() {
			//System.out.println("DisplayBuffer.advanceFrame - " + Thread.currentThread().getName());
			frameQ.poll();
			if (frameQ.size() <= minBufferSize) {
				notifyAll();
			}
		
	}
	
	public boolean isEmpty() {
		if(frameQ.size() < minBufferSize)
			{return true;}
		return false;
	}

	public boolean isFull() {
		if (frameQ.size() >= maxBufferSize) {return true;}
		return false;
	}

	public int maxSize() {
		return maxBufferSize;
	}
	
	public int currentSize() {
		return frameQ.size();
	}
	
		
	
	//frame data getters
		public List<Blob> getBlobs() {
			if(frameQ.isEmpty()) {return null;}		
			return frameQ.peek().blobs;
		}
		public List<ChargePoint> getCharges() {
			if(frameQ.isEmpty()) {return null;}
			return frameQ.peek().charges;
		}
		public List<Vec> getCollisions() {
			if(frameQ.isEmpty()) {return null;}
			return frameQ.peek().collisions;
		}
		public Vec getGravity() {
			if(frameQ.isEmpty()) {return null;}
			return frameQ.peek().gravity;
		}
		public double getGround() {
			if(frameQ.isEmpty()) {return -1;}
			return frameQ.peek().ground;
		}
		public double getSpeed() {
			return frameQ.peek().speed;
		}
	
		
		
		
}
