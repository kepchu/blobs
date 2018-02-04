package collisions;

import static data.Vec.projectAonB;
import static data.Vec.vecFromAtoB;

import java.util.List;
import java.util.Scanner;

import data.Vec;
import view.Stage;

public class ColliderCollidables {

	Scanner s;
	public ColliderCollidables() {
		s = new Scanner (System.in);
	}
	
	Collidable[] detect(Collidable subj, List<Collidable> obj, double radiusFactor, Detection det) {
		return det.detectInteraction(subj, obj, radiusFactor);
	}

	void bounceCollidablesss(Collidable subj, Collidable obj) {
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

	void bounceCollidabless(Collidable subj, Collidable obj) {
				
		
		
//		double kinSubj = invVelSubj.getMagnitude() * subj.getMass();
//		double kinObj = invVelObj.getMagnitude() * obj.getMass();				
//		System.out.println("SubVel: " + subj.getVelocity());
//		System.out.println("invVelSubj: " + invVelSubj + ", mag: " + invVelSubj.getMagnitude());
//		System.out.println("subj.getMass(): " + subj.getMass());
//		System.out.println("ObjVel: " + obj.getVelocity());
//		System.out.println("invVelObj: " + invVelObj + ", mag: " + invVelObj.getMagnitude());
//		System.out.println("obj.getMass(): " + obj.getMass());
//		System.out.println("action: " + action);
//		System.out.println("reaction: " + reaction);		
		//s.nextLine();
		
//		double dotProd = VecMath.dot(VecMath.normalise(kinSubj),VecMath.normalise(kinObj));
//		int div = 1;
//		if (dotProd < 0) div = -1;
//		subj.getVelocity().subAndSet(invVelSubj);	
//		obj.getVelocity().subAndSet(invVelObj);
//		subj.getVelocity().subAndSet(kinObj.multiply(div/subj.getMass()));
//		obj.getVelocity().subAndSet(kinSubj.multiply(div/obj.getMass()));
				
//		subj.getVelocity().subAndSet(invVelSubj);	
//		obj.getVelocity().subAndSet(invVelObj);
//		subj.getVelocity().addAndSet(invVelObj);
//		obj.getVelocity().addAndSet(invVelSubj);
		
		// mark as already "collided"
		subj.setColDetDone(true);
		obj.setColDetDone(true);

		// A calculate
		// Velocities involved at the angle of collision:
		Vec fromSubToObj = vecFromAtoB(subj.getPosition(), obj.getPosition());
		Vec fromObjToSubj = fromSubToObj.multiply(-1);// .multiply(-1) reverses fromSubToObj
		Vec invVelSubj = involovedVelocity(subj, fromSubToObj);
		Vec invVelObj = involovedVelocity(obj, fromObjToSubj);

		Vec kinSubj = invVelSubj.multiply(subj.getMass());
		Vec kinObj = invVelObj.multiply(obj.getMass());

		subj.getVelocity().subAndSet(invVelSubj);
		obj.getVelocity().subAndSet(invVelObj);
		subj.getVelocity().addAndSet(kinObj.multiply(1 / subj.getMass()));
		obj.getVelocity().addAndSet(kinSubj.multiply(1 / obj.getMass()));

	}
	
	void bounceCollidables(Collidable subj, Collidable obj) {
		
		final double FORCE = 2;
		
		// mark as processed
		subj.setColDetDone(true);
		obj.setColDetDone(true);

		// A calculate
		// Velocities involved at the angle of collision:
		Vec fromSubToObj = vecFromAtoB(subj.getPosition(), obj.getPosition());
		Vec fromObjToSubj = fromSubToObj.multiply(-1);// .multiply(-1) reverses fromSubToObj
		Vec invVelSubj = involovedVelocity(subj, fromSubToObj);
		Vec invVelObj = involovedVelocity(obj, fromObjToSubj);

		//relative velocity difference of subj & obj (head-on collision vs. collidables moving fast in similar direction)
		Vec relativeVel = invVelSubj.sub(invVelObj);
		//energy of the collision as speed
		double reflectionSpeed = FORCE * relativeVel.getMagnitude();
		
		//divide the collision's energy between collidables according to masses
		//TODO: 1) this is equation for collision points for x & y! 2) change .getMass() call to external method that would calculate kinetic energy?
		double subjKin = calculateHitEnergy(subj.getVelocity().getMagnitude(), subj.getMass());
		double objKin = calculateHitEnergy(obj.getVelocity().getMagnitude(), obj.getMass());
		double reflectionSpeedSubj = (objKin * reflectionSpeed) / (subjKin + objKin);
		double reflectionSpeedObj = reflectionSpeed - reflectionSpeedSubj;
//		System.out.println("reflectionSpeed = " + reflectionSpeed + ", reflectionSpeedObj = " + reflectionSpeedObj
//				+ ", reflectionSpeedSubj " + reflectionSpeedSubj);
				
		//add reflection forces, directed away from collision to velocities of collidees (using already created Vecs)
		subj.getVelocity().addAndSet(fromObjToSubj.setMagnitude(reflectionSpeedSubj));
		obj.getVelocity().addAndSet(fromSubToObj.setMagnitude(reflectionSpeedObj));

	}
	
	private double calculateHitEnergy(double velocity, double mass) {
		//return 0.5 * mass * (velocity * velocity);
		//return mass*mass;
		return mass;
	}

	private Vec involovedVelocity(Collidable c, Vec angle) {
		// get amount of collidable's kinetic energy transferred at the angle of collision
		Vec involvement = projectAonB(c.getVelocity(), angle);
		// account for change of size:
//		if (c.inflationDelta() != 0)
//			involvement.setMagnitude(
//			involvement.getMagnitude() + c.inflationDelta());
		
		//account for mass
		return involvement;
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
