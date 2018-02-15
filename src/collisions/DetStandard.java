package collisions;

import java.util.List;

import data.Vec;

public class DetStandard implements Detection {
	
	double currentDistance;
	Vec subjToObj;
	
	public DetStandard () {
		System.out.println(this.getClass().getSimpleName());
	}

	//corrects overlapping and
	//returns first object that passes interaction test or null if nothing found
	//Criterion: tested collidables are overlapping and distance between them is shrinking
	public Collidable[] detectInteraction (Collidable subj, List<Collidable> objects, double radiusFactor) {
		
		for (int i = 0; i < objects.size(); i++) {
					
			//do not test against self
			Collidable obj = objects.get(i);			
			if (obj == subj) continue;
			
			//filter out colliders already computed during this frame
			if (obj.isColDetDone()) continue;
			
			//vector pointing from subj to obj
			subjToObj = obj.getPosition().sub(subj.getPosition());
			//length of the vector
			currentDistance = subjToObj.getMagnitudePOW();//.getMagnitude();
			
			// if (sub and obj are overlapping)
			if (currentDistance < Math.pow(subj.getRadius() + obj.getRadius(), 2)) {
				
				correctOverlapping(subj, obj);
				
				
				
				//TODO add test for (and deal with) growing blobs here because:
				// 1. inflating/growing blob has to affect POSITION and not just velocity of its neighbours
				// 2. distance test below excludes many valid cases
				// 3. shrinking should be taken care of in "bounceOff
				
				//do not process collision if blobs already move away
				double previousDistance = Vec.distanceBetweenPOW(//.distanceBetween(
						subj.getPreviousPosition(),
						obj.getPreviousPosition());
								
				if (currentDistance < previousDistance) {
				//if (futureDistance < actualDistance) {
					return new Collidable[] {subj, obj};
				}
			}
			
		}
		return null;	
	}
	
	private void correctOverlapping(Collidable subj, Collidable obj) {
		//this method moves subject away from object in the direction of fromObjToSub
		//for the length that make both collidees touch at the time the collision was detected (just now)
		//(so pretty bad)
		double overlap = (subj.getRadius() + obj.getRadius()) - Math.sqrt(currentDistance);
		Vec displacement = subjToObj.withMagnitudeOf(overlap);
		subj.getPosition().subAndSet(displacement);
	}
	
}
