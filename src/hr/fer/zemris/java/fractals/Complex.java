package hr.fer.zemris.java.fractals;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class offers basic operations for complex number manipulation. There are
 * two class constructors for creating complex numbers:
 * <ul>
 * <li>a default constructor that sets both real and imaginary part to 0 and
 * <li>a constructor that accepts two arguments, the <tt>realPart</tt> and
 * <tt>imaginaryPart</tt>
 * </ul>
 * This class is an immutable model of complex number, which means that every
 * time an operation is performed upon this complex number, a new complex number
 * object is returned.
 *
 * @author Mario Bobic
 */
public class Complex {
	
	/** This class instance counter. */
	static long instanceCounter = 0;

	/** The constant zero of a complex number. */
	public static final Complex ZERO = new Complex(0, 0);
	/** The constant one of a complex number. */
	public static final Complex ONE = new Complex(1, 0);
	/** The constant negative one of a complex number. */
	public static final Complex ONE_NEG = new Complex(-1, 0);
	/** The constant i of a complex number. */
	public static final Complex IM = new Complex(0, 1);
	/** The constant negative i of a complex number. */
	public static final Complex IM_NEG = new Complex(0, -1);
	
	/**
	 * Default maximum number of decimal places of real and imaginary part. This
	 * constant is used by the {@linkplain #toString()} method to round the
	 * complex number to three decimals.
	 */
	public static final int DEFAULT_DECPLACES = 3;
	
	/**
	 * The default cached decimal formatter, linked with
	 * {@linkplain #DEFAULT_DECPLACES}.
	 */
	private static final DecimalFormat DEFAULT_FORMATTER = new DecimalFormat("#.###");
	
	/** Lowest decimal number value until it is regarded as zero. */
	private static final double ZERO_LIMIT = 1E-20;
	/** Offset that may exist while comparing the equality of two doubles. */
	private static final double EQUALS_LIMIT = 1E-6;
	
	/** Real part of the complex number. */
	private final double real;
	/** Imaginary part of the complex number. */
	private final double imag;
	
	/**
	 * Constructs a new instance of a ComplexNumber,
	 * with the real and imaginary part both set to 0.
	 */
	public Complex() {
		this(0, 0);
	}
	
	/**
	 * Constructs a new instance of a ComplexNumber,
	 * with the specified parameters.
	 * 
	 * @param re real part of the complex number
	 * @param im imaginary part of the complex number
	 */
	public Complex(double re, double im) {
		real = re;
		imag = im;
		
		instanceCounter++;
	}
	
	/**
	 * Returns a new instance of a ComplexNumber created from the given
	 * <code>magnitude</code> and <code>angle</code> of a polar form.
	 * 
	 * @param magnitude magnitude of the polar form
	 * @param angle angle of the polar form
	 * @return a new instance of a complex number with the given parameters
	 * @throws IllegalArgumentException if <tt>magnitude &lt; 0 </tt>
	 */
	private static Complex fromMagnitudeAndAngle(double magnitude, double angle) {
		if (magnitude < 0) {
			throw new IllegalArgumentException("Magnitude must not be negative.");
		}
		
		return new Complex(magnitude*Math.cos(angle), magnitude*Math.sin(angle));
	}
	
	/**
	 * Returns the angle of a complex number specified by the real and imaginary
	 * parameters from the polar form representation.
	 * 
	 * @param re real part of the complex number
	 * @param im imaginary part of the complex number
	 * @return the angle of a complex number from the polar form
	 */
	private static double angle(double re, double im) {
		return Math.atan2(im, re);
	}
	
	/**
	 * Parses the given string into a complex number and returns it. The string
	 * is expected to be in an adequate format for parsing. Here are some
	 * examples of the expected string:<br>
	 * <tt>"3.51", "-3.17", "-2.71i", "i", "1", "-2.71-3.15i", "2 + 3i"</tt>
	 * <br>
	 * The given string will not be parsed if the format of the complex number
	 * has swapped real and imaginary part places. Instead, a
	 * {@linkplain NumberFormatException} will be thrown. In case of an empty
	 * string or <tt>null</tt>, an {@linkplain IllegalArgumentException}
	 * will be thrown.
	 * 
	 * @param s string to be parsed
	 * @return an instance of ComplexNumber
	 * @throws IllegalArgumentException if the given string is null or empty
	 * @throws NumberFormatException if the string cannot be parsed
	 */
	public static Complex parse(String s) {
		if (s == null || s.trim().isEmpty()) {
			throw new IllegalArgumentException("Cannot parse empty string.");
		}
		
		/* No need for whitespaces. Replace commas with dots. */
		s = s.replaceAll("\\s+", "");
		s = s.replace(",", ".");

		// split on + or - using negative lookbehind
		String[] parts = s.split("(?<!^)(\\+|-)", 2);
		if (parts.length == 2) {
			boolean secondNegative = s.charAt(parts[0].length()) == '-';
			Complex c1 = parseOnePart(parts[0]);
			Complex c2 = parseOnePart(parts[1]);
			return secondNegative ? c1.sub(c2) : c1.add(c2);
		} else {
			return parseOnePart(s);
		}
	}
	
	/**
	 * Parses the string <tt>s</tt> expecting it to be only one part of the
	 * complex number, real or imaginary. Returns a complex number with only
	 * either real or imaginary part.
	 * 
	 * @param s string to be parsed as a part of a complex number
	 * @return a complex number with only one part
	 * @throws NumberFormatException if the string cannot be parsed
	 */
	private static Complex parseOnePart(String s) {
		s = s.trim();
		Complex result;
		
		if (s.equals("i") || s.equals("+i")) {
			result = Complex.IM;
		} else if (s.equals("-i")) {
			result = Complex.IM_NEG;
			
		} else if (s.startsWith("i") || s.endsWith("i")
				|| s.startsWith("+i") || s.startsWith("-i")) {
			double imag = Double.parseDouble(s.replaceFirst("i", ""));
			result = new Complex(0.0, imag);
		} else {
			double real = Double.parseDouble(s);
			result = new Complex(real, 0.0);
		}
		
		return result;
	}
	
	/**
	 * Returns the real part of this complex number.
	 * 
	 * @return the real part of this complex number
	 */
	public double getReal() {
		return real;
	}

	/**
	 * Returns the imaginary part of this complex number.
	 * 
	 * @return the imaginary part of this complex number
	 */
	public double getImag() {
		return imag;
	}
	
	/**
	 * Returns the absolute value of this complex number by formula
	 * <tt>sqrt(real<sup>2</sup>+imag<sup>2</sup>)</tt>.
	 * 
	 * @return the absolute value (modulus) of this complex number
	 */
	public double module() {
		return Math.hypot(real, imag);
	}
	
	/**
	 * Performs multiplication of the two complex numbers by formula
	 * <tt>this * c</tt> and returns the result.
	 * 
	 * @param c complex number to be multiplied with this one
	 * @return the product of the two complex numbers
	 * @throws NullPointerException if <tt>c == null</tt>
	 */
	public Complex multiply(Complex c) {
		double realMul = real*c.real - imag*c.imag;
		double imagMul = imag*c.real + real*c.imag;
		return new Complex(realMul, imagMul);
	}
	
	/**
	 * Performs division of the two complex numbers by formula
	 * <tt>this / c</tt> and returns the result.
	 * <p>
	 * Special case of this method is when the complex denominator is
	 * <tt>0+0i</tt>, then the {@linkplain ArithmeticException} is thrown.
	 * 
	 * @param c complex number to be divided with this one
	 * @return the quotient of the two complex numbers
	 * @throws ArithmeticException if <tt>c</tt> is <tt>0+0i</tt>
	 * @throws NullPointerException if <tt>c == null</tt>
	 */
	public Complex divide(Complex c) {
		Complex conjugated = new Complex(c.real, -c.imag);
		
		Complex numerator = this.multiply(conjugated);
		double denominator = c.multiply(conjugated).real;
		if (Math.abs(denominator) < ZERO_LIMIT) {
			throw new ArithmeticException("Division by zero: " + c);
		}
		
		double realDiv = numerator.real / denominator;
		double imagDiv = numerator.imag / denominator;
		
		return new Complex(realDiv, imagDiv);
	}
	
	/**
	 * Performs addition of the two complex numbers by formula
	 * <tt>this + c</tt> and returns the result.
	 * 
	 * @param c complex number to be added to this one
	 * @return the sum of the two complex numbers
	 * @throws NullPointerException if <tt>c == null</tt>
	 */
	public Complex add(Complex c) {
		return new Complex(real+c.real, imag+c.imag);
	}
	
	/**
	 * Performs subtraction of the two complex numbers by formula
	 * <tt>this - c</tt> and returns the result.
	 * 
	 * @param c complex number to be subtracted from this one
	 * @return the difference of the two complex numbers
	 * @throws NullPointerException if <tt>c == null</tt>
	 */
	public Complex sub(Complex c) {
		return new Complex(real-c.real, imag-c.imag);
	}
	
	/**
	 * Returns the negative value of this complex number by formula
	 * <tt>-this</tt>.
	 * 
	 * @return the negation of this complex number
	 */
	public Complex negate() {
		return new Complex(-real, -imag);
	}
	
	/**
	 * Performs the power operation of this complex number and n by formula
	 * <tt>this<sup>n</sup></tt> and returns the result.
	 * <p>
	 * If n is less than 0, an {@linkplain IllegalArgumentException} is thrown.
	 * 
	 * @param n the power n to which the complex number is to be raised
	 * @return the result of the power operation
	 * @throws IllegalArgumentException if <code>n &lt; 0</code>
	 */
	public Complex power(int n) {
		if (n < 0) {
			throw new IllegalArgumentException("n must be greater than or equal to 0.");
		}
		
		double magnitude = Math.pow(module(), n);
		double angle = n*angle(real, imag);
		
		return fromMagnitudeAndAngle(magnitude, angle);
	}
	
	/**
	 * Calculates and returns a list of root solutions calculated by formula<br>
	 * <tt>r<sup>1/n</sup> [cos((&phi; + 2k&pi;)/n) + isin((&phi; + 2k&pi;)/n)]</tt>
	 * 
	 * @param n root to be calculated
	 * @return a list of root solutions
	 * @throws IllegalArgumentException if <code>n &lt;= 0</code>
	 */
	public List<Complex> root(int n) {
		if (n <= 0) {
			throw new IllegalArgumentException("n must be greater than 0.");
		}
		
		double magnitude = Math.pow(module(), 1.0 / n);
		double angle = angle(real, imag);
		
		List<Complex> solutions = new ArrayList<>(n);
		for (int k = 0; k < n; k++) {
			solutions.add(fromMagnitudeAndAngle(magnitude, (angle + 2*k*Math.PI) / n));
		}
		
		return solutions;
	}
	
	/**
	 * Returns the distance between this complex number and complex number
	 * <tt>c</tt> by calculating the distance between each of the coordinates
	 * and using the {@linkplain #module()} method.
	 * 
	 * @param c complex number from whose distance is to be calculated
	 * @return the distance between this complex number and <tt>c</tt>
	 * @throws NullPointerException if <tt>c == null</tt>
	 */
	public double distance(Complex c) {
		return new Complex(real-c.real, imag-c.imag).module();
	}
	
	/**
	 * Returns a formatted string of this complex number with real and imaginary
	 * part rounded to three decimal places <b>at most</b>. The returned string
	 * of a complex number <tt>2.570 + 3.141593i</tt> will look like this:
	 * <tt>2.57 + 3.142i</tt>
	 * <p>
	 * For a more detailed view of the string representation, check the
	 * {@linkplain #toString(int)} method.
	 */
	@Override
	public String toString() {
		return toString(DEFAULT_DECPLACES);
	}
	
	/**
	 * Returns a formatted string of this complex number with real and imaginary
	 * part rounded to <tt>n</tt> decimal places <b>at most</b>. The returned
	 * string of a complex number <tt>2.570 + 3.141593i</tt> and <tt>n=5</tt>
	 * will look like this: <tt>2.57 + 3.14159i</tt>
	 * <p>
	 * Further examples of the string representation are listed in a table below:
	 * <table border="1">
	 * <tr><th>Method call</th><th>String result</th></tr>
	 * <tr><td>Complex.ZERO.toString(3);</td><td>"0"</td></tr>
	 * <tr><td>Complex.ONE.toString(1);</td><td>"1"</td></tr>
	 * <tr><td>Complex.ONE_NEG.toString(9);</td><td>"-1"</td></tr>
	 * <tr><td>Complex.IM.toString(3);</td><td>"i"</td></tr>
	 * <tr><td>Complex.IM_NEG.toString(0);</td><td>"-i"</td></tr>
	 * <tr><td><br/></td><td></td></tr>
	 * <tr><td>new Complex(-1, -1).toString(3);</td><td>"-1 - i"</td></tr>
	 * <tr><td>new Complex(2.22, 3.14).toString(3);</td><td>"2.22 + 3.14i"</td></tr>
	 * <tr><td>new Complex(3.1415926535, -22.0 / 7).toString(3);</td><td>"3.142 - 3.143i"</td></tr>
	 * <tr><td>new Complex(-12.3456789, 0).toString(3);</td><td>"-12.346"</td></tr>
	 * <tr><td>new Complex(0.00021, 0.000044).toString(3);</td><td>"0"</td></tr>
	 * <tr><td>new Complex(2.570, 3.141593).toString(5);</td><td>"2.57 + 3.14159i"</td></tr>
	 * <tr><td>new Complex(2.570, 3.141593).toString(0);</td><td>"3 + 3i"</td></tr>
	 * </table>
	 * 
	 * @param n most decimal places to which this complex number will be rounded
	 * @return a string representation of this complex number
	 * @throws IllegalArgumentException if <tt>n &lt; 0</tt>
	 */
	public String toString(int n) {
		if (n < 0) {
			throw new IllegalArgumentException("Number of decimals must not be negative.");
		}

		final double LIMIT = Double.parseDouble("1E-" + n);
		DecimalFormat df = getFormatter(n);
		
		/* If imaginary is practically zero */
		if (Math.abs(imag) < LIMIT) {
			return df.format(real);
		}
		
		/* If real is practically zero */
		if (Math.abs(real) < LIMIT) {
			return formatImaginary(imag, df);
		}
		
		if (imag < 0) {
			return df.format(real) + " - " + formatImaginary(-imag, df);
		} else {
			return df.format(real) + " + " + formatImaginary(imag, df);
		}
	}
	
	/**
	 * Returns a {@linkplain DecimalFormat} object that rounds decimal numbers
	 * to <tt>n</tt> decimal places. If the specified number of decimal places
	 * <tt>n</tt> is different than {@linkplain #DEFAULT_DECPLACES}, a new
	 * <tt>DecimalFormat</tt> object is returned, else a cached one is returned.
	 * 
	 * @param n the number of decimal places to be considered
	 * @return a decimal formatter with a pattern of <tt>n</tt> decimal places
	 */
	private static DecimalFormat getFormatter(int n) {
		if (n == DEFAULT_DECPLACES) {
			return DEFAULT_FORMATTER;
		}
		
		StringBuilder pattern = new StringBuilder(n+2);
		
		/* Create the decimal format pattern */
		pattern.append("#").append(n == 0 ? "" : ".");
		for (int i = 0; i < n; i++) {
			pattern.append('#');
		}
		
		return new DecimalFormat(pattern.toString());
	}
	
	/**
	 * Checks if the absolute value of the specified <tt>imag</tt> double value
	 * equals <tt>1.0</tt>, considering the {@linkplain #EQUALS_LIMIT} constant,
	 * or more formally if <tt>Math.abs(imag) - IM.getImag() &lt; EQUALS_LIMIT</tt>.
	 * <p>
	 * If the condition above results in <tt>true</tt>, this method returns "i",
	 * else it returns the <tt>imag</tt> value formatted with the specified
	 * decimal format <tt>df</tt> followed by the imaginary unit "i".
	 * 
	 * @param imag the imaginary value whose string representation is to be returned
	 * @param df a decimal format to format the imaginary double value
	 * @return the formatted string representation of the imaginary value
	 */
	private static String formatImaginary(double imag, DecimalFormat df) {
		if (Math.abs(Math.abs(imag) - IM.getImag()) < EQUALS_LIMIT) {
			return imag < 0 ? "-i" : "i";
		} else {
			return df.format(imag) + "i";
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(imag);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(real);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * Returns true if the specified <tt>obj</tt> is an instance of
	 * <tt>Complex</tt> considering only the first 6 digits after the decimal
	 * point of the <tt>real</tt> and <tt>imaginary</tt> part of the complex.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Complex))
			return false;
		Complex other = (Complex) obj;
		if (Math.abs(real - other.real) > EQUALS_LIMIT)
			return false;
		if (Math.abs(imag - other.imag) > EQUALS_LIMIT)
			return false;
		return true;
	}
	
}
