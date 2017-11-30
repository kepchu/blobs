package data;

public class Vec {

	private double x, y = 0;// coordinates

	public Vec(Vec v) {
		this(v.getX(), v.getY());
	}

	public Vec(double x, double y) {
		this.x = x;
		this.y = y;
	}

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
		double m = getMagnitude();
		if (!Double.isFinite(m)) {
			System.out.println("Got NaN/Infinite in normalise()");
		}
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
		double margin = 0.0000001;
		
		double m = Math.pow(x, 2) + Math.pow(y, 2);
		if ( m < margin) {//!Double.isFinite(m)
			System.out.println("Got NaN/Infinite or very small value in  getMagnitude()");
			return margin;
		}
		return Math.sqrt(m);
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
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	// GETTERS/SETTERS - END
	@Override
	public String toString() {
		return "x:" + x + ", y:" + y;
	}
	
}
