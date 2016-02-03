package io.kiah.common.pool.utils;

import java.util.Collection;

public final class IntegerUtils {

	/**
	 * 
	 * @param values
	 * @return
	 */
	public static int sumInts(final Collection<Integer> values) {

		if (values == null || values.isEmpty())
			return 0;

		int result = 0;
		for (Integer val : values) {
			result += val;
		}

		return result;
	}

	/**
	 * 
	 * @param values
	 * @return
	 */
	public static long sumLongs(final Collection<Long> values) {

		if (values == null || values.isEmpty())
			return 0L;

		long result = 0L;
		for (Long val : values) {
			result += val;
		}

		return result;
	}

	/**
	 * Returns a random unsigned integer belongs to [0, max].
	 * 
	 * @param max
	 *            The max value can be returned. (MUST greater than zero)
	 * @return A random unsigned integer.
	 */
	public static int random(final int max) {

		if (max <= 0) {
			throw new IllegalArgumentException("max cannot less than or equals to zero");
		}

		return (int) Math.ceil(Math.random() * max);
	}

	/**
	 * Returns a random unsigned integer belongs to [min, max].
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int random(final int min, final int max) {
		return random(max - min) + min;
	}

	/**
	 * 
	 * 
	 * @param s
	 * @return Returns NULL if failed.
	 */
	public static Integer parse(final String s) {

		if (StringUtils.isNullOrEmpty(s))
			return null;

		try {
			return Integer.parseInt(s.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 
	 * @param s
	 * @param def
	 * @return
	 */
	public static int parse(final String s, final int def) {

		if (StringUtils.isNullOrEmpty(s))
			return def;

		try {
			return Integer.parseInt(s.trim());
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/**
	 * 
	 * @param s
	 * @param def
	 * @return
	 */
	public static long parse(final String s, final long def) {

		if (StringUtils.isNullOrEmpty(s))
			return def;

		try {
			return Long.parseLong(s.trim());
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/**
	 * 
	 * @param s
	 * @param def
	 * @return
	 */
	public static float parse(final String s, final float def) {

		if (StringUtils.isNullOrEmpty(s))
			return def;

		try {
			return Float.parseFloat(s.trim());
		} catch (NumberFormatException e) {
			return def;
		}
	}
}