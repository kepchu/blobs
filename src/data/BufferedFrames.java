package data;

import java.util.List;

public interface BufferedFrames {
	
		public void advanceFrame();
		public boolean isFull();
		public boolean isEmpty();
		public int maxSize();
		public int currentSize();//big -> hint to speed-up gfx rendering
		
		public List<Blob> getBlobs();
		public List<ChargePoint> getCharges();
		public List<Vec> getCollisions();
		public Vec getGravity();
		public double getGround();
		public double getSpeed();
}
