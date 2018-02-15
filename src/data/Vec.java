package data;

import java.io.Serializable;

//All instance methods except ...AndSet() are just convenience methods
// that call static Vec class methods.
//...AndSet() methods duplicate static code - to allow for changing values of fields
// without creation of new object
public class Vec  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//used in magnitudeOf() to bail out of calculations that generate NaNs
	private static final double MARGIN = 0.0000001;
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
		//return new Vec (this.x * x, this.y * y);
		return Vec.multiply(this, x, y);
	}
	public Vec multiplyAndSet(double x, double y) {
		setX(this.x * x);
		setY(this.y * y);
		return this;
	}

	public Vec multiply(double in) {
		//return new Vec(this.x * in, this.y * in);
		return Vec.multiply(this, in);
	}
	public Vec multiplyAndSet(double in) {
		setX(this.x * in);
		setY(this.y * in);
		return this;
	}

	public Vec add (Vec v) {
		//return new Vec(this.x + v.getX(), this.y + v.getY());
		return Vec.add(this, v);
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
		//return this.add(v.multiply(-1));
		return Vec.sub(this, v);
	}
	public Vec subAndSet(Vec v) {
		addAndSet(v.multiply(-1));
		return this;
	}

//	private Vec normalise() {
//		double m = getMagnitude();
//		if (!Double.isFinite(m)) {
////			System.out.println("Got NaN/Infinite magnitude in normalise()");
//		}
//		return multiply(1d/getMagnitude());
//	}
	
	public Vec withMagnitudeOf(double magnitude) {
		//return normalise().multiplyAndSet(magnitude);
		return Vec.normalise(this).multiplyAndSet(magnitude);
	}
	
	// GETTERS/SETTERS START
	public Vec setMagnitude(double magnitude) {
		Vec v = this.withMagnitudeOf(magnitude);
		setX(v.x);
		setY(v.y);
		return this;
	}

	public double getMagnitude() {
//		double margin = 0.0000001;
//		
//		double m = Math.pow(x, 2) + Math.pow(y, 2);
//		if ( m < margin) {//!Double.isFinite(m)
//			System.out.println("Got NaN/Infinite or very small value in  getMagnitude()");
//			return margin;
//		}
//		return Math.sqrt(m);
		return Vec.magnitudeOf(this);
	}

	public double getMagnitudePOW() {
		return Vec.magnitudeOfPOW(this);
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
		return "x: " + x + ", y: " + y;
	}
	

	//STATIC VEC MATH METHODS	
	public static double distanceBetween (Vec a, Vec b) {
		return magnitudeOf(vecFromAtoB(a, b));		
	}
	
	//optimisation: work on squared distances to avoid square root calculation
	public static double distanceBetweenPOW (Vec a, Vec b) {
		return magnitudeOfPOW(vecFromAtoB(a, b));
	}

	public static Vec multiply (Vec v, double d) {
		return new Vec (
				v.getX() * d,
				v.getY() * d);
	}
	
	public static Vec multiply (Vec v, double x, double y) {
		return new Vec (
				v.getX() * x,
				v.getY() * y);
	}
	
	public static  Vec multiply(Vec a, Vec b) {
		return new Vec (
				a.getX() * b.getX(),
				a.getY() * b.getY());
	}
	
	public static Vec add (Vec a, Vec b) {
		return new Vec (
				a.getX() + b.getX(),
				a.getY() + b.getY());
	}
	
	public static Vec sub(Vec a, Vec b) {
		Vec negativeB = multiply(b, -1);
		return add(a, negativeB);
	}
	
	public static double magnitudeOf(Vec v) {
		
		double m = Math.pow(v.getX(), 2) + 
				Math.pow(v.getY(), 2);
		
		if ( m < MARGIN) {//!Double.isFinite(m) ||
			//System.out.println("Got very small value in magnitudeOf()");
			return MARGIN;
		}			
		return Math.sqrt(m);
	}
	
	//optimisation: work on squared distances to avoid square root calculations
	public static double magnitudeOfPOW(Vec v) {
		return Math.pow(v.getX(), 2) + 
				Math.pow(v.getY(), 2);
	}
	
	// normalise = divide x & y by vector's length == multiplying by 1/length
	public static Vec normalise(Vec v) {
		return multiply (v, 1.0 / magnitudeOf (v));
		//return multiply(1d / getLength());
	}

	/*dot product - applied to normalised vectors yields cos(angle between vectors) -
	 positive when the vectors face each other (within 180 degrees) and negative if not:
	
		|--"facing plane" of Vec a
		|
	-	|	+
		|
		A----Vec a---->
		|\
		| \
		|  \--Vec b - from object A to object B. Dot product of normalised Vec a 
		|	\	and normalised vec b is positive when object B is "in front" of A
		|	 \
		|	  \
			   B
	 	*/
		
	public static double dot(Vec a, Vec b) {
		return a.getX() * b.getX() +
				a.getY() * b.getY();
	}
	
	//produces vector starting at a and pointing at b
	public static Vec vecFromAtoB (Vec a, Vec b) {
		return sub(b, a);
	}
	
	//produce projection of vec A on vec b
	//(shadow casted by a on b at noon when b is perfectly horizontal)
	public static Vec projectAonB (Vec a, Vec b) {
		double dot = dot(a, b);
		double sqLengthOfB = Math.pow(b.getMagnitude(), 2);
		return multiply(b,(dot/sqLengthOfB));
	}

	//LOW LEVEL VECTOR-MANIPULATING METHODS - end
}
