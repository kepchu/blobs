package collisions;

import static utils.VecMath.distanceBetween;
import static utils.VecMath.projectAonB;
import static utils.VecMath.vecFromAtoB;

import java.util.List;

import data.Vec;

public class CollisionDetection {
	
	private List<Vec> listOfCollisionPoints;
	@SuppressWarnings("unused")
	private double timeInterval;
	private ColliderStageLimits colBorders;
	private ColliderCollidables colBlobs;
	private Detection detStandard;
	private Detection detDebris;
	private Detection detInward;

	public CollisionDetection(List<Vec> listOfCollisionPoints, double timeInterval) {
		this.listOfCollisionPoints = listOfCollisionPoints;
		this.timeInterval = timeInterval;
		this.colBorders = new ColliderStageLimits();
		this.colBlobs = new ColliderCollidables();
		this.detStandard = new DetStandard();
		this.detDebris = new DetDebris();
		this.detInward = new DetInward();
		
		System.out.println(this.getClass().getSimpleName());
	}

	public void doCollisions(List<Collidable> blobs, int minX, int maxX, int minY, int maxY,
			double radiusFactor, ColFlag flag) {
		
		Detection d = null;
		switch(flag) {
			case DISABLE:
			case DO_NOT_FORCE:
				d = null;
				break;
			case FORCE_STANDARD:
				d = detStandard;
				break;
			case FORCE_DEBRIS:
				d = detDebris;
				break;
			case FORCE_INWARD:
				d = detInward;
				break;
			case FORCE_BOUNDED:
				break;//to be implemented
		}
		
		
		for (int i = 0; i < blobs.size(); i++) {
			Collidable c = blobs.get(i);

			//A. Interaction with scene/stage
			colBorders.bounceOffGround(c, maxY, radiusFactor);
			colBorders.wrapX(c, minX, maxX, radiusFactor);
			// borders.wrapY(c, minY, maxY, radiusFactor);
			// borders.wrapXY(c, minX, maxX, minY, maxY, radiusFactor);
			
			
			//B. Interaction with collidable objects
			if(flag == ColFlag.DISABLE) {//TODO: branch to optimised loop at the beginning of this method?
				continue;
			}
			
			//find a collider using selected detection logic
			Collidable[] col = null;
			if (d != null) {
				col = colBlobs.detect(c, blobs, 1, d);
			} else {
				switch(c.getColliderType()) {
				case STANDARD:
					col = colBlobs.detect(c, blobs, 1, detStandard);
					break;
				case DEBRIS:
					col = colBlobs.detect(c, blobs, 1, detDebris);
					break;
				case INWARD:
					col = colBlobs.detect(c, blobs, 1, detInward);
				case BOUNDED:
				case DECORATION:
					default:
						continue;
				}			
			}
			
			if (col == null)
				continue;
			
			//newest collisions first in order to clarify display code
			//listOfCollisionPoints.add(0, colBlobs.computeCollisionPoint(col[0], col [1]));
			
			//change direction
			colBlobs.bounceBlobs(col[0], col [1]);
		}
	}
}
