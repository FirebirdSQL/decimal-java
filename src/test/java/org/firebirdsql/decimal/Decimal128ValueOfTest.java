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
 * Test for the {@link Decimal128#valueOf(String)} method, the rounding applied, and handling of specials.
 * <p>
 * Indirectly also tests {@link Decimal128#valueOf(BigDecimal)} and {@link Decimal128#toString()}.
 * </p>
 *
 * @author <a href="mailto:mark@lawinegevaar.nl">Mark Rotteveel</a>
 */
class Decimal128ValueOfTest {

    @ParameterizedTest(name = "{index}: value {0} (expect {1}))")
    @MethodSource("data")
    void valueOfTest(String sourceValue, String expectedValue) {
        Decimal128 value = Decimal128.valueOf(sourceValue);

        assertEquals(expectedValue, value.toString());
    }

    static Stream<Arguments> data() {
        return Stream.of(
                // Zero
                testCase("0", "0"),
                testCase("+0", "0"),
                testCase("-0", "-0"),
                // Clamping zeroes
                testCase("0E+6112", "0E+6111"),
                testCase("0E+6200", "0E+6111"),
                testCase("0E-6177", "0E-6176"),
                testCase("0E-6300", "0E-6176"),
                // edge cases
                testCase("1234567890123456789012345678901234E+0", "1234567890123456789012345678901234"),
                testCase("9999999999999999999999999999999999E+0", "9999999999999999999999999999999999"),
                testCase("1234567890123456789012345678901234E+6111", "1.234567890123456789012345678901234E+6144"),
                testCase("9999999999999999999999999999999999E+6111", "9.999999999999999999999999999999999E+6144"),
                testCase("1234567890123456789012345678901234E-6176", "1.234567890123456789012345678901234E-6143"),
                testCase("9999999999999999999999999999999999E-6176", "9.999999999999999999999999999999999E-6143"),
                // rounding by multiplying succeeds
                testCase("123456789012345678901234567890123E+6112", "1.234567890123456789012345678901230E+6144"),
                testCase("-123456789012345678901234567890123E+6112", "-1.234567890123456789012345678901230E+6144"),
                testCase("12345678901234567890123456789012E+6113", "1.234567890123456789012345678901200E+6144"),
                testCase("1E+6144", "1.000000000000000000000000000000000E+6144"),
                testCase("-1E+6144", "-1.000000000000000000000000000000000E+6144"),
                // rounding yields infinity for too large exponents
                testCase("1234567890123456789012345678901234E+6112", "+Infinity"),
                testCase("-1234567890123456789012345678901234E+6112", "-Infinity"),
                testCase("1E+6145", "+Infinity"),
                testCase("-1E+6145", "-Infinity"),
                // Rounding by chopping of zeroes succeeds without loss of information
                testCase("1234567890123456789012345678901230E-6177", "1.23456789012345678901234567890123E-6144"),
                testCase("-1234567890123456789012345678901230E-6177", "-1.23456789012345678901234567890123E-6144"),
                testCase("1000000000000000000000000000000000E-6209", "1E-6176"),
                testCase("-1000000000000000000000000000000000E-6209", "-1E-6176"),
                // Rounding half even when insufficient zeroes
                testCase("1234567890123456789012345678901234E-6177", "1.23456789012345678901234567890123E-6144"),
                testCase("1200000000000000000000000000000000E-6209", "1E-6176"),
                testCase("1000000000000000000000000000000000E-6210", "0E-6176"),
                testCase("1230000000000000000000000000000000E-6210", "0E-6176"),
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
