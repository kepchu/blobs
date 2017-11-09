package app;

import java.util.List;
import static app.VecMath.*;
import data.Vec;

public class ColDetDisabled extends ColDetAllignBordersonAtoBvec {

	public ColDetDisabled (List <Vec> listOfCollisionPoints) {
		super(listOfCollisionPoints, 1.0);
	}
	
	
	public void detectCollisions (List<Collidable> blobs) {
		// nothing to do...
	}
}
