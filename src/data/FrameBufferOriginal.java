package data;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class FrameBufferOriginal {//implements BufferableData {
	
	private boolean consumed = true;	
	private FrameData data;
	private Queue <FrameData> frameQ;
	
	
	//this is a singleton
	private final static FrameBufferOriginal displayBuffer = new FrameBufferOriginal();
	private FrameBufferOriginal() {}
	public static FrameBufferOriginal getInstance() {
		System.out.println("DisplayBuffer getInstance()");
		return displayBuffer;
	}
	
	synchronized public void setData(FrameData data) {
		//System.out.println("DisplayBuffer setData()");
		consumed = false;
		this.data = data;
	}
	
	synchronized public FrameData getData() {
		//System.out.println("DisplayBuffer getData()");
		//consumed = true;
		return data;
	}
	
	public void advanceFrame() {
		consumed = true;
	}
	
	public boolean isEmpty() {
		return consumed;
	}

	public boolean isFull() {
		if (data != null && !consumed) {return true;}
		return false;
	}

	
	
	class Data {
		public List<Blob> blobs;
		public List<ChargePoint> charges;
		public List<Vec> collisions;
		public double ground;
		
		//Deep copy during instantiation
		public Data(List<Blob> blobs, List<ChargePoint> charges, List<Vec> collisions, double ground) {
			super();
			this.blobs = cloneBlobs(blobs);
			this.charges = cloneCharges(charges);
			this.collisions = cloneCollisions(collisions);
			this.ground = ground;
		}
		
		//Cloning methods
		private List<Blob> cloneBlobs(List<Blob> blobs) {
			List<Blob> result = new ArrayList<Blob>(blobs.size());
			for (Blob b : blobs) {
				result.add(new Blob(b));
			}
			return result;
		}
		private List<ChargePoint> cloneCharges(List<ChargePoint> charges) {
			List<ChargePoint> result = new ArrayList<ChargePoint>(charges.size());
			for (ChargePoint c : charges) {
				result.add(new ChargePoint(c));
			}
			return result;
		}
		private List<Vec> cloneCollisions(List<Vec> collisions) {
			List<Vec> result = new ArrayList<Vec>(collisions.size());
			for (Vec c : collisions) {
				result.add(new Vec(c));
			}
			return result;
		}
				
	}
	
	
	//getters
		public List<Blob> getBlobs() {
			return data.blobs;
		}
		public List<ChargePoint> getCharges() {
			return data.charges;
		}
		public List<Vec> getCollisions() {
			return data.collisions;
		}
		public double getGround() {
			return data.ground;
		}
	
}
