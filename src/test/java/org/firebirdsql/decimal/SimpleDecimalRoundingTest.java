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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.firebirdsql.decimal.SimpleDecimalHelper.fromString;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:mark@lawinegevaar.nl">Mark Rotteveel</a>
 */
@RunWith(Parameterized.class)
public class SimpleDecimalRoundingTest {

    @Parameterized.Parameter
    public SimpleDecimal sourceValue;
    @Parameterized.Parameter(1)
    public DecimalFormat decimalFormat;
    @Parameterized.Parameter(2)
    public SimpleDecimal expectedValue;

    @Test
    public void testRounding() {
        SimpleDecimal result = sourceValue.round(decimalFormat);

        assertEquals(expectedValue, result);
    }

    @Parameterized.Parameters(name = "{index}: value {0} format {1} (expect {2}))")
    public static Collection<Object[]> data() {
        List<Object[]> params = new ArrayList<>();
        for (SimpleDecimal special : new SimpleDecimal[] {
                SimpleDecimal.POSITIVE_INFINITY, SimpleDecimal.NEGATIVE_INFINITY,
                SimpleDecimal.POSITIVE_NAN, SimpleDecimal.NEGATIVE_NAN,
                SimpleDecimal.POSITIVE_SIGNALING_NAN, SimpleDecimal.NEGATIVE_SIGNALING_NAN }) {
            for (DecimalFormat format : DecimalFormat.values()) {
                params.add(testCase(special, format, special));
            }
        }
        params.addAll(Arrays.asList(
                // Clamping zeroes
                testCase("0E+6112", DecimalFormat.Decimal128, "0E+6111"),
                testCase("0E+6200", DecimalFormat.Decimal128, "0E+6111"),
                testCase("0E+370", DecimalFormat.Decimal64, "0E+369"),
                testCase("0E+400", DecimalFormat.Decimal64, "0E+369"),
                testCase("0E+91", DecimalFormat.Decimal32, "0E+90"),
                testCase("0E+100", DecimalFormat.Decimal32, "0E+90"),
                testCase("0E-6177", DecimalFormat.Decimal128, "0E-6176"),
                testCase("0E-6300", DecimalFormat.Decimal128, "0E-6176"),
                testCase("0E-399", DecimalFormat.Decimal64, "0E-398"),
                testCase("0E-500", DecimalFormat.Decimal64, "0E-398"),
                testCase("0E-102", DecimalFormat.Decimal32, "0E-101"),
                testCase("0E-200", DecimalFormat.Decimal32, "0E-101"),
                // edge cases
                testCase("1234567E+0", DecimalFormat.Decimal32, "1234567E+0"),
                testCase("9999999E+0", DecimalFormat.Decimal32, "9999999E+0"),
                testCase("1234567E+90", DecimalFormat.Decimal32, "1234567E+90"),
                testCase("9999999E+90", DecimalFormat.Decimal32, "9999999E+90"),
                testCase("1234567E-101", DecimalFormat.Decimal32, "1234567E-101"),
                testCase("9999999E-101", DecimalFormat.Decimal32, "9999999E-101"),
                testCase("1234567890123456E+0", DecimalFormat.Decimal64, "1234567890123456E+0"),
                testCase("9999999999999999E+0", DecimalFormat.Decimal64, "9999999999999999E+0"),
                testCase("1234567890123456E+369", DecimalFormat.Decimal64, "1234567890123456E+369"),
                testCase("9999999999999999E+369", DecimalFormat.Decimal64, "9999999999999999E+369"),
                testCase("1234567890123456E-398", DecimalFormat.Decimal64, "1234567890123456E-398"),
                testCase("9999999999999999E-398", DecimalFormat.Decimal64, "9999999999999999E-398"),
                testCase("1234567890123456789012345678901234E+0", DecimalFormat.Decimal128,
                        "1234567890123456789012345678901234E+0"),
                testCase("9999999999999999999999999999999999E+0", DecimalFormat.Decimal128,
                        "9999999999999999999999999999999999E+0"),
                testCase("1234567890123456789012345678901234E+6111", DecimalFormat.Decimal128,
                        "1234567890123456789012345678901234E+6111"),
                testCase("9999999999999999999999999999999999E+6111", DecimalFormat.Decimal128,
                        "9999999999999999999999999999999999E+6111"),
                testCase("1234567890123456789012345678901234E-6176", DecimalFormat.Decimal128,
                        "1234567890123456789012345678901234E-6176"),
                testCase("9999999999999999999999999999999999E-6176", DecimalFormat.Decimal128,
                        "9999999999999999999999999999999999E-6176"),
                // rounding by multiplying succeeds
                testCase("123456E+91", DecimalFormat.Decimal32, "1234560E+90"),
                testCase("-123456E+91", DecimalFormat.Decimal32, "-1234560E+90"),
                testCase("12345E+92", DecimalFormat.Decimal32, "1234500E+90"),
                testCase("1E+96", DecimalFormat.Decimal32, "1000000E+90"),
                testCase("-1E+96", DecimalFormat.Decimal32, "-1000000E+90"),
                testCase("123456789012345E+370", DecimalFormat.Decimal64, "1234567890123450E+369"),
                testCase("-123456789012345E+370", DecimalFormat.Decimal64, "-1234567890123450E+369"),
                testCase("12345678901234E+371", DecimalFormat.Decimal64, "1234567890123400E+369"),
                testCase("1E+384", DecimalFormat.Decimal64, "1000000000000000E+369"),
                testCase("-1E+384", DecimalFormat.Decimal64, "-1000000000000000E+369"),
                testCase("123456789012345678901234567890123E+6112", DecimalFormat.Decimal128,
                        "1234567890123456789012345678901230E+6111"),
                testCase("-123456789012345678901234567890123E+6112", DecimalFormat.Decimal128,
                        "-1234567890123456789012345678901230E+6111"),
                testCase("12345678901234567890123456789012E+6113", DecimalFormat.Decimal128,
                        "1234567890123456789012345678901200E+6111"),
                testCase("1E+6144", DecimalFormat.Decimal128, "1000000000000000000000000000000000E+6111"),
                testCase("-1E+6144", DecimalFormat.Decimal128, "-1000000000000000000000000000000000E+6111"),
                // rounding yields infinity for too large exponents
                testCase("1234567E+91", DecimalFormat.Decimal32, "+Infinity"),
                testCase("-1234567E+91", DecimalFormat.Decimal32, "-Infinity"),
                testCase("1234567890123456E+370", DecimalFormat.Decimal64, "+Infinity"),
                testCase("-1234567890123456E+370", DecimalFormat.Decimal64, "-Infinity"),
                testCase("1234567890123456789012345678901234E+6112", DecimalFormat.Decimal128, "+Infinity"),
                testCase("-1234567890123456789012345678901234E+6112", DecimalFormat.Decimal128, "-Infinity"),
                testCase("1E+97", DecimalFormat.Decimal32, "+Infinity"),
                testCase("-1E+97", DecimalFormat.Decimal32, "-Infinity"),
                testCase("1E+385", DecimalFormat.Decimal64, "+Infinity"),
                testCase("-1E+385", DecimalFormat.Decimal64, "-Infinity"),
                testCase("1E+6145", DecimalFormat.Decimal128, "+Infinity"),
                testCase("-1E+6145", DecimalFormat.Decimal128, "-Infinity"),
                // Rounding by chopping of zeroes succeeds without loss of information
                testCase("1234560E-102", DecimalFormat.Decimal32, "123456E-101"),
                testCase("-1234560E-102", DecimalFormat.Decimal32, "-123456E-101"),
                testCase("1000000E-107", DecimalFormat.Decimal32, "1E-101"),
                testCase("-1000000E-107", DecimalFormat.Decimal32, "-1E-101"),
                testCase("1234567890123450E-399", DecimalFormat.Decimal64, "123456789012345E-398"),
                testCase("-1234567890123450E-399", DecimalFormat.Decimal64, "-123456789012345E-398"),
                testCase("1000000000000000E-413", DecimalFormat.Decimal64, "1E-398"),
                testCase("-1000000000000000E-413", DecimalFormat.Decimal64, "-1E-398"),
                testCase("1234567890123456789012345678901230E-6177", DecimalFormat.Decimal128,
                        "123456789012345678901234567890123E-6176"),
                testCase("-1234567890123456789012345678901230E-6177", DecimalFormat.Decimal128,
                        "-123456789012345678901234567890123E-6176"),
                testCase("1000000000000000000000000000000000E-6209", DecimalFormat.Decimal128, "1E-6176"),
                testCase("-1000000000000000000000000000000000E-6209", DecimalFormat.Decimal128, "-1E-6176"),
                // Rounding half even when insufficient zeroes
                testCase("1234567E-102", DecimalFormat.Decimal32, "123457E-101"),
                testCase("1200000E-107", DecimalFormat.Decimal32, "1E-101"),
                testCase("1234567890123456E-399", DecimalFormat.Decimal64, "123456789012346E-398"),
                testCase("1200000000000000E-413", DecimalFormat.Decimal64, "1E-398"
                ),
                testCase("1234567890123456789012345678901234E-6177", DecimalFormat.Decimal128,
                        "123456789012345678901234567890123E-6176"),
                testCase("1200000000000000000000000000000000E-6209", DecimalFormat.Decimal128, "1E-6176"),
                testCase("1000000E-108", DecimalFormat.Decimal32, "0E-101"),
                testCase("1230000E-108", DecimalFormat.Decimal32, "0E-101"),
                testCase("1000000000000000E-414", DecimalFormat.Decimal64, "0E-398"),
                testCase("1230000000000000E-414", DecimalFormat.Decimal64, "0E-398"),
                testCase("1000000000000000000000000000000000E-6210", DecimalFormat.Decimal128, "0E-6176"),
                testCase("1230000000000000000000000000000000E-6210", DecimalFormat.Decimal128, "0E-6176"),
                // specials
                testCase("+Infinity", DecimalFormat.Decimal32, "+Infinity"),
                testCase("-Infinity", DecimalFormat.Decimal32, "-Infinity"),
                testCase("+NaN", DecimalFormat.Decimal32, "+NaN"),
                testCase("-NaN", DecimalFormat.Decimal32, "-NaN"),
                testCase("+sNaN", DecimalFormat.Decimal32, "+sNaN"),
                testCase("-sNaN", DecimalFormat.Decimal32, "-sNaN"),
                testCase("+Infinity", DecimalFormat.Decimal64, "+Infinity"),
                testCase("-Infinity", DecimalFormat.Decimal64, "-Infinity"),
                testCase("+NaN", DecimalFormat.Decimal64, "+NaN"),
                testCase("-NaN", DecimalFormat.Decimal64, "-NaN"),
                testCase("+sNaN", DecimalFormat.Decimal64, "+sNaN"),
                testCase("-sNaN", DecimalFormat.Decimal64, "-sNaN"),
                testCase("+Infinity", DecimalFormat.Decimal128, "+Infinity"),
                testCase("-Infinity", DecimalFormat.Decimal128, "-Infinity"),
                testCase("+NaN", DecimalFormat.Decimal128, "+NaN"),
                testCase("-NaN", DecimalFormat.Decimal128, "-NaN"),
                testCase("+sNaN", DecimalFormat.Decimal128, "+sNaN"),
                testCase("-sNaN", DecimalFormat.Decimal128, "-sNaN")
        ));

        return params;
    }

    private static Object[] testCase(SimpleDecimal source, DecimalFormat format, SimpleDecimal target) {
        return new Object[] { source, format, target };
    }

    private static Object[] testCase(String source, DecimalFormat format, String target) {
        return testCase(fromString(source), format, fromString(target));
    }



}
