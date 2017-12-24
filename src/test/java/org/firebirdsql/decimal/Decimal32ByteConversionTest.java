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

/**
 * @author <a href="mailto:mark@lawinegevaar.nl">Mark Rotteveel</a>
 */
@RunWith(Parameterized.class)
public class Decimal32ByteConversionTest {

    @Parameterized.Parameter
    public String description;
    @Parameterized.Parameter(1)
    public byte[] sourceBytes;
    @Parameterized.Parameter(2)
    public Decimal32 decimalValue;
    @Parameterized.Parameter(3)
    public byte[] targetBytes;

    @Test
    public void testConversionFromBytesToDecimal32() {
        Decimal32 result = Decimal32.parseBytes(sourceBytes);

        assertEquals("Expected " + description, decimalValue, result);
    }

    @Test
    public void testConversionFromDecimal32ToBytes() {
        byte[] result = decimalValue.toBytes();

        assertArrayEquals(targetBytes, result);
    }

    @Parameterized.Parameters(name = "{index}: value {0} ({2})")
    public static Collection<Object[]> data() {
        return Arrays.asList(
                testCase("POSITIVE_INFINITY",
                        new byte[] { 0b0_11110_00, 0, 0, 0 }, Decimal32.POSITIVE_INFINITY),
                testCase("POSITIVE_INFINITY",
                        new byte[] { 0b0_11110_10, 0, 0, 0 }, Decimal32.POSITIVE_INFINITY,
                        new byte[] { 0b0_11110_00, 0, 0, 0 }),
                testCase("NEGATIVE_INFINITY",
                        new byte[] { (byte) 0b1_11110_00, 0, 0, 0 }, Decimal32.NEGATIVE_INFINITY),
                testCase("NEGATIVE_INFINITY",
                        new byte[] { (byte) 0b1_11110_10, 0, 0, 0 }, Decimal32.NEGATIVE_INFINITY,
                        new byte[] { (byte) 0b1_11110_00, 0, 0, 0 }),
                testCase("POSITIVE_NAN",
                        new byte[] { 0b0_11111_00, 0, 0, 0 }, Decimal32.POSITIVE_NAN),
                testCase("NEGATIVE_NAN",
                        new byte[] { (byte) 0b1_11111_00, 0, 0, 0 }, Decimal32.NEGATIVE_NAN),
                testCase("POSITIVE_SIGNALING_NAN",
                        new byte[] { 0b0_11111_10, 0, 0, 0 }, Decimal32.POSITIVE_SIGNALING_NAN),
                testCase("NEGATIVE_SIGNALING_NAN",
                        new byte[] { (byte) 0b1_11111_10, 0, 0, 0 }, Decimal32.NEGATIVE_SIGNALING_NAN),
                // cases from http://speleotrove.com/decimal/dectest.zip (dsEncode.decTest)
                // Comments copied to easier correlate file
                // General testcases
                // (mostly derived from the Strawman 4 document and examples)
                testCase("A23003D0", "-7.50"),
                // derivative canonical plain strings
                testCase("A26003D0", "-7.50E+3"),
                testCase("A25003D0", "-750"),
                testCase("A24003D0", "-75.0"),
                testCase("A22003D0", "-0.750"),
                testCase("A21003D0", "-0.0750"),
                testCase("A1f003D0", "-0.000750"),
                testCase("A1d003D0", "-0.00000750"),
                testCase("A1c003D0", "-7.50E-7"),
                // Normality
                testCase("2654d2e7", "1234567"),
                testCase("A654d2e7", "-1234567"),
                testCase("26524491", "1111111"),
                // Nmax and similar
                testCase("77f3fcff", "9.999999E+96"),
                testCase("47f4d2e7", "1.234567E+96"),
                // fold-downs (more below)
                // clamped testcase doesn't work (implementation limit of dec test method; TODO Fix)
                // TODO testCase("47f4c000", "1.23E+96"),
                testCase("47f4c000", "1.230000E+96"),
                // TODO testCase("47f00000", "1E+96"),
                testCase("47f00000", "1.000000E+96"),
                testCase("225049c5", "12345"),
                testCase("22500534", "1234"),
                testCase("225000a3", "123"),
                testCase("22500012", "12"),
                testCase("22500001", "1"),
                testCase("223000a3", "1.23"),
                testCase("223049c5", "123.45"),
                // Nmin and below
                testCase("00600001", "1E-95"),
                testCase("04000000", "1.000000E-95"),
                testCase("04000001", "1.000001E-95"),
                // TODO include other subnormal cases
                testCase("00020000", "1.00000E-96"),
                testCase("00000010", "1.0E-100"),
                testCase("00000010", "1.0E-100"),
                testCase("00000001", "1E-101"),
                // same again, negatives
                // Nmax and similar
                testCase("f7f3fcff", "-9.999999E+96"),
                testCase("c7f4d2e7", "-1.234567E+96"),
                // fold-downs (more below)
                // clamped testcase doesn't work (implementation limit of dec test method; TODO Fix)
                // TODO testCase("c7f4c000", "-1.23E+96"),
                testCase("c7f4c000", "-1.230000E+96"),
                // TODO testCase("c7f00000", "-1E+96"),
                testCase("c7f00000", "-1.000000E+96"),
                testCase("a25049c5", "-12345"),
                testCase("a2500534", "-1234"),
                testCase("a25000a3", "-123"),
                testCase("a2500012", "-12"),
                testCase("a2500001", "-1"),
                testCase("a23000a3", "-1.23"),
                testCase("a23049c5", "-123.45"),
                // Nmin and below
                testCase("80600001", "-1E-95"),
                testCase("84000000", "-1.000000E-95"),
                testCase("84000001", "-1.000001E-95"),
                // TODO include other subnormal cases
                testCase("80020000", "-1.00000E-96"),
                testCase("80000010", "-1.0E-100"),
                testCase("80000001", "-1E-101"),
                // zeros
                // TODO skipping some of the clamped cases
                testCase("00000000", "0E-101"),
                testCase("00000000", "0.000000E-95"),
                testCase("22300000", "0.00"),
                testCase("22500000", "0"),
                testCase("22800000", "0E+3"),
                testCase("43f00000", "0E+90"),
                // clamped zeros...
                // TODO skipping some of the clamped cases
                // negative zeros
                // TODO skipping some of the clamped cases
                testCase("-0E-101", hexToBytes("80000000"), dec("0E-101").negate()),
                testCase("-0.00", hexToBytes("a2300000"), dec("0.00").negate()),
                testCase("-0", hexToBytes("a2500000"), dec("0").negate()),
                testCase("-0E+3", hexToBytes("a2800000"), dec("0E+3").negate()),
                testCase("-0E+90", hexToBytes("c3f00000"), dec("0E+90").negate()),
                // clamped zeros...
                // TODO skipping some of the clamped cases
                //specials (may have overlap with earlier cases) TODO Some of these will certainly not roundtrip
                testCase("Infinity", hexToBytes("78000000"), Decimal32.POSITIVE_INFINITY),
                testCase("Infinity", hexToBytes("78787878"), Decimal32.POSITIVE_INFINITY, hexToBytes("78000000")),
                testCase("Infinity", hexToBytes("79797979"), Decimal32.POSITIVE_INFINITY, hexToBytes("78000000")),
                testCase("Infinity", hexToBytes("79000000"), Decimal32.POSITIVE_INFINITY, hexToBytes("78000000")),
                testCase("Infinity", hexToBytes("7a7a7a7a"), Decimal32.POSITIVE_INFINITY, hexToBytes("78000000")),
                testCase("Infinity", hexToBytes("7a000000"), Decimal32.POSITIVE_INFINITY, hexToBytes("78000000")),
                testCase("Infinity", hexToBytes("7b7b7b7b"), Decimal32.POSITIVE_INFINITY, hexToBytes("78000000")),
                testCase("Infinity", hexToBytes("7b000000"), Decimal32.POSITIVE_INFINITY, hexToBytes("78000000")),
                testCase("NaN", hexToBytes("7c000000"), Decimal32.POSITIVE_NAN),
                testCase("NaN", hexToBytes("7c7c7c7c"), Decimal32.POSITIVE_NAN, hexToBytes("7c000000")),
                testCase("NaN", hexToBytes("7c0c7c7c"), Decimal32.POSITIVE_NAN, hexToBytes("7c000000")),
                testCase("NaN", hexToBytes("7d7d7d7d"), Decimal32.POSITIVE_NAN, hexToBytes("7c000000")),
                testCase("NaN", hexToBytes("7c0d7d7d"), Decimal32.POSITIVE_NAN, hexToBytes("7c000000")),
                testCase("NaN", hexToBytes("7d000000"), Decimal32.POSITIVE_NAN, hexToBytes("7c000000")),
                testCase("sNaN", hexToBytes("7e000000"), Decimal32.POSITIVE_SIGNALING_NAN),
                testCase("sNaN", hexToBytes("7e7e7e7e"), Decimal32.POSITIVE_SIGNALING_NAN, hexToBytes("7e000000")),
                testCase("sNaN", hexToBytes("7e0e7c7e"), Decimal32.POSITIVE_SIGNALING_NAN, hexToBytes("7e000000")),
                testCase("sNaN", hexToBytes("7f000000"), Decimal32.POSITIVE_SIGNALING_NAN, hexToBytes("7e000000")),
                testCase("sNaN", hexToBytes("7f7f7f7f"), Decimal32.POSITIVE_SIGNALING_NAN, hexToBytes("7e000000")),
                testCase("sNaN", hexToBytes("7f0f7c7f"), Decimal32.POSITIVE_SIGNALING_NAN, hexToBytes("7e000000")),
                testCase("sNaN", hexToBytes("7fffffff"), Decimal32.POSITIVE_SIGNALING_NAN, hexToBytes("7e000000")),
                testCase("sNaN", hexToBytes("7e03fcff"), Decimal32.POSITIVE_SIGNALING_NAN, hexToBytes("7e000000")),
                testCase("-Infinity", hexToBytes("f8000000"), Decimal32.NEGATIVE_INFINITY),
                testCase("-Infinity", hexToBytes("f8787878"), Decimal32.NEGATIVE_INFINITY, hexToBytes("f8000000")),
                testCase("-Infinity", hexToBytes("f9797979"), Decimal32.NEGATIVE_INFINITY, hexToBytes("f8000000")),
                testCase("-Infinity", hexToBytes("f9000000"), Decimal32.NEGATIVE_INFINITY, hexToBytes("f8000000")),
                testCase("-Infinity", hexToBytes("fa7a7a7a"), Decimal32.NEGATIVE_INFINITY, hexToBytes("f8000000")),
                testCase("-Infinity", hexToBytes("fa000000"), Decimal32.NEGATIVE_INFINITY, hexToBytes("f8000000")),
                testCase("-Infinity", hexToBytes("fb7b7b7b"), Decimal32.NEGATIVE_INFINITY, hexToBytes("f8000000")),
                testCase("-Infinity", hexToBytes("fb000000"), Decimal32.NEGATIVE_INFINITY, hexToBytes("f8000000")),
                testCase("-NaN", hexToBytes("fc000000"), Decimal32.NEGATIVE_NAN),
                testCase("-NaN", hexToBytes("fc7c7c7c"), Decimal32.NEGATIVE_NAN, hexToBytes("fc000000")),
                testCase("-NaN", hexToBytes("fc0c7c7c"), Decimal32.NEGATIVE_NAN, hexToBytes("fc000000")),
                testCase("-NaN", hexToBytes("fd7d7d7d"), Decimal32.NEGATIVE_NAN, hexToBytes("fc000000")),
                testCase("-NaN", hexToBytes("fc0d7d7d"), Decimal32.NEGATIVE_NAN, hexToBytes("fc000000")),
                testCase("-NaN", hexToBytes("fd000000"), Decimal32.NEGATIVE_NAN, hexToBytes("fc000000")),
                testCase("-sNaN", hexToBytes("fe000000"), Decimal32.NEGATIVE_SIGNALING_NAN),
                testCase("-sNaN", hexToBytes("fe7e7e7e"), Decimal32.NEGATIVE_SIGNALING_NAN, hexToBytes("fe000000")),
                testCase("-sNaN", hexToBytes("fe0e7c7e"), Decimal32.NEGATIVE_SIGNALING_NAN, hexToBytes("fe000000")),
                testCase("-sNaN", hexToBytes("ff000000"), Decimal32.NEGATIVE_SIGNALING_NAN, hexToBytes("fe000000")),
                testCase("-sNaN", hexToBytes("ff7f7f7f"), Decimal32.NEGATIVE_SIGNALING_NAN, hexToBytes("fe000000")),
                testCase("-sNaN", hexToBytes("ff0f7c7f"), Decimal32.NEGATIVE_SIGNALING_NAN, hexToBytes("fe000000")),
                testCase("-sNaN", hexToBytes("ffffffff"), Decimal32.NEGATIVE_SIGNALING_NAN, hexToBytes("fe000000")),
                testCase("-sNaN", hexToBytes("fe03fcff"), Decimal32.NEGATIVE_SIGNALING_NAN, hexToBytes("fe000000")),
                // diagnostic NaNs skipped, not supported, handled as 'normal' NaNs!
                // fold-down full sequence
                // fold-down full sequence skipped (TODO Add?)
                // Selected DPD codes
                testCase("22500000", "0"),
                testCase("22500009", "9"),
                testCase("22500010", "10"),
                testCase("22500019", "19"),
                testCase("22500020", "20"),
                testCase("22500029", "29"),
                testCase("22500030", "30"),
                testCase("22500039", "39"),
                testCase("22500040", "40"),
                testCase("22500049", "49"),
                testCase("22500050", "50"),
                testCase("22500059", "59"),
                testCase("22500060", "60"),
                testCase("22500069", "69"),
                testCase("22500070", "70"),
                testCase("22500071", "71"),
                testCase("22500072", "72"),
                testCase("22500073", "73"),
                testCase("22500074", "74"),
                testCase("22500075", "75"),
                testCase("22500076", "76"),
                testCase("22500077", "77"),
                testCase("22500078", "78"),
                testCase("22500079", "79"),
                testCase("2250029e", "994"),
                testCase("2250029f", "995"),
                testCase("225002a0", "520"),
                testCase("225002a1", "521"),
                // DPD: one of each of the huffman groups
                testCase("225003f7", "777"),
                testCase("225003f8", "778"),
                testCase("225003eb", "787"),
                testCase("2250037d", "877"),
                testCase("2250039f", "997"),
                testCase("225003bf", "979"),
                testCase("225003df", "799"),
                testCase("2250006e", "888"),
                // DPD all-highs cases (includes the 24 redundant codes)
                testCase("2250006e", "888"),
                testCase("2250016e", "888", "2250006e"),
                testCase("2250026e", "888", "2250006e"),
                testCase("2250036e", "888", "2250006e"),
                testCase("2250006f", "889"),
                testCase("2250016f", "889", "2250006f"),
                testCase("2250026f", "889", "2250006f"),
                testCase("2250036f", "889", "2250006f"),
                testCase("2250007e", "898"),
                testCase("2250017e", "898", "2250007e"),
                testCase("2250027e", "898", "2250007e"),
                testCase("2250037e", "898", "2250007e"),
                testCase("2250007f", "899"),
                testCase("2250017f", "899", "2250007f"),
                testCase("2250027f", "899", "2250007f"),
                testCase("2250037f", "899", "2250007f"),
                testCase("225000ee", "988"),
                testCase("225001ee", "988", "225000ee"),
                testCase("225002ee", "988", "225000ee"),
                testCase("225003ee", "988", "225000ee"),
                testCase("225000ef", "989"),
                testCase("225001ef", "989", "225000ef"),
                testCase("225002ef", "989", "225000ef"),
                testCase("225003ef", "989", "225000ef"),
                testCase("225000fe", "998"),
                testCase("225001fe", "998", "225000fe"),
                testCase("225002fe", "998", "225000fe"),
                testCase("225003fe", "998", "225000fe"),
                testCase("225000ff", "999"),
                testCase("225001ff", "999", "225000ff"),
                testCase("225002ff", "999", "225000ff"),
                testCase("225003ff", "999", "225000ff"),
                // narrowing case
                testCase("00000100", "2.00E-99")
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
     * For test cases that don't round-trip, use {@link #testCase(String, byte[], Decimal32, byte[])}
     * </p>
     *
     * @param sourceBytes
     *         Binary encoding of decimal (source and target)
     * @param decimal32Value
     *         Decimal32 value
     * @return Test case data
     */
    private static Object[] testCase(String description, byte[] sourceBytes, Decimal32 decimal32Value) {
        return testCase(description, sourceBytes, decimal32Value, sourceBytes);
    }

    /**
     * Test case from source bytes, to decimal string, and from decimal string to source bytes.
     * <p>
     * For round-trippable conversions, use {@link #testCase(String, byte[], Decimal32)}
     * </p>
     *
     * @param sourceBytes
     *         Binary encoding of decimal (source)
     * @param decimal32Value
     *         Decimal32 value
     * @param targetBytes
     *         Binary encoding of decimal (target)
     * @return Test case data
     */
    private static Object[] testCase(String description, byte[] sourceBytes, Decimal32 decimal32Value,
            byte[] targetBytes) {
        return new Object[] { description, sourceBytes, decimal32Value, targetBytes };
    }

    private static Decimal32 dec(String decimalString) {
        BigDecimal bigDecimal = new BigDecimal(decimalString);
        return Decimal32.valueOfExact(bigDecimal);
    }
}
