package utils;

public class U {

	// generates random integer in range from min to max
	public static int rndInt(int min, int max) {
		int range = max - min;
		return (int) (Math.random() * range) + min;
	}

	// generates random int in range between 0 and received argument
	public static int rndInt(int max) {
		return (int) (Math.random() * max);
	}

}
