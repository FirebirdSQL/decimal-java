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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import static org.firebirdsql.decimal.util.ByteArrayHelper.hexToBytes;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

/**
 * @author <a href="mailto:mark@lawinegevaar.nl">Mark Rotteveel</a>
 */
@RunWith(Parameterized.class)
public class Decimal128ByteConversionTest {

    @Parameterized.Parameter
    public String description;
    @Parameterized.Parameter(1)
    public byte[] sourceBytes;
    @Parameterized.Parameter(2)
    public Decimal128 decimalValue;
    @Parameterized.Parameter(3)
    public byte[] targetBytes;

    @Test
    public void testConversionFromBytesToDecimal128() {
        assumeTrue("No source bytes for " + description, sourceBytes != null);
        Decimal128 result = Decimal128.parseBytes(sourceBytes);

        assertEquals("Expected " + description, decimalValue, result);
    }

    @Test
    public void testConversionFromDecimal128ToBytes() {
        assumeTrue("No target bytes for " + description, targetBytes != null);
        byte[] result = decimalValue.toBytes();

        assertArrayEquals(targetBytes, result);
    }

    @Parameterized.Parameters(name = "{index}: value {0} ({2})")
    public static Collection<Object[]> data() {
        return Arrays.asList(
                testCase("POSITIVE_INFINITY",
                        new byte[] { 0b0_11110_00, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        Decimal128.POSITIVE_INFINITY),
                testCase("POSITIVE_INFINITY",
                        new byte[] { 0b0_11110_10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        Decimal128.POSITIVE_INFINITY,
                        new byte[] { 0b0_11110_00, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }),
                testCase("NEGATIVE_INFINITY",
                        new byte[] { (byte) 0b1_11110_00, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        Decimal128.NEGATIVE_INFINITY),
                testCase("NEGATIVE_INFINITY",
                        new byte[] { (byte) 0b1_11110_10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        Decimal128.NEGATIVE_INFINITY,
                        new byte[] { (byte) 0b1_11110_00, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }),
                testCase("POSITIVE_NAN",
                        new byte[] { 0b0_11111_00, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        Decimal128.POSITIVE_NAN),
                testCase("NEGATIVE_NAN",
                        new byte[] { (byte) 0b1_11111_00, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        Decimal128.NEGATIVE_NAN),
                testCase("POSITIVE_SIGNALING_NAN",
                        new byte[] { 0b0_11111_10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        Decimal128.POSITIVE_SIGNALING_NAN),
                testCase("NEGATIVE_SIGNALING_NAN",
                        new byte[] { (byte) 0b1_11111_10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        Decimal128.NEGATIVE_SIGNALING_NAN),
                // cases from http://speleotrove.com/decimal/dectest.zip (dqEncode.decTest)
                // Comments copied to easier correlate file
                // General testcases
                // (mostly derived from the Strawman 4 document and examples)
                testCase("A20780000000000000000000000003D0", "-7.50"),
                testCase("A20840000000000000000000000003D0", "-7.50E+3"),
                testCase("A20800000000000000000000000003D0", "-750"),
                testCase("A207c0000000000000000000000003D0", "-75.0"),
                testCase("A20740000000000000000000000003D0", "-0.750"),
                testCase("A20700000000000000000000000003D0", "-0.0750"),
                testCase("A20680000000000000000000000003D0", "-0.000750"),
                testCase("A20600000000000000000000000003D0", "-0.00000750"),
                testCase("A205c0000000000000000000000003D0", "-7.50E-7"),
                // Normality
                testCase("2608134b9c1e28e56f3c127177823534", "1234567890123456789012345678901234"),
                testCase("a608134b9c1e28e56f3c127177823534", "-1234567890123456789012345678901234"),
                testCase("26080912449124491244912449124491", "1111111111111111111111111111111111"),
                // Nmax and similar
                testCase("77ffcff3fcff3fcff3fcff3fcff3fcff", "9.999999999999999999999999999999999E+6144"),
                testCase("47ffd34b9c1e28e56f3c127177823534", "1.234567890123456789012345678901234E+6144"),
                // fold-downs (more below)
                testCase(null, "1.23E+6144", "47ffd300000000000000000000000000"),
                testCase("47ffd300000000000000000000000000", "1.230000000000000000000000000000000E+6144"),
                testCase(null, "1E+6144", "47ffc000000000000000000000000000"),
                testCase("47ffc000000000000000000000000000", "1.000000000000000000000000000000000E+6144"),
                testCase("220800000000000000000000000049c5", "12345"),
                testCase("22080000000000000000000000000534", "1234"),
                testCase("220800000000000000000000000000a3", "123"),
                testCase("22080000000000000000000000000012", "12"),
                testCase("22080000000000000000000000000001", "1"),
                testCase("220780000000000000000000000000a3", "1.23"),
                testCase("220780000000000000000000000049c5", "123.45"),
                // Nmin and below
                testCase("00084000000000000000000000000001", "1E-6143"),
                testCase("04000000000000000000000000000000", "1.000000000000000000000000000000000E-6143"),
                testCase("04000000000000000000000000000001", "1.000000000000000000000000000000001E-6143"),
                testCase(null, "0.100000000000000000000000000000000E-6143", "00000800000000000000000000000000"),
                testCase("00000800000000000000000000000000", "1.00000000000000000000000000000000E-6144"),
                testCase(null, "0.000000000000000000000000000000010E-6143", "00000000000000000000000000000010"),
                testCase("00000000000000000000000000000010", "1.0E-6175"),
                testCase(null, "0.00000000000000000000000000000001E-6143", "00004000000000000000000000000001"),
                testCase("00004000000000000000000000000001", "1E-6175"),
                testCase(null, "0.000000000000000000000000000000001E-6143", "00000000000000000000000000000001"),
                testCase("00000000000000000000000000000001", "1E-6176"),
                // underflows cannot be tested for simple copies, check edge cases
                testCase("00000ff3fcff3fcff3fcff3fcff3fcff", "999999999999999999999999999999999e-6176"),
                // same again, negatives
                // Nmax and similar
                testCase("f7ffcff3fcff3fcff3fcff3fcff3fcff", "-9.999999999999999999999999999999999E+6144"),
                testCase("c7ffd34b9c1e28e56f3c127177823534", "-1.234567890123456789012345678901234E+6144"),
                // fold-downs (more below)
                testCase(null, "-1.23E+6144", "c7ffd300000000000000000000000000"),
                testCase("c7ffd300000000000000000000000000", "-1.230000000000000000000000000000000E+6144"),
                testCase(null, "-1E+6144", "c7ffc000000000000000000000000000"),
                testCase("c7ffc000000000000000000000000000", "-1.000000000000000000000000000000000E+6144"),
                testCase("a20800000000000000000000000049c5", "-12345"),
                testCase("a2080000000000000000000000000534", "-1234"),
                testCase("a20800000000000000000000000000a3", "-123"),
                testCase("a2080000000000000000000000000012", "-12"),
                testCase("a2080000000000000000000000000001", "-1"),
                testCase("a20780000000000000000000000000a3", "-1.23"),
                testCase("a20780000000000000000000000049c5", "-123.45"),
                // Nmin and below
                testCase("80084000000000000000000000000001", "-1E-6143"),
                testCase("84000000000000000000000000000000", "-1.000000000000000000000000000000000E-6143"),
                testCase("84000000000000000000000000000001", "-1.000000000000000000000000000000001E-6143"),
                testCase(null, "-0.100000000000000000000000000000000E-6143", "80000800000000000000000000000000"),
                testCase("80000800000000000000000000000000", "-1.00000000000000000000000000000000E-6144"),
                testCase(null, "-0.000000000000000000000000000000010E-6143", "80000000000000000000000000000010"),
                testCase("80000000000000000000000000000010", "-1.0E-6175"),
                testCase(null, "-0.00000000000000000000000000000001E-6143", "80004000000000000000000000000001"),
                testCase("80004000000000000000000000000001", "-1E-6175"),
                testCase(null, "-0.000000000000000000000000000000001E-6143", "80000000000000000000000000000001"),
                testCase("80000000000000000000000000000001", "-1E-6176"),
                // underflow edge cases
                testCase("80000ff3fcff3fcff3fcff3fcff3fcff", "-999999999999999999999999999999999e-6176"),
                // zeros
                // TODO skipping some of the clamped cases
                testCase(null, "0E-8000", "00000000000000000000000000000000"),
                testCase(null, "0E-6177", "00000000000000000000000000000000"),
                testCase("00000000000000000000000000000000", "0E-6176"),
                testCase(null, "0.000000000000000000000000000000000E-6143", "00000000000000000000000000000000"),
                testCase(null, "0E-2", "22078000000000000000000000000000"),
                testCase("22078000000000000000000000000000", "0.00"),
                testCase("22080000000000000000000000000000", "0"),
                testCase("2208c000000000000000000000000000", "0E+3"),
                testCase("43ffc000000000000000000000000000", "0E+6111"),
                // clamped zeros...
                testCase(null, "0E+6112", "43ffc000000000000000000000000000"),
                testCase(null, "0E+6144", "43ffc000000000000000000000000000"),
                testCase(null, "0E+8000", "43ffc000000000000000000000000000"),
                // negative zeros
                testCase("-0E-8000", null, dec("0E-8000").negate(), hexToBytes("80000000000000000000000000000000")),
                testCase("-0E-6177", null, dec("0E-6177").negate(), hexToBytes("80000000000000000000000000000000")),
                testCase("-0E-6176", hexToBytes("80000000000000000000000000000000"), dec("0E-6176").negate()),
                testCase("-0.000000000000000000000000000000000E-6143", null,
                        dec("0.000000000000000000000000000000000E-6143").negate(),
                        hexToBytes("80000000000000000000000000000000")),
                testCase("-0E-2", null, dec("0E-2").negate(), hexToBytes("a2078000000000000000000000000000")),
                testCase("-0.00", hexToBytes("a2078000000000000000000000000000"), dec("0.00").negate()),
                testCase("-0", hexToBytes("a2080000000000000000000000000000"), dec("0").negate()),
                testCase("-0E+3", hexToBytes("a208c000000000000000000000000000"), dec("-0E+3").negate()),
                testCase("-0E+6111", hexToBytes("c3ffc000000000000000000000000000"), dec("-0E+6111").negate()),
                // clamped zeros...
                testCase("-0E+6112", null, dec("0E+6112").negate(), hexToBytes("c3ffc000000000000000000000000000")),
                testCase("-0E+6144", null, dec("0E+6144").negate(), hexToBytes("c3ffc000000000000000000000000000")),
                testCase("-0E+8000", null, dec("0E+8000").negate(), hexToBytes("c3ffc000000000000000000000000000")),
                // exponent lengths
                testCase("22080000000000000000000000000007", "7"),
                testCase("220a4000000000000000000000000007", "7E+9"),
                testCase("2220c000000000000000000000000007", "7E+99"),
                testCase("2301c000000000000000000000000007", "7E+999"),
                testCase("43e3c000000000000000000000000007", "7E+5999"),
                //specials (may have overlap with earlier cases)
                testCase("Infinity", hexToBytes("78000000000000000000000000000000"), Decimal128.POSITIVE_INFINITY),
                testCase("Infinity", hexToBytes("78787878787878787878787878787878"), Decimal128.POSITIVE_INFINITY,
                        null),
                testCase("Infinity", hexToBytes("79797979797979797979797979797979"), Decimal128.POSITIVE_INFINITY,
                        null),
                testCase("Infinity", hexToBytes("7a7a7a7a7a7a7a7a7a7a7a7a7a7a7a7a"), Decimal128.POSITIVE_INFINITY,
                        null),
                testCase("Infinity", hexToBytes("7a000000000000000000000000000000"), Decimal128.POSITIVE_INFINITY,
                        null),
                testCase("Infinity", hexToBytes("7b7b7b7b7b7b7b7b7b7b7b7b7b7b7b7b"), Decimal128.POSITIVE_INFINITY,
                        null),
                testCase("Infinity", hexToBytes("7b000000000000000000000000000000"), Decimal128.POSITIVE_INFINITY,
                        null),
                testCase("NaN", hexToBytes("7c000000000000000000000000000000"), Decimal128.POSITIVE_NAN),
                testCase("NaN", hexToBytes("7c7c7c7c7c7c7c7c7c7c7c7c7c7c7c7c"), Decimal128.POSITIVE_NAN, null),
                testCase("NaN", hexToBytes("7c003c7c7c7c7c7c7c7c7c7c7c7c7c7c"), Decimal128.POSITIVE_NAN, null),
                testCase("NaN", hexToBytes("7d7d7d7d7d7d7d7d7d7d7d7d7d7d7d7d"), Decimal128.POSITIVE_NAN, null),
                testCase("NaN", hexToBytes("7c003d7d7d7d7d7d7d7d7d7d7d7d7d7d"), Decimal128.POSITIVE_NAN, null),
                testCase("NaN", hexToBytes("7d000000000000000000000000000000"), Decimal128.POSITIVE_NAN, null),
                testCase("sNaN", hexToBytes("7e000000000000000000000000000000"), Decimal128.POSITIVE_SIGNALING_NAN),
                testCase("sNaN", hexToBytes("7e7e7e7e7e7e7e7e7e7e7e7e7e7e7e7e"), Decimal128.POSITIVE_SIGNALING_NAN,
                        null),
                testCase("sNaN", hexToBytes("7e003e7e7c7e7e7e7e7c7e7e7e7e7c7e"), Decimal128.POSITIVE_SIGNALING_NAN,
                        null),
                testCase("sNaN", hexToBytes("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f"), Decimal128.POSITIVE_SIGNALING_NAN,
                        null),
                testCase("sNaN", hexToBytes("7e003f7f7c7f7f7f7f7c7f7f7f7f7c7f"), Decimal128.POSITIVE_SIGNALING_NAN,
                        null),
                testCase("sNaN", hexToBytes("7f000000000000000000000000000000"), Decimal128.POSITIVE_SIGNALING_NAN,
                        null),
                testCase("sNaN", hexToBytes("7fffffffffffffffffffffffffffffff"), Decimal128.POSITIVE_SIGNALING_NAN,
                        null),
                testCase("-Infinity", hexToBytes("f8000000000000000000000000000000"), Decimal128.NEGATIVE_INFINITY),
                testCase("-Infinity", hexToBytes("f8787878787878787878787878787878"), Decimal128.NEGATIVE_INFINITY,
                        null),
                testCase("-Infinity", hexToBytes("f9797979797979797979797979797979"), Decimal128.NEGATIVE_INFINITY,
                        null),
                testCase("-Infinity", hexToBytes("fa7a7a7a7a7a7a7a7a7a7a7a7a7a7a7a"), Decimal128.NEGATIVE_INFINITY,
                        null),
                testCase("-Infinity", hexToBytes("fa000000000000000000000000000000"), Decimal128.NEGATIVE_INFINITY,
                        null),
                testCase("-Infinity", hexToBytes("fb7b7b7b7b7b7b7b7b7b7b7b7b7b7b7b"), Decimal128.NEGATIVE_INFINITY,
                        null),
                testCase("-Infinity", hexToBytes("fb000000000000000000000000000000"), Decimal128.NEGATIVE_INFINITY,
                        null),
                testCase("-NaN", hexToBytes("fc000000000000000000000000000000"), Decimal128.NEGATIVE_NAN),
                testCase("-NaN", hexToBytes("fc7c7c7c7c7c7c7c7c7c7c7c7c7c7c7c"), Decimal128.NEGATIVE_NAN, null),
                testCase("-NaN", hexToBytes("fc003c7c7c7c7c7c7c7c7c7c7c7c7c7c"), Decimal128.NEGATIVE_NAN, null),
                testCase("-NaN", hexToBytes("fd7d7d7d7d7d7d7d7d7d7d7d7d7d7d7d"), Decimal128.NEGATIVE_NAN, null),
                testCase("-NaN", hexToBytes("fc003d7d7d7d7d7d7d7d7d7d7d7d7d7d"), Decimal128.NEGATIVE_NAN, null),
                testCase("-NaN", hexToBytes("fd000000000000000000000000000000"), Decimal128.NEGATIVE_NAN, null),
                testCase("-sNaN", hexToBytes("fe000000000000000000000000000000"), Decimal128.NEGATIVE_SIGNALING_NAN),
                testCase("-sNaN", hexToBytes("fe7e7e7e7e7e7e7e7e7e7e7e7e7e7e7e"), Decimal128.NEGATIVE_SIGNALING_NAN,
                        null),
                testCase("-sNaN", hexToBytes("fe003e7e7c7e7e7e7e7c7e7e7e7e7c7e"), Decimal128.NEGATIVE_SIGNALING_NAN,
                        null),
                testCase("-sNaN", hexToBytes("ff7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f"), Decimal128.NEGATIVE_SIGNALING_NAN,
                        null),
                testCase("-sNaN", hexToBytes("fe003f7f7c7f7f7f7f7c7f7f7f7f7c7f"), Decimal128.NEGATIVE_SIGNALING_NAN,
                        null),
                testCase("-sNaN", hexToBytes("ff000000000000000000000000000000"), Decimal128.NEGATIVE_SIGNALING_NAN,
                        null),
                testCase("-sNaN", hexToBytes("ffffffffffffffffffffffffffffffff"), Decimal128.NEGATIVE_SIGNALING_NAN,
                        null),
                // diagnostic NaNs skipped, not supported, handled as 'normal' NaNs!
                // fold-down full sequence
                testCase(null, "1E+6144", "47ffc000000000000000000000000000"),
                testCase("47ffc000000000000000000000000000", "1.000000000000000000000000000000000E+6144"),
                testCase(null, "1E+6143", "43ffc800000000000000000000000000"),
                testCase("43ffc800000000000000000000000000", "1.00000000000000000000000000000000E+6143"),
                testCase(null, "1E+6142", "43ffc100000000000000000000000000"),
                testCase("43ffc100000000000000000000000000", "1.0000000000000000000000000000000E+6142"),
                testCase(null, "1E+6141", "43ffc010000000000000000000000000"),
                testCase("43ffc010000000000000000000000000", "1.000000000000000000000000000000E+6141"),
                testCase(null, "1E+6140", "43ffc002000000000000000000000000"),
                testCase("43ffc002000000000000000000000000", "1.00000000000000000000000000000E+6140"),
                testCase(null, "1E+6139", "43ffc000400000000000000000000000"),
                testCase("43ffc000400000000000000000000000", "1.0000000000000000000000000000E+6139"),
                testCase(null, "1E+6138", "43ffc000040000000000000000000000"),
                testCase("43ffc000040000000000000000000000", "1.000000000000000000000000000E+6138"),
                testCase(null, "1E+6137", "43ffc000008000000000000000000000"),
                testCase("43ffc000008000000000000000000000", "1.00000000000000000000000000E+6137"),
                testCase(null, "1E+6136", "43ffc000001000000000000000000000"),
                testCase("43ffc000001000000000000000000000", "1.0000000000000000000000000E+6136"),
                testCase(null, "1E+6135", "43ffc000000100000000000000000000"),
                testCase("43ffc000000100000000000000000000", "1.000000000000000000000000E+6135"),
                testCase(null, "1E+6134", "43ffc000000020000000000000000000"),
                testCase("43ffc000000020000000000000000000", "1.00000000000000000000000E+6134"),
                testCase(null, "1E+6133", "43ffc000000004000000000000000000"),
                testCase("43ffc000000004000000000000000000", "1.0000000000000000000000E+6133"),
                testCase(null, "1E+6132", "43ffc000000000400000000000000000"),
                testCase("43ffc000000000400000000000000000", "1.000000000000000000000E+6132"),
                testCase(null, "1E+6131", "43ffc000000000080000000000000000"),
                testCase("43ffc000000000080000000000000000", "1.00000000000000000000E+6131"),
                testCase(null, "1E+6130", "43ffc000000000010000000000000000"),
                testCase("43ffc000000000010000000000000000", "1.0000000000000000000E+6130"),
                testCase(null, "1E+6129", "43ffc000000000001000000000000000"),
                testCase("43ffc000000000001000000000000000", "1.000000000000000000E+6129"),
                testCase(null, "1E+6128", "43ffc000000000000200000000000000"),
                testCase("43ffc000000000000200000000000000", "1.00000000000000000E+6128"),
                testCase(null, "1E+6127", "43ffc000000000000040000000000000"),
                testCase("43ffc000000000000040000000000000", "1.0000000000000000E+6127"),
                testCase(null, "1E+6126", "43ffc000000000000004000000000000"),
                testCase("43ffc000000000000004000000000000", "1.000000000000000E+6126"),
                testCase(null, "1E+6125", "43ffc000000000000000800000000000"),
                testCase("43ffc000000000000000800000000000", "1.00000000000000E+6125"),
                testCase(null, "1E+6124", "43ffc000000000000000100000000000"),
                testCase("43ffc000000000000000100000000000", "1.0000000000000E+6124"),
                testCase(null, "1E+6123", "43ffc000000000000000010000000000"),
                testCase("43ffc000000000000000010000000000", "1.000000000000E+6123"),
                testCase(null, "1E+6122", "43ffc000000000000000002000000000"),
                testCase("43ffc000000000000000002000000000", "1.00000000000E+6122"),
                testCase(null, "1E+6121", "43ffc000000000000000000400000000"),
                testCase("43ffc000000000000000000400000000", "1.0000000000E+6121"),
                testCase(null, "1E+6120", "43ffc000000000000000000040000000"),
                testCase("43ffc000000000000000000040000000", "1.000000000E+6120"),
                testCase(null, "1E+6119", "43ffc000000000000000000008000000"),
                testCase("43ffc000000000000000000008000000", "1.00000000E+6119"),
                testCase(null, "1E+6118", "43ffc000000000000000000001000000"),
                testCase("43ffc000000000000000000001000000", "1.0000000E+6118"),
                testCase(null, "1E+6117", "43ffc000000000000000000000100000"),
                testCase("43ffc000000000000000000000100000", "1.000000E+6117"),
                testCase(null, "1E+6116", "43ffc000000000000000000000020000"),
                testCase("43ffc000000000000000000000020000", "1.00000E+6116"),
                testCase(null, "1E+6115", "43ffc000000000000000000000004000"),
                testCase("43ffc000000000000000000000004000", "1.0000E+6115"),
                testCase(null, "1E+6114", "43ffc000000000000000000000000400"),
                testCase("43ffc000000000000000000000000400", "1.000E+6114"),
                testCase(null, "1E+6113", "43ffc000000000000000000000000080"),
                testCase("43ffc000000000000000000000000080", "1.00E+6113"),
                testCase(null, "1E+6112", "43ffc000000000000000000000000010"),
                testCase("43ffc000000000000000000000000010", "1.0E+6112"),
                testCase("43ffc000000000000000000000000001", "1E+6111"),
                testCase("43ff8000000000000000000000000001", "1E+6110"),
                // Selected DPD codes
                testCase("22080000000000000000000000000000", "0"),
                testCase("22080000000000000000000000000009", "9"),
                testCase("22080000000000000000000000000010", "10"),
                testCase("22080000000000000000000000000019", "19"),
                testCase("22080000000000000000000000000020", "20"),
                testCase("22080000000000000000000000000029", "29"),
                testCase("22080000000000000000000000000030", "30"),
                testCase("22080000000000000000000000000039", "39"),
                testCase("22080000000000000000000000000040", "40"),
                testCase("22080000000000000000000000000049", "49"),
                testCase("22080000000000000000000000000050", "50"),
                testCase("22080000000000000000000000000059", "59"),
                testCase("22080000000000000000000000000060", "60"),
                testCase("22080000000000000000000000000069", "69"),
                testCase("22080000000000000000000000000070", "70"),
                testCase("22080000000000000000000000000071", "71"),
                testCase("22080000000000000000000000000072", "72"),
                testCase("22080000000000000000000000000073", "73"),
                testCase("22080000000000000000000000000074", "74"),
                testCase("22080000000000000000000000000075", "75"),
                testCase("22080000000000000000000000000076", "76"),
                testCase("22080000000000000000000000000077", "77"),
                testCase("22080000000000000000000000000078", "78"),
                testCase("22080000000000000000000000000079", "79"),
                testCase("2208000000000000000000000000029e", "994"),
                testCase("2208000000000000000000000000029f", "995"),
                testCase("220800000000000000000000000002a0", "520"),
                testCase("220800000000000000000000000002a1", "521"),
                // DPD: one of each of the huffman groups
                testCase("220800000000000000000000000003f7", "777"),
                testCase("220800000000000000000000000003f8", "778"),
                testCase("220800000000000000000000000003eb", "787"),
                testCase("2208000000000000000000000000037d", "877"),
                testCase("2208000000000000000000000000039f", "997"),
                testCase("220800000000000000000000000003bf", "979"),
                testCase("220800000000000000000000000003df", "799"),
                testCase("2208000000000000000000000000006e", "888"),
                // DPD all-highs cases (includes the 24 redundant codes)
                testCase("2208000000000000000000000000006e", "888"),
                testCase("2208000000000000000000000000016e", "888", "2208000000000000000000000000006e"),
                testCase("2208000000000000000000000000026e", "888", "2208000000000000000000000000006e"),
                testCase("2208000000000000000000000000036e", "888", "2208000000000000000000000000006e"),
                testCase("2208000000000000000000000000006f", "889"),
                testCase("2208000000000000000000000000016f", "889", "2208000000000000000000000000006f"),
                testCase("2208000000000000000000000000026f", "889", "2208000000000000000000000000006f"),
                testCase("2208000000000000000000000000036f", "889", "2208000000000000000000000000006f"),
                testCase("2208000000000000000000000000007e", "898"),
                testCase("2208000000000000000000000000017e", "898", "2208000000000000000000000000007e"),
                testCase("2208000000000000000000000000027e", "898", "2208000000000000000000000000007e"),
                testCase("2208000000000000000000000000037e", "898", "2208000000000000000000000000007e"),
                testCase("2208000000000000000000000000007f", "899"),
                testCase("2208000000000000000000000000017f", "899", "2208000000000000000000000000007f"),
                testCase("2208000000000000000000000000027f", "899", "2208000000000000000000000000007f"),
                testCase("2208000000000000000000000000037f", "899", "2208000000000000000000000000007f"),
                testCase("220800000000000000000000000000ee", "988"),
                testCase("220800000000000000000000000001ee", "988", "220800000000000000000000000000ee"),
                testCase("220800000000000000000000000002ee", "988", "220800000000000000000000000000ee"),
                testCase("220800000000000000000000000003ee", "988", "220800000000000000000000000000ee"),
                testCase("220800000000000000000000000000ef", "989"),
                testCase("220800000000000000000000000001ef", "989", "220800000000000000000000000000ef"),
                testCase("220800000000000000000000000002ef", "989", "220800000000000000000000000000ef"),
                testCase("220800000000000000000000000003ef", "989", "220800000000000000000000000000ef"),
                testCase("220800000000000000000000000000fe", "998"),
                testCase("220800000000000000000000000001fe", "998", "220800000000000000000000000000fe"),
                testCase("220800000000000000000000000002fe", "998", "220800000000000000000000000000fe"),
                testCase("220800000000000000000000000003fe", "998", "220800000000000000000000000000fe"),
                testCase("220800000000000000000000000000ff", "999"),
                testCase("220800000000000000000000000001ff", "999", "220800000000000000000000000000ff"),
                testCase("220800000000000000000000000002ff", "999", "220800000000000000000000000000ff"),
                testCase("220800000000000000000000000003ff", "999", "220800000000000000000000000000ff"),
                // Miscellaneous (testers' queries, etc.)
                testCase("2208000000000000000000000000c000", "30000"),
                testCase("22080000000000000000000000007800", "890000"),
                // values around [u]int32 edges (zeros done earlier)
                testCase("a208000000000000000000008c78af46", "-2147483646"),
                testCase("a208000000000000000000008c78af47", "-2147483647"),
                testCase("a208000000000000000000008c78af48", "-2147483648"),
                testCase("a208000000000000000000008c78af49", "-2147483649"),
                testCase("2208000000000000000000008c78af46", "2147483646"),
                testCase("2208000000000000000000008c78af47", "2147483647"),
                testCase("2208000000000000000000008c78af48", "2147483648"),
                testCase("2208000000000000000000008c78af49", "2147483649"),
                testCase("22080000000000000000000115afb55a", "4294967294"),
                testCase("22080000000000000000000115afb55b", "4294967295"),
                testCase("22080000000000000000000115afb57a", "4294967296"),
                testCase("22080000000000000000000115afb57b", "4294967297"),
                // VG testcase
                testCase("2080000000000000F294000000172636", "8.81125000000001349436E-1548"),
                testCase("20800000000000008000000000000000", "8.000000000000000000E-1550"),
                testCase("1EF98490000000010F6E4E0000000000", "7.049000000000010795488000000000000E-3097")
        );
    }

    /**
     * Test case from source encoded string, to decimal string, and from decimal string to source encoded string.
     * <p>
     * For test cases that don't round-trip, use {@link #testCase(String, String, String)}
     * </p>
     *
     * @param sourceEncodedString
     *         Hex string of binary encoding of decimal (source and target)
     * @param decimalString
     *         String encoding of the decimal value (compatible with parsing by {@link BigDecimal}
     * @return Test case data
     */
    private static Object[] testCase(String sourceEncodedString, String decimalString) {
        return testCase(decimalString, hexToBytes(sourceEncodedString), dec(decimalString));
    }

    /**
     * Test case from source encoded string, to decimal string, and from decimal string to target encoded string.
     * <p>
     * For round-trippable conversions, use {@link #testCase(String, String)}
     * </p>
     *
     * @param sourceEncodedString
     *         Hex string of binary encoding of decimal (source)
     * @param decimalString
     *         String encoding of the decimal value (compatible with parsing by {@link BigDecimal}
     * @param targetEncodedString
     *         Hex string of binary encoding of decimal (target)
     * @return Test case data
     */
    private static Object[] testCase(String sourceEncodedString, String decimalString, String targetEncodedString) {
        return testCase(decimalString, hexToBytes(sourceEncodedString), dec(decimalString),
                hexToBytes(targetEncodedString));
    }

    /**
     * Test case from source bytes, to decimal string, and from decimal string to source bytes.
     * <p>
     * For test cases that don't round-trip, use {@link #testCase(String, byte[], Decimal128, byte[])}
     * </p>
     *
     * @param sourceBytes
     *         Binary encoding of decimal (source and target)
     * @param decimal128Value
     *         Decimal128 value
     * @return Test case data
     */
    private static Object[] testCase(String description, byte[] sourceBytes, Decimal128 decimal128Value) {
        return testCase(description, sourceBytes, decimal128Value, sourceBytes);
    }

    /**
     * Test case from source bytes, to decimal string, and from decimal string to source bytes.
     * <p>
     * For round-trippable conversions, use {@link #testCase(String, byte[], Decimal128)}
     * </p>
     *
     * @param sourceBytes
     *         Binary encoding of decimal (source)
     * @param decimal128Value
     *         Decimal128 value
     * @param targetBytes
     *         Binary encoding of decimal (target)
     * @return Test case data
     */
    private static Object[] testCase(String description, byte[] sourceBytes, Decimal128 decimal128Value,
            byte[] targetBytes) {
        return new Object[] { description, sourceBytes, decimal128Value, targetBytes };
    }

    private static Decimal128 dec(String decimalString) {
        BigDecimal bigDecimal = new BigDecimal(decimalString);
        return Decimal128.valueOfExact(bigDecimal);
    }
}
