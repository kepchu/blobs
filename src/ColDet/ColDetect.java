package ColDet;

import static utils.VecMath.distanceBetween;
import static utils.VecMath.projectAonB;
import static utils.VecMath.vecFromAtoB;

import java.util.List;

import data.Vec;
import data.World;

public class ColDetect {
	
	private List<Vec> listOfCollisionPoints;
	@SuppressWarnings("unused")
	private double timeInterval;
	//private double dampeningFactor = 1.0;
	private ColBorders borders;
	
	public ColDetect (List <Vec> listOfCollisionPoints, double timeInterval) {
		this.listOfCollisionPoints = listOfCollisionPoints;
		this.timeInterval = timeInterval;
		//this.borders = new ColBorders();
	}
	
	public void detectCollisions (List<Collidable> blobs, int minX, int maxX, int minY, int maxY) {
				
		Collidable c;
		for (int i = 0; i < blobs.size(); i++ ) {
			c = blobs.get(i);
			
			System.out.println("detectCollisions");
			//borders.bounceOffGround(c, maxY, radiusFactor);
			//borders.wrapX(c, minX, maxX);
			//borders.wrapY(c, minY, maxY);
			
			//doBlobs(c, blobs, 1);
		}
	}
	
	
		
	//detection of collisions with blobs
	private void doBlobs (Collidable subject, List<Collidable> objects, double radiusFactor) {
			
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
			if (actualDistance < subject.getRadius() + object.getRadius()) {
				placeOverlappingCollideesNextToEachOther (subject, object);
				
				//TODO add test for (and deal with) growing blobs here because:
				// 1. inflating/growing blob has to affect POSITION and not just velocity of its neighbours
				// 2. distance test below excludes many valid cases
				// 3. shrinking should be taken care of in "bounceOff"
				
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
		//newest collisions first for simple display code	
		listOfCollisionPoints.add(0, prodCollPoint(subject, object));
		//listOfCollisionPoints.add(new Vec(subject.getPosition()));
		
		bounceBlobs(subject, object);
		//placeOverlappingCollideesNextToEachOther(subject, object);
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
	
	private void bounceBlobs (Collidable subj, Collidable obj) {
		//TODO: tagging instead of simple removal from input list (can be used for visual cues and logic)
		//Will switch later from boolean tags to location of collision.
		subj.setColDetDone(true);
		//obj.setColDetDone(true);
		
		//1. Transfer of kinetic energy
		//TODO: velocity will be changed to force (generally speed * mass but maybe some per/colour variations of "mass")
		Vec subVel = subj.getVelocity();
		Vec objVel = obj.getVelocity();
		
		//Subject's side:
		//get direction to hitting blob = angle of collision
		Vec fromSubToObj = vecFromAtoB(
				subj.getPosition(),
				obj.getPosition());
		//get amount of subject's kinetic energy transferred at the angle of collision
		Vec kineticInvolvementOfSubject = projectAonB(subVel, fromSubToObj);
		
		//Object's side:
		//reverse the vector pointing from subject to object:
		Vec fromObjToSub = fromSubToObj.multiply(-1);
		
		Vec kineticInvolvementOfObject = projectAonB(objVel, fromObjToSub);
		
		//TODO: inflating blobs to be dealt with in calling method - only shrinking blobs here
		//2. Account for inflation:
		if (subj.inflationDelta() != 0)
			kineticInvolvementOfSubject.setMagnitude(kineticInvolvementOfSubject.getMagnitude() + subj.inflationDelta());
		if (obj.inflationDelta() != 0)
			kineticInvolvementOfObject.setMagnitude(kineticInvolvementOfObject.getMagnitude() + obj.inflationDelta());
		
		
		//apply reflection forces
		subVel.addAndSet(kineticInvolvementOfSubject.multiply(-1)
				.add(kineticInvolvementOfObject));
		
		objVel.addAndSet(kineticInvolvementOfObject.multiply(-1)
				.add(kineticInvolvementOfSubject));	
		
		
		
		
		
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
