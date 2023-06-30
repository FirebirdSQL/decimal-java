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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the {@link Decimal32#valueOf(String)} method, the rounding applied, and handling of specials.
 * <p>
 * Indirectly also tests {@link Decimal32#valueOf(BigDecimal)} and {@link Decimal32#toString()}.
 * </p>
 *
 * @author Mark Rotteveel
 */
class Decimal32ValueOfTest {

    @ParameterizedTest(name = "{index}: value {0} (expect {1}))")
    @MethodSource("data")
    void valueOfTest(String sourceValue, String expectedValue) {
        Decimal32 value = Decimal32.valueOf(sourceValue);

        assertEquals(expectedValue, value.toString());
    }

    static Stream<Arguments> data() {
        return Stream.of(
                // Zero
                testCase("0", "0"),
                testCase("+0", "0"),
                testCase("-0", "-0"),
                // Clamping zeroes
                testCase("0E+91", "0E+90"),
                testCase("0E+100", "0E+90"),
                testCase("0E-102", "0E-101"),
                testCase("0E-200", "0E-101"),
                // edge cases
                testCase("1234567E+0", "1234567"),
                testCase("9999999E+0", "9999999"),
                testCase("1234567E+90", "1.234567E+96"),
                testCase("9999999E+90", "9.999999E+96"),
                testCase("1234567E-101", "1.234567E-95"),
                testCase("9999999E-101", "9.999999E-95"),
                // rounding by multiplying succeeds
                testCase("123456E+91", "1.234560E+96"),
                testCase("-123456E+91", "-1.234560E+96"),
                testCase("12345E+92", "1.234500E+96"),
                testCase("1E+96", "1.000000E+96"),
                testCase("-1E+96", "-1.000000E+96"),
                // rounding yields infinity for too large exponents
                testCase("1234567E+91", "+Infinity"),
                testCase("-1234567E+91", "-Infinity"),
                testCase("1E+97", "+Infinity"),
                testCase("-1E+97", "-Infinity"),
                // Rounding by chopping of zeroes succeeds without loss of information
                testCase("1234560E-102", "1.23456E-96"),
                testCase("-1234560E-102", "-1.23456E-96"),
                testCase("1000000E-107", "1E-101"),
                testCase("-1000000E-107", "-1E-101"),
                // Rounding half even when insufficient zeroes
                testCase("1234567E-102", "1.23457E-96"),
                testCase("1200000E-107", "1E-101"),
                testCase("1000000E-108", "0E-101"),
                testCase("1230000E-108", "0E-101"),
                // specials
                testCase("+Inf", "+Infinity"),
                testCase("+inf", "+Infinity"),
                testCase("+INF", "+Infinity"),
                testCase("-Inf", "-Infinity"),
                testCase("-inf", "-Infinity"),
                testCase("-INF", "-Infinity"),
                testCase("+Infinity", "+Infinity"),
                testCase("+infinity", "+Infinity"),
                testCase("+INFINITY", "+Infinity"),
                testCase("-Infinity", "-Infinity"),
                testCase("-infinity", "-Infinity"),
                testCase("-INFINITY", "-Infinity"),
                testCase("+NaN", "+NaN"),
                testCase("+nan", "+NaN"),
                testCase("+NAN", "+NaN"),
                testCase("-NaN", "-NaN"),
                testCase("-nan", "-NaN"),
                testCase("-NAN", "-NaN"),
                testCase("+sNaN", "+sNaN"),
                testCase("+snan", "+sNaN"),
                testCase("+SNAN", "+sNaN"),
                testCase("-sNaN", "-sNaN"),
                testCase("-snan", "-sNaN"),
                testCase("-SNAN", "-sNaN")
        );
    }

    private static Arguments testCase(String source, String result) {
        return Arguments.of(source, result);
    }
}
