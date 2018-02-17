package collisions;


import java.util.List;

import data.Vec;

class DetInward implements Detection {
	//TODO:
	//Note that eliminated bug was causing the last blobs velocity to be unaffected by collisions
	//while the blob was influencing other blobs normally. This irregularity caused instability
	// resulting in "droplets on hot hob" effect. Would be interesting to add controllable "insensitivity"
	// even 1 immune blob tears apart whole bunch.
	@Override
	public Collidable[] detectInteraction(Collidable subj, List<Collidable> objects, double radiusFactor) {
		//returns first object that passes interaction test or null if nothing found
		//Criterion: tested collidables overlap and move away
		for (int i = 0; i < objects.size(); i++) {
						
			Collidable obj = objects.get(i);
			//filter out the object & already tested collidables
			if (subj.isColDetDone() || obj == subj) {continue;}
	
			
			double actualDistance = Vec.distanceBetween(
					subj.getPosition(),
					obj.getPosition());
			if (actualDistance <
					subj.getRadius() + obj.getRadius()) {

				double futureDistance = Vec.distanceBetween(
						Vec.add(subj.getPosition(), subj.getVelocity()),
						Vec.add(obj.getPosition(), obj.getVelocity()));
				//do not process collision if blobs already move in the right direction
				if (actualDistance < futureDistance) {
					return new Collidable[] {subj, obj};
				}
			}
		}
		return null;	
	}
}
