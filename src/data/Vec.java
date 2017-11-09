package data;

public class Vec {

	private double x, y = 0;// coordinates
	private boolean boundByStageX;// = true;// to allow objects to drift in and out of stage without being "warped"
	private boolean boundByStageY;

	public Vec(Vec v) {
		this(v.getX(), v.getY());
	}

	public Vec(double x, double y, boolean boundByStage) {
		this(x, y);
		this.boundByStageX = boundByStageY = boundByStage;
	}

	public Vec(double x, double y) {
		this.x = x;
		this.y = y;
	}

	// both "WRAP" METHODS below wrap x and y around the world
	// (== connect the opposite edges of the stage)
	private void wrapX() {
		if (x < World.minX) {
			x = World.maxX - ((World.minX - x) % World.spanX);
		}
		if (x > World.maxX) {
			x = World.minX + ((x - World.maxX) % World.spanX);
		}
	}

	private void wrapY() {
		if (y < World.minY)
			y = World.maxY - ((World.minY - y) % World.spanY);
		if (y > World.maxY)
			y = World.minY + ((y - World.maxY) % World.spanY);
	}

	// WRAPPING THE STAGE STUFF - END

	// VECTOR MATH START

	public Vec multiply (double x, double y) {
		return new Vec (this.x * x, this.y * y);
	}
	public Vec multiplyAndSet(double x, double y) {
		setX(this.x * x);
		setY(this.y * y);
		return this;
	}

	public Vec multiply(double in) {
		return new Vec(this.x * in, this.y * in);
	}
	public Vec multiplyAndSet(double in) {
		setX(this.x * in);
		setY(this.y * in);
		return this;
	}

	public Vec add (Vec v) {
		return new Vec(this.x + v.getX(), this.y + v.getY());
	}
	public Vec addAndSet(Vec v) {
		setX(this.x + v.getX());
		setY(this.y + v.getY());
		return this;
	}
	public Vec addAndSet(double x, double y) {
		setX(this.x + x);
		setY(this.y + x);
		return this;
	}
	
	public Vec sub(Vec v) {
		return this.add(v.multiply(-1));
	}
	public Vec subAndSet(Vec v) {
		addAndSet(v.multiply(-1));
		return this;
	}

	private Vec normalise() {
		return multiply(1d/getMagnitude());
	}
	
	// GETTERS/SETTERS START
	public Vec setMagnitude(double magnitude) {
		Vec v = normalise().multiplyAndSet(magnitude);
		setX(v.x);
		setY(v.y);
		return this;
	}

	public double getMagnitude() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	public boolean isboundByStageX() {
		return boundByStageX;
	}

	public boolean isboundByStageY() {
		return boundByStageY;
	}

	public void setboundByStageX(boolean boundByStage) {
		this.boundByStageX = boundByStage;
	}

	public void setboundByStageY(boolean boundByStage) {
		this.boundByStageY = boundByStage;
	}

	public Vec setXY(double x, double y) {
		setX(x);
		setY(y);
		return this;
	}
	public Vec setXY(Vec v) {
		return setXY(v.x, v.y);
	}
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
		if (boundByStageX)
			wrapX();
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
		if (boundByStageY)
			wrapY();
	}

	// GETTERS/SETTERS - END
	@Override
	public String toString() {
		return super.toString() + " x:" + x + ", y:" + y;
	}
	
}
