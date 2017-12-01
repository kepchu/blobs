package collisions;

import static utils.VecMath.projectAonB;
import static utils.VecMath.vecFromAtoB;

import java.util.List;

import data.Vec;

public class ColliderCollidables {

	Collidable[] detect(Collidable subj, List<Collidable> obj, double radiusFactor, Detection det) {
		return det.detectInteraction(subj, obj, radiusFactor);
	}

	void bounceBlobs(Collidable subj, Collidable obj) {
		// TODO: tagging instead of simple removal from input list (can be used for
		// visual cues and logic)
		// Will switch later from boolean tags to location of collision.
		//subj.setColDetDone(true);
		// obj.setColDetDone(true);

		// 1. Transfer of kinetic energy
		// TODO: velocity will be changed to force (generally speed * mass but maybe
		// some per/colour variations of "mass")
		Vec subVel = subj.getVelocity();
		Vec objVel = obj.getVelocity();

		// Subject's side:
		// get direction to hitting blob = angle of collision
		Vec fromSubToObj = vecFromAtoB(subj.getPosition(), obj.getPosition());
		// get amount of subject's kinetic energy transferred at the angle of collision
		Vec kineticInvolvementOfSubject = projectAonB(subVel, fromSubToObj);

		// Object's side:
		// reverse the vector pointing from subject to object:
		Vec fromObjToSub = fromSubToObj.multiply(-1);

		Vec kineticInvolvementOfObject = projectAonB(objVel, fromObjToSub);

		// TODO: inflating blobs to be dealt with in calling method - only shrinking
		// blobs here
		// 2. Account for inflation:
		if (subj.inflationDelta() != 0)
			kineticInvolvementOfSubject
					.setMagnitude(kineticInvolvementOfSubject.getMagnitude() + subj.inflationDelta());
		if (obj.inflationDelta() != 0)
			kineticInvolvementOfObject.setMagnitude(kineticInvolvementOfObject.getMagnitude() + obj.inflationDelta());

		// apply reflection forces
		subVel.addAndSet(kineticInvolvementOfSubject.multiply(-1).add(kineticInvolvementOfObject));

		objVel.addAndSet(kineticInvolvementOfObject.multiply(-1).add(kineticInvolvementOfSubject));

	}

	// This the only method I didn't create
	Vec computeCollisionPoint(Collidable subject, Collidable object) {
		double x = (subject.getPosition().getX() * object.getRadius()
				+ object.getPosition().getX() * subject.getRadius())
				/ (subject.getRadius() + object.getRadius());
		double y = (subject.getPosition().getY() * object.getRadius()
				+ object.getPosition().getY() * subject.getRadius())
				/ (subject.getRadius() + object.getRadius());

		return new Vec(x, y);
	}

}
