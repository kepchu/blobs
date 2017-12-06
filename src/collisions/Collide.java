package collisions;

import static utils.VecMath.projectAonB;
import static utils.VecMath.vecFromAtoB;

import data.Vec;

public class Collide {

	Vec fromSubToObj;
	Vec invVelSubj;
	Vec invVelObj;
	
	void collide(Collidable subj, Collidable obj) {
		
//		subj.setColDetDone(true);
//		obj.setColDetDone(true);
		
		fromSubToObj = vecFromAtoB(subj.getPosition(), obj.getPosition());
		invVelSubj = involovedVelocity(subj, fromSubToObj);
		invVelObj = involovedVelocity(obj, fromSubToObj.multiply(-1));//.multiply(-1) reverses fromSubToObj

//		Vec velSubj = computeSubj(subj, obj);
//		Vec velObj = computeObj(subj, obj);
		
		double velSubj = compute(
				invVelSubj.getMagnitude(), invVelObj.getMagnitude()*-1,
				subj.getMass(), obj.getMass());
		
		double velObj = compute(
				invVelObj.getMagnitude(), invVelSubj.getMagnitude()*-1,
				obj.getMass(), subj.getMass());
				
		subj.setVelocity(invVelObj.withMagnitudeOf(velSubj));
		obj.setVelocity(invVelSubj.withMagnitudeOf(velObj));
		
//		//wygaszenie bez odbicia
//		subj.getVelocity().addAndSet(invVelSubj.withMagnitudeOf(velSubj));
//		obj.getVelocity().addAndSet(invVelObj.withMagnitudeOf(velObj));
		
//		subj.getVelocity().subAndSet(invVelSubj.withMagnitudeOf(velSubj));
//		obj.getVelocity().subAndSet(invVelObj.withMagnitudeOf(velObj));

		
	}
	
	double compute (double vs, double vo, double ms, double mo) {
		return ((vs*(ms - mo))+(2*mo*vo)) / (ms + mo);
		//https://en.wikipedia.org/wiki/Elastic_collision
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
	
//private Vec computeSubj (Collidable subj, Collidable obj) {
//		
//		double x = (2 * obj.getMass() * invVelObj.getX() +
//				invVelSubj.getX()*(obj.getMass()- subj.getMass()))
//				/(subj.getMass() + obj.getMass());
//		double y = (2 * obj.getMass() * invVelObj.getY() +
//				invVelSubj.getY()*(obj.getMass()- subj.getMass()))
//				/(subj.getMass() + obj.getMass());
//		
//		return new Vec(x,y);
//	}
//
//private Vec computeObj (Collidable subj, Collidable obj) {
//	
//	double x = (2 * subj.getMass() * invVelSubj.getX() +
//			invVelObj.getX()*(obj.getMass()- subj.getMass()))
//			/(subj.getMass() + obj.getMass());
//	double y = (2 * subj.getMass() * invVelSubj.getY() +
//			invVelObj.getY()*(obj.getMass()- subj.getMass()))
//			/(subj.getMass() + obj.getMass());
//	
//	return new Vec(x,y);
//}
	
	
//private Vec computeSubj (Collidable subj, Collidable obj, Vec v) {
//		
//		double x = (2 * obj.getMass() * obj.getVelocity().getX() +
//				subj.getVelocity().getX()*(obj.getMass()- subj.getMass()))
//				/(subj.getMass() + obj.getMass());
//		double y = (2 * obj.getMass() * obj.getVelocity().getY() +
//				subj.getVelocity().getY()*(obj.getMass()- subj.getMass()))
//				/(subj.getMass() + obj.getMass());
//		
//		return new Vec(x,y);
//	}
//
//private Vec computeObj (Collidable subj, Collidable obj, Vec v) {
//	
//	double x = (2 * subj.getMass() * subj.getVelocity().getX() +
//			obj.getVelocity().getX()*(obj.getMass()- subj.getMass()))
//			/(subj.getMass() + obj.getMass());
//	double y = (2 * subj.getMass() * subj.getVelocity().getY() +
//			obj.getVelocity().getY()*(obj.getMass()- subj.getMass()))
//			/(subj.getMass() + obj.getMass());
//	
//	return new Vec(x,y);
//}
	
}
