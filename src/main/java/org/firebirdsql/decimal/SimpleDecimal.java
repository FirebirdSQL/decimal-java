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
        if (type == DecimalType.NORMAL) {
            return new SimpleDecimal(-1 * signum, coefficient, exponent);
        }
        return getSpecialConstant(type, -1 * signum);
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
