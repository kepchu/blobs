package interactions;

import static utils.VecMath.distanceBetween;
import static utils.VecMath.projectAonB;
import static utils.VecMath.vecFromAtoB;

import java.util.List;

import data.Vec;

public class ColDet {
	
	private List<Vec> listOfCollisionPoints;
	@SuppressWarnings("unused")
	private double timeInterval;
	private double defaultRadiusFactor = 1.0;
	private ColBorders colBorders;
	private ColBlobs colBlobs;
	private Detection detection;

	public ColDet(List<Vec> listOfCollisionPoints, double timeInterval) {
		this.listOfCollisionPoints = listOfCollisionPoints;
		this.timeInterval = timeInterval;
		this.colBorders = new ColBorders();
		this.colBlobs = new ColBlobs();
		this.detection = new DetStandard();
		System.out.println(this.getClass().getSimpleName());
	}

	public void doCollisions(List<Collidable> blobs, int minX, int maxX, int minY, int maxY,
			double radiusFactor, ColFlag flag) {
		
		for (int i = 0; i < blobs.size(); i++) {
			Collidable c = blobs.get(i);

			//A. Interaction with scene/stage
			colBorders.bounceOffGround(c, maxY, radiusFactor);
			colBorders.wrapX(c, minX, maxX, radiusFactor);
			// borders.wrapY(c, minY, maxY, radiusFactor);
			// borders.wrapXY(c, minX, maxX, minY, maxY, radiusFactor);
			
			
			//B. Interaction with collidable objects
			//find a collider using chosen detection logic
			Collidable[] col = colBlobs.detect(c, blobs, 1, detection);
			if (col == null)
				continue;
			
			//newest collisions first in order to clarify display code
			//listOfCollisionPoints.add(0, colBlobs.computeCollisionPoint(col[0], col [1]));
			//for "debris" collisions do not remove overlaps
			colBlobs.placeOverlappingCollideesNextToEachOther(col[0], col [1]);
			//change collidables' direction
			colBlobs.bounceBlobs(col[0], col [1]);
		}
	}
}
