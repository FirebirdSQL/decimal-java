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

import java.math.BigDecimal;

/**
 * An IEEE-754 Decimal32.
 *
 * @author <a href="mailto:mark@lawinegevaar.nl">Mark Rotteveel</a>
 */
public final class Decimal32 extends AbstractDecimal<Decimal32> {

    public static final Decimal32 POSITIVE_INFINITY = new Decimal32(Signum.POSITIVE, DecimalType.INFINITY);
    public static final Decimal32 NEGATIVE_INFINITY = new Decimal32(Signum.NEGATIVE, DecimalType.INFINITY);
    public static final Decimal32 POSITIVE_NAN = new Decimal32(Signum.POSITIVE, DecimalType.NAN);
    public static final Decimal32 NEGATIVE_NAN = new Decimal32(Signum.NEGATIVE, DecimalType.NAN);
    public static final Decimal32 POSITIVE_SIGNALING_NAN = new Decimal32(Signum.POSITIVE, DecimalType.SIGNALING_NAN);
    public static final Decimal32 NEGATIVE_SIGNALING_NAN = new Decimal32(Signum.NEGATIVE, DecimalType.SIGNALING_NAN);

    private static final Decimal32Factory DECIMAL_32_FACTORY = new Decimal32Factory();
    private static final DecimalCodec<Decimal32> DECIMAL_32_CODEC = new DecimalCodec<>(DECIMAL_32_FACTORY);

    private Decimal32(int signum, DecimalType decimalType) {
        super(signum, decimalType);
    }

    private Decimal32(int signum, BigDecimal bigDecimal) {
        super(signum, bigDecimal);
    }

    @Override
    DecimalCodec<Decimal32> getDecimalCodec() {
        return DECIMAL_32_CODEC;
    }

    @Override
    DecimalFactory<Decimal32> getDecimalFactory() {
        return DECIMAL_32_FACTORY;
    }

    public static Decimal32 parseBytes(final byte[] decBytes) {
        return DECIMAL_32_CODEC.parseBytes(decBytes);
    }

    /**
     * Creates a {@code Decimal32} from {@code value}, applying rounding where necessary.
     * <p>
     * Values exceeding the range of this type will be returned as +/-Infinity.
     * </p>
     *
     * @param value
     *         Big decimal value to convert
     * @return Decimal32 equivalent
     */
    public static Decimal32 valueOf(final BigDecimal value) {
        return DECIMAL_32_FACTORY.valueOf(value);
    }

    /**
     * Creates a {@code Decimal32} from {@code value}, applying rounding where necessary.
     * <p>
     * Except for the special values [+/-]Inf, [+/-]Infinity, [+/-]NaN and [+/-]sNaN (case insensitive), the rules
     * of {@link BigDecimal#BigDecimal(String)} apply, with special handling in place to discern between positive
     * and negative zero.
     * </p>
     * <p>
     * Values exceeding the range of this type will be returned as +/-Infinity.
     * </p>
     *
     * @param value
     *         String value to convert
     * @return Decimal equivalent
     */
    public static Decimal32 valueOf(final String value) {
        return DECIMAL_32_FACTORY.valueOf(value);
    }

    private static class Decimal32Factory extends AbstractDecimalFactory<Decimal32> {

        private Decimal32Factory() {
            super(DecimalFormat.Decimal32,
                    POSITIVE_INFINITY, NEGATIVE_INFINITY,
                    POSITIVE_NAN, NEGATIVE_NAN,
                    POSITIVE_SIGNALING_NAN, NEGATIVE_SIGNALING_NAN);
        }

        @Override
        public Decimal32 createDecimal(int signum, BigDecimal value) {
            return new Decimal32(signum, validateRange(value));
        }

    }

}
