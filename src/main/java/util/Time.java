package util;

public class Time {
	
	public static float timeStarted = System.nanoTime();
	
	public static float getTime() {
		// "E" - more compact way to write "x 10^" or in English
		// "times ten raised to the power of".
		// "1E-9" - "1 x 10-9" (-9 goes on top right corner of 10)
		// "one times ten raised to the power of negative nine".
		// In this case we use it to convert nanoseconds to seconds.   
		return (float)((System.nanoTime() - timeStarted) * 1E-9);
	}

}
