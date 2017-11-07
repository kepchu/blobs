package app;

import data.Vec;

public class VecMath {
	
	//HIGHER LEVEL (MORE MININGFUL) VECTOR STUFF
		public static double distanceBetween (Vec a, Vec b) {
			return magnitudeOf(vecFromAtoB(a, b));		
		}
		
		//LOW LEVEL VECTOR-MANIPULATING METHODS - start
		
			public static Vec multiply (Vec v, double d) {
				return new Vec (
						v.getX() * d,
						v.getY() * d);
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
				return Math.sqrt(
						Math.pow(v.getX(), 2) + 
						Math.pow(v.getY(), 2));
			}
			
			// normalise = divide x & y by vector's length == multiplying by 1/length
			public static Vec normalise(Vec v) {
				return multiply (v, 1.0 / magnitudeOf (v));
				//return multiply(1d / getLength());
			}

			/*dot product - applied to normalised vectors yields cos(angle between vectors) - this
			will be positive when Vec a is "facing" coordinatesif vectors are
			"facing each other (within 180 degrees) and negative if not:
			
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
			//(shadow of a on b at noon when b is perfectly horizontal)
			public static Vec projectAonB (Vec a, Vec b) {
				double dot = dot(a, b);
				double sqLengthOfB = Math.pow(b.getMagnitude(), 2);
				return multiply(b,(dot/sqLengthOfB));
			}
		
			//LOW LEVEL VECTOR-MANIPULATING METHODS - end
}