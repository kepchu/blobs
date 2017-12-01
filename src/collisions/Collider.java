package collisions;

import java.util.List;

public interface Collider {
	public void doCollisions(List<Collidable> blobs, int minX, int maxX, int minY, int maxY,
			double radiusFactor, ColFlag flag);
}
