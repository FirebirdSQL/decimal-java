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

import java.math.BigInteger;
import java.util.Arrays;

import static org.firebirdsql.decimal.DenselyPackedDecimalCodec.BITS_PER_GROUP;
import static org.firebirdsql.decimal.DenselyPackedDecimalCodec.DIGITS_PER_GROUP;

/**
 * Constant values for the decimal 32, 64 and 128 formats.
 *
 * @author <a href="mailto:mark@lawinegevaar.nl">Mark Rotteveel</a>
 */
enum DecimalFormat {

    Decimal32(32, 7),
    Decimal64(64, 16),
    Decimal128(128, 34);

    private static final int SIGN_BITS = 1;
    private static final int COMBINATION_BITS = 5;

    final int formatBitLength;
    final int formatByteLength;
    final int coefficientDigits;
    final int exponentContinuationBits;
    final int coefficientContinuationBits;
    final int eLimit;
    private final int eMax;
    private final int eMin;
    private final int exponentBias;
    private final BigInteger maxCoefficient;
    private final BigInteger minCoefficient;

    DecimalFormat(int formatBitLength, int coefficientDigits) {
        assert formatBitLength > 0 && formatBitLength % 8 == 0;
        this.formatBitLength = formatBitLength;
        formatByteLength = formatBitLength / 8;
        this.coefficientDigits = coefficientDigits;
        coefficientContinuationBits = calculateCoefficientContinuationBits(coefficientDigits);
        exponentContinuationBits = calculateExponentContinuationBits(formatBitLength, coefficientContinuationBits);
        eLimit = calculateExponentLimit(exponentContinuationBits);
        eMin = -eLimit / 2;
        eMax = -eMin + 1;
        exponentBias = -eMin + coefficientDigits - 1;
        char[] digits = new char[coefficientDigits];
        Arrays.fill(digits, '9');
        maxCoefficient = new BigInteger(new String(digits));
        minCoefficient = maxCoefficient.negate();
    }

    final int biasedExponent(int unbiasedExponent) {
        return unbiasedExponent + exponentBias;
    }

    final int unbiasedExponent(int biasedExponent) {
        return biasedExponent - exponentBias;
    }

    /**
     * Validates the format length.
     *
     * @param decBytes
     *         Decimal bytes
     * @throws IllegalArgumentException
     *         If the byte array has the wrong length
     */
    final void validateByteLength(byte[] decBytes) {
        if (decBytes.length != formatByteLength) {
            throw new IllegalArgumentException("decBytes argument must be " + formatByteLength + " bytes");
        }
    }

    final boolean isCoefficientInRange(BigInteger coefficient) {
        return minCoefficient.compareTo(coefficient) > 0 || maxCoefficient.compareTo(coefficient) < 0;
    }

    private static int calculateCoefficientContinuationBits(int coefficientDigits) {
        return BITS_PER_GROUP * (coefficientDigits - 1) / DIGITS_PER_GROUP;
    }

    private static int calculateExponentContinuationBits(int formatBitLength, int coefficientContinuationBits) {
        return formatBitLength - SIGN_BITS - COMBINATION_BITS - coefficientContinuationBits;
    }

    private static int calculateExponentLimit(int exponentContinuationBits) {
        return 3 * (1 << exponentContinuationBits) - 1;
    }

}
