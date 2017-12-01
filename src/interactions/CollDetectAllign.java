package ColDet;

import static utils.VecMath.*;

import java.util.List;

import data.Blob;
import data.Vec;

public class CollDetectAllign extends ColDetect {
	
	private List<Vec> listOfCollisionPoints;
	@SuppressWarnings("unused")
	private double timeInterval;
	
	public CollDetectAllign (List <Vec> listOfCollisionPoints, double timeInterval) {
		super(listOfCollisionPoints, timeInterval);
		this.listOfCollisionPoints = listOfCollisionPoints;
		this.timeInterval = timeInterval;
	}
	
	
	public void detectCollisions (List<Collidable> blobs) {
		//listOfCollisionPoints.clear();
		//for (int i = blobs.size()-1; i > 0; i--) {
		for (int i = 0; i < blobs.size(); i++ ) {
			detectCollisions(blobs.get(i), blobs, 1);
		}	
	}
	
	//detection of collisions
	private void detectCollisions (Collidable subject, List<Collidable> objects, double radius) {
			
		double actualDistance;
		double previousDistance;
		double futureDistance;
		
		Collidable object;
		for (int i = 0; i < objects.size(); i++) {
					
			//filter out the collidable we are testing against
			object = objects.get(i);			
			if (object == subject) {
				//System.out.println("obj == subj");
				continue;
			}
			
			//filter out colliders already computed during this frame
			//TODO: swap this for check of subject before the loop
			if (object.isColDetDone()) {continue;}
			
			actualDistance = distanceBetween(
					subject.getPosition(),
					object.getPosition());
			previousDistance = distanceBetween(
					subject.getPreviousPosition(), 
					object.getPreviousPosition());
			futureDistance = distanceBetween(
					subject.getPosition().add(subject.getVelocity()),
					object.getPosition().add(object.getVelocity()));
			if (futureDistance < subject.getRadius() + object.getRadius()) {
				//placeOverlappingCollideesNextToEachOther (subject, object);
				
				//do not process collision if blobs already move in the right direction
				if (actualDistance < previousDistance) {
				//if (futureDistance < actualDistance) {
					doCollision(subject, object);
				}
			}
		}	
	}
	
	
	//Execution of collisions - any data modification only via this method
	private void doCollision (Collidable subject, Collidable object) {
		//newest collisions first to simplify display code	
		listOfCollisionPoints.add(0, prodCollPoint(subject, object));
		//listOfCollisionPoints.add(new Vec(subject.getPosition()));
		
		bounceOff(subject, object);
			
	}
	
	private void placeOverlappingCollideesNextToEachOther (Collidable subject, Collidable object) {
		//this method moves subject away from object in the direction of fromObjToSub
		//for the length that make both collidees touch at the time the collision was detected (just now)
		//(so pretty bad)
		Vec fromObjToSub = vecFromAtoB(
				object.getPosition(),
				subject.getPosition());
		
		double overlap = (subject.getRadius() + object.getRadius()) - fromObjToSub.getMagnitude();
		Vec displacement = fromObjToSub.setMagnitude(overlap);
		subject.setPosition(subject.getPosition().add(displacement));		
	}
	
	private void bounceOff (Collidable subj, Collidable obj) {
		//System.out.println("Coll. of: " + ((Blob)subj).ID + " and " + ((Blob) obj).ID);
		//TODO: velocity will be changed to force (generally speed * mass but maybe some per/colour variations of "mass")
		Vec subVel = subj.getVelocity();
		Vec objVel = obj.getVelocity();
		
		//Subject's side:
		//get direction to hitting blob = angle of collision
		Vec fromSubToObj = vecFromAtoB(
				subj.getPosition().add(subj.getVelocity()),
				obj.getPosition().add(obj.getVelocity()));
		//get amount of subject's kinetic energy transferred at the angle of collision
		Vec kineticInvolvementOfSubject = projectAonB(subVel, fromSubToObj);
		
		//Object's side:
		//reverse the vector pointing from subject to object:
		Vec fromObjToSub = fromSubToObj.multiply(-1);
		
		Vec kineticInvolvementOfObject = projectAonB(objVel, fromObjToSub);
			
		subVel.addAndSet(kineticInvolvementOfSubject.multiply(-1)
				.add(kineticInvolvementOfObject));
		
		objVel.addAndSet(kineticInvolvementOfObject.multiply(-1)
				.add(kineticInvolvementOfSubject));
		
		subj.setColDetDone(true);
		obj.setColDetDone(true);
		
//		System.out.println("Pre-coll. speeds: " + subVel + " and " + objVel);
//		System.out.println("K. inv. of sub. " + ((Blob)subject).ID + ": " + kineticInvolvementOfSubject +
//				". K. inv. of obj. " + ((Blob) object).ID + ": " + kineticInvolvementOfObject);
		
		
		
		
		//TODO:? It's a pity that only subject is updated because all the info for updating object
		//has been computed. Some kind of efficient caching system to store list of
		//of already calculated bounces would allow for update of both parties and 
//		Vec tempSubVel = subVel.add(kineticInvolvementOfSubject.multiplyAndSet(-1)
//				.addAndSet(kineticInvolvementOfObject));
			
//		objVel.addAndSet(kineticInvolvementOfObject.multiplyAndSet(-1)
//				.addAndSet(kineticInvolvementOfSubject));
		
//		subVel = tempSubVel;
		
//		Vec tempSubVel = subVel.add(kineticInvolvementOfSubject.multiplyAndSet(-1));
//		objVel.addAndSet(kineticInvolvementOfObject.multiplyAndSet(-1));
//		subVel = tempSubVel;
		//subject.setColDetDone(true);
//		object.setColDetDone(true);
		
		//System.out.println("Post coll. speeds: " + subVel + " and " + objVel + "\n");
	}
	
	//This the only method I didn't create
	private Vec prodCollPoint(Collidable subject, Collidable object) {
		double x = (subject.getPosition().getX() * object.getRadius() +
				object.getPosition().getX() * subject.getRadius())
				/ (subject.getRadius() + object.getRadius());
		double y = (subject.getPosition().getY() * object.getRadius() +
				object.getPosition().getY() * subject.getRadius())
				/ (subject.getRadius() + object.getRadius());
		
		return new Vec(x,y);		
	}
	
}
