package hr.fer.zemris.java.fractals;

import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class ComplexPolynomialTests {

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorEmpty() {
        // must throw
        new ComplexPolynomial();
    }

    @Test(expected=Exception.class)
    public void testConstructorNull() {
        Complex[] factors = null;
        // must throw
        new ComplexPolynomial(factors);
    }

    @Test(expected=Exception.class)
    public void testConstructorWhereOneComplexIsNull() {
        Complex[] factors = {
            new Complex(1, 0),
            new Complex(2, 0),
            null,
            new Complex(0, -4)
        };

        // must throw
        new ComplexPolynomial(factors);
    }

    @Test
    public void testOrder1() {
        // -4iz^3 - 3iz^2 + 2z + 1
        Complex[] factors = parseFactors("1", "2", "-3i", "-4i");

        assertEquals(3, new ComplexPolynomial(factors).order());
    }

    @Test
    public void testOrder2() {
        // 1
        Complex[] factors = {Complex.ONE};

        assertEquals(0, new ComplexPolynomial(factors).order());
    }

    @Test
    public void testOrder3() {
        // 0z^3 + 0z^2 + 2z + 1
        Complex[] factors = parseFactors("1", "2", "0", "0");

        // the highest two factors are 0 !!
        assertEquals(1, new ComplexPolynomial(factors).order());
    }

    @Test
    public void testDerive1() {
        // (7 + 2i)z^3 + 2z^2 + 5z + 1
        Complex[] factorsA = parseFactors("1", "5", "2", "7+2i");
        ComplexPolynomial actual = new ComplexPolynomial(factorsA).derive();

        // (21 + 6i)z^2 + 4z + 5
        Complex[] factorsE = parseFactors("5", "4", "21+6i");
        ComplexPolynomial expected = new ComplexPolynomial(factorsE);

        assertEquals(expected, actual);
    }

    @Test
    public void testDerive2() {
        // 1
        Complex[] factorsA = parseFactors("1");
        ComplexPolynomial actual = new ComplexPolynomial(factorsA).derive();

        // 0
        Complex[] factorsE = parseFactors("0");
        ComplexPolynomial expected = new ComplexPolynomial(factorsE);

        assertEquals(expected, actual);

        // derive 0
        ComplexPolynomial derivedAgain = actual.derive();
        assertEquals(expected, derivedAgain);
    }

    @Test
    public void testMultiply1() {
        // 4z^2 + 2z + 1
        Complex[] factors1 = parseFactors("1", "2", "4");
        // z^2 - z
        Complex[] factors2 = parseFactors("0", "-1", "1");

        ComplexPolynomial actual = new ComplexPolynomial(factors1)
                         .multiply(new ComplexPolynomial(factors2));

        // 4z^4 - 2z^3 - z^2 - z
        Complex[] factorsE = parseFactors("0", "-1", "-1", "-2", "4");
        ComplexPolynomial expected = new ComplexPolynomial(factorsE);

        assertEquals(expected, actual);
    }

    @Test
    public void testMultiply2() {
        // (4 + i)z^4 + (2 - 3i)z^2 - z
        Complex[] factors = parseFactors("0", "-1", "2-3i", "0", "4+i");

        ComplexPolynomial cp1 = new ComplexPolynomial(factors);
        // (16 + 4i)z^3 + (4 - 6i)z - 1
        ComplexPolynomial cp2 = cp1.derive();

        ComplexPolynomial actual = cp1.multiply(cp2);
        // (60 + 32i)z^7 + (66 - 60i)z^5 - (20 + 5i)z^4 - (10 + 24i)z^3 + (-6 + 9i)z^2 + z
        Complex[] factorsE = parseFactors("0", "1", "-6+9i", "-10-24i", "-20-5i", "66-60i", "0", "60+32i");
        ComplexPolynomial expected = new ComplexPolynomial(factorsE);

        assertEquals(expected, actual);
    }

    @Test
    public void testApply1() {
        // (4 + i)z^4 + (2 - 3i)z^2 - z
        Complex[] factors = parseFactors("0", "-1", "2-3i", "0", "4+i");
        ComplexPolynomial cp = new ComplexPolynomial(factors);

        Complex z = new Complex();

        Complex actual = cp.apply(z);
        Complex expected = Complex.ZERO;

        assertEquals(expected, actual);
    }

    @Test
    public void testApply2() {
        // (4 + i)z^4 + (2 - 3i)z^2 - z
        Complex[] factors = parseFactors("0", "-1", "2-3i", "0", "4+i");
        ComplexPolynomial cp = new ComplexPolynomial(factors);

        Complex z = new Complex(-2, 5);

        Complex actual = cp.apply(z);
        Complex expected = new Complex(-776, 3419);

        assertEquals(expected, actual);
    }


    /* ------------------------------ Hardcore string tests ------------------------------ */
    // Can be ignored as it is not a homework requirement

    @Test
    public void testToString1() {
        // z^2 + 2z + 5
        Complex[] factors = parseFactors("5", "2", "1");

        String actual = new ComplexPolynomial(factors).toString();
        String expected = "z^2 + 2z + 5";

        assertEquals(expected, actual);
    }

    @Test
    public void testToString2() {
        // (1 - i)z^2 - 2iz + 5
        Complex[] factors = parseFactors("5", "-2i", "1-i");

        String actual = new ComplexPolynomial(factors).toString();
        String expected = "(1 - i)z^2 - 2iz + 5";

        assertEquals(expected, actual);
    }

    @Test
    public void testToString3() {
        // (1 + i)z^4 - (1 + i)z^3 - 2z^2 - i
        Complex[] factors = parseFactors("-i", "0", "-2", "-1-i", "1+i");

        String actual = new ComplexPolynomial(factors).toString();
        String expected = "(1 + i)z^4 - (1 + i)z^3 - 2z^2 - i";

        assertEquals(expected, actual);
    }

    @Test
    public void testToString4() {
        // -iz^4 - z^3 + iz^2 + z
        Complex[] factors = parseFactors("0", "1", "i", "-1", "-i");

        String actual = new ComplexPolynomial(factors).toString();
        String expected = "-iz^4 - z^3 + iz^2 + z";

        assertEquals(expected, actual);
    }


    /* ------------------------------ Utility methods ------------------------------ */

    /**
     * Returns an array of complex factors, where index <tt>i</tt> of the returned
     * array is a parsed string from the specified <tt>s</tt> at index <tt>i</tt>.
     *
     * @param s array of strings to be parsed
     * @return an array of parsed complex factors
     */
    private static Complex[] parseFactors(String ...s) {
        Complex[] factors = new Complex[s.length];

        for (int i = 0; i < s.length; i++) {
            factors[i] = Complex.parse(s[i]);
        }

        return factors;
    }
}
