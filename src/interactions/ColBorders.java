package interactions;

import data.Vec;

public class ColBorders {
	//collisions with "world edges"
	void bounce(Collidable c, int minX, int maxX, int minY, int maxY, double radiusMultiplier) {
		bounceOffGround(c, maxY, radiusMultiplier);
		bounceOffSides(c, minX, maxX, radiusMultiplier);
	}
		
	void bounceOffSides(Collidable c, int minX, int maxX, double radiusMultiplier) {

		Vec p = c.getPosition();
		Vec v = c.getVelocity();
		double radius = c.getRadius() * radiusMultiplier;
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
	
	void bounceOffGround(Collidable c, int ground, double radiusMultiplier) {
		Vec p = c.getPosition();
		Vec v = c.getVelocity();
		double radius = c.getRadius() * radiusMultiplier;
		double damp = c.getBounceDampeningFactor();
		
		if (p.getY() + radius >= ground) {//below bottom edge
			p.setY(ground - radius);	//align with bottom edge
			v.multiplyAndSet(1, -damp);//reverse vertical velocity
		}
	}
		
	// wrap x and y around the world (== connect the opposite edges of the stage)
	void wrapXY(Collidable c, int minX, int maxX, int minY, int maxY, double radiusMultiplier) {
		wrapX(c, minX, maxX, radiusMultiplier);
		wrapY(c, minY, maxY, radiusMultiplier);
	}	
	//X axis wrapping
	void wrapX(Collidable c, int minX, int maxX, double radiusMultiplier) {
		c.getPosition().setX(
				computeWrap (c.getPosition().getX(), minX, maxX, c.getRadius() * radiusMultiplier));
	}
	//wrap vertically
	void wrapY(Collidable c, int minY, int maxY, double radiusFactor) {
		c.getPosition().setY(
				computeWrap(c.getPosition().getY(), minY, maxY,
						c.getRadius() * radiusFactor));
	}
	
	private double computeWrap(Double position, int min, int max, double margin) {
		//this method expands stage's dimensions for each "wrapped" collidable
		//by margin*2. (and "margin" is particular collidable's radius + mod factor)...
		int span = max - min;
		//wrap when "position" outside limits denoted by min & max by more than margin
		if (position - margin > max) {
			//Subtracting margin twice to:
			//A. account for "position - margin" in previous line, and
			//B. place blob off-stage (except movement calculated for the frame)
			position = min + ((position - max - margin - margin) % span);
			return position;
		} else if (position + margin < min) {
			position = max - ((min - position - margin - margin) % span);
			return position;
		}
		
		return position;
	}
	
	@SuppressWarnings("unused")
	private double computeWrap(Double position, int min, int max) {
		//straight-forward wrapping that accounts only for collidable's position
		int span = max - min;
		
		if (position < min)
			position = max - ((min - position) % span);
		if (position > max)
			position = min + ((position - max) % span);
		
		return position;
	}
}
