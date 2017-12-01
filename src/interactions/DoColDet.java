package interactions;

import static utils.VecMath.projectAonB;
import static utils.VecMath.vecFromAtoB;

import java.util.List;

import data.Blob;
import data.Vec;

public class DoColDet {
	
	
	
	public void doCollisions(List<Blob> blobs, ColDet det) {
		
	}
	
	
	//Util methods
	private void placeNextToEachOther (Collidable subject, Collidable object) {
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
