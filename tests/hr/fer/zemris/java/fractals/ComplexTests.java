package hr.fer.zemris.java.fractals;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class ComplexTests {
	
	private static final double DOUBLE_TOLERATION = 1E-6;

	/* ------------------------------ Public method tests ------------------------------ */
	
	@Test
	public void testDefaultConstructor() {
		Complex c = new Complex();
		
		assertEquals(0, c.getReal(), DOUBLE_TOLERATION);
		assertEquals(0, c.getImag(), DOUBLE_TOLERATION);
	}
	
	@Test
	public void testConstructor() {
		Complex c = new Complex(-1, 0.5);
		
		assertEquals(-1, c.getReal(), DOUBLE_TOLERATION);
		assertEquals(0.5, c.getImag(), DOUBLE_TOLERATION);
	}
	
	@Test
	public void testModule() {
		Complex c = new Complex(-1, 2);
		
		assertEquals(Math.sqrt(5), c.module(), DOUBLE_TOLERATION);
	}
	
	@Test
	public void testMultiply1() {
		Complex c1 = new Complex(-1, 2);
		Complex c2 = new Complex(2, -1);
		
		Complex actual = c1.multiply(c2);
		Complex expected = new Complex(0, 5);
		
		assertComplexEquals(expected, actual);
	}
	
	@Test
	public void testMultiply2() {
		Complex c = new Complex(-1, -1);
		
		Complex actual = c.multiply(c);
		Complex expected = new Complex(0, 2);
		
		assertComplexEquals(expected, actual);
	}
	
	@Test
	public void testDivide1() {
		Complex c1 = new Complex(-1, 2);
		Complex c2 = new Complex(2, 1);
		
		Complex actual = c1.divide(c2);
		Complex expected = Complex.IM;
		
		assertComplexEquals(expected, actual);
	}
	
	@Test
	public void testDivide2() {
		Complex c = new Complex(-1, -1);
		
		Complex actual = c.divide(c);
		Complex expected = Complex.ONE;
		
		assertComplexEquals(expected, actual);
	}
	
	@Test(expected=Exception.class)
	public void testDivideException() {
		Complex c = new Complex(0, 0);
		// must throw
		c.divide(c);
	}
	
	@Test
	public void testAdd1() {
		Complex c1 = new Complex(1, 1);
		Complex c2 = new Complex(-1, -1);
		
		Complex actual = c1.add(c2);
		Complex expected = Complex.ZERO;
		
		assertComplexEquals(expected, actual);
	}
	
	@Test
	public void testAdd2() {
		Complex c1 = Complex.IM_NEG;
		Complex c2 = Complex.ZERO;
		
		Complex actual = c1.add(c2);
		Complex expected = Complex.IM_NEG;
		
		assertComplexEquals(expected, actual);
	}
	
	@Test
	public void testSub1() {
		Complex c1 = Complex.ONE;
		Complex c2 = Complex.IM;
		
		Complex actual = c1.sub(c2);
		Complex expected = new Complex(1, -1);
		
		assertComplexEquals(expected, actual);
	}
	
	@Test
	public void testSub2() {
		Complex c1 = Complex.IM_NEG;
		Complex c2 = Complex.ZERO;
		
		Complex actual = c1.sub(c2);
		Complex expected = Complex.IM_NEG;
		
		assertComplexEquals(expected, actual);
	}
	
	@Test
	public void testNegate1() {
		Complex c = Complex.ZERO;
		
		Complex actual = c.negate();
		Complex expected = Complex.ZERO;
		
		assertComplexEquals(expected, actual);
		assertComplexEquals(expected.negate(), actual.negate());
	}
	
	@Test
	public void testNegate2() {
		Complex c = Complex.ONE;
		
		Complex actual = c.negate();
		Complex expected = Complex.ONE_NEG;
		
		assertComplexEquals(expected, actual);
		assertComplexEquals(expected.negate(), actual.negate());
	}
	
	@Test
	public void testNegate3() {
		Complex c = Complex.IM;
		
		Complex actual = c.negate();
		Complex expected = Complex.IM_NEG;
		
		assertComplexEquals(expected, actual);
		assertComplexEquals(expected.negate(), actual.negate());
	}
	
	@Test
	public void testPower1() {
		Complex c = new Complex(-1, 2);

		Complex actual = c.power(3);
		Complex expected = new Complex(11, -2);

		assertComplexEquals(expected, actual);
	}

	@Test
	public void testPower2() {
		Complex c = new Complex(-1, -1);

		Complex actual = c.power(2);
		Complex expected = new Complex(0, 2);

		assertComplexEquals(expected, actual);
	}
	
	@Test
	public void testPower3() {
		Complex c = new Complex(-5, 12);

		Complex actual = c.power(0);
		Complex expected = Complex.ONE;

		assertComplexEquals(expected, actual);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testPowerIllegalArgument() {
		// must throw
		Complex.ONE.power(-1);
	}

	@Test
	public void testRoot1() {
		Complex c = new Complex(3, 4);

		List<Complex> actual = c.root(2);
		List<Complex> expected = new ArrayList<>();
		
		expected.add(new Complex(2, 1));
		expected.add(new Complex(-2, -1));
		
		assertListEquals(expected, actual);
	}
	
	@Test
	public void testRoot2() {
		Complex c = new Complex(1, 1);

		List<Complex> actual = c.root(5);
		List<Complex> expected = new ArrayList<>();
		
		double magnitude = Math.pow(2, 1.0/10);
		expected.add(fromMagnitudeAndAngle(magnitude, Math.PI/20));
		expected.add(fromMagnitudeAndAngle(magnitude, 9*Math.PI/20));
		expected.add(fromMagnitudeAndAngle(magnitude, 17*Math.PI/20));
		expected.add(fromMagnitudeAndAngle(magnitude, -15*Math.PI/20));
		expected.add(fromMagnitudeAndAngle(magnitude, -7*Math.PI/20));
		
		assertListEquals(expected, actual);
	}
	
	@Test
	public void testDistance1() {
		Complex c1 = new Complex(0, 0);
		Complex c2 = new Complex(1, 0);

		double actual = c1.distance(c2);
		double expected = 1.0;
		
		assertEquals(expected, actual, DOUBLE_TOLERATION);
	}
	
	@Test
	public void testDistance2() {
		Complex c1 = new Complex(-1, -1);
		Complex c2 = new Complex(1, 1);

		double actual = c1.distance(c2);
		double expected = Math.sqrt(8);
		
		assertEquals(expected, actual, DOUBLE_TOLERATION);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testRootIllegalArgument() {
		// must throw
		Complex.ONE.root(0);
	}
	
	@Test
	public void testToString() {
		assertTrue(Complex.ZERO.toString().contains("0"));
		assertTrue(Complex.IM.toString().contains("i"));
		assertTrue(Complex.IM_NEG.toString().contains("-"));
		
		assertTrue(new Complex(-1, 0).toString().contains("-"));
		assertTrue(new Complex(-1, 1).toString().contains("+"));
	}
	
	@Test
	public void testToStringInt0() {
		assertEquals("-3", new Complex(-3.14159265, 0.09).toString(0));
	}
	
	@Test
	public void testToStringInt5() {
		assertEquals("-3,14159 + 0,09i", new Complex(-3.14159265, 0.09).toString(5));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testToStringIntMinus1() {
		// must throw
		new Complex(-3.14159265, 0.09).toString(-1);
	}
	
	/* ------------------------------ Parsing tests ------------------------------ */
	
	// Regular input tests
	@Test
	public void testParse01() {
		assertComplexEquals(new Complex(1, 0), Complex.parse("1"));
	}
	
	@Test
	public void testParse02() {
		assertComplexEquals(new Complex(999, 0), Complex.parse("999"));
	}
	
	@Test
	public void testParse03() {
		assertComplexEquals(new Complex(1.0, 0), Complex.parse("1.0"));
	}
	
	@Test
	public void testParse04() {
		assertComplexEquals(new Complex(-1.0, 0), Complex.parse("-1,0"));
	}
	
	@Test
	public void testParse05() {
		assertComplexEquals(new Complex(0, 1), Complex.parse("i"));
	}
	
	@Test
	public void testParse06() {
		assertComplexEquals(new Complex(0, -1), Complex.parse("-i"));
	}
	
	@Test
	public void testParse07() {
		assertComplexEquals(new Complex(0, 1), Complex.parse("+i"));
	}
	
	@Test
	public void testParse08() {
		assertComplexEquals(new Complex(0, -1), Complex.parse("-1i"));
	}
	
	@Test
	public void testParse09() {
		assertComplexEquals(new Complex(0, 1), Complex.parse("+i1"));
	}
	
	@Test
	public void testParse10() {
		assertComplexEquals(new Complex(0, -1), Complex.parse("-i1"));
	}
	
	@Test
	public void testParse11() {
		assertComplexEquals(new Complex(0, 2), Complex.parse("2i"));
	}
	
	@Test
	public void testParse12() {
		assertComplexEquals(new Complex(0, 2), Complex.parse("i2"));
	}
	
	@Test
	public void testParse13() {
		assertComplexEquals(new Complex(2, 2), Complex.parse("2 + 2i"));
	}
	
	@Test
	public void testParse14() {
		assertComplexEquals(new Complex(2, 2), Complex.parse("2+2i"));
	}
	
	@Test
	public void testParse15() {
		assertComplexEquals(new Complex(2, 2), Complex.parse("2i+2"));
	}
	
	@Test
	public void testParse16() {
		assertComplexEquals(new Complex(2, 2), Complex.parse("i2+2"));
	}
	
	@Test
	public void testParse17() {
		assertComplexEquals(new Complex(2, 2), Complex.parse("2   +i2"));
	}
	
	@Test
	public void testParse18() {
		assertComplexEquals(new Complex(2, -2), Complex.parse("+2-2i"));
	}
	
	@Test
	public void testParse19() {
		assertComplexEquals(new Complex(2, -2), Complex.parse("2 - +2i"));
	}
	
	@Test
	public void testParse20() {
		assertComplexEquals(new Complex(-2, 2), Complex.parse("+i2 + -2"));
	}
	
	@Test
	public void testParse21() {
		assertComplexEquals(new Complex(3, -2), Complex.parse("-2i - -3"));
	}
	
	@Test
	public void testParse22() {
		assertComplexEquals(new Complex(2, 2), Complex.parse("+2++2i"));
	}
	
	@Test
	public void testParse23() {
		assertComplexEquals(new Complex(0, 0), Complex.parse("0+0i"));
	}
	
	@Test
	public void testParse24() {
		assertComplexEquals(new Complex(0, 0), Complex.parse("+i0 + -0"));
	}
	
	// Irregular input tests
	@Test
	public void testParse25() {
		assertComplexEquals(new Complex(4, 0), Complex.parse("2+2"));
	}
	
	@Test
	public void testParse26() {
		assertComplexEquals(new Complex(0, 4), Complex.parse("2i+2i"));
	}
	
	@Test
	public void testParse27() {
		assertComplexEquals(new Complex(0, 4), Complex.parse("i2+2i"));
	}
	
	@Test
	public void testParse28() {
		assertComplexEquals(new Complex(-2.5, 2.0), Complex.parse("2.0i + -2.5"));
	}
	
	@Test
	public void testParse29() {
		assertComplexEquals(new Complex(3.1415, 1000), Complex.parse("3.1415+  1000i"));
	}
	
	@Test
	public void testParse30() {
		assertComplexEquals(new Complex(0, 0), Complex.parse("-i--i"));
	}
	
	@Test
	public void testParse31() {
		assertComplexEquals(new Complex(0, 2), Complex.parse("2.i"));
	}
	
	// Wrong number format and illegal argument input tests
	@Test(expected=IllegalArgumentException.class)
	public void testParse32() {
		Complex.parse("2ii");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParse33() {
		Complex.parse("i2i");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParse34() {
		Complex.parse("ii+1");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParse35() {
		Complex.parse("abc");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParse36() {
		Complex.parse("2..0");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParse37() {
		Complex.parse("+");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParse38() {
		Complex.parse("i.");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParse39() {
		Complex.parse(".i");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParse40() {
		Complex.parse("");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParse41() {
		Complex.parse(" 	 	 	");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParse42() {
		Complex.parse(null);
	}
	
	
	/* ------------------------------ Utility methods ------------------------------ */
	
	/**
	 * Asserts that the complex <tt>expected</tt> equals the complex
	 * <tt>actual</tt> both by comparing the object equality and double value
	 * equality.
	 * 
	 * @param expected the expected result
	 * @param actual the actual result
	 */
	private static void assertComplexEquals(Complex expected, Complex actual) {
		assertEquals(expected, actual);
		assertEquals(expected.getReal(), actual.getReal(), DOUBLE_TOLERATION);
		assertEquals(expected.getImag(), actual.getImag(), DOUBLE_TOLERATION);
	}
	
	/**
	 * Asserts that the list of <tt>actual</tt> complex numbers contains all
	 * complex numbers from the list of <tt>expected</tt> ones.
	 * <p>
	 * Since the order in the list does not matter, the <tt>Complex</tt> is
	 * expected to have implemented the {@link Complex#equals(Object) equals}
	 * method to test contains.
	 * 
	 * @param expected list of expected complex numbers
	 * @param actual list of actual complex numbers
	 */
	private void assertListEquals(List<Complex> expected, List<Complex> actual) {
		for (Complex exp : expected) {
			assertTrue("Expected complex list to contain: " + exp, actual.contains(exp));
		}
	}
	
	/**
	 * Returns a new instance of a ComplexNumber created from the given
	 * <code>magnitude</code> and <code>angle</code> of a polar form.
	 * 
	 * @param magnitude magnitude of the polar form
	 * @param angle angle of the polar form
	 * @return a new instance of a complex number with the given parameters
	 */
	private static Complex fromMagnitudeAndAngle(double magnitude, double angle) {
		if (magnitude < 0) {
			throw new IllegalArgumentException("Magnitude must not be negative.");
		}
		
		return new Complex(magnitude*Math.cos(angle), magnitude*Math.sin(angle));
	}
	
}
