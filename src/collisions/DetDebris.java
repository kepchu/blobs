package collisions;

import static data.Vec.*;

import java.util.List;

public class DetDebris implements Detection{

	@Override
	public Collidable[] detectInteraction(Collidable subj, List<Collidable> objects, double radiusFactor) {
		//returns first object that passes interaction test or null if nothing found
		//Criterion: tested collidables overlap and distance between them is shrinking
		for (int i = 0; i < objects.size(); i++) {
						
			Collidable obj = objects.get(i);
			//filter out the object & already tested collidables
			if (subj.isColDetDone() || obj == subj) {continue;}
	
			
			double actualDistance = distanceBetween(
					subj.getPosition(),
					obj.getPosition());
			if (actualDistance <
					subj.getRadius() + obj.getRadius()) {

				double futureDistance = distanceBetween(
						add(subj.getPosition(), subj.getVelocity()),
						add(obj.getPosition(), obj.getVelocity()));
				//do not process collision if blobs already move in the right direction
				if (actualDistance > futureDistance) {
					return new Collidable[] {subj, obj};
				}
			}
		}
		return null;	
	}
}
