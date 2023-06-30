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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecimalTypeFromFirstByteTest {

    private static final int SPECIALS_MASK = 0b0_11110_00;

    @ParameterizedTest(name = "{index}: {0} => {1}")
    @MethodSource("data")
    void testFromFirstByte_byte(int firstByte, DecimalType decimalType) {
        assertEquals(decimalType, DecimalType.fromFirstByte((byte) firstByte));
    }

//    TODO Add tests with high-bits set?
//    @Test
//    public void testFromFirstByte_int() {
//        assertEquals(decimalType, DecimalType.fromFirstByte(firstByte));
//    }

    static Stream<Arguments> data() {
        return Stream.concat(
                Stream.of(
                        testCase(0b0_11110_00, DecimalType.INFINITY),
                        testCase(0b1_11110_00, DecimalType.INFINITY),
                        testCase(0b0_11110_10, DecimalType.INFINITY),
                        testCase(0b1_11110_10, DecimalType.INFINITY),
                        testCase(0b0_11111_00, DecimalType.NAN),
                        testCase(0b1_11111_00, DecimalType.NAN),
                        testCase(0b0_11111_10, DecimalType.SIGNALING_NAN),
                        testCase(0b1_11111_10, DecimalType.SIGNALING_NAN),
                        testCase(0b0_11110_01, DecimalType.INFINITY),
                        testCase(0b1_11110_01, DecimalType.INFINITY),
                        testCase(0b0_11110_11, DecimalType.INFINITY),
                        testCase(0b1_11110_11, DecimalType.INFINITY),
                        testCase(0b0_11111_01, DecimalType.NAN),
                        testCase(0b1_11111_01, DecimalType.NAN),
                        testCase(0b0_11111_11, DecimalType.SIGNALING_NAN),
                        testCase(0b1_11111_11, DecimalType.SIGNALING_NAN)),
                IntStream.rangeClosed(0, 0XFF)
                        .filter(value -> (value & SPECIALS_MASK) != SPECIALS_MASK)
                        .mapToObj(value -> testCase(value, DecimalType.FINITE)));
    }

    private static Arguments testCase(int firstByte, DecimalType decimalType) {
        return Arguments.of(firstByte, decimalType);
    }

}