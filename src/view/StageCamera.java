package view;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

import data.Vec;

public class StageCamera implements ComponentListener{

	
	private final static double
		ZOOM_STEP = 0.02, ZOOM_DECAY = 0.96, ZOOM_MIN = 0.009, SCALE_MIN = 0.02, SCALE_MAX = 10,
		SCROLL_STEP = 14, SCROLL_DECAY = 0.93, SCROLL_MIN = 0.3, GROUND_LEVEL = 0,
		
		INIT_SCALE = 1, INIT_ELEVATION = 700.0;
	private double scrollDelta, zoomDelta;//scrolling & zooming step(=distance)
	volatile double scale;//zooming factor
	volatile double elevation;//unscaled distance from top of window to maxX (= 0 = ground)
	int width;//width of Stage JPanel
	int height;//height of Stage JPanel. Y of blobs is NEGATIVE. Y=0 = bottom of the panel - ground
	
	Vec middlePoint = new Vec(0,0);
	
	private boolean autoRescaleToWidth;
		
	
	public StageCamera(double elevation, int width, int height) {
		super();
		
		this.scale = INIT_SCALE;
		this.elevation = GROUND_LEVEL + height;
		this.width = width;
		this.height = height;
	}
	
	public StageCamera(int width, int height) {
		this(GROUND_LEVEL + height, width, height);
	}

	
	//SCALING SECTION
	//GENERAL FORMULA (finally, brawo ja!!):
	//scale: x * scale + (scaling centre - scaled scaling* scale) + shift
	//descale: (x - ((scaling centre - scaled scaling* scale) - shift)/scale
	public double descaleX(int x) {
		double scale = this.scale;
		return (x-((width/2.0) - ((width/2.0) * scale)))/scale;
		//return (x + ((width/2.0)*(scale - 1)))/scale;//working alternative
	}
	public double descaleY(int y) {
		return (y - ((-elevation+ height/2) - ((-elevation+ height/2) * scale)) - elevation)/scale ;
	}
	
	int scaleX(double x) {
		//return (int)(x * scale - ((width/2.0) * (scale -1.0)));//alternative		
		return (int)((x * scale) + ((width/2.0) - ((width/2.0) * scale)));
	}
	int scaleY(double y) {
		//(y * scale) + ((-elevation+ height/2) - ((-elevation+ height/2) * scale)) + elevation;-YESSSSS	
		return (int)((y * scale) + ((-elevation + height/2.0) - ((-elevation + height/2.0) * scale)) + elevation);
	}
	
	double descaledStageCentreY() {
		return descaleY(height/2);
	}

	double descaledStageCentreX() {
		return descaleX(width/2);
	}
	
	Vec descaledMinXY() {
		return new Vec (descaleX(0), descaleY(0));
	}
	Vec descaledMaxXY() {
		return new Vec (descaleX(width), descaleY(height));
	}
	
	
	
	void stageResized (int newWidth, int newHeight) {
				
		//Adjust elevation to new height
		elevation = elevation - (height - newHeight);
		
		//auto rescale
		if (autoRescaleToWidth) {
			scale = rescaleToWidth(width, newWidth);
		}
		
		//TODO: add "centering"
		width = newWidth;
		height = newHeight;
		
	}
	
	
	private double rescaleToWidth (int previousWidth, int newWidth) {
		if(previousWidth <= 0) {
			throw new IllegalStateException("Previous width is not a positive integer."
					+ "(Re)initialize properly with resetDimentions()");
		}
		return (newWidth * scale) / previousWidth;
	}
	
	
	void setAutoRescalingToWidth(boolean autoRescaleToWidth) {
		this.autoRescaleToWidth = autoRescaleToWidth;
	}
	
	boolean isAutoRescalingToWidth() {
		return autoRescaleToWidth;
	}
	
	
	//changes applied in update(), actual scaling in scaling methods
	void zoomOut() {
		zoomDelta = ZOOM_STEP * -1;
	}
	
	void zoomIn () {
		zoomDelta = ZOOM_STEP;
	}	
	
	void up() {
		//System.out.println("scrollDelta: " + scrollDelta);
		scrollDelta = SCROLL_STEP * -1;
	}
	
	void down() {
		scrollDelta = SCROLL_STEP;
	}
	
	
	//UPDATE SECTION "MAIN LOOP" - START
	void update() {
		//Scrolling
		updateScroll();
		//Zooming
		updateZoom();
	}
	
	private void updateScroll() {
		
		if (Math.abs(scrollDelta) > SCROLL_MIN) {
			scrollDelta *= SCROLL_DECAY;//reduce scrollDelta (make it closer to 0)
			double newElevation = elevation + scrollDelta / scale;//dividing by scale to counteract zooming effects
			//set elevation to newly calculated value or panel's bottom, whichever is higher
			elevation = (newElevation < height + GROUND_LEVEL ? height + GROUND_LEVEL : newElevation);
			//elevation = newElevation;//debugging
		}
	}
	
	private void updateZoom() {
		if (Math.abs(zoomDelta) > ZOOM_MIN) {//scale > SCALE_MIN &&
			//when zooming over limit make sure that zooming direction is always right
			//(bounce-off effect)
			if (scale < SCALE_MIN) zoomDelta = Math.abs(zoomDelta);//always positive/in
			if (scale > SCALE_MAX) zoomDelta = Math.abs(zoomDelta) * -1;//always negative/out
			
			zoomDelta *= ZOOM_DECAY;
			scale *= 1 + zoomDelta;//scale is being multiplied by value approaching 1 over time
			//System.out.println("scale: " + scale);
		}
	}
	
	//UPDATE SECTION "MAIN LOOP" - END
	
	void resetDimentions(int width, int height) {
		this.width = width;
		this.height = height;
		this.elevation = height;
	}
	
	void resetDimentions(int width, int height, double scale) {
		resetDimentions(width, height);
		this.scale = scale;
	}
	
	@Override
	public void componentResized(ComponentEvent arg0) {
		Stage stage = (Stage)arg0.getSource();
		System.out.println("componentResized(ComponentEvent arg0): "
		+ height + " -> " + stage.getHeight() + "; "  + width + " -> " + stage.getWidth());
		stageResized(stage.getWidth(), stage.getHeight());		
	}
	//ComponentListener methods.
	//StageCamera is listening to Stage JPanel
	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
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
	
	
	
	//getters / setters (TODO: auto-generated...)
	
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
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getStageCentreX() {
		return width/2;
	}

	public int getStageCentreY() {
		return height/2;
	}

	
}
	