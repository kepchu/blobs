package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
//Purpose of this class: provide easy duplication/separation of data for asynchronous processing
public class FrameData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;
	
	public final List<Blob> blobs;
	public final List<Mod> charges;
	public final List<Vec> collisions;
	public final double gravity;
	public final double speed;
	public final int minX, minY, maxX, maxY;
	
	//DEEP COPY during instantiation
	public FrameData(List<Blob> blobs, List<Mod> charges, List<Vec> collisions,
			double gravity, double speed, int minX, int minY, int maxX, int maxY) {
		super();
		this.blobs = cloneBlobs(blobs);
		this.charges = cloneCharges(charges);
		this.collisions = cloneCollisions(collisions);
		this.gravity = gravity;
		this.speed = speed;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	//Cloning methods
	private List<Blob> cloneBlobs(List<Blob> blobs) {
		List<Blob> result = new ArrayList<Blob>(blobs.size());
		for (Blob b : blobs) {
			result.add(new Blob(b));
		}
		return result;
	}
	private List<Mod> cloneCharges(List<Mod> charges) {
		List<Mod> result = new ArrayList<Mod>(charges.size());
		for (Mod c : charges) {
			result.add(new Mod(c));
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