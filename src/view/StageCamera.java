package view;

public class StageCamera {

	
	private final static double
		ZOOM_STEP = 0.02, ZOOM_DECAY = 0.96, ZOOM_MIN = 0.009, SCALE_MIN = 0.02, SCALE_MAX = 10,
		SCROLL_STEP = 20, SCROLL_DECAY = 0.93, SCROLL_MIN = 0.3, GROUND_LEVEL = 0;
	private double scrollDelta, zoomDelta, scale, elevation;
	private int width, height;
	
	private boolean autoRescaleToWidth;
	
	
	public StageCamera(double scale, double elevation, int width, int height) {
		super();
		this.scale = scale;
		this.elevation = GROUND_LEVEL + height;
		this.width = width;
		this.height = height;
	}
	
	public StageCamera(double scale, int width, int height) {
		this(scale, GROUND_LEVEL + height, width, height);
	}
	
	
	
	void stageResized (int newWidth, int newHeight) {
		
		//Adjust elevation to new height
		elevation = elevation - (height - newHeight);
		
		//auto rescale
		if (autoRescaleToWidth) {
			rescaleToWidth(newWidth);
		}
		
		//TODO: add "centering"
		width = newWidth;
		height = newHeight;
		update();
	}
	
	//rescale so the new window width covers the same "game space" as previous width
	private void rescaleToWidth (int newWidth) {		
		//check for 0 because ComponentListener onSizeChanged is fired during initialisation
		if (newWidth != width && width != 0) {
			scale = (newWidth * scale) / width;
		}
	}
	
	
	void setAutoRescalingToWidth(boolean autoRescaleToWidth) {
		this.autoRescaleToWidth = autoRescaleToWidth;
	}
	
	boolean isAutoRescalingToWidth() {
		return autoRescaleToWidth;
	}
	
	
	
	void zoomOut() {
		zoomDelta = ZOOM_STEP * -1;
	}
	
	void zoomIn () {
		zoomDelta = ZOOM_STEP;
	}	
	
	void up() {
		scrollDelta = SCROLL_STEP * -1;
	}
	
	void down() {
		scrollDelta = SCROLL_STEP;
	}
	
	
	
	//Update section
	void update() {
		//Scrolling
		updateScroll();
		//Zooming
		updateZoom();
	}
	
	private void updateScroll() {
		
		if (Math.abs(scrollDelta) > SCROLL_MIN) {
			scrollDelta *= SCROLL_DECAY;//reduce scrollDelta (make it closer to 0)
			double newElevation = elevation + scrollDelta;
			//set elevation to newly calculated value or panel's bottom, whichever is higher
			elevation = (newElevation < height + GROUND_LEVEL ? height + GROUND_LEVEL : newElevation);
		}
	}
	
	private void updateZoom() {
		if (Math.abs(zoomDelta) > ZOOM_MIN) {//scale > SCALE_MIN &&
			//when over zooming limit make sure that zooming direction is always right
			//(bounce-off effect)
			if (scale < SCALE_MIN) zoomDelta = Math.abs(zoomDelta);//always positive/in
			if (scale > SCALE_MAX) zoomDelta = Math.abs(zoomDelta) * -1;//always negative/out
			
			zoomDelta *= ZOOM_DECAY;
			scale *= 1 + zoomDelta;//scale is being multiplied by value approaching 1 over time
			System.out.println("scale: " + scale);
		}
	}
	
	//getters / setters
	int getEleveation () {
		return (int)elevation;
	}
	
	double getScale () {
		return scale;
	}
	
	void setElevation (int elevation) {
		this.elevation = elevation;
	}
	
	void setScale (double scale) {
		this.scale = scale;
	}




//		//follow followed blob
//		private void stageFollow(Being target, double marginUp, double marginDown) {
//			//both marginUp and marginDown define the area that triggers
//			//camera movement.
//			//marginUp == 1/x of upper portion of screen
//			//marginDown == 1/x of lower portion of screen
//			//ratio->amplifies influence of target's speed TODO:
//			//if ratioUp and ratioDown are set to two different values everything gets weird
//				
//			// 1. follow up
//			// top of window (where y == 0)
//			if (GROUND_LEVEL + target.getPosition().getY()
//			// margin of 1/x of window height
//					- d.getHeight() / marginUp		
//					< 0) {// adjust stageYDelta toward "down" (==++) 
//				//m.setLastMessage(" fallUp");
//				
//				//stageYDelta += stageYDeltaUnit * Math.abs(groundLevel / followed.getPosition().getY());
//				
//				stageYDelta += 	stageYDeltaUnit * Math.abs(followed.getVelocity().getY()//speed, not position => camera effect
//						+ stageYAwayMultiplayer * (GROUND_LEVEL + followed.getPosition().getY())/d.getHeight());//no of Screens away			
//			}
//			
//			// 2. follow down:
//			// bottom of window (where y == Display.getHeight())
//			if ((GROUND_LEVEL - d.getHeight())
//					//position of player
//					+ d.getHeight()/marginDown
//							//margin of 1/x of window height
//							+ (followed.getPosition().getY())
//					> 0) {
//				// adjust stageYDelta towards "up" direction (== reduce stageYDelta)
//				//m.setLastMessage(" fallDown");a-b / height
//				stageYDelta -= stageYDeltaUnit * Math.abs(followed.getVelocity().getY()//speed, not position => camera effect
//						+ stageYAwayMultiplayer * (GROUND_LEVEL + followed.getPosition().getY())/d.getHeight());//no of Screens away
//				
//				/*System.out.println("YDeltaRatio: " + Math.abs(groundLevel / followed.getPosition().getY()));
//				System.out.println("two: " + ((groundLevel + followed.getPosition().getY())/d.getHeight()));*/
//				// + followed.getPosition().getY()));//groundLevel / d.getHeight());
//			}
//			
//			m.setLastMessage("ground: " + (int)GROUND_LEVEL +
//					", stageYDelta: " + (int)stageYDelta + "YDeltaSHeights final: " + (int)(stageYAwayMultiplayer * (GROUND_LEVEL + followed.getPosition().getY())/d.getHeight())
//					+ ", YDeltaMult.: " + (GROUND_LEVEL + followed.getPosition().getY())/d.getHeight());	
//		}
	
}
	