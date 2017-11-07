package app;

import data.Vec;

public interface Collidable {

	Vec getPosition();
	void setPosition(Vec v);
	double getRadius();

	Vec getVelocity();
	
	double getEnergy();
	
	double inflationDelta();

	Vec getPreviousPosition();

	

}
