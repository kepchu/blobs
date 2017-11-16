package ColDet;

import static utils.VecMath.*;

import java.util.List;

import data.Vec;

public class ColDetDisabled extends ColDetect {

	public ColDetDisabled (List <Vec> listOfCollisionPoints) {
		super(listOfCollisionPoints, 1.0);
	}
	
	
	public void detectCollisions (List<Collidable> blobs) {
		// nothing to do...
	}
}
