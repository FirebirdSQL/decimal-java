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

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecimalCodecTest {

    @Test
    void testCalculateExponent() {
        assertEquals(0b01000001, DecimalCodec.decodeExponent(new byte[] { -1, 0b0100 }, 1, 6));
        assertEquals(0b01000011, DecimalCodec.decodeExponent(new byte[] { -1, 0b1111 }, 1, 6));
        assertEquals(0b01000010, DecimalCodec.decodeExponent(new byte[] { -1, 0b01000 }, 1, 6));
        assertEquals(0b01000100, DecimalCodec.decodeExponent(new byte[] { -1, 0b010000 }, 1, 6));
        assertEquals(0b01001000, DecimalCodec.decodeExponent(new byte[] { -1, 0b0100000 }, 1, 6));
        assertEquals(0b01010000, DecimalCodec.decodeExponent(new byte[] { -1, 0b01000000 }, 1, 6));
        assertEquals(0b01100000, DecimalCodec.decodeExponent(new byte[] { -1, (byte) 0b10000000 }, 1, 6));
        assertEquals(0b0110000, DecimalCodec.decodeExponent(new byte[] { -1, (byte) 0b10000000 }, 1, 5));
        assertEquals(0b011000, DecimalCodec.decodeExponent(new byte[] { -1, (byte) 0b10000000 }, 1, 4));
        assertEquals(0b01100, DecimalCodec.decodeExponent(new byte[] { -1, (byte) 0b10000000 }, 1, 3));
        assertEquals(0b0110, DecimalCodec.decodeExponent(new byte[] { -1, (byte) 0b10000000 }, 1, 2));
        assertEquals(0b011, DecimalCodec.decodeExponent(new byte[] { -1, (byte) 0b10000000 }, 1, 1));
        assertEquals(0b01, DecimalCodec.decodeExponent(new byte[] { -1, (byte) 0b10000000 }, 1, 0));
        assertEquals(0b0100001111, DecimalCodec.decodeExponent(new byte[] { -1, 0b1111 }, 1, 8));
        assertEquals(0b0100001100, DecimalCodec.decodeExponent(new byte[] { -1, 0b1100 }, 1, 8));
        assertEquals(0b01000011111, DecimalCodec.decodeExponent(new byte[] { -1, 0b1111, -1 }, 1, 9));
        assertEquals(0b01000011001, DecimalCodec.decodeExponent(new byte[] { -1, 0b1100, -1 }, 1, 9));
        assertEquals(0b010000110011, DecimalCodec.decodeExponent(new byte[] { -1, 0b1100, -1 }, 1, 10));
    }

}