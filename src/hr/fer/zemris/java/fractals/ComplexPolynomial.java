package hr.fer.zemris.java.fractals;

import java.util.Arrays;
import java.util.Objects;

/**
 * This class offers basic operations for complex polynomial manipulation. The
 * class constructor for creating complex polynomials expects at least one
 * factor in form of array of {@linkplain Complex} numbers.
 * <p>
 * This class is an immutable model of complex polynomial, which means that
 * every time an operation is performed upon this complex polynomial, a new
 * complex polynomial object is returned.
 *
 * @author Mario Bobic
 */
public class ComplexPolynomial {

	/** Lowest decimal number value until it is regarded as zero. */
	private static final double ZERO_LIMIT =
			Double.parseDouble("1E-" + Complex.DEFAULT_DECPLACES);

	/** The polynomial factors. */
	private final Complex[] factors;
	
	/**
	 * Constructs an instance of ComplexPolynomial with the specified
	 * <tt>factors</tt>. The factors are expected to be sorted by degree, where
	 * the factor at index 0 is by x<sup>0</sup>, factor at index 1 is by x
	 * <sup>1</sup> etc.
	 * <p>
	 * Throws {@linkplain NullPointerException} if the specified
	 * <tt>factors</tt> is <tt>null</tt> or the array contains <tt>null</tt>.
	 * <p>
	 * Throws {@linkplain IllegalArgumentException} if the specified
	 * <tt>factors</tt> array does not contain at least 1 element.
	 * 
	 * @param factors factors of the polynomial
	 * @throws NullPointerException if factors is null or contains null
	 * @throws IllegalArgumentException if <tt>factors.length == 0</tt>
	 */
	public ComplexPolynomial(Complex ...factors) {
		checkArgument(factors);
		
		this.factors = processFactors(factors);
	}

	/**
	 * Throws {@linkplain NullPointerException} if the specified
	 * <tt>factors</tt> is <tt>null</tt> or the array contains <tt>null</tt>.
	 * <p>
	 * Throws {@linkplain IllegalArgumentException} if the specified
	 * <tt>factors</tt> array does not contain at least 1 element.
	 * 
	 * @param factors array of factors
	 * @throws NullPointerException if factors is null or contains null
	 * @throws IllegalArgumentException if <tt>factors.length == 0</tt>
	 */
	private void checkArgument(Complex[] factors) {
		Objects.requireNonNull(factors, "Factors must not be null.");
		
		if (factors.length == 0) {
			throw new IllegalArgumentException("Factors must contain at least 1 factor.");
		}
		
		for (int i = 0; i < factors.length; i++) {
			Objects.requireNonNull(factors[i], "Factors contains null at index " + i);
		}
	}
	
	/**
	 * Processes the factors by removing the highest ones if they are equal to
	 * {@linkplain Complex#ZERO}.
	 * 
	 * @param factors array of factors to be processed
	 * @return the processed array of factors
	 */
	private Complex[] processFactors(Complex[] factors) {
		int zeroCount = 0;
		
		// start from end and exclude z^0
		for (int i = factors.length - 1; i >= 1; i--) {
			if (factors[i].equals(Complex.ZERO)) {
				zeroCount++;
			} else {
				break;
			}
		}
		
		if (zeroCount == 0) {
			return factors;
		} else {
			int endIndex = factors.length - zeroCount;
			return Arrays.copyOfRange(factors, 0, endIndex);
		}
	}

	/**
	 * Returns the order of this polynomial.
	 * <p>
	 * The degree of a polynomial is the highest degree of its terms when the
	 * polynomial is expressed in its canonical form consisting of a linear
	 * combination of monomials.
	 * <p>
	 * e.g. for (7+2i)z<sup>3</sup> + 2z<sup>2</sup> + 5z + 1 it returns 3.
	 * 
	 * @return the order of this polynomial
	 */
	public short order() {
		return (short) (factors.length - 1);
	}
	
	/**
	 * Multiplies this ComplexPolynomial with another ComplexPolynomial and
	 * returns a new object with the multiplication result.
	 * 
	 * @param p a ComplexPolynomial to be multiplied with
	 * @return a result of multiplication complex polynomials
	 * @throws NullPointerException if <tt>p == null</tt>
	 */
	public ComplexPolynomial multiply(ComplexPolynomial p) {
		Complex[] multiplied = getComplexArray(order() + p.order() + 1);
		
		for (int i = 0; i < factors.length; i++) {
			for (int j = 0; j < p.factors.length; j++) {
				// multiplied[i+j] += factors[i] * p.factors[j];
				multiplied[i+j] = multiplied[i+j].add(factors[i].multiply(p.factors[j]));
			}
		}
		
		return new ComplexPolynomial(multiplied);
	}
	
	/**
	 * Returns an array filled with complex zeroes, that is with
	 * {@linkplain Complex#ZERO} values.
	 * 
	 * @param length the length of the new array
	 * @return an array filled with complex zeroes
	 */
	private static Complex[] getComplexArray(int length) {
		Complex[] array = new Complex[length];
		
		for (int i = 0; i < length; i++) {
			array[i] = Complex.ZERO;
		}
		
		return array;
	}
	
	/**
	 * Computes the first derivative of this polynomial and returns a new object
	 * of the derivative.
	 * <p>
	 * For an example, for polynomial <tt>(7 + 2i)z^3 + 2z^2 + 5z + 1</tt>,
	 * derivative <tt>(21 + 6i)z^2 + 4z + 5</tt> is returned.
	 * 
	 * @return the first derivative of this polynomial
	 */
	public ComplexPolynomial derive() {
		if (factors.length == 1) { // deriving a constant
			return new ComplexPolynomial(Complex.ZERO);
		}
		
		Complex[] derivative = new Complex[factors.length-1];
		
		for (int i = 0; i < factors.length-1; i++) {
			Complex factor = factors[i+1];
			derivative[i] = factor.multiply(new Complex(i+1, 0));
		}
		
		return new ComplexPolynomial(derivative);
	}
	
	/**
	 * Computes polynomial value at the given point <tt>z</tt> and returns the
	 * complex result.
	 * 
	 * @param z the point for which the polynomial value is computed
	 * @return the computed polynomial value
	 * @throws NullPointerException if <tt>z == null</tt>
	 */
	public Complex apply(Complex z) {
		Complex result = Complex.ZERO;
		
		for (int i = 0; i < factors.length; i++) {
			Complex zValue = z.power(i);
			result = result.add(factors[i].multiply(zValue));
		}
		
		return result;
	}
	
	/**
	 * Returns a pretty string representation of this complex polynomial.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (int i = factors.length - 1; i >= 0; i--) {
			if (factors[i].equals(Complex.ZERO)) continue;
			
			String pretty = prettify(factors[i], i);
			switch (pretty.charAt(0)) {
			case '-':
				sb.append(" - ").append(pretty.substring(1));
				break;
			default: // bracket-opening or number
				sb.append(" + ").append(pretty);
			}
		}
		
		// delete the two spaces
		sb.deleteCharAt(2);
		sb.deleteCharAt(0);
		
		if (sb.charAt(0) == '+') {
			sb.deleteCharAt(0);
		}
		
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(factors);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ComplexPolynomial))
			return false;
		ComplexPolynomial other = (ComplexPolynomial) obj;
		if (!Arrays.equals(factors, other.factors))
			return false;
		return true;
	}

	/**
	 * Prettifies the specified <tt>factor</tt> depending on the factor index
	 * <tt>i</tt> (which represents the factor degree).
	 * <p>
	 * The prettification is performed with the following cases:
	 * <ul>
	 * <li>if the factor index is <tt>0</tt>, the same factor is returned,
	 * <li>else if the factor index is <tt>1</tt>, and if the factor is 1 or -1,
	 * the "1" will not be contained in the string, as the sign will precede the
	 * variable z,
	 * <li>else the factor index is greater than <tt>1</tt>, the same rules
	 * apply for factors 1 and -1, but the variable z will be raised to the
	 * specified index <tt>i</tt>.
	 * </ul>
	 * If the factor has both real and imaginary part, it is surrounded by
	 * brackets: <tt>(realPart op imagPart)</tt>.
	 * 
	 * @param factor factor to be prettified
	 * @param i the index of the factor
	 * @return a pretty version of the factor
	 */
	private static String prettify(Complex factor, int i) {
		StringBuilder sb = new StringBuilder();
		
		if (i >= 1) {
			sb.append(equalsOne(factor) ? sign(factor) : brackets(factor));
			sb.append('z');
			if (i > 1)
				sb.append('^').append(i);
		} else {
			sb.append(factor);
		}
		
		return sb.toString();
	}

	/**
	 * Returns true if the specified <tt>factor</tt> equals
	 * {@linkplain Complex#ONE} or {@linkplain Complex#ONE_NEG}. False
	 * otherwise.
	 * 
	 * @param factor factor to be tested
	 * @return true if the factor is real and equals one or negative one
	 */
	private static boolean equalsOne(Complex factor) {
		return sign(factor) != null;
	}
	
	/**
	 * Returns an empty string if the <tt>factor</tt> equals
	 * {@linkplain Complex#ONE} and a minus sign ("-") if it equals
	 * {@linkplain Complex#ONE_NEG}.
	 * <p>
	 * Returns <tt>null</tt> if <tt>factor</tt> is neither.
	 * 
	 * @param factor factor whose sign is to be returned
	 * @return an empty string or a minus sign, or <tt>null</tt>
	 */
	private static String sign(Complex factor) {
		if (factor.equals(Complex.ONE))		return "";
		if (factor.equals(Complex.ONE_NEG))	return "-";
		
		return null;
	}

	/**
	 * Adds brackets to the specified <tt>factor</tt> if necessary, that is if
	 * both real and imaginary part of the complex number are greater than zero.
	 * 
	 * @param factor factor to which the brackets may be added
	 * @return the factor with brackets, if necessary
	 */
	private static String brackets(Complex factor) {
		// If both absolutes are greater than zero
		if (Math.abs(factor.getReal()) >= ZERO_LIMIT &&
			Math.abs(factor.getImag()) >= ZERO_LIMIT) {
			// If both are negative, extract the minus
			if (factor.getReal() < 0 && factor.getImag() < 0) {
				return "-(" + factor.negate() + ")";
			} else {
				return "(" + factor + ")";
			}
		}
		
		return factor.toString();
	}
	
}
