/*
 * Copyright (c) 2018 Firebird development team and individual contributors
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

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.*;

public class Decimal128Test {

    private static final Decimal128 POSITIVE_ZERO = Decimal128.valueOf(BigDecimal.ZERO);
    private static final Decimal128 NEGATIVE_ZERO = POSITIVE_ZERO.negate();

    @Test
    public void valueOf_Decimal128_isIdentity() {
        Decimal128 decimal128Value = Decimal128.valueOf("123");

        assertSame(decimal128Value, Decimal128.valueOf(decimal128Value));
    }

    @Test
    public void valueOf_Decimal32_conversion() {
        Decimal32 decimal32Value = Decimal32.valueOf("123");
        Decimal128 decimal128Value = Decimal128.valueOf("123");

        assertEquals(decimal128Value, Decimal128.valueOf(decimal32Value));
    }

    @Test
    public void valueOf_Decimal64_conversion() {
        Decimal64 decimal64Value = Decimal64.valueOf("123");
        Decimal128 decimal128Value = Decimal128.valueOf("123");

        assertEquals(decimal128Value, Decimal128.valueOf(decimal64Value));
    }

    @Test
    public void valueOf_Decimal32_full_precision() {
        Decimal32 decimal32Value = Decimal32.valueOf("1.234567");
        Decimal128 decimal128Value = Decimal128.valueOf("1.234567");

        assertEquals(decimal128Value, Decimal128.valueOf(decimal32Value));
    }

    @Test
    public void valueOf_Decimal64_full_precision() {
        Decimal64 decimal64Value = Decimal64.valueOf("1.234567890123456");
        Decimal128 decimal128Value = Decimal128.valueOf("1.234567890123456");

        assertEquals(decimal128Value, Decimal128.valueOf(decimal64Value));
    }

    @Test
    public void valueOf_Decimal32_small() {
        Decimal32 decimal32Value = Decimal32.valueOf("1.234567E-95");
        Decimal128 decimal128Value = Decimal128.valueOf("1.234567E-95");

        assertEquals(decimal128Value, Decimal128.valueOf(decimal32Value));
    }

    @Test
    public void valueOf_Decimal64_small() {
        Decimal64 decimal64Value = Decimal64.valueOf("1.234567890123456E-383");
        Decimal128 decimal128Value = Decimal128.valueOf("1.234567890123456E-383");

        assertEquals(decimal128Value, Decimal128.valueOf(decimal64Value));
    }

    @Test
    public void valueOf_Decimal32_large() {
        Decimal64 decimal32Value = Decimal64.valueOf("1.234567E96");
        Decimal128 decimal128Value = Decimal128.valueOf("1.234567E96");

        assertEquals(decimal128Value, Decimal128.valueOf(decimal32Value));
    }

    @Test
    public void valueOf_Decimal64_large() {
        Decimal64 decimal64Value = Decimal64.valueOf("1.234567890123456E384");
        Decimal128 decimal128Value = Decimal128.valueOf("1.234567890123456E384");

        assertEquals(decimal128Value, Decimal128.valueOf(decimal64Value));
    }

    @Test
    public void valueOf_specials() {
        for (Decimal<?> decimal : Arrays.asList(Decimal32.POSITIVE_INFINITY, Decimal64.POSITIVE_INFINITY,
                Decimal128.POSITIVE_INFINITY)) {
            assertSame(Decimal128.POSITIVE_INFINITY, Decimal128.valueOf(decimal));
        }
        for (Decimal<?> decimal : Arrays.asList(Decimal32.NEGATIVE_INFINITY, Decimal64.NEGATIVE_INFINITY,
                Decimal128.NEGATIVE_INFINITY)) {
            assertSame(Decimal128.NEGATIVE_INFINITY, Decimal128.valueOf(decimal));
        }
        for (Decimal<?> decimal : Arrays.asList(Decimal32.POSITIVE_NAN, Decimal64.POSITIVE_NAN,
                Decimal128.POSITIVE_NAN)) {
            assertSame(Decimal128.POSITIVE_NAN, Decimal128.valueOf(decimal));
        }
        for (Decimal<?> decimal : Arrays.asList(Decimal32.NEGATIVE_NAN, Decimal64.NEGATIVE_NAN,
                Decimal128.NEGATIVE_NAN)) {
            assertSame(Decimal128.NEGATIVE_NAN, Decimal128.valueOf(decimal));
        }
        for (Decimal<?> decimal : Arrays.asList(Decimal32.POSITIVE_SIGNALING_NAN, Decimal64.POSITIVE_SIGNALING_NAN,
                Decimal128.POSITIVE_SIGNALING_NAN)) {
            assertSame(Decimal128.POSITIVE_SIGNALING_NAN, Decimal128.valueOf(decimal));
        }
        for (Decimal<?> decimal : Arrays.asList(Decimal32.NEGATIVE_SIGNALING_NAN, Decimal64.NEGATIVE_SIGNALING_NAN,
                Decimal128.NEGATIVE_SIGNALING_NAN)) {
            assertSame(Decimal128.NEGATIVE_SIGNALING_NAN, Decimal128.valueOf(decimal));
        }
    }

    @Test
    public void toBigDecimal_normalValue() {
        String decimalString = "1.23456E10";
        Decimal128 decimal128Value = Decimal128.valueOf(decimalString);
        BigDecimal bigDecimalValue = new BigDecimal(decimalString);

        assertEquals(bigDecimalValue, decimal128Value.toBigDecimal());
    }

    @Test
    public void toBigDecimal_specials() {
        for (Decimal128 special : Arrays.asList(Decimal128.POSITIVE_INFINITY, Decimal128.NEGATIVE_INFINITY,
                Decimal128.POSITIVE_NAN, Decimal128.NEGATIVE_NAN, Decimal128.POSITIVE_SIGNALING_NAN,
                Decimal128.NEGATIVE_SIGNALING_NAN)) {
            try {
                special.toBigDecimal();
                fail("toBigDecimal should have thrown DecimalInconvertibleException for " + special);
            } catch (DecimalInconvertibleException e) {
                assertEquals("DecimalInconvertibleException.getDecimalType", special.getType(), e.getDecimalType());
                assertEquals("DecimalInconvertibleException.getSignum", special.signum(), e.getSignum());
            }
        }
    }

    @Test
    public void doubleValue_normalValue() {
        String decimalString = "1.23456E10";
        Decimal128 decimal128Value = Decimal128.valueOf(decimalString);

        assertEquals(1.23456E10, decimal128Value.doubleValue(), 0.1);
    }

    @Test
    public void doubleValue_positiveInfinity() {
        assertEquals(Double.POSITIVE_INFINITY, Decimal128.POSITIVE_INFINITY.doubleValue(), 0);
    }

    @Test
    public void doubleValue_negativeInfinity() {
        assertEquals(Double.NEGATIVE_INFINITY, Decimal128.NEGATIVE_INFINITY.doubleValue(), 0);
    }

    @Test
    public void doubleValue_NaNs() {
        for (Decimal128 special : Arrays.asList(Decimal128.POSITIVE_NAN, Decimal128.NEGATIVE_NAN,
                Decimal128.POSITIVE_SIGNALING_NAN, Decimal128.NEGATIVE_SIGNALING_NAN)) {
            assertTrue("NaN for " + special, Double.isNaN(special.doubleValue()));
        }
    }

    @Test
    public void valueOf_normalDouble() {
        assertEquals(Decimal128.valueOf("1.23456"), Decimal128.valueOf(1.23456));
    }

    @Test
    public void valueOf_doublePositiveInfinity() {
        assertSame(Decimal128.POSITIVE_INFINITY, Decimal128.valueOf(Double.POSITIVE_INFINITY));
    }

    @Test
    public void valueOf_doubleNegativeInfinity() {
        assertSame(Decimal128.NEGATIVE_INFINITY, Decimal128.valueOf(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void valueOf_doubleNaN() {
        assertSame(Decimal128.POSITIVE_NAN, Decimal128.valueOf(Double.NaN));
    }

    @Test
    public void toDecimal_Decimal128_Decimal128() {
        Decimal128 value = Decimal128.valueOf("1.23456");

        assertSame(value, value.toDecimal(Decimal128.class));
    }

    @Test
    public void toDecimal_Decimal128_Decimal32() {
        Decimal128 value = Decimal128.valueOf("1.23456");

        assertEquals(Decimal32.valueOf("1.23456"), value.toDecimal(Decimal32.class));
    }

    @Test
    public void toDecimal_Decimal128_Decimal64() {
        Decimal128 value = Decimal128.valueOf("1.23456");

        assertEquals(Decimal64.valueOf("1.23456"), value.toDecimal(Decimal64.class));
    }

    @Test
    public void validateConstant_POSITIVE_ZERO() {
        assertEquals(BigDecimal.ZERO, POSITIVE_ZERO.toBigDecimal());
        assertEquals(Signum.POSITIVE, POSITIVE_ZERO.signum());
    }

    @Test
    public void validateConstant_NEGATIVE_ZERO() {
        assertEquals(BigDecimal.ZERO, NEGATIVE_ZERO.toBigDecimal());
        assertEquals(Signum.NEGATIVE, NEGATIVE_ZERO.signum());
    }

    @Test
    public void negate_One() {
        Decimal128 negativeOne = Decimal128.valueOf(BigDecimal.ONE).negate();

        assertEquals(Decimal128.valueOf(BigDecimal.ONE.negate()),
                Decimal128.valueOf(BigDecimal.ONE).negate());
        assertEquals(BigDecimal.ONE.negate(), negativeOne.toBigDecimal());
        assertEquals(Signum.NEGATIVE, negativeOne.signum());
    }

    @Test
    public void negate_positiveZero() {
        Decimal128 negativeZero = POSITIVE_ZERO.negate();

        assertEquals(NEGATIVE_ZERO, negativeZero);
        assertEquals(BigDecimal.ZERO, negativeZero.toBigDecimal());
        assertEquals(Signum.NEGATIVE, negativeZero.signum());
    }

    @Test
    public void negate_negativeZero() {
        Decimal128 positiveZero = NEGATIVE_ZERO.negate();

        assertEquals(POSITIVE_ZERO, positiveZero);
        assertEquals(BigDecimal.ZERO, positiveZero.toBigDecimal());
        assertEquals(Signum.POSITIVE, positiveZero.signum());
    }

    @Test
    public void negate_positiveInfinity() {
        assertEquals(Decimal128.NEGATIVE_INFINITY, Decimal128.POSITIVE_INFINITY.negate());
    }

    @Test
    public void negate_negativeInfinity() {
        assertEquals(Decimal128.POSITIVE_INFINITY, Decimal128.NEGATIVE_INFINITY.negate());
    }

    @Test
    public void negate_positiveNaN() {
        assertEquals(Decimal128.NEGATIVE_NAN, Decimal128.POSITIVE_NAN.negate());
    }

    @Test
    public void negate_negativeNaN() {
        assertEquals(Decimal128.POSITIVE_NAN, Decimal128.NEGATIVE_NAN.negate());
    }

    @Test
    public void negate_positiveSignallingNaN() {
        assertEquals(Decimal128.NEGATIVE_SIGNALING_NAN, Decimal128.POSITIVE_SIGNALING_NAN.negate());
    }

    @Test
    public void negate_negativeSignallingNaN() {
        assertEquals(Decimal128.POSITIVE_SIGNALING_NAN, Decimal128.NEGATIVE_SIGNALING_NAN.negate());
    }

}
