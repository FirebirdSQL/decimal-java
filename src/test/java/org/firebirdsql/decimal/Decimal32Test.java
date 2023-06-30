/*
 * Copyright (c) 2018-2023 Firebird development team and individual contributors
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

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class Decimal32Test {

    private static final Decimal32 POSITIVE_ZERO = Decimal32.valueOf(BigDecimal.ZERO);
    private static final Decimal32 NEGATIVE_ZERO = POSITIVE_ZERO.negate();

    @Test
    void valueOf_Decimal32_isIdentity() {
        Decimal32 decimal32Value = Decimal32.valueOf("123");

        assertSame(decimal32Value, Decimal32.valueOf(decimal32Value));
    }

    @Test
    void valueOf_Decimal64_conversion() {
        Decimal64 decimal64Value = Decimal64.valueOf("123");
        Decimal32 decimal32Value = Decimal32.valueOf("123");

        assertEquals(decimal32Value, Decimal32.valueOf(decimal64Value));
    }

    @Test
    void valueOf_Decimal128_conversion() {
        Decimal128 decimal128Value = Decimal128.valueOf("123");
        Decimal32 decimal32Value = Decimal32.valueOf("123");

        assertEquals(decimal32Value, Decimal32.valueOf(decimal128Value));
    }

    @Test
    void valueOf_Decimal64_rounding() {
        Decimal64 decimal64Value = Decimal64.valueOf("1.234567890123456");
        Decimal32 decimal32Value = Decimal32.valueOf("1.234568");

        assertEquals(decimal32Value, Decimal32.valueOf(decimal64Value));
    }

    @Test
    void valueOf_Decimal128_rounding() {
        Decimal128 decimal128Value = Decimal128.valueOf("1.23456789012345678901234568901234");
        Decimal32 decimal32Value = Decimal32.valueOf("1.234568");

        assertEquals(decimal32Value, Decimal32.valueOf(decimal128Value));
    }

    @Test
    void valueOf_Decimal64_roundingToZero() {
        Decimal64 decimal64Value = Decimal64.valueOf("1E-300");
        Decimal32 decimal32Value = Decimal32.valueOf("0E-101");

        assertEquals(decimal32Value, Decimal32.valueOf(decimal64Value));
    }

    @Test
    void valueOf_Decimal128_roundingToZero() {
        Decimal128 decimal128Value = Decimal128.valueOf("1E-6000");
        Decimal32 decimal32Value = Decimal32.valueOf("0E-101");

        assertEquals(decimal32Value, Decimal32.valueOf(decimal128Value));
    }

    @Test
    void valueOf_Decimal64_overflowToInfinity() {
        Decimal64 decimal64Value = Decimal64.valueOf("1E300");

        assertEquals(Decimal32.POSITIVE_INFINITY, Decimal32.valueOf(decimal64Value));
    }

    @Test
    void valueOf_Decimal64_overflow_ThrowException() {
        Decimal64 decimal64Value = Decimal64.valueOf("1E300");

        var exception = assertThrows(DecimalOverflowException.class, () ->
                Decimal32.valueOf(decimal64Value, OverflowHandling.THROW_EXCEPTION));
        assertEquals("The scale -300 is out of range for this type", exception.getMessage());
    }

    @Test
    void valueOf_Decimal128_overflowToInfinity() {
        Decimal128 decimal128Value = Decimal128.valueOf("1E6000");

        assertEquals(Decimal32.POSITIVE_INFINITY, Decimal32.valueOf(decimal128Value));
    }

    @Test
    void valueOf_Decimal128_overflow_ThrowException() {
        Decimal128 decimal128Value = Decimal128.valueOf("1E6000");

        var exception = assertThrows(DecimalOverflowException.class, () ->
                Decimal32.valueOf(decimal128Value, OverflowHandling.THROW_EXCEPTION));
        assertEquals("The scale -6000 is out of range for this type", exception.getMessage());
    }

    @Test
    void valueOf_specials() {
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
    void toBigDecimal_finiteValue() {
        String decimalString = "1.23456E10";
        Decimal32 decimal32Value = Decimal32.valueOf(decimalString);
        BigDecimal bigDecimalValue = new BigDecimal(decimalString);

        assertEquals(bigDecimalValue, decimal32Value.toBigDecimal());
    }

    @Test
    void toBigDecimal_specials() {
        for (Decimal32 special : Arrays.asList(Decimal32.POSITIVE_INFINITY, Decimal32.NEGATIVE_INFINITY,
                Decimal32.POSITIVE_NAN, Decimal32.NEGATIVE_NAN, Decimal32.POSITIVE_SIGNALING_NAN,
                Decimal32.NEGATIVE_SIGNALING_NAN)) {
            try {
                special.toBigDecimal();
                fail("toBigDecimal should have thrown DecimalInconvertibleException for " + special);
            } catch (DecimalInconvertibleException e) {
                assertEquals(special.getType(), e.getDecimalType(), "DecimalInconvertibleException.getDecimalType");
                assertEquals(special.signum(), e.getSignum(), "DecimalInconvertibleException.getSignum");
            }
        }
    }

    @Test
    void doubleValue_finiteValue() {
        String decimalString = "1.23456E10";
        Decimal32 decimal32Value = Decimal32.valueOf(decimalString);

        assertEquals(1.23456E10, decimal32Value.doubleValue(), 0.1);
    }

    @Test
    void doubleValue_positiveInfinity() {
        assertEquals(Double.POSITIVE_INFINITY, Decimal32.POSITIVE_INFINITY.doubleValue(), 0);
    }

    @Test
    void doubleValue_negativeInfinity() {
        assertEquals(Double.NEGATIVE_INFINITY, Decimal32.NEGATIVE_INFINITY.doubleValue(), 0);
    }

    @Test
    void doubleValue_NaNs() {
        for (Decimal32 special : Arrays.asList(Decimal32.POSITIVE_NAN, Decimal32.NEGATIVE_NAN,
                Decimal32.POSITIVE_SIGNALING_NAN, Decimal32.NEGATIVE_SIGNALING_NAN)) {
            assertTrue(Double.isNaN(special.doubleValue()), "NaN for " + special);
        }
    }

    @Test
    void valueOf_finiteDouble() {
        assertEquals(Decimal32.valueOf("1.23456"), Decimal32.valueOf(1.23456));
    }

    @Test
    void valueOf_doublePositiveInfinity() {
        assertSame(Decimal32.POSITIVE_INFINITY, Decimal32.valueOf(Double.POSITIVE_INFINITY));
    }

    @Test
    void valueOf_doubleNegativeInfinity() {
        assertSame(Decimal32.NEGATIVE_INFINITY, Decimal32.valueOf(Double.NEGATIVE_INFINITY));
    }

    @Test
    void valueOf_doubleNaN() {
        assertSame(Decimal32.POSITIVE_NAN, Decimal32.valueOf(Double.NaN));
    }

    @Test
    void valueOf_doubleMax_OverflowToInfinity() {
        assertEquals(Decimal32.POSITIVE_INFINITY, Decimal32.valueOf(Double.MAX_VALUE));
    }

    @Test
    void valueOf_doubleMax_Overflow_ThrowException() {
        var exception = assertThrows(DecimalOverflowException.class, () ->
                Decimal32.valueOf(Double.MAX_VALUE, OverflowHandling.THROW_EXCEPTION));
        assertEquals("The scale -302 is out of range for this type", exception.getMessage());
    }

    @Test
    void valueOf_stringOutOfRange_toPositiveInfinity() {
        assertSame(Decimal32.POSITIVE_INFINITY, Decimal32.valueOf("9.9E10000"));
    }

    @Test
    void valueOf_stringOutOfRange_toNegativeInfinity() {
        assertSame(Decimal32.NEGATIVE_INFINITY, Decimal32.valueOf("-9.9E10000"));
    }

    @Test
    void valueOf_stringOutOfRange_throwException() {
        var exception = assertThrows(DecimalOverflowException.class, () ->
                Decimal32.valueOf("9.9E10000", OverflowHandling.THROW_EXCEPTION));
        assertEquals("The scale -9999 is out of range for this type", exception.getMessage());
    }

    @Test
    void toDecimal_Decimal32_Decimal32() {
        Decimal32 value = Decimal32.valueOf("1.23456");

        assertSame(value, value.toDecimal(Decimal32.class));
    }

    @Test
    void toDecimal_Decimal32_Decimal64() {
        Decimal32 value = Decimal32.valueOf("1.23456");

        assertEquals(Decimal64.valueOf("1.23456"), value.toDecimal(Decimal64.class));
    }

    @Test
    void toDecimal_Decimal32_Decimal128() {
        Decimal32 value = Decimal32.valueOf("1.23456");

        assertEquals(Decimal128.valueOf("1.23456"), value.toDecimal(Decimal128.class));
    }

    @Test
    void validateConstant_POSITIVE_ZERO() {
        assertEquals(BigDecimal.ZERO, POSITIVE_ZERO.toBigDecimal());
        assertEquals(Signum.POSITIVE, POSITIVE_ZERO.signum());
    }

    @Test
    void validateConstant_NEGATIVE_ZERO() {
        assertEquals(BigDecimal.ZERO, NEGATIVE_ZERO.toBigDecimal());
        assertEquals(Signum.NEGATIVE, NEGATIVE_ZERO.signum());
    }

    @Test
    void negate_One() {
        Decimal32 negativeOne = Decimal32.valueOf(BigDecimal.ONE).negate();

        assertEquals(Decimal32.valueOf(BigDecimal.ONE.negate()),
                Decimal32.valueOf(BigDecimal.ONE).negate());
        assertEquals(BigDecimal.ONE.negate(), negativeOne.toBigDecimal());
        assertEquals(Signum.NEGATIVE, negativeOne.signum());
    }

    @Test
    void negate_positiveZero() {
        Decimal32 negativeZero = POSITIVE_ZERO.negate();

        assertEquals(NEGATIVE_ZERO, negativeZero);
        assertEquals(BigDecimal.ZERO, negativeZero.toBigDecimal());
        assertEquals(Signum.NEGATIVE, negativeZero.signum());
    }

    @Test
    void negate_negativeZero() {
        Decimal32 positiveZero = NEGATIVE_ZERO.negate();

        assertEquals(POSITIVE_ZERO, positiveZero);
        assertEquals(BigDecimal.ZERO, positiveZero.toBigDecimal());
        assertEquals(Signum.POSITIVE, positiveZero.signum());
    }

    @Test
    void negate_positiveInfinity() {
        assertEquals(Decimal32.NEGATIVE_INFINITY, Decimal32.POSITIVE_INFINITY.negate());
    }

    @Test
    void negate_negativeInfinity() {
        assertEquals(Decimal32.POSITIVE_INFINITY, Decimal32.NEGATIVE_INFINITY.negate());
    }

    @Test
    void negate_positiveNaN() {
        assertEquals(Decimal32.NEGATIVE_NAN, Decimal32.POSITIVE_NAN.negate());
    }

    @Test
    void negate_negativeNaN() {
        assertEquals(Decimal32.POSITIVE_NAN, Decimal32.NEGATIVE_NAN.negate());
    }

    @Test
    void negate_positiveSignallingNaN() {
        assertEquals(Decimal32.NEGATIVE_SIGNALING_NAN, Decimal32.POSITIVE_SIGNALING_NAN.negate());
    }

    @Test
    void negate_negativeSignallingNaN() {
        assertEquals(Decimal32.POSITIVE_SIGNALING_NAN, Decimal32.NEGATIVE_SIGNALING_NAN.negate());
    }

    @Test
    void valueOfExact_BigInteger_min() {
        final BigInteger value = new BigInteger("-9999999");

        assertEquals(new BigDecimal("-9999999"), Decimal32.valueOfExact(value).toBigDecimal());
    }

    @Test
    void valueOfExact_BigInteger_min_minusOne() {
        final BigInteger value = new BigInteger("-9999999").subtract(BigInteger.ONE);

        assertThrows(DecimalOverflowException.class, () -> Decimal32.valueOfExact(value));
    }

    @Test
    void valueOfExact_BigInteger_max() {
        final BigInteger value = new BigInteger("9999999");

        assertEquals(new BigDecimal("9999999"), Decimal32.valueOfExact(value).toBigDecimal());
    }

    @Test
    void valueOfExact_BigInteger_max_plusOne() {
        final BigInteger value = new BigInteger("9999999").add(BigInteger.ONE);

        assertThrows(DecimalOverflowException.class, () -> Decimal32.valueOfExact(value));
    }

    @Test
    void valueOf_BigInteger_min() {
        final BigInteger value = new BigInteger("-9999999");

        assertEquals(new BigDecimal("-9999999"), Decimal32.valueOf(value).toBigDecimal());
    }

    @Test
    void valueOf_BigInteger_min_minusOne() {
        final BigInteger value = new BigInteger("-9999999").subtract(BigInteger.ONE);

        assertEquals(new BigDecimal("-1000000E+1"), Decimal32.valueOf(value).toBigDecimal());
    }

    @Test
    void valueOf_BigInteger_max() {
        final BigInteger value = new BigInteger("9999999");

        assertEquals(new BigDecimal("9999999"), Decimal32.valueOf(value).toBigDecimal());
    }

    @Test
    void valueOf_BigInteger_max_plusOne() {
        final BigInteger value = new BigInteger("9999999").add(BigInteger.ONE);

        assertEquals(new BigDecimal("1000000E+1"), Decimal32.valueOf(value).toBigDecimal());
    }

}
