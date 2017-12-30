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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
public class SimpleDecimalRescalingTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Parameterized.Parameter
    public SimpleDecimal sourceValue;
    @Parameterized.Parameter(1)
    public DecimalFormat decimalFormat;
    @Parameterized.Parameter(2)
    public SimpleDecimal expectedValue;
    @Parameterized.Parameter(3)
    public String expectedExceptionSubString;

    @Test
    public void testRescale() {
        SimpleDecimal result = sourceValue.rescale(decimalFormat);

        assertEquals(expectedValue, result);
    }

    @Test
    public void testRescaleAndValidate() {
        if (expectedExceptionSubString != null) {
            expectedException.expect(DecimalOverflowException.class);
            expectedException.expectMessage(expectedExceptionSubString);
        }

        SimpleDecimal result = sourceValue.rescaleAndValidate(decimalFormat);

        assertEquals(expectedValue, result);
    }

    @Parameterized.Parameters(name = "{index}: value {0} format {1} (expect {2} ({3}))")
    public static Collection<Object[]> data() {
        List<Object[]> params = new ArrayList<>();
        for (SimpleDecimal special : new SimpleDecimal[] {
                SimpleDecimal.POSITIVE_INFINITY, SimpleDecimal.NEGATIVE_INFINITY,
                SimpleDecimal.POSITIVE_NAN, SimpleDecimal.NEGATIVE_NAN,
                SimpleDecimal.POSITIVE_SIGNALING_NAN, SimpleDecimal.NEGATIVE_SIGNALING_NAN }) {
            for (DecimalFormat format : DecimalFormat.values()) {
                params.add(testCase(special, format, special, null));
            }
        }
        params.addAll(Arrays.asList(
                // Clamping zeroes
                testCase("0E+6112", DecimalFormat.Decimal128, "0E+6111", null),
                testCase("0E+6200", DecimalFormat.Decimal128, "0E+6111", null),
                testCase("0E+370", DecimalFormat.Decimal64, "0E+369", null),
                testCase("0E+400", DecimalFormat.Decimal64, "0E+369", null),
                testCase("0E+91", DecimalFormat.Decimal32, "0E+90", null),
                testCase("0E+100", DecimalFormat.Decimal32, "0E+90", null),
                testCase("0E-6177", DecimalFormat.Decimal128, "0E-6176", null),
                testCase("0E-6300", DecimalFormat.Decimal128, "0E-6176", null),
                testCase("0E-399", DecimalFormat.Decimal64, "0E-398", null),
                testCase("0E-500", DecimalFormat.Decimal64, "0E-398", null),
                testCase("0E-102", DecimalFormat.Decimal32, "0E-101", null),
                testCase("0E-200", DecimalFormat.Decimal32, "0E-101", null),
                // edge cases
                testCase("1234567E+0", DecimalFormat.Decimal32, "1234567E+0", null),
                testCase("9999999E+0", DecimalFormat.Decimal32, "9999999E+0", null),
                testCase("1234567E+90", DecimalFormat.Decimal32, "1234567E+90", null),
                testCase("9999999E+90", DecimalFormat.Decimal32, "9999999E+90", null),
                testCase("1234567E-101", DecimalFormat.Decimal32, "1234567E-101", null),
                testCase("9999999E-101", DecimalFormat.Decimal32, "9999999E-101", null),
                testCase("1234567890123456E+0", DecimalFormat.Decimal64, "1234567890123456E+0", null),
                testCase("9999999999999999E+0", DecimalFormat.Decimal64, "9999999999999999E+0", null),
                testCase("1234567890123456E+369", DecimalFormat.Decimal64, "1234567890123456E+369", null),
                testCase("9999999999999999E+369", DecimalFormat.Decimal64, "9999999999999999E+369", null),
                testCase("1234567890123456E-398", DecimalFormat.Decimal64, "1234567890123456E-398", null),
                testCase("9999999999999999E-398", DecimalFormat.Decimal64, "9999999999999999E-398", null),
                testCase("1234567890123456789012345678901234E+0", DecimalFormat.Decimal128,
                        "1234567890123456789012345678901234E+0", null),
                testCase("9999999999999999999999999999999999E+0", DecimalFormat.Decimal128,
                        "9999999999999999999999999999999999E+0", null),
                testCase("1234567890123456789012345678901234E+6111", DecimalFormat.Decimal128,
                        "1234567890123456789012345678901234E+6111", null),
                testCase("9999999999999999999999999999999999E+6111", DecimalFormat.Decimal128,
                        "9999999999999999999999999999999999E+6111", null),
                testCase("1234567890123456789012345678901234E-6176", DecimalFormat.Decimal128,
                        "1234567890123456789012345678901234E-6176", null),
                testCase("9999999999999999999999999999999999E-6176", DecimalFormat.Decimal128,
                        "9999999999999999999999999999999999E-6176", null),
                // rescale by multiplying succeeds
                testCase("123456E+91", DecimalFormat.Decimal32, "1234560E+90", null),
                testCase("-123456E+91", DecimalFormat.Decimal32, "-1234560E+90", null),
                testCase("12345E+92", DecimalFormat.Decimal32, "1234500E+90", null),
                testCase("1E+96", DecimalFormat.Decimal32, "1000000E+90", null),
                testCase("-1E+96", DecimalFormat.Decimal32, "-1000000E+90", null),
                testCase("123456789012345E+370", DecimalFormat.Decimal64, "1234567890123450E+369", null),
                testCase("-123456789012345E+370", DecimalFormat.Decimal64, "-1234567890123450E+369", null),
                testCase("12345678901234E+371", DecimalFormat.Decimal64, "1234567890123400E+369", null),
                testCase("1E+384", DecimalFormat.Decimal64, "1000000000000000E+369", null),
                testCase("-1E+384", DecimalFormat.Decimal64, "-1000000000000000E+369", null),
                testCase("123456789012345678901234567890123E+6112", DecimalFormat.Decimal128,
                        "1234567890123456789012345678901230E+6111", null),
                testCase("-123456789012345678901234567890123E+6112", DecimalFormat.Decimal128,
                        "-1234567890123456789012345678901230E+6111", null),
                testCase("12345678901234567890123456789012E+6113", DecimalFormat.Decimal128,
                        "1234567890123456789012345678901200E+6111", null),
                testCase("1E+6144", DecimalFormat.Decimal128, "1000000000000000000000000000000000E+6111", null),
                testCase("-1E+6144", DecimalFormat.Decimal128, "-1000000000000000000000000000000000E+6111", null),
                // rescale did nothing as difference in exponent was too much
                testCase("1234567E+91", DecimalFormat.Decimal32, "1234567E+91", "Exponent is out of range"),
                testCase("1234567E+96", DecimalFormat.Decimal32, "1234567E+96", "Exponent is out of range"),
                testCase("1234567890123456E+370", DecimalFormat.Decimal64, "1234567890123456E+370",
                        "Exponent is out of range"),
                testCase("1234567890123456E+375", DecimalFormat.Decimal64, "1234567890123456E+375",
                        "Exponent is out of range"),
                testCase("1234567890123456789012345678901234E+6112", DecimalFormat.Decimal128,
                        "1234567890123456789012345678901234E+6112", "Exponent is out of range"),
                testCase("1234567890123456789012345678901234E+6117", DecimalFormat.Decimal128,
                        "1234567890123456789012345678901234E+6117", "Exponent is out of range"),
                testCase("1E+97", DecimalFormat.Decimal32, "1E+97", "Exponent is out of range"),
                testCase("1234567E+97", DecimalFormat.Decimal32, "1234567E+97", "Exponent is out of range"),
                testCase("1E+385", DecimalFormat.Decimal64, "1E+385", "Exponent is out of range"),
                testCase("1234567890123456E+385", DecimalFormat.Decimal64, "1234567890123456E+385",
                        "Exponent is out of range"),
                testCase("1E+6145", DecimalFormat.Decimal128, "1E+6145", "Exponent is out of range"),
                testCase("1234567890123456789012345678901234E+6145", DecimalFormat.Decimal128,
                        "1234567890123456789012345678901234E+6145", "Exponent is out of range"),
                // Rescale by chopping of zeroes succeeds
                testCase("1234560E-102", DecimalFormat.Decimal32, "123456E-101", null),
                testCase("-1234560E-102", DecimalFormat.Decimal32, "-123456E-101", null),
                testCase("1000000E-107", DecimalFormat.Decimal32, "1E-101", null),
                testCase("-1000000E-107", DecimalFormat.Decimal32, "-1E-101", null),
                testCase("1234567890123450E-399", DecimalFormat.Decimal64, "123456789012345E-398", null),
                testCase("-1234567890123450E-399", DecimalFormat.Decimal64, "-123456789012345E-398", null),
                testCase("1000000000000000E-413", DecimalFormat.Decimal64, "1E-398", null),
                testCase("-1000000000000000E-413", DecimalFormat.Decimal64, "-1E-398", null),
                testCase("1234567890123456789012345678901230E-6177", DecimalFormat.Decimal128,
                        "123456789012345678901234567890123E-6176", null),
                testCase("-1234567890123456789012345678901230E-6177", DecimalFormat.Decimal128,
                        "-123456789012345678901234567890123E-6176", null),
                testCase("1000000000000000000000000000000000E-6209", DecimalFormat.Decimal128, "1E-6176", null),
                testCase("-1000000000000000000000000000000000E-6209", DecimalFormat.Decimal128, "-1E-6176", null),
                // Rescale not possible as there aren't sufficient zeroes
                testCase("1234567E-102", DecimalFormat.Decimal32, "1234567E-102", "Exponent is out of range"),
                testCase("1200000E-107", DecimalFormat.Decimal32, "1200000E-107", "Exponent is out of range"),
                testCase("1234567890123456E-399", DecimalFormat.Decimal64, "1234567890123456E-399",
                        "Exponent is out of range"),
                testCase("1200000000000000E-413", DecimalFormat.Decimal64, "1200000000000000E-413",
                        "Exponent is out of range"),
                testCase("1234567890123456789012345678901234E-6177", DecimalFormat.Decimal128,
                        "1234567890123456789012345678901234E-6177", "Exponent is out of range"),
                testCase("1200000000000000000000000000000000E-6209", DecimalFormat.Decimal128,
                        "1200000000000000000000000000000000E-6209", "Exponent is out of range"),
                testCase("1000000E-108", DecimalFormat.Decimal32, "1000000E-108", "Exponent is out of range"),
                testCase("1230000E-108", DecimalFormat.Decimal32, "1230000E-108", "Exponent is out of range"),
                testCase("1000000000000000E-414", DecimalFormat.Decimal64, "1000000000000000E-414",
                        "Exponent is out of range"),
                testCase("1230000000000000E-414", DecimalFormat.Decimal64, "1230000000000000E-414",
                        "Exponent is out of range"),
                testCase("1000000000000000000000000000000000E-6210", DecimalFormat.Decimal128,
                        "1000000000000000000000000000000000E-6210", "Exponent is out of range"),
                testCase("1230000000000000000000000000000000E-6210", DecimalFormat.Decimal128,
                        "1230000000000000000000000000000000E-6210", "Exponent is out of range"),
                // specials
                testCase("+Infinity", DecimalFormat.Decimal32, "+Infinity", null),
                testCase("-Infinity", DecimalFormat.Decimal32, "-Infinity", null),
                testCase("+NaN", DecimalFormat.Decimal32, "+NaN", null),
                testCase("-NaN", DecimalFormat.Decimal32, "-NaN", null),
                testCase("+sNaN", DecimalFormat.Decimal32, "+sNaN", null),
                testCase("-sNaN", DecimalFormat.Decimal32, "-sNaN", null),
                testCase("+Infinity", DecimalFormat.Decimal64, "+Infinity", null),
                testCase("-Infinity", DecimalFormat.Decimal64, "-Infinity", null),
                testCase("+NaN", DecimalFormat.Decimal64, "+NaN", null),
                testCase("-NaN", DecimalFormat.Decimal64, "-NaN", null),
                testCase("+sNaN", DecimalFormat.Decimal64, "+sNaN", null),
                testCase("-sNaN", DecimalFormat.Decimal64, "-sNaN", null),
                testCase("+Infinity", DecimalFormat.Decimal128, "+Infinity", null),
                testCase("-Infinity", DecimalFormat.Decimal128, "-Infinity", null),
                testCase("+NaN", DecimalFormat.Decimal128, "+NaN", null),
                testCase("-NaN", DecimalFormat.Decimal128, "-NaN", null),
                testCase("+sNaN", DecimalFormat.Decimal128, "+sNaN", null),
                testCase("-sNaN", DecimalFormat.Decimal128, "-sNaN", null)
        ));

        return params;
    }

    private static Object[] testCase(SimpleDecimal source, DecimalFormat format, SimpleDecimal target,
            String expectExceptionSubString) {
        return new Object[] { source, format, target, expectExceptionSubString };
    }

    private static Object[] testCase(String source, DecimalFormat format, String target,
            String expectExceptionSubString) {
        return testCase(fromString(source), format, fromString(target), expectExceptionSubString);
    }

}
