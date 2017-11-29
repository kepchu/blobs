package ColDet;

import data.Vec;

public class ColBorders {
	//collisions with "world edges"
	public void bounce(Collidable c, int minX, int maxX, int minY, int maxY) {
		bounceOffGround(c, maxY);
		bounceOffSides(c, minX, maxX);
	}
		
	public void bounceOffSides(Collidable c, int minX, int maxX) {

		Vec p = c.getPosition();
		Vec v = c.getVelocity();
		double radius = c.getRadius();
		double damp = c.getBounceDampeningFactor();
		//bounce off of stage edges:
		//left
		if (p.getX() - radius < minX) {
			p.setX(minX + radius);
			v.multiplyAndSet(-damp, 1);
		}
		//right
		if (p.getX() + radius > maxX) {
			p.setX(maxX - radius);
			v.multiplyAndSet(-damp, 1);
		}		
	}
	
	public void bounceOffGround(Collidable c, int ground) {
		Vec p = c.getPosition();
		Vec v = c.getVelocity();
		double radius = c.getRadius();
		double damp = c.getBounceDampeningFactor();
		
		if (p.getY() + radius >= ground) {//below bottom edge
			p.setY(ground - radius);	//align with bottom edge
			v.multiplyAndSet(1, -damp);//reverse vertical velocity
		}
	}
		
	// wrap x and y around the world (== connect the opposite edges of the stage)
	public void wrapXY(Collidable c, int minX, int maxX, int minY, int maxY) {
		wrapX(c, minX, maxX);
		wrapY(c, minY, maxY);
	}	
	//X axis wrapping
	public void wrapX(Collidable c, int minX, int maxX) {
		c.getPosition().setX(
				computeWrap (c.getPosition().getX(), minX, maxX, c.getRadius()));
	}
	//wrap vertically
	public void wrapY(Collidable c, int minY, int maxY) {
		c.getPosition().setY(
				computeWrap(c.getPosition().getY(), minY, maxY,
						c.getRadius()));
	}
	
	private double computeWrap(Double position, int min, int max, double radius) {
		int span = max - min;
		int margin = 1000;
		System.out.println("s: " + span + ", m: " + margin);
		if (position < min)
			position = max - ((min - position) % span);
		if (position - margin > max)
			position = min + ((position - max) % span);
		
		/*if (position < min)
			position = max - ((min - position) % span);
		if (position > max)
			position = min + ((position - max) % span);*/
				
		return position;
	}
}
