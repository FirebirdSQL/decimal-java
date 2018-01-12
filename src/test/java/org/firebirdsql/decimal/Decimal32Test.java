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

public class Decimal32Test {

    private static final Decimal32 POSITIVE_ZERO = Decimal32.valueOf(BigDecimal.ZERO);
    private static final Decimal32 NEGATIVE_ZERO = POSITIVE_ZERO.negate();

    @Test
    public void valueOf_Decimal32_isIdentity() {
        Decimal32 decimal32Value = Decimal32.valueOf("123");

        assertSame(decimal32Value, Decimal32.valueOf(decimal32Value));
    }

    @Test
    public void valueOf_Decimal64_conversion() {
        Decimal64 decimal64Value = Decimal64.valueOf("123");
        Decimal32 decimal32Value = Decimal32.valueOf("123");

        assertEquals(decimal32Value, Decimal32.valueOf(decimal64Value));
    }

    @Test
    public void valueOf_Decimal128_conversion() {
        Decimal128 decimal128Value = Decimal128.valueOf("123");
        Decimal32 decimal32Value = Decimal32.valueOf("123");

        assertEquals(decimal32Value, Decimal32.valueOf(decimal128Value));
    }

    @Test
    public void valueOf_Decimal64_rounding() {
        Decimal64 decimal64Value = Decimal64.valueOf("1.234567890123456");
        Decimal32 decimal32Value = Decimal32.valueOf("1.234568");

        assertEquals(decimal32Value, Decimal32.valueOf(decimal64Value));
    }

    @Test
    public void valueOf_Decimal128_rounding() {
        Decimal128 decimal128Value = Decimal128.valueOf("1.23456789012345678901234568901234");
        Decimal32 decimal32Value = Decimal32.valueOf("1.234568");

        assertEquals(decimal32Value, Decimal32.valueOf(decimal128Value));
    }

    @Test
    public void valueOf_Decimal64_roundingToZero() {
        Decimal64 decimal64Value = Decimal64.valueOf("1E-300");
        Decimal32 decimal32Value = Decimal32.valueOf("0E-101");

        assertEquals(decimal32Value, Decimal32.valueOf(decimal64Value));
    }

    @Test
    public void valueOf_Decimal128_roundingToZero() {
        Decimal128 decimal128Value = Decimal128.valueOf("1E-6000");
        Decimal32 decimal32Value = Decimal32.valueOf("0E-101");

        assertEquals(decimal32Value, Decimal32.valueOf(decimal128Value));
    }

    @Test
    public void valueOf_Decimal64_overflowToInfinity() {
        Decimal64 decimal64Value = Decimal64.valueOf("1E300");

        assertEquals(Decimal32.POSITIVE_INFINITY, Decimal32.valueOf(decimal64Value));
    }

    @Test
    public void valueOf_Decimal128_overflowToInfinity() {
        Decimal128 decimal128Value = Decimal128.valueOf("1E6000");

        assertEquals(Decimal32.POSITIVE_INFINITY, Decimal32.valueOf(decimal128Value));
    }

    @Test
    public void valueOf_specials() {
        for (Decimal<?> decimal : Arrays.asList(Decimal32.POSITIVE_INFINITY, Decimal64.POSITIVE_INFINITY,
                Decimal128.POSITIVE_INFINITY)) {
            assertSame(Decimal32.POSITIVE_INFINITY, Decimal32.valueOf(decimal));
        }
        for (Decimal<?> decimal : Arrays.asList(Decimal32.NEGATIVE_INFINITY, Decimal64.NEGATIVE_INFINITY,
                Decimal128.NEGATIVE_INFINITY)) {
            assertSame(Decimal32.NEGATIVE_INFINITY, Decimal32.valueOf(decimal));
        }
        for (Decimal<?> decimal : Arrays.asList(Decimal32.POSITIVE_NAN, Decimal64.POSITIVE_NAN,
                Decimal128.POSITIVE_NAN)) {
            assertSame(Decimal32.POSITIVE_NAN, Decimal32.valueOf(decimal));
        }
        for (Decimal<?> decimal : Arrays.asList(Decimal32.NEGATIVE_NAN, Decimal64.NEGATIVE_NAN,
                Decimal128.NEGATIVE_NAN)) {
            assertSame(Decimal32.NEGATIVE_NAN, Decimal32.valueOf(decimal));
        }
        for (Decimal<?> decimal : Arrays.asList(Decimal32.POSITIVE_SIGNALING_NAN, Decimal64.POSITIVE_SIGNALING_NAN,
                Decimal128.POSITIVE_SIGNALING_NAN)) {
            assertSame(Decimal32.POSITIVE_SIGNALING_NAN, Decimal32.valueOf(decimal));
        }
        for (Decimal<?> decimal : Arrays.asList(Decimal32.NEGATIVE_SIGNALING_NAN, Decimal64.NEGATIVE_SIGNALING_NAN,
                Decimal128.NEGATIVE_SIGNALING_NAN)) {
            assertSame(Decimal32.NEGATIVE_SIGNALING_NAN, Decimal32.valueOf(decimal));
        }
    }

    @Test
    public void toBigDecimal_finiteValue() {
        String decimalString = "1.23456E10";
        Decimal32 decimal32Value = Decimal32.valueOf(decimalString);
        BigDecimal bigDecimalValue = new BigDecimal(decimalString);

        assertEquals(bigDecimalValue, decimal32Value.toBigDecimal());
    }

    @Test
    public void toBigDecimal_specials() {
        for (Decimal32 special : Arrays.asList(Decimal32.POSITIVE_INFINITY, Decimal32.NEGATIVE_INFINITY,
                Decimal32.POSITIVE_NAN, Decimal32.NEGATIVE_NAN, Decimal32.POSITIVE_SIGNALING_NAN,
                Decimal32.NEGATIVE_SIGNALING_NAN)) {
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
    public void doubleValue_finiteValue() {
        String decimalString = "1.23456E10";
        Decimal32 decimal32Value = Decimal32.valueOf(decimalString);

        assertEquals(1.23456E10, decimal32Value.doubleValue(), 0.1);
    }

    @Test
    public void doubleValue_positiveInfinity() {
        assertEquals(Double.POSITIVE_INFINITY, Decimal32.POSITIVE_INFINITY.doubleValue(), 0);
    }

    @Test
    public void doubleValue_negativeInfinity() {
        assertEquals(Double.NEGATIVE_INFINITY, Decimal32.NEGATIVE_INFINITY.doubleValue(), 0);
    }

    @Test
    public void doubleValue_NaNs() {
        for (Decimal32 special : Arrays.asList(Decimal32.POSITIVE_NAN, Decimal32.NEGATIVE_NAN,
                Decimal32.POSITIVE_SIGNALING_NAN, Decimal32.NEGATIVE_SIGNALING_NAN)) {
            assertTrue("NaN for " + special, Double.isNaN(special.doubleValue()));
        }
    }

    @Test
    public void valueOf_finiteDouble() {
        assertEquals(Decimal32.valueOf("1.23456"), Decimal32.valueOf(1.23456));
    }

    @Test
    public void valueOf_doublePositiveInfinity() {
        assertSame(Decimal32.POSITIVE_INFINITY, Decimal32.valueOf(Double.POSITIVE_INFINITY));
    }

    @Test
    public void valueOf_doubleNegativeInfinity() {
        assertSame(Decimal32.NEGATIVE_INFINITY, Decimal32.valueOf(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void valueOf_doubleNaN() {
        assertSame(Decimal32.POSITIVE_NAN, Decimal32.valueOf(Double.NaN));
    }

    @Test
    public void toDecimal_Decimal32_Decimal32() {
        Decimal32 value = Decimal32.valueOf("1.23456");

        assertSame(value, value.toDecimal(Decimal32.class));
    }

    @Test
    public void toDecimal_Decimal32_Decimal64() {
        Decimal32 value = Decimal32.valueOf("1.23456");

        assertEquals(Decimal64.valueOf("1.23456"), value.toDecimal(Decimal64.class));
    }

    @Test
    public void toDecimal_Decimal32_Decimal128() {
        Decimal32 value = Decimal32.valueOf("1.23456");

        assertEquals(Decimal128.valueOf("1.23456"), value.toDecimal(Decimal128.class));
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
        Decimal32 negativeOne = Decimal32.valueOf(BigDecimal.ONE).negate();

        assertEquals(Decimal32.valueOf(BigDecimal.ONE.negate()),
                Decimal32.valueOf(BigDecimal.ONE).negate());
        assertEquals(BigDecimal.ONE.negate(), negativeOne.toBigDecimal());
        assertEquals(Signum.NEGATIVE, negativeOne.signum());
    }

    @Test
    public void negate_positiveZero() {
        Decimal32 negativeZero = POSITIVE_ZERO.negate();

        assertEquals(NEGATIVE_ZERO, negativeZero);
        assertEquals(BigDecimal.ZERO, negativeZero.toBigDecimal());
        assertEquals(Signum.NEGATIVE, negativeZero.signum());
    }

    @Test
    public void negate_negativeZero() {
        Decimal32 positiveZero = NEGATIVE_ZERO.negate();

        assertEquals(POSITIVE_ZERO, positiveZero);
        assertEquals(BigDecimal.ZERO, positiveZero.toBigDecimal());
        assertEquals(Signum.POSITIVE, positiveZero.signum());
    }

    @Test
    public void negate_positiveInfinity() {
        assertEquals(Decimal32.NEGATIVE_INFINITY, Decimal32.POSITIVE_INFINITY.negate());
    }

    @Test
    public void negate_negativeInfinity() {
        assertEquals(Decimal32.POSITIVE_INFINITY, Decimal32.NEGATIVE_INFINITY.negate());
    }

    @Test
    public void negate_positiveNaN() {
        assertEquals(Decimal32.NEGATIVE_NAN, Decimal32.POSITIVE_NAN.negate());
    }

    @Test
    public void negate_negativeNaN() {
        assertEquals(Decimal32.POSITIVE_NAN, Decimal32.NEGATIVE_NAN.negate());
    }

    @Test
    public void negate_positiveSignallingNaN() {
        assertEquals(Decimal32.NEGATIVE_SIGNALING_NAN, Decimal32.POSITIVE_SIGNALING_NAN.negate());
    }

    @Test
    public void negate_negativeSignallingNaN() {
        assertEquals(Decimal32.POSITIVE_SIGNALING_NAN, Decimal32.NEGATIVE_SIGNALING_NAN.negate());
    }

}
