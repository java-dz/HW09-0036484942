package hr.fer.zemris.java.fractals;

import java.util.Objects;

/**
 * This class offers basic operations for complex rooted polynomial
 * manipulation. The class constructor for creating complex root solutions
 * expects at least one root in form of array of {@linkplain Complex} numbers.
 * <p>
 * This class is an immutable model of complex rooted polynomial, which means
 * that every time an operation is performed upon this complex rooted
 * polynomial, a new complex rooted polynomial object is returned.
 *
 * @author Mario Bobic
 */
public class ComplexRootedPolynomial {
	
	/** The polynomial roots. */
	private final Complex[] roots;

	/**
	 * Constructs an instance of ComplexPolynomial with the specified
	 * <tt>roots</tt>.
	 * <p>
	 * Throws {@linkplain NullPointerException} if the specified
	 * <tt>roots</tt> is <tt>null</tt> or the array contains <tt>null</tt>.
	 * <p>
	 * Throws {@linkplain IllegalArgumentException} if the specified
	 * <tt>roots</tt> array does not contain at least 1 element.
	 * 
	 * @param roots roots of the polynomial
	 * @throws NullPointerException if roots is null or contains null
	 * @throws IllegalArgumentException if <tt>roots.length == 0</tt>
	 */
	public ComplexRootedPolynomial(Complex ...roots) {
		checkArgument(roots);
		
		this.roots = roots;
	}
	
	/**
	 * Throws {@linkplain NullPointerException} if the specified
	 * <tt>roots</tt> is <tt>null</tt> or the array contains <tt>null</tt>.
	 * <p>
	 * Throws {@linkplain IllegalArgumentException} if the specified
	 * <tt>roots</tt> array does not contain at least 1 element.
	 * 
	 * @param roots array of roots
	 * @throws NullPointerException if roots is null or contains null
	 * @throws IllegalArgumentException if <tt>roots.length == 0</tt>
	 */
	private void checkArgument(Complex[] roots) {
		Objects.requireNonNull(roots, "Roots must not be null.");
		
		if (roots.length == 0) {
			throw new IllegalArgumentException("Roots must contain at least 1 root.");
		}
		
		for (int i = 0; i < roots.length; i++) {
			Objects.requireNonNull(roots[i], "Roots contains null at index " + i);
		}
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
		Complex result = Complex.ONE;
		
		for (Complex root : roots) {
			Complex el = z.sub(root);
			result = result.multiply(el);
		}
		
		return result;
	}
	
	/**
	 * Converts this representation to {@linkplain ComplexPolynomial} type by
	 * multiplying every member of the complex rooted polynomial.
	 * 
	 * @return a complex polynomial object
	 */
	public ComplexPolynomial toComplexPolynom() {
		ComplexPolynomial result = new ComplexPolynomial(Complex.ONE);
		
		for (Complex root : roots) {
			ComplexPolynomial member = new ComplexPolynomial(
				new Complex[] {
					root.negate(),
					Complex.ONE
			});
			result = result.multiply(member);
		}
		
		return result;
	}
	
	/**
	 * Finds and returns the index of closest root to the specified complex
	 * number <tt>z</tt> that is within the threshold.
	 * <p>
	 * If there is no such root, <tt>-1</tt> is returned.
	 * 
	 * @param z complex number whose closest root index is to be returned
	 * @param threshold the maximum allowed distance
	 * @return the index of closest root to the specified complex number z
	 * @throws NullPointerException if <tt>z == null</tt>
	 */
	public int indexOfClosestRootFor(Complex z, double threshold) {
		double minDistance = z.distance(roots[0]);
		int index = 0;
		
		for (int i = 1; i < roots.length; i++) {
			double distance = z.distance(roots[i]);
			if (distance < minDistance) {
				minDistance = distance;
				index = i;
			}
		}
		
		if (minDistance > threshold) {
			index = -1;
		}
		
		return index;
	}
	
	/**
	 * Returns a string representation of this complex rooted polynomial object.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Complex root : roots) {
			sb.append(asMember(root));
		}
		
		return sb.toString();
	}
	
	/**
	 * Returns the specified <tt>root</tt> in the form of a complex rooted
	 * polynomial member. This means that the specified complex <tt>root</tt>
	 * will be negated and preceded by "(z" and followed by ")"
	 * 
	 * @param root the complex root to be returned in the form of a member
	 * @return root in the form of a complex rooted polynomial member
	 */
	private static String asMember(Complex root) {
		return "(z" + negateRoot(root) + ")";
	}
	
	/**
	 * Returns a string representation of the specified <tt>root</tt> negated,
	 * where one of two of the following scenarios may occur:
	 * <ol>
	 * <li>if the string representation of the root negated has a leading minus,
	 * the leading minus is replaced by space-minus-space, resulting in a " - ",
	 * <li>else the root negated has a positive leading value, so a plus symbol
	 * is added, surrounded by spaces, resulting in a " + ".
	 * </ol>
	 * 
	 * @param root root to be negated and returned as a string
	 * @return a string representation of the root negated
	 */
	private static String negateRoot(Complex root) {
		String rootStr = root.negate().toString();
		if (rootStr.startsWith("-")) {
			rootStr = " - " + rootStr.substring(1);
		} else {
			rootStr = " + " + rootStr;
		}
		
		return rootStr;
	}
	
}
