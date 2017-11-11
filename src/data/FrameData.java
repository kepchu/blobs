package data;

import java.util.ArrayList;
import java.util.List;
//Purpose of this class: provide easy duplication/separation of data for asynchronous processing
public class FrameData {
	public List<Blob> blobs;
	public List<ChargePoint> charges;
	public List<Vec> collisions;
	public Vec gravity;
	public double ground;
	public double speed;
	
	//DEEP COPY during instantiation
	public FrameData(List<Blob> blobs, List<ChargePoint> charges, List<Vec> collisions, Vec gravity,
			double ground, double speed) {
		super();
		this.blobs = cloneBlobs(blobs);
		this.charges = cloneCharges(charges);
		this.collisions = cloneCollisions(collisions);
		this.gravity = new Vec(gravity);
		this.ground = ground;
		this.speed = speed;
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
	
	//getters
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