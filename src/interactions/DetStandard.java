package interactions;

import static utils.VecMath.distanceBetween;

import java.util.List;

public class DetStandard implements Detection {
	
	public DetStandard () {
		System.out.println(this.getClass().getSimpleName());
	}
	
	
	//returns first object that passes interaction test or null if nothing found
	//Criterion: closer than sum of radii of tested pair and moving closer
	public Collidable[] detectInteraction (Collidable subj, List<Collidable> obj, double radiusFactor) {
		
		double currentDistance;
		double previousDistance;
		
		Collidable object;
		for (int i = 0; i < obj.size(); i++) {
					
			//filter out the collidable we are testing against
			object = obj.get(i);			
			if (object == subj) {
				//System.out.println("obj == subj");
				continue;
			}
			
			//filter out colliders already computed during this frame
			//TODO: swap this for check of subject before the loop
			if (object.isColDetDone()) {continue;}
			
			currentDistance = distanceBetween(
					subj.getPosition(),
					object.getPosition());
			previousDistance = distanceBetween(
					subj.getPreviousPosition(),
					object.getPreviousPosition());
			
			if (currentDistance < subj.getRadius() + object.getRadius()) {
				//placeOverlappingCollideesNextToEachOther (subj, object);
				
				//TODO add test for (and deal with) growing blobs here because:
				// 1. inflating/growing blob has to affect POSITION and not just velocity of its neighbours
				// 2. distance test below excludes many valid cases
				// 3. shrinking should be taken care of in "bounceOff"
				
				//do not process collision if blobs already move away
				if (currentDistance < previousDistance) {
				//if (futureDistance < actualDistance) {
					return new Collidable[] {subj, object};
				}
			}
		}
		//nothing found
		return null;	
	}
}
