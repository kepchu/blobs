package data;

import java.util.ArrayList;
import java.util.List;

public class BufferData {
	public List<Blob> blobs;
	public List<ChargePoint> charges;
	public List<Vec> collisions;
	public double ground;
	
	public BufferData(List<Blob> blobs, List<ChargePoint> charges, List<Vec> collisions, double ground) {
		super();
		this.blobs = new ArrayList<Blob> (blobs);
		this.charges = new ArrayList<ChargePoint>(charges);
		this.collisions = new ArrayList<Vec>(collisions);
		this.ground = ground;
	}
	
	public List<Blob> getBlobs() {
		return blobs;
	}
	public List<ChargePoint> getCharges() {
		return charges;
	}
	public List<Vec> getCollisions() {
		return collisions;
	}
	public double getGround() {
		return ground;
	}
}