package data;

public class StageDescription {
	//TODO: no Vecs needed, settings (from GUI) have to be sent and included
	//in FrameData for saving (null when frameData is sent to GUI).
	//ALTERNATIVE: pack  FrameData & settings in save-file

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
