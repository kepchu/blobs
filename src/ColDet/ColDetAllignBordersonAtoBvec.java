package ColDet;

import static utils.VecMath.*;

import java.util.List;

import data.Vec;

public class ColDetAllignBordersonAtoBvec {
	
	private List<Vec> listOfCollisionPoints;
	@SuppressWarnings("unused")
	private double timeInterval;
	
	public ColDetAllignBordersonAtoBvec (List <Vec> listOfCollisionPoints, double timeInterval) {
		this.listOfCollisionPoints = listOfCollisionPoints;
		this.timeInterval = timeInterval;
	}
	
	
	public void detectCollisions (List<Collidable> blobs) {
		//listOfCollisionPoints.clear();
		for (int i = blobs.size()-1; i > 0; i--) {
		//for (int i = 0; i < blobs.size(); i++ ) {
			detectCollisions(blobs.get(i), blobs, 1);
		}	
	}
	
	//detection of collisions
	private void detectCollisions (Collidable subject, List<Collidable> objects, double radius) {
			
		double actualDistance;
		double previousDistance;
		
		Collidable object;
		for (int i = 0; i < objects.size(); i++) {
					
			//filter out the collidable we are testing against
			object = objects.get(i);			
			if (object == subject) {continue;}
	
			
			actualDistance = distanceBetween(
					subject.getPosition(),
					object.getPosition());
			previousDistance = distanceBetween(
					subject.getPreviousPosition(), 
					object.getPreviousPosition());
			
			if (actualDistance < subject.getRadius() + object.getRadius()) {
				placeOverlappingCollideesNextToEachOther (subject, object);
				//TODO: align borders!!!
				
				//do not process collision if blobs already move in the right direction
				if (actualDistance <= previousDistance) {
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
	
	private Vec bounceOff (Collidable subject, Collidable object) {
		
		//TODO: velocity will be changed to force (speed * mass)
		Vec subVel = subject.getVelocity();
		Vec objVel = object.getVelocity();
		
		//Subject's side:
		//get direction to hitting blob = angle of collision
		Vec fromSubToObj = vecFromAtoB(
				subject.getPosition(),
				object.getPosition());
		//get amount of subject's kinetic energy transferred at the angle of collision
		Vec kineticInvolvementOfSubject = projectAonB(
				subVel, fromSubToObj);
		
		//Object's side:
		//reverse the vector pointing from subject to object:
		Vec fromObjToSub = fromSubToObj.multiplyAndSet(-1);//!fromSubToObj refers the same vec!
		Vec kineticInvolvementOfObject = projectAonB(objVel, fromObjToSub);
		
		//TODO:? It's a pity that only subject is updated because all the info for updating object
		//has been computed. Some kind of efficient caching system to store list of
		//of already calculated bounces would allow for update of both parties and 
		subVel.addAndSet(kineticInvolvementOfSubject.multiplyAndSet(-1)
				.addAndSet(kineticInvolvementOfObject));
		return fromObjToSub;
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
