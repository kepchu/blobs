package data;

import java.util.List;

public interface BufferableData {
		public void updateData();
		public boolean isUpdated();
		public boolean isDisplayed();
		public List<Blob> getBlobs();
		public List<ChargePoint> getCharges();
		public List<Vec> getCollisions();
		public double getGround();
}
