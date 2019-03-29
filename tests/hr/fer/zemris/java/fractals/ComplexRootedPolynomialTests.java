package hr.fer.zemris.java.fractals;

import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class ComplexRootedPolynomialTests {

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorEmpty() {
        // must throw
        new ComplexRootedPolynomial();
    }

    @Test(expected=Exception.class)
    public void testConstructorNull() {
        Complex[] roots = null;
        // must throw
        new ComplexRootedPolynomial(roots);
    }

    @Test(expected=Exception.class)
    public void testConstructorWhereOneComplexIsNull() {
        Complex[] roots = {
            new Complex(1, 0),
            new Complex(2, 0),
            null,
            new Complex(0, -4)
        };

        // must throw
        new ComplexRootedPolynomial(roots);
    }

    @Test
    public void testApply1() {
        Complex[] roots = parseRoots("1", "-1", "i", "-i");
        ComplexRootedPolynomial crp = new ComplexRootedPolynomial(roots);

        Complex z = new Complex();

        Complex actual = crp.apply(z);
        Complex expected = Complex.ONE_NEG;

        assertEquals(expected, actual);
    }

    @Test
    public void testApply2() {
        Complex[] roots = parseRoots("1", "-1", "2+i", "-2-i");
        ComplexRootedPolynomial crp = new ComplexRootedPolynomial(roots);

        Complex z = new Complex(1, -1);

        Complex actual = crp.apply(z);
        Complex expected = new Complex(-9, 12);

        assertEquals(expected, actual);
    }

    @Test
    public void testIndexOfClosestRootFor1() {
        Complex[] roots = parseRoots("2+2i", "-2+2i", "-2-2i", "1+i");
        ComplexRootedPolynomial crp = new ComplexRootedPolynomial(roots);

        int actual = crp.indexOfClosestRootFor(Complex.ZERO, 3);
        int expected = 3;

        assertEquals(expected, actual);
    }

    @Test
    public void testIndexOfClosestRootFor2() {
        Complex[] roots = parseRoots("1", "-1", "i", "-i");
        ComplexRootedPolynomial crp = new ComplexRootedPolynomial(roots);

        int actual = crp.indexOfClosestRootFor(Complex.ZERO, 1);
        int expected = 0; // the first one

        assertEquals(expected, actual);
    }

    @Test
    public void testIndexOfClosestRootFor3() {
        Complex[] roots = parseRoots("1+i", "1-i", "-1+i", "-1-i");
        ComplexRootedPolynomial crp = new ComplexRootedPolynomial(roots);

        int actual = crp.indexOfClosestRootFor(Complex.IM, 0.5);
        int expected = -1; // threshold too low

        assertEquals(expected, actual);
    }


    @Test
    public void testToComplexPolynom1() {
        Complex[] roots = parseRoots("1", "-1", "i", "-i");
        ComplexRootedPolynomial crp = new ComplexRootedPolynomial(roots);

        ComplexPolynomial actual = crp.toComplexPolynom();
        ComplexPolynomial expected = new ComplexPolynomial(
            new Complex[] {
                Complex.ONE_NEG,    // -1
                Complex.ZERO,        // 0z
                Complex.ZERO,        // 0z^2
                Complex.ZERO,        // 0z^3
                Complex.ONE            // z^4
        });

        assertEquals(expected, actual);
    }

    @Test
    public void testToComplexPolynom2() {
        Complex[] roots = parseRoots("1", "-1", "2+i", "-2-i");
        ComplexRootedPolynomial crp = new ComplexRootedPolynomial(roots);

        ComplexPolynomial actual = crp.toComplexPolynom();
        ComplexPolynomial expected = new ComplexPolynomial(
                // z^4  +  0z^3  -  (4 + 4i)z^2  +  0z  +  3 + 4i
                parseRoots("3+4i", "0", "-4-4i", "0", "1")
        );

        assertEquals(expected, actual);
    }


    /* ------------------------------ Hardcore string tests ------------------------------ */
    // Can be ignored as it is not a homework requirement

    @Test
    public void testToString1() {
        Complex[] roots = parseRoots("1", "-1", "i", "-i");

        String actual = new ComplexRootedPolynomial(roots).toString();
        String expected = "(z - 1)(z + 1)(z - i)(z + i)";

        assertEquals(expected, actual);
    }

    @Test
    public void testToString2() {
        Complex[] roots = parseRoots("1", "-1", "2+i", "-2-i");

        String actual = new ComplexRootedPolynomial(roots).toString();
        String expected = "(z - 1)(z + 1)(z - 2 - i)(z + 2 + i)";

        assertEquals(expected, actual);
    }


    /* ------------------------------ Utility methods ------------------------------ */

    /**
     * Returns an array of complex roots, where index <tt>i</tt> of the returned
     * array is a parsed string from the specified <tt>s</tt> at index <tt>i</tt>.
     *
     * @param s array of strings to be parsed
     * @return an array of parsed complex roots
     */
    private static Complex[] parseRoots(String ...s) {
        Complex[] roots = new Complex[s.length];

        for (int i = 0; i < s.length; i++) {
            roots[i] = Complex.parse(s[i]);
        }

        return roots;
    }

}
