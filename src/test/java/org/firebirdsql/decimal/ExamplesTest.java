/*
 * Copyright (c) 2019 Firebird development team and individual contributors
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the examples in README.md.
 *
 * @author <a href="mailto:mark@lawinegevaar.nl">Mark Rotteveel</a>
 */
class ExamplesTest {

    @Test
    void decimal32ParseBytes() {
        byte[] bytes = { (byte) 0xc7, (byte) 0xf4, (byte) 0xd2, (byte) 0xe7 };
        Decimal32 decimal32 = Decimal32.parseBytes(bytes);
        BigDecimal bigDecimal = decimal32.toBigDecimal();
        assertEquals(new BigDecimal("-1.234567E+96"), bigDecimal);
    }

    @Test
    void decimal32ToBytes() {
        BigDecimal bigDecimal = new BigDecimal("-7.50E-7");
        Decimal32 decimal32 = Decimal32.valueOf(bigDecimal);
        byte[] bytes = decimal32.toBytes();
        assertArrayEquals(new byte[] { (byte) 0xa1, (byte) 0xc0, 0x03, (byte) 0xd0 }, bytes);
    }

    @Test
    void decimal32ToBytes_overflowThrowException() {
        BigDecimal bigDecimal = new BigDecimal("-7.50E-7");
        Decimal32 decimal32 = Decimal32.valueOf(bigDecimal, OverflowHandling.THROW_EXCEPTION);
        byte[] bytes = decimal32.toBytes();
        assertArrayEquals(new byte[] { (byte) 0xa1, (byte) 0xc0, 0x03, (byte) 0xd0 }, bytes);
    }
}
