/*
 * Copyright (c) 2017 Firebird development team and individual contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.firebirdsql.decimal;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class SimpleDecimalTest {

    @Test
    public void negate_One() {
        assertEquals(new SimpleDecimal(BigInteger.valueOf(-1), 0),
                new SimpleDecimal(BigInteger.ONE, 0).negate());
    }

    @Test
    public void negate_positiveZero() {
        assertEquals(new SimpleDecimal(Signum.NEGATIVE, BigInteger.ZERO, 0),
                new SimpleDecimal(Signum.POSITIVE, BigInteger.ZERO, 0).negate());
    }

    @Test
    public void negate_negativeZero() {
        assertEquals(new SimpleDecimal(Signum.POSITIVE, BigInteger.ZERO, 0),
                new SimpleDecimal(Signum.NEGATIVE, BigInteger.ZERO, 0).negate());
    }

    @Test
    public void negate_positiveInfinity() {
        assertEquals(SimpleDecimal.NEGATIVE_INFINITY, SimpleDecimal.POSITIVE_INFINITY.negate());
    }

    @Test
    public void negate_negativeInfinity() {
        assertEquals(SimpleDecimal.POSITIVE_INFINITY, SimpleDecimal.NEGATIVE_INFINITY.negate());
    }

    @Test
    public void negate_positiveNaN() {
        assertEquals(SimpleDecimal.NEGATIVE_NAN, SimpleDecimal.POSITIVE_NAN.negate());
    }

    @Test
    public void negate_negativeNaN() {
        assertEquals(SimpleDecimal.POSITIVE_NAN, SimpleDecimal.NEGATIVE_NAN.negate());
    }

    @Test
    public void negate_positiveSignallingNaN() {
        assertEquals(SimpleDecimal.NEGATIVE_SIGNALING_NAN, SimpleDecimal.POSITIVE_SIGNALING_NAN.negate());
    }

    @Test
    public void negate_negativeSignallingNaN() {
        assertEquals(SimpleDecimal.POSITIVE_SIGNALING_NAN, SimpleDecimal.NEGATIVE_SIGNALING_NAN.negate());
    }
}
