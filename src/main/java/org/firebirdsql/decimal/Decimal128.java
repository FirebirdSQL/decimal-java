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

import static org.firebirdsql.decimal.Signum.NEGATIVE;

/**
 * An IEEE-754 Decimal128.
 *
 * @author <a href="mailto:mark@lawinegevaar.nl">Mark Rotteveel</a>
 */
public final class Decimal128 extends AbstractDecimal<Decimal128> {

    public static final Decimal128 POSITIVE_INFINITY = new Decimal128(SimpleDecimal.POSITIVE_INFINITY);
    public static final Decimal128 NEGATIVE_INFINITY = new Decimal128(SimpleDecimal.NEGATIVE_INFINITY);
    public static final Decimal128 POSITIVE_NAN = new Decimal128(SimpleDecimal.POSITIVE_NAN);
    public static final Decimal128 NEGATIVE_NAN = new Decimal128(SimpleDecimal.NEGATIVE_NAN);
    public static final Decimal128 POSITIVE_SIGNALING_NAN = new Decimal128(SimpleDecimal.POSITIVE_SIGNALING_NAN);
    public static final Decimal128 NEGATIVE_SIGNALING_NAN = new Decimal128(SimpleDecimal.NEGATIVE_SIGNALING_NAN);

    private static final DecimalCodec CODEC = new DecimalCodec(DecimalFormat.Decimal128);

    private Decimal128(SimpleDecimal value) {
        super(value.rescaleAndValidate(DecimalFormat.Decimal128));
    }

    @Override
    Decimal128 negate() {
        final DecimalType type = getType();
        if (type == DecimalType.NORMAL) {
            return new Decimal128(getValue().negate());
        }
        return getSpecialConstant(type, -1 * getSignum());
    }

    @Override
    DecimalCodec getDecimalCodec() {
        return CODEC;
    }

    public static Decimal128 parseBytes(final byte[] decBytes) {
        SimpleDecimal value = CODEC.parseBytes(decBytes);
        final DecimalType decimalType = value.getType();
        if (decimalType != DecimalType.NORMAL) {
            return getSpecialConstant(decimalType, value.getSignum());
        } else {
            return new Decimal128(value);
        }
    }

    /**
     * Creates a {@code Decimal128} from the provided {@code BigDecimal}.
     *
     * @param bigDecimal
     *         BigDecimal to convert
     * @return Decimal128 equivalent
     * @throws DecimalOverflowException
     *         if the exponent ({@code -1 * scale}) or the coefficient is out of range
     */
    public static Decimal128 valueOfExact(final BigDecimal bigDecimal) {
        return new Decimal128(SimpleDecimal.valueOf(bigDecimal));
    }

    static Decimal128 valueOf(SimpleDecimal simpleDecimal) {
        return new Decimal128(simpleDecimal);
    }

    private static Decimal128 getSpecialConstant(DecimalType decimalType, int signum) {
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
}
