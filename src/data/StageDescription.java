package data;

public class StageDescription {

	Vec alternativeGravityCentre, minXY, maxXY;

	public StageDescription(Vec alternateiveGravity, Vec minXY, Vec maxXY) {
		super();
		this.alternativeGravityCentre = alternateiveGravity;
		this.minXY = minXY;
		this.maxXY = maxXY;
	}

	public Vec getAlternativeGravityCentre() {
		return alternativeGravityCentre;
	}

	public Vec getMinXY() {
		return minXY;
	}

	public Vec getMaxXY() {
		return maxXY;
	}
	
}
