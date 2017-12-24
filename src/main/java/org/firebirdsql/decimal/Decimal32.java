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
import java.math.MathContext;

import static org.firebirdsql.decimal.Signum.NEGATIVE;

/**
 * An IEEE-754 Decimal32.
 *
 * @author <a href="mailto:mark@lawinegevaar.nl">Mark Rotteveel</a>
 */
public final class Decimal32 extends AbstractDecimal<Decimal32> {

    public static final Decimal32 POSITIVE_INFINITY = new Decimal32(SimpleDecimal.POSITIVE_INFINITY);
    public static final Decimal32 NEGATIVE_INFINITY = new Decimal32(SimpleDecimal.NEGATIVE_INFINITY);
    public static final Decimal32 POSITIVE_NAN = new Decimal32(SimpleDecimal.POSITIVE_NAN);
    public static final Decimal32 NEGATIVE_NAN = new Decimal32(SimpleDecimal.NEGATIVE_NAN);
    public static final Decimal32 POSITIVE_SIGNALING_NAN = new Decimal32(SimpleDecimal.POSITIVE_SIGNALING_NAN);
    public static final Decimal32 NEGATIVE_SIGNALING_NAN = new Decimal32(SimpleDecimal.NEGATIVE_SIGNALING_NAN);

    private static final DecimalCodec CODEC = new DecimalCodec(DecimalFormat.Decimal32);

    private Decimal32(SimpleDecimal value) {
        super(DecimalFormat.Decimal32.validate(value));
    }

    @Override
    Decimal32 negate() {
        final DecimalType type = getType();
        if (type == DecimalType.NORMAL) {
            return new Decimal32(getValue().negate());
        }
        return getSpecialConstant(type, getSignum());
    }

    @Override
    DecimalCodec getDecimalCodec() {
        return CODEC;
    }

    public static Decimal32 parseBytes(final byte[] decBytes) {
        SimpleDecimal value = CODEC.parseBytes(decBytes);
        final DecimalType decimalType = value.getType();
        if (decimalType != DecimalType.NORMAL) {
            return getSpecialConstant(decimalType, value.getSignum());
        } else {
            return new Decimal32(value);
        }
    }

    /**
     * Creates a {@code Decimal32} from the provided {@code BigDecimal}, rounding if necessary.
     *
     * @param bigDecimal BigDecimal to convert
     * @return Decimal32 equivalent
     * @throws IllegalArgumentException if the exponent ({@code -1 * scale}) is out of range
     * @see #valueOfExact(BigDecimal)
     */
    public static Decimal32 valueOf(final BigDecimal bigDecimal) {
        return valueOfExact(bigDecimal.round(MathContext.DECIMAL32));
    }

    /**
     * Creates a {@code Decimal32} from the provided {@code BigDecimal}.
     *
     * @param bigDecimal BigDecimal to convert
     * @return Decimal32 equivalent
     * @throws IllegalArgumentException if the exponent ({@code -1 * scale}) or the coefficient is out of range
     */
    public static Decimal32 valueOfExact(final BigDecimal bigDecimal) {
        return new Decimal32(SimpleDecimal.valueOf(bigDecimal));
    }

    private static Decimal32 getSpecialConstant(DecimalType decimalType, int signum) {
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
