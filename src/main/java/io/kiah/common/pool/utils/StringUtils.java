package io.kiah.common.pool.utils;

import java.text.DecimalFormat;
import java.util.*;

public final class StringUtils {

	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
	public static final char[] DIGITS = "0123456789".toCharArray();
	public static final char[] LETTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	/**
	 * Returns true if the input string is null or empty.
	 */
	public static boolean isNullOrEmpty(final String value) {
		return value == null || value.isEmpty();
	}

	/**
	 * Returns true if the input string is null or whitespace.
	 */
	public static boolean isNullOrWhitespace(final String value) {
		return isNullOrEmpty(value) || value.trim().isEmpty();
	}

	/**
	 * Returns true if the input string array contains a null or empty string.
	 */
	public static boolean isNullOrEmpty(final String... values) {

		if (values == null)
			return false;

		for (String s : values) {
			if (isNullOrEmpty(s))
				return true;
		}

		return false;
	}

	/**
	 * Returns true if the input string array contains a null or whitespace
	 * string.
	 */
	public static boolean isNullOrWhitespace(final String... values) {

		if (values == null)
			return false;

		for (String s : values) {
			if (isNullOrWhitespace(s))
				return true;
		}

		return false;
	}

	/**
	 * Trim input string.
	 */
	public static String trim(final String input) {

		if (StringUtils.isNullOrEmpty(input))
			return input;

		return input.trim();
	}

	/**
	 * Return random string contains characters.
	 */
	public static String random(final int length) {
		return random(length, false, false);
	}

	/**
	 * Return random string contains characters.
	 */
	public static String random(final int length, boolean letterOff, boolean digitOff) {

		if (length <= 0 || (letterOff && digitOff))
			return R.STR_EMPTY;

		char[] randomChars = new char[length];

		for (int i = 0; i < randomChars.length; i++) {
			if (letterOff) {
				randomChars[i] = DIGITS[IntegerUtils.random(DIGITS.length - 1)];
			} else if (digitOff) {
				randomChars[i] = LETTERS[IntegerUtils.random(LETTERS.length - 1)];
			} else {
				randomChars[i] = CHARS[IntegerUtils.random(CHARS.length - 1)];
			}
		}

		return new String(randomChars);
	}

	/**
	 * Concatenates a specified separator {@link String} between each element of
	 * a specified {@link String} collection, yielding a single concatenated
	 * {@link String}.
	 */
	public static String join(final String separator, final Collection<String> values) {

		if (values == null || values.size() == 0)
			return null;

		StringBuilder sb = new StringBuilder();

		for (String v : values) {

			if (isNullOrEmpty(v))
				continue;

			if (sb.length() > 0 && !isNullOrEmpty(separator)) {
				sb.append(separator);
			}

			sb.append(v);
		}

		return sb.toString();
	}

	/**
	 * Concatenates a specified separator {@link String} between each element of
	 * a specified {@link String} array, yielding a single concatenated
	 * {@link String}.
	 */
	public static String join(final String separator, final String... values) {
		return join(separator, Arrays.asList(values));
	}

	/**
	 * Concatenates a specified separator {@link String} between each element of
	 * a specified {@link Long} array, yielding a single concatenated
	 * {@link String}.
	 */
	public static String join(final String separator, final Long... values) {

		if (values == null)
			return R.STR_EMPTY;

		List<String> tmp = new ArrayList<String>(values.length);

		for (int i = 0; i < values.length; i++) {
			tmp.add(values[i].toString());
		}

		return join(separator, tmp);
	}

	/**
	 * Concatenates a specified separator {@link String} between each element of
	 * a specified {@link Long} collection, yielding a single concatenated
	 * {@link String}.
	 */
	public static String joinLong(final String separator, final Collection<Long> values) {

		if (values == null)
			return R.STR_EMPTY;

		List<String> tmp = new ArrayList<String>(values.size());

		for (long i : values) {
			tmp.add(String.valueOf(i));
		}

		return join(separator, tmp);
	}

	/**
	 * Splits this string around matches of the given separator. <br/>
	 * The result items are keep by appearance order.
	 */
	public static Set<String> split(final String input, final String regex, final StringSplitOptions option) {

		if (StringUtils.isNullOrEmpty(regex, input))
			return Collections.emptySet();

		String[] tmp = input.split(regex);

		Set<String> result = new LinkedHashSet<String>(tmp.length);

		for (String v : tmp) {

			if (option == StringSplitOptions.TrimAndRemoveEmptyEntries || option == StringSplitOptions.Trim)
				v = v.trim();

			if ((option == StringSplitOptions.RemoveEmptyEntries
					|| option == StringSplitOptions.TrimAndRemoveEmptyEntries) && v.isEmpty())
				continue;

			result.add(v);
		}

		return result;
	}

	/**
	 * Splits this string around matches of the given separator.
	 */
	public static List<String> split(final String input, final String regex, final StringSplitOptions option,
			final List<String> def) {

		if (StringUtils.isNullOrEmpty(regex, input))
			return def;

		String[] tmp = input.split(regex);

		List<String> result = new ArrayList<String>(tmp.length);

		for (String v : tmp) {

			if (option == StringSplitOptions.TrimAndRemoveEmptyEntries || option == StringSplitOptions.Trim)
				v = v.trim();

			if ((option == StringSplitOptions.RemoveEmptyEntries
					|| option == StringSplitOptions.TrimAndRemoveEmptyEntries) && v.isEmpty())
				continue;

			result.add(v);
		}

		return result;
	}

	/**
	 * Split string into to a set of number.
	 */
	public static Set<Long> toLongSet(final String value, final String regex, final Set<Long> def,
			final long minValueAccepted) {

		if (value == null || value.isEmpty()) {
			return def;
		}

		Set<String> strSet = split(value, regex, StringSplitOptions.TrimAndRemoveEmptyEntries);

		Set<Long> set = new LinkedHashSet<Long>(strSet.size());
		Long threhold = minValueAccepted - 1;

		for (String str : strSet) {
			long n = IntegerUtils.parse(str, threhold);
			if (n >= minValueAccepted) {
				set.add(n);
			}
		}

		return set;
	}

	/**
	 * Split string into to a set of number.
	 */
	public static Set<Long> toLongSet(final String value, final String regex, final Set<Long> def) {

		if (value == null || value.isEmpty())
			return Collections.emptySet();

		Set<String> strSet = split(value, regex, StringSplitOptions.TrimAndRemoveEmptyEntries);

		Set<Long> set = new LinkedHashSet<Long>(strSet.size());

		for (String str : strSet) {
			long n = IntegerUtils.parse(str, R.L_MINUS_99999999);
			if (n != R.L_MINUS_99999999) {
				set.add(n);
			}
		}

		return set;
	}

	/**
	 * Split string into to a set of number.
	 */
	public static Set<Long> toLongSet(final String value, final String regex) {
		return toLongSet(value, regex, null);
	}

	/**
	 * Split string into to a list of number. This method will ignore items
	 * whose value is -1.
	 */
	public static List<Long> toLongList(final String value, final String regex, final List<Long> def) {

		if (value == null || value.isEmpty())
			return def;

		Set<String> strSet = split(value, regex, StringSplitOptions.TrimAndRemoveEmptyEntries);

		List<Long> set = new ArrayList<Long>(strSet.size());

		for (String str : strSet) {
			long n = IntegerUtils.parse(str, R.L_MINUS_99999999);
			if (n != R.L_MINUS_99999999) {
				set.add(n);
			}
		}

		return set;
	}

	/**
	 * Split string into to a list of number. This method will ignore items
	 * whose value is -99999999.
	 */
	public static List<Integer> toIntList(final String value, final String regex, final List<Integer> def) {

		if (value == null || value.isEmpty())
			return def;

		Set<String> strSet = split(value, regex, StringSplitOptions.TrimAndRemoveEmptyEntries);

		List<Integer> set = new ArrayList<Integer>(strSet.size());

		for (String str : strSet) {
			int x = IntegerUtils.parse(str, R.D_MINUS_99999999);
			if (x != R.D_MINUS_99999999) {
				set.add(x);
			}
		}

		return set;
	}

	/**
	 * Split string into to a list of number.
	 */
	public static List<Long> toLongList(final String value, final String regex) {
		return toLongList(value, regex, null);
	}

	/**
	 * Split string into to a list of number.
	 */
	public static List<Integer> toIntList(final String value, final String regex) {
		return toIntList(value, regex, null);
	}

	private static final DecimalFormat FormatterWithDecimal = new DecimalFormat("$###,##0.00");
	private static final DecimalFormat Formatter = new DecimalFormat("$###,###");

	public static String formatCurrency(double amount) {

		String result = null;

		float epsilon = 0.004f; // 4 tenths of a cent

		if (Math.abs(Math.round(amount) - amount) < epsilon) {
			result = Formatter.format(amount);
		} else {
			result = FormatterWithDecimal.format(amount);
		}

		return result;
	}

	/**
	 * Count total number of numbers and letters in a string.
	 */
	public static int CountNumbersAndLetters(final String input) {
		if (isNullOrEmpty(input))
			return 0;

		int count = 0;

		for (char c : input.toCharArray()) {
			if ((c >= 48 && c <= 57) || (c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
				count += 1;
			}
		}

		return count;
	}
}