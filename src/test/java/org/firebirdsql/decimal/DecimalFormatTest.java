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

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test to check if the decimal constants match the specification.
 *
 * @author Mark Rotteveel
 */
class DecimalFormatTest {

    @Test
    void decimal32() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal32;
        assertEquals(32, decimalFormat.formatBitLength, "formatBitLength");
        assertEquals(4, decimalFormat.formatByteLength, "formatByteLength");
        assertEquals(7, decimalFormat.coefficientDigits, "coefficientDigits");
        assertEquals(6, decimalFormat.exponentContinuationBits, "exponentContinuationBits");
        assertEquals(20, decimalFormat.coefficientContinuationBits, "coefficientContinuationBits");
        assertEquals(191, decimalFormat.eLimit, "eLimit");
        assertEquals(101, decimalFormat.biasedExponent(0), "exponentBias");
    }

    @Test
    void decimal32_validateCoefficient_min() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal32;
        final BigInteger value = new BigInteger("-9999999");

        assertSame(value, decimalFormat.validateCoefficient(value));
    }

    @Test
    void decimal32_validateCoefficient_min_minusOne() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal32;
        final BigInteger value = new BigInteger("-9999999").subtract(BigInteger.ONE);

        assertThrows(DecimalOverflowException.class, () -> decimalFormat.validateCoefficient(value));
    }

    @Test
    void decimal32_validateCoefficient_max() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal32;
        final BigInteger value = new BigInteger("9999999");

        assertSame(value, decimalFormat.validateCoefficient(value));
    }

    @Test
    void decimal32_validateCoefficient_max_plusOne() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal32;
        final BigInteger value = new BigInteger("9999999").add(BigInteger.ONE);

        assertThrows(DecimalOverflowException.class, () -> decimalFormat.validateCoefficient(value));
    }

    @Test
    void decimal64() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal64;
        assertEquals(64, decimalFormat.formatBitLength, "formatBitLength");
        assertEquals(8, decimalFormat.formatByteLength, "formatByteLength");
        assertEquals(16, decimalFormat.coefficientDigits, "coefficientDigits");
        assertEquals(8, decimalFormat.exponentContinuationBits, "exponentContinuationBits");
        assertEquals(50, decimalFormat.coefficientContinuationBits, "coefficientContinuationBits");
        assertEquals(767, decimalFormat.eLimit, "eLimit");
        assertEquals(398, decimalFormat.biasedExponent(0), "exponentBias");
    }

    @Test
    void decimal64_validateCoefficient_min() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal64;
        final BigInteger value = new BigInteger("-9999999999999999");

        assertSame(value, decimalFormat.validateCoefficient(value));
    }

    @Test
    void decimal64_validateCoefficient_min_minusOne() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal64;
        final BigInteger value = new BigInteger("-9999999999999999").subtract(BigInteger.ONE);

        assertThrows(DecimalOverflowException.class, () -> decimalFormat.validateCoefficient(value));
    }

    @Test
    void decimal64_validateCoefficient_max() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal64;
        final BigInteger value = new BigInteger("9999999999999999");

        assertSame(value, decimalFormat.validateCoefficient(value));
    }

    @Test
    void decimal64_validateCoefficient_max_plusOne() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal64;
        final BigInteger value = new BigInteger("9999999999999999").add(BigInteger.ONE);

        assertThrows(DecimalOverflowException.class, () -> decimalFormat.validateCoefficient(value));
    }

    @Test
    void decimal128() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal128;
        assertEquals(128, decimalFormat.formatBitLength, "formatBitLength");
        assertEquals(16, decimalFormat.formatByteLength, "formatByteLength");
        assertEquals(34, decimalFormat.coefficientDigits, "coefficientDigits");
        assertEquals(12, decimalFormat.exponentContinuationBits, "exponentContinuationBits");
        assertEquals(110, decimalFormat.coefficientContinuationBits, "coefficientContinuationBits");
        assertEquals(12287, decimalFormat.eLimit, "eLimit");
        assertEquals(6176, decimalFormat.biasedExponent(0), "exponentBias");
    }

    @Test
    void decimal128_validateCoefficient_min() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal128;
        final BigInteger value = new BigInteger("-9999999999999999999999999999999999");

        assertSame(value, decimalFormat.validateCoefficient(value));
    }

    @Test
    void decimal128_validateCoefficient_min_minusOne() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal128;
        final BigInteger value = new BigInteger("-9999999999999999999999999999999999").subtract(BigInteger.ONE);

        assertThrows(DecimalOverflowException.class, () -> decimalFormat.validateCoefficient(value));
    }

    @Test
    void decimal128_validateCoefficient_max() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal128;
        final BigInteger value = new BigInteger("9999999999999999999999999999999999");

        assertSame(value, decimalFormat.validateCoefficient(value));
    }

    @Test
    void decimal128_validateCoefficient_max_plusOne() {
        final DecimalFormat decimalFormat = DecimalFormat.Decimal128;
        final BigInteger value = new BigInteger("9999999999999999999999999999999999").add(BigInteger.ONE);

        assertThrows(DecimalOverflowException.class, () -> decimalFormat.validateCoefficient(value));
    }

}