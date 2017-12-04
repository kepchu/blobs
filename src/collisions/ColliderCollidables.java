package collisions;

import static utils.VecMath.projectAonB;
import static utils.VecMath.vecFromAtoB;

import java.util.List;
import java.util.Scanner;

import data.Vec;

public class ColliderCollidables {

	Scanner s;
	public ColliderCollidables() {
		s = new Scanner (System.in);
	}
	
	Collidable[] detect(Collidable subj, List<Collidable> obj, double radiusFactor, Detection det) {
		return det.detectInteraction(subj, obj, radiusFactor);
	}

	void bounceCollidabless(Collidable subj, Collidable obj) {
		// TODO: tagging instead of simple removal from input list (can be used for
		// visual cues and logic)
		// Will switch later from boolean tags to location of collision.
		subj.setColDetDone(true);
		obj.setColDetDone(true);

		
		// TODO: velocity will be changed to force (generally speed * mass but maybe
		// some per/colour variations of "mass")

		// Subject's side:
		// Transfer of kinetic energy
		// get direction to hitting blob = angle of collision
		Vec fromSubToObj = vecFromAtoB(subj.getPosition(), obj.getPosition());
		Vec subVel = subj.getVelocity();
		
		// get amount of subject's kinetic energy transferred at the angle of collision
		Vec kineticInvolvementOfSubject = projectAonB(subVel, fromSubToObj);
		// account for change of size:
		if (subj.inflationDelta() != 0)
			kineticInvolvementOfSubject.setMagnitude(
					kineticInvolvementOfSubject.getMagnitude() + subj.inflationDelta());
		// Object's side:
		// Transfer of kinetic energy
		// reverse the vector pointing from subject to object:
		Vec fromObjToSub = fromSubToObj.multiply(-1);
		Vec objVel = obj.getVelocity();
		Vec kineticInvolvementOfObject = projectAonB(objVel, fromObjToSub);
		// account for change of size:
		if (obj.inflationDelta() != 0)
			kineticInvolvementOfObject.setMagnitude(
					kineticInvolvementOfObject.getMagnitude() + obj.inflationDelta());
		
		// apply reflection forces
		subVel.addAndSet(kineticInvolvementOfSubject.multiply(-1).add(kineticInvolvementOfObject));

		objVel.addAndSet(kineticInvolvementOfObject.multiply(-1).add(kineticInvolvementOfSubject));

	}

	void bounceCollidables(Collidable subj, Collidable obj) {
		
		//mark as already "collided"
		subj.setColDetDone(true);
		obj.setColDetDone(true);
		
		//calculate changes
		Vec fromSubToObj = vecFromAtoB(subj.getPosition(), obj.getPosition());
		Vec involvementOfSubj = bounce(subj, fromSubToObj);
		Vec involvementOfObj = bounce(obj, fromSubToObj.multiply(-1));//reversed fromSubToObj
		
	/*	System.out.println("SubVel: " + subj.getVelocity());
		System.out.println("SubInv: " + involvementOfSubj);
		System.out.println("ObjVel: " + obj.getVelocity());
		System.out.println("ObjInv: " + involvementOfObj);
		
		s.nextLine();*/
		
		//apply changes according to 3rd law of motion (action meets equal opposite reaction):
		//A. influence of self
//		subj.getVelocity().subAndSet(involvementOfSubj);///subj.getEnergy()));
//		obj.getVelocity().subAndSet(involvementOfObj);//obj.getEnergy()));
		subj.setVelocity(subj.getVelocity().sub(involvementOfSubj));
		obj.setVelocity(obj.getVelocity().sub(involvementOfObj));
//		subj.getVelocity().addAndSet(involvementOfSubj.multiply(-1).add(involvementOfObj));
//		obj.getVelocity().addAndSet(involvementOfObj.multiply(-1).add(involvementOfSubj));
		
//		//B. influence of other
//		subj.getVelocity().addAndSet(involvementOfObj);///subj.getEnergy()));
//		obj.getVelocity().addAndSet(involvementOfSubj);//obj.getEnergy()));
//		subj.getVelocity().addAndSet(involvementOfObj.multiply(1));
//		obj.getVelocity().addAndSet(involvementOfSubj.multiply(1));
		
		//account for mass
		/*//involvementOfObj.multiplyAndSet(obj.getEnergy());
		subj.getVelocity().addAndSet(involvementOfObj.multiply(1/subj.getEnergy()));
		
		//involvementOfSubj.multiplyAndSet(subj.getEnergy());
		obj.getVelocity().addAndSet(involvementOfSubj.multiply(1/obj.getEnergy()));*/
		
		subj.setVelocity(subj.getVelocity().addAndSet(involvementOfObj));
		obj.setVelocity(obj.getVelocity().addAndSet(involvementOfSubj));
//		subj.getVelocity().addAndSet(involvementOfObj.multiply(obj.getEnergy()).
//				multiply(1/subj.getEnergy()));
//		obj.getVelocity().addAndSet(involvementOfSubj.multiply(subj.getEnergy()).
//				multiply(1/obj.getEnergy()));
//		
//		subj.getVelocity().addAndSet(involvementOfObj.multiply(obj.getEnergy()/subj.getEnergy()));
//		obj.getVelocity().addAndSet(involvementOfSubj.multiply(subj.getEnergy()/obj.getEnergy()));
//		subj.getVelocity().addAndSet(involvementOfObj.multiply(1/(obj.getEnergy()/subj.getEnergy())));
//		obj.getVelocity().addAndSet(involvementOfSubj.multiply(1/(subj.getEnergy()/obj.getEnergy())));
		//.add(involvementOfObj.multiply(1/subj.getEnergy())));obj.getEnergy()/subj.getEnergy())
		
	}
	
	private Vec bounce(Collidable c, Vec angle) {
		// get amount of collidable's kinetic energy transferred at the angle of collision
		Vec involvement = projectAonB(c.getVelocity(), angle);
		// account for change of size:
		if (c.inflationDelta() != 0)
			involvement.setMagnitude(
			involvement.getMagnitude() + c.inflationDelta());
		
		//account for mass
		return involvement.multiply(c.getEnergy());
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
