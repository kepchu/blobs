package collisions;

import java.util.List;

import data.Vec;

public class ColliderMAIN {
	
	private ColliderStageLimits colBorders;
	private ColliderCollidables colBlobs;
	private Detection detStandard;
	private Detection detDebris;
	private Detection detInward;

	public ColliderMAIN() {
		
		this.colBorders = new ColliderStageLimits();
		this.colBlobs = new ColliderCollidables();
		this.detStandard = new DetStandard();
		this.detDebris = new DetDebris();
		this.detInward = new DetInward();
		
		System.out.println(this.getClass().getSimpleName());
	}

	public void doCollisions(List<Collidable> collidables, int minX, int maxX, int minY, int maxY,
			double radiusFactor, ColFlag flag) {
		
		//System.out.println(collidables.size());
		
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
	
		
		for (int i = 0; i < collidables.size(); i++) {
			Collidable c = collidables.get(i);
			if (c.isColDetDone()) continue;
						
			//A. Interaction with scene/stage
			colBorders.bounceOffGround(c, maxY, radiusFactor);
			colBorders.wrapX(c, minX, maxX, radiusFactor);
			// borders.wrapY(c, minY, maxY, radiusFactor);
			// borders.wrapXY(c, minX, maxX, minY, maxY, radiusFactor);
						
			//B. Interaction with collidable objects
			if(flag == ColFlag.DISABLE) {
				continue;
			}
			
			//detect collisions using selected logic
			Collidable[] col = null;
			if (d != null) {
				col = colBlobs.detect(c, collidables, 1, d);
			} else {
				switch(c.getColliderType()) {
				case STANDARD:
					col = colBlobs.detect(c, collidables, 1, detStandard);
					break;
				case DEBRIS:
					col = colBlobs.detect(c, collidables, 1, detDebris);
					break;
				case INWARD:
					col = colBlobs.detect(c, collidables, 1, detInward);
				case BOUNDED:
				case DECORATION:
					default:
						continue;
				}			
			}
			
			c.setColDetDone(true);
			
			
			if (col == null)
				continue;
			
			//change direction
			colBlobs.bounceCollidables(col[0], col [1]);
			//collider.collide(col[0], col [1]);
		}
	}
}
