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

import static java.util.Objects.requireNonNull;

/**
 * Abstract base class for IEEE-754 decimals.
 *
 * @author <a href="mailto:mark@lawinegevaar.nl">Mark Rotteveel</a>
 */
abstract class AbstractDecimal<T extends AbstractDecimal<T>> {

    private final SimpleDecimal value;

    AbstractDecimal(SimpleDecimal value) {
        this.value = requireNonNull(value, "value cannot be null");
    }

    /**
     * Converts this decimal to a {@code BigDecimal}.
     *
     * @return Value as BigDecimal
     * @throws NumberFormatException
     *         If this value is a NaN or Infinity, which can't be represented as a {@code BigDecimal).
     */
    public final BigDecimal toBigDecimal() {
        return value.toBigDecimal();
    }

    /**
     * Converts this decimal to its IEEE-754 byte encoding.
     *
     * @return byte array
     */
    public final byte[] toBytes() {
        return getDecimalCodec().encodeDecimal(getValue());
    }

    /**
     * @return The codec for this decimal type.
     */
    abstract DecimalCodec getDecimalCodec();

    final DecimalType getType() {
        return value.getType();
    }

    final int getSignum() {
        return value.getSignum();
    }

    final SimpleDecimal getValue() {
        return value;
    }

    /**
     * Negates this decimal (positive to negative, negative to positive).
     *
     * @return Negated value
     */
    abstract T negate();

    @Override
    public final String toString() {
        return getClass().getSimpleName() + ": " + value.toString();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractDecimal decimal = (AbstractDecimal) o;

        return value.equals(decimal.value);
    }

    @Override
    public final int hashCode() {
        return value.hashCode();
    }
}
