/*
 * Copyright (c) 2018-2023 Firebird development team and individual contributors
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
 * Test for the {@link Decimal64#valueOf(String)} method, the rounding applied, and handling of specials.
 * <p>
 * Indirectly also tests {@link Decimal64#valueOf(BigDecimal)} and {@link Decimal64#toString()}.
 * </p>
 *
 * @author Mark Rotteveel
 */
class Decimal64ValueOfTest {

    @ParameterizedTest(name = "{index}: value {0} (expect {1}))")
    @MethodSource("data")
    void valueOfTest(String sourceValue, String expectedValue) {
        Decimal64 value = Decimal64.valueOf(sourceValue);

        assertEquals(expectedValue, value.toString());
    }

    static Stream<Arguments> data() {
        return Stream.of(
                // Zero
                testCase("0", "0"),
                testCase("+0", "0"),
                testCase("-0", "-0"),
                // Clamping zeroes
                testCase("0E+370", "0E+369"),
                testCase("0E+400", "0E+369"),
                testCase("0E-399", "0E-398"),
                testCase("0E-500", "0E-398"),
                // edge cases
                testCase("1234567890123456E+0", "1234567890123456"),
                testCase("9999999999999999E+0", "9999999999999999"),
                testCase("1234567890123456E+369", "1.234567890123456E+384"),
                testCase("9999999999999999E+369", "9.999999999999999E+384"),
                testCase("1234567890123456E-398", "1.234567890123456E-383"),
                testCase("9999999999999999E-398", "9.999999999999999E-383"),
                // rounding by multiplying succeeds
                testCase("123456789012345E+370", "1.234567890123450E+384"),
                testCase("-123456789012345E+370", "-1.234567890123450E+384"),
                testCase("12345678901234E+371", "1.234567890123400E+384"),
                testCase("1E+384", "1.000000000000000E+384"),
                testCase("-1E+384", "-1.000000000000000E+384"),
                // rounding yields infinity for too large exponents
                testCase("1234567890123456E+370", "+Infinity"),
                testCase("-1234567890123456E+370", "-Infinity"),
                testCase("1E+385", "+Infinity"),
                testCase("-1E+385", "-Infinity"),
                // Rounding by chopping of zeroes succeeds without loss of information
                testCase("1234567890123450E-399", "1.23456789012345E-384"),
                testCase("-1234567890123450E-399", "-1.23456789012345E-384"),
                testCase("1000000000000000E-413", "1E-398"),
                testCase("-1000000000000000E-413", "-1E-398"),
                // Rounding half even when insufficient zeroes
                testCase("1234567890123456E-399", "1.23456789012346E-384"),
                testCase("1200000000000000E-413", "1E-398"),
                testCase("1000000000000000E-414", "0E-398"),
                testCase("1230000000000000E-414", "0E-398"),
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
