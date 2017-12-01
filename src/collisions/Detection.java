package collisions;

import java.util.List;

public interface Detection {
	//TODO: return a container class to preserve results of calculations
	Collidable[] detectInteraction (Collidable subj, List<Collidable> obj, double radiusFactor);
}
