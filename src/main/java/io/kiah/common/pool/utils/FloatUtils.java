package io.kiah.common.pool.utils;

public final class FloatUtils {

	private FloatUtils() {
	}

	/**
	 * Returns a new float initialized to the value represented by the specified
	 * String, as performed by the valueOf method of class Float.
	 * 
	 * Returns {@code def} if the string does not contain a parsable float.
	 * 
	 * @param s
	 * @param def
	 * @return
	 */
	public static float parse(String s, float def) {
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/**
	 * Returns a new float initialized to the value represented by the specified
	 * String, as performed by the valueOf method of class Float.
	 * 
	 * Returns null if the string does not contain a parsable float.
	 * 
	 * @param s
	 * @return
	 */
	public static Float parse(String s) {
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}