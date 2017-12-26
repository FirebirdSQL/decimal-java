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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import static org.firebirdsql.decimal.Signum.NEGATIVE;
import static org.firebirdsql.decimal.Signum.POSITIVE;

/**
 * Class with all relevant data of a decimal.
 *
 * @author <a href="mailto:mark@lawinegevaar.nl">Mark Rotteveel</a>
 */
final class SimpleDecimal {

    static final SimpleDecimal POSITIVE_INFINITY = new SimpleDecimal(POSITIVE, DecimalType.INFINITY);
    static final SimpleDecimal NEGATIVE_INFINITY = new SimpleDecimal(NEGATIVE, DecimalType.INFINITY);
    static final SimpleDecimal POSITIVE_NAN = new SimpleDecimal(POSITIVE, DecimalType.NAN);
    static final SimpleDecimal NEGATIVE_NAN = new SimpleDecimal(NEGATIVE, DecimalType.NAN);
    static final SimpleDecimal POSITIVE_SIGNALING_NAN = new SimpleDecimal(POSITIVE, DecimalType.SIGNALING_NAN);
    static final SimpleDecimal NEGATIVE_SIGNALING_NAN = new SimpleDecimal(NEGATIVE, DecimalType.SIGNALING_NAN);

    private final int signum;
    private final DecimalType type;
    private final BigInteger coefficient;
    private final int exponent;

    private SimpleDecimal(int signum, DecimalType type) {
        assert type != null : "Type should not be null";
        assert type != DecimalType.NORMAL : "Constructor only suitable for non-NORMAL";
        this.signum = signum;
        this.type = type;
        coefficient = null;
        exponent = Integer.MIN_VALUE;
    }

    SimpleDecimal(BigInteger coefficient, int exponent) {
        this.type = DecimalType.NORMAL;
        // Consider zero as positive
        this.signum = coefficient.equals(BigInteger.ZERO)
                ? POSITIVE
                : coefficient.signum();
        this.coefficient = coefficient;
        this.exponent = exponent;
    }

    SimpleDecimal(int signum, BigInteger coefficient, int exponent) {
        assert Objects.equals(coefficient, BigInteger.ZERO) || signum == coefficient.signum()
                : "signum value not consistent with coefficient";
        type = DecimalType.NORMAL;
        this.signum = signum;
        this.coefficient = coefficient;
        this.exponent = exponent;
    }

    DecimalType getType() {
        return type;
    }

    int getSignum() {
        return signum;
    }

    BigInteger getCoefficient() {
        return coefficient;
    }

    int getExponent() {
        return exponent;
    }

    /**
     * Rescales and then validates the decimal.
     * <p>
     * A rescale may not fit all requirements of the provided decimal format. This method will first rescale and then
     * validate the simple decimal.
     * </p>
     *
     * @param decimalFormat
     *         Decimal format
     * @return An instance of simple decimal that fits the requirements of the decimal format
     * @throws DecimalOverflowException If the coefficient or exponent is out of range even after (attempted) rescaling
     */
    SimpleDecimal rescaleAndValidate(DecimalFormat decimalFormat) {
        return rescale(decimalFormat)
                .validate(decimalFormat);
    }

    /**
     * Attempts to rescale this simple decimal to fit the requirements of the specified decimal format.
     * <p>
     * A decimal format has upper and lower limits for the exponent and coefficient, this method will try to
     * rescale a number so the exponent fits these upper and lower limits.
     * </p>
     * <p>
     * Rescaling has a number of limitations: reducing the exponent (if the existing exponent is too large) may lead to
     * a simple decimal that has a coefficient that is too long for the specified decimal format. Increasing
     * the exponent (if it is too small) will only work in as far there are zeroes as the least significant digit in the
     * coefficient.
     * </p>
     *
     * @param decimalFormat
     *         Decimal format
     * @return A new instance of {@code SimpleDecimal}, which may or may not fit the {@code decimalFormat}.
     */
    SimpleDecimal rescale(DecimalFormat decimalFormat) {
        if (type != DecimalType.NORMAL) {
            return this;
        }
        int biasedExponent = decimalFormat.biasedExponent(exponent);
        if (biasedExponent < 0) {
            return increaseExponent(-1 * biasedExponent);
        } else if (biasedExponent > decimalFormat.eLimit) {
            return decreaseExponent(biasedExponent - decimalFormat.eLimit, decimalFormat);
        }
        return this;
    }

    /**
     * Attempts to decreases the exponent by the requested number of steps.
     * <p>
     * The decrease of exponent is achieved by multiplying the coefficient by {@code 10^exponentDecrease}.
     * </p>
     * <p>
     * The decrease can only be achieved if smaller than the number of coefficient digits, except for a zero value. If
     * the requested decrease is larger, then no decrease is attempted and {@code this} simple decimal is returned
     * unchanged.
     * </p>
     *
     * @param exponentDecrease
     *         Requested decrease of the exponent
     * @return A simple decimal with decreased exponent, or this unchanged vaue
     */
    private SimpleDecimal decreaseExponent(int exponentDecrease, DecimalFormat decimalFormat) {
        assert exponentDecrease > 0 : "decreaseExponent requires > 0 value";
        if (coefficient.equals(BigInteger.ZERO)) {
            return new SimpleDecimal(BigInteger.ZERO, exponent - exponentDecrease);
        } else if (exponentDecrease > decimalFormat.coefficientDigits - 1) {
            // attempting exponent decrease is useless (and this avoids multiplication below)
            return this;
        }
        BigInteger newCoefficient = coefficient.multiply(BigInteger.TEN.pow(exponentDecrease));
        return new SimpleDecimal(newCoefficient, exponent - exponentDecrease);
    }

    /**
     * Attempts to increase the exponent by the requested number of steps.
     * <p>
     * The increase of exponent is achieved by chopping of zeroes from the coefficient.
     * </p>
     * <p>
     * This increase can only be achieved if there are sufficient least significant digits with value
     * zero ({@code '0'}), otherwise either a partially increased value or {@code this} simple decimal may be returned.
     * </p>
     *
     * @param exponentIncrease
     *         Requested increase of the exponent
     * @return A simple decimal which may have its exponent increased, this increase may be less than requested.
     */
    private SimpleDecimal increaseExponent(int exponentIncrease) {
        assert exponentIncrease > 0 : "increaseExponent requires > 0 value";
        if (coefficient.equals(BigInteger.ZERO)) {
            return new SimpleDecimal(BigInteger.ZERO, exponent + exponentIncrease);
        }
        BigInteger newCoefficient = coefficient;
        int newExponent = exponent;
        BigInteger[] divAndRemainder = newCoefficient.divideAndRemainder(BigInteger.TEN);
        while (exponentIncrease-- > 0 && divAndRemainder[1].equals(BigInteger.ZERO)) {
            newExponent++;
            newCoefficient = divAndRemainder[0];
            divAndRemainder = newCoefficient.divideAndRemainder(BigInteger.TEN);
        }
        if (newCoefficient.equals(coefficient) && newExponent == exponent) {
            return this;
        }
        return new SimpleDecimal(newCoefficient, newExponent);
    }

    /**
     * Validates a {@code SimpleDecimal}.
     *
     * @param decimalFormat
     *         Decimal format
     * @return The {@code simpleDecimal}
     * @throws DecimalOverflowException
     *         If the coefficient or exponent are out of range.
     */
    final SimpleDecimal validate(DecimalFormat decimalFormat) {
        if (type == DecimalType.NORMAL) {
            validateExponent0(decimalFormat);
            validateCoefficient0(decimalFormat);
        }
        return this;
    }

    /**
     * Validates and returns the exponent if within range, otherwise throws an {@code DecimalOverflowException}.
     * <p>
     * In cases where the exponent is out of range, rescaling may be appropriate, this validation does not take
     * rescaling into consideration.
     * </p>
     *
     * @param decimalFormat
     *         Decimal format
     * @return {@code exponent}
     * @throws DecimalOverflowException
     *         If the exponent is out of range.
     */
    final int validateExponent(DecimalFormat decimalFormat) {
        validateExponent0(decimalFormat);
        return exponent;
    }

    private void validateExponent0(DecimalFormat decimalFormat) {
        final int biasedExponent = decimalFormat.biasedExponent(exponent);
        if (biasedExponent < 0 || biasedExponent > decimalFormat.eLimit) {
            throw new DecimalOverflowException("Exponent is out of range, exponent: " + exponent);
        }
    }

    /**
     * Validates and returns the coefficient if within range, otherwise throws an {@code IllegalArgumentException}.
     * <p>
     * In cases where the coefficient is out of range, rescaling may be appropriate, this validation does not take
     * rescaling into consideration.
     * </p>
     *
     * @param decimalFormat
     *         Decimal format
     * @return {@code coefficient}
     * @throws DecimalOverflowException
     *         If the coefficient is out of range.
     */
    final BigInteger validateCoefficient(DecimalFormat decimalFormat) {
        validateCoefficient0(decimalFormat);
        return coefficient;
    }

    private void validateCoefficient0(DecimalFormat decimalFormat) {
        if (decimalFormat.isCoefficientInRange(coefficient)) {
            throw new DecimalOverflowException("Coefficient is out of range, coefficient: " + coefficient);
        }
    }

    /**
     * Converts this decimal to a {@code BigDecimal}.
     *
     * @return Value as BigDecimal
     * @throws NumberFormatException
     *         If this value is a NaN or Infinity, which can't be represented as a {@code BigDecimal).
     */
    BigDecimal toBigDecimal() {
        if (getType() != DecimalType.NORMAL) {
            throw new NumberFormatException("Value " + toString() + " cannot be converted to a BigDecimal");
        }
        return new BigDecimal(getCoefficient(), -1 * getExponent());
    }

    SimpleDecimal negate() {
        if (type != DecimalType.NORMAL) {
            return getSpecialConstant(type, -1 * signum);
        } else if (coefficient.equals(BigInteger.ZERO)) {
            return new SimpleDecimal(-1 * signum, BigInteger.ZERO, exponent);
        } else {
            return new SimpleDecimal(-1 * signum, coefficient.negate(), exponent);
        }
    }

    static SimpleDecimal getSpecialConstant(DecimalType decimalType, int signum) {
        switch (decimalType) {
        case INFINITY:
            return signum == NEGATIVE
                    ? NEGATIVE_INFINITY
                    : POSITIVE_INFINITY;

        case NAN:
            return signum == NEGATIVE
                    ? NEGATIVE_NAN
                    : POSITIVE_NAN;

        case SIGNALING_NAN:
            return signum == NEGATIVE
                    ? NEGATIVE_SIGNALING_NAN
                    : POSITIVE_SIGNALING_NAN;

        default:
            throw new AssertionError("Invalid special value for decimalType " + decimalType);
        }
    }

    public static SimpleDecimal valueOf(BigDecimal bigDecimal) {
        return new SimpleDecimal(bigDecimal.unscaledValue(), -1 * bigDecimal.scale());
    }

    @Override
    public String toString() {
        switch (type) {
        case NORMAL:
            return coefficient + (exponent != 0 ? "E" + exponent : "");
        case INFINITY:
            return signum == -1 ? "-Infinity" : "+Infinity";
        case NAN:
        case SIGNALING_NAN:
            return signum == -1 ? "-NaN" : "+NaN";
        default:
            throw new IllegalStateException("Unsupported DecimalType " + type);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof SimpleDecimal)) return false;

        SimpleDecimal simpleDecimal = (SimpleDecimal) o;

        if (signum != simpleDecimal.signum) return false;
        if (exponent != simpleDecimal.exponent) return false;
        if (type != simpleDecimal.type) return false;
        return coefficient != null ? coefficient.equals(simpleDecimal.coefficient) : simpleDecimal.coefficient == null;
    }

    @Override
    public int hashCode() {
        int result = signum;
        result = 31 * result + type.hashCode();
        result = 31 * result + (coefficient != null ? coefficient.hashCode() : 0);
        result = 31 * result + exponent;
        return result;
    }

}
