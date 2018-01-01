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
public class Decimal64ByteConversionTest {

    @Parameterized.Parameter
    public String description;
    @Parameterized.Parameter(1)
    public byte[] sourceBytes;
    @Parameterized.Parameter(2)
    public Decimal64 decimalValue;
    @Parameterized.Parameter(3)
    public byte[] targetBytes;

    @Test
    public void testConversionFromBytesToDecimal64() {
        assumeTrue("No source bytes for " + description, sourceBytes != null);
        Decimal64 result = Decimal64.parseBytes(sourceBytes);

        assertEquals("Expected " + description, decimalValue, result);
    }

    @Test
    public void testConversionFromDecimal64ToBytes() {
        assumeTrue("No target bytes for " + description, targetBytes != null);
        byte[] result = decimalValue.toBytes();

        assertArrayEquals(targetBytes, result);
    }

    @Parameterized.Parameters(name = "{index}: value {0} ({2})")
    public static Collection<Object[]> data() {
        return Arrays.asList(
                testCase("POSITIVE_INFINITY",
                        new byte[] { 0b0_11110_00, 0, 0, 0, 0, 0, 0, 0 }, Decimal64.POSITIVE_INFINITY),
                testCase("POSITIVE_INFINITY",
                        new byte[] { 0b0_11110_10, 0, 0, 0, 0, 0, 0, 0 }, Decimal64.POSITIVE_INFINITY,
                        new byte[] { 0b0_11110_00, 0, 0, 0, 0, 0, 0, 0 }),
                testCase("NEGATIVE_INFINITY",
                        new byte[] { (byte) 0b1_11110_00, 0, 0, 0, 0, 0, 0, 0 }, Decimal64.NEGATIVE_INFINITY),
                testCase("NEGATIVE_INFINITY",
                        new byte[] { (byte) 0b1_11110_10, 0, 0, 0, 0, 0, 0, 0 }, Decimal64.NEGATIVE_INFINITY,
                        new byte[] { (byte) 0b1_11110_00, 0, 0, 0, 0, 0, 0, 0 }),
                testCase("POSITIVE_NAN",
                        new byte[] { 0b0_11111_00, 0, 0, 0, 0, 0, 0, 0 }, Decimal64.POSITIVE_NAN),
                testCase("NEGATIVE_NAN",
                        new byte[] { (byte) 0b1_11111_00, 0, 0, 0, 0, 0, 0, 0 }, Decimal64.NEGATIVE_NAN),
                testCase("POSITIVE_SIGNALING_NAN",
                        new byte[] { 0b0_11111_10, 0, 0, 0, 0, 0, 0, 0 }, Decimal64.POSITIVE_SIGNALING_NAN),
                testCase("NEGATIVE_SIGNALING_NAN",
                        new byte[] { (byte) 0b1_11111_10, 0, 0, 0, 0, 0, 0, 0 }, Decimal64.NEGATIVE_SIGNALING_NAN),
                testCase("2238000000000010", "10"),
                // cases from http://speleotrove.com/decimal/dectest.zip (ddEncode.decTest)
                // Comments copied to easier correlate file
                testCase("A2300000000003D0", "-7.50"),
                // derivative canonical plain strings
                testCase("A23c0000000003D0", "-7.50E+3"),
                testCase("A2380000000003D0", "-750"),
                testCase("A2340000000003D0", "-75.0"),
                testCase("A22c0000000003D0", "-0.750"),
                testCase("A2280000000003D0", "-0.0750"),
                testCase("A2200000000003D0", "-0.000750"),
                testCase("A2180000000003D0", "-0.00000750"),
                testCase("A2140000000003D0", "-7.50E-7"),
                // Normality
                testCase("263934b9c1e28e56", "1234567890123456"),
                testCase("a63934b9c1e28e56", "-1234567890123456"),
                testCase("260934b9c1e28e56", "1234.567890123456"),
                testCase("2638912449124491", "1111111111111111"),
                testCase("6e38ff3fcff3fcff", "9999999999999999"),
                // Nmax and similar
                testCase("77fcff3fcff3fcff", "9999999999999999E369"),
                testCase("77fcff3fcff3fcff", "9.999999999999999E+384"),
                testCase("47fd34b9c1e28e56", "1.234567890123456E+384"),
                // fold-downs
                testCase(null, "1.23E+384", "47fd300000000000"),
                testCase("47fd300000000000", "1.230000000000000E+384"),
                testCase(null, "1E+384", "47fc000000000000"),
                testCase("47fc000000000000", "1.000000000000000E+384"),
                testCase("22380000000049c5", "12345"),
                testCase("2238000000000534", "1234"),
                testCase("22380000000000a3", "123"),
                testCase("2238000000000012", "12"),
                testCase("2238000000000001", "1"),
                testCase("22300000000000a3", "1.23"),
                testCase("22300000000049c5", "123.45"),
                // Nmin and below
                testCase("003c000000000001", "1E-383"),
                testCase("0400000000000000", "1.000000000000000E-383"),
                testCase("0400000000000001", "1.000000000000001E-383"),
                testCase("0000800000000000", "0.100000000000000E-383"),
                testCase("0000800000000000", "1.00000000000000E-384"),
                testCase("0000000000000010", "0.000000000000010E-383"),
                testCase("0000000000000010", "1.0E-397"),
                testCase("0004000000000001", "0.00000000000001E-383"),
                testCase("0004000000000001", "1E-397"),
                testCase("0000000000000001", "0.000000000000001E-383"),
                testCase("0000000000000001", "1E-398"),
                // next is smallest all-nines
                testCase("6400ff3fcff3fcff", "9999999999999999E-398"),
                testCase("6400ff3fcff3fcff", "9.999999999999999E-383"),
                // and a problematic divide result
                testCase("0400912449124491", "1.111111111111111E-383"),
                // forties
                testCase("2238000000000040", "40"),
                testCase("2230000000000cff", "39.99"),
                // Same again, negatives
                // Nmax and similar
                testCase("f7fcff3fcff3fcff", "-9.999999999999999E+384"),
                testCase("c7fd34b9c1e28e56", "-1.234567890123456E+384"),
                // fold-downs
                testCase(null, "-1.23E+384", "c7fd300000000000"),
                testCase("c7fd300000000000", "-1.230000000000000E+384"),
                testCase(null, "-1E+384", "c7fc000000000000"),
                testCase("c7fc000000000000", "-1.000000000000000E+384"),
                // overflows
                testCase("a2380000000049c5", "-12345"),
                testCase("a238000000000534", "-1234"),
                testCase("a2380000000000a3", "-123"),
                testCase("a238000000000012", "-12"),
                testCase("a238000000000001", "-1"),
                testCase("a2300000000000a3", "-1.23"),
                testCase("a2300000000049c5", "-123.45"),
                // Nmin and below
                testCase("803c000000000001", "-1E-383"),
                testCase("8400000000000000", "-1.000000000000000E-383"),
                testCase("8400000000000001", "-1.000000000000001E-383"),
                testCase("8000800000000000", "-0.100000000000000E-383"),
                testCase("8000800000000000", "-1.00000000000000E-384"),
                testCase("8000000000000010", "-0.000000000000010E-383"),
                testCase("8000000000000010", "-1.0E-397"),
                testCase("8004000000000001", "-0.00000000000001E-383"),
                testCase("8004000000000001", "-1E-397"),
                testCase("8000000000000001", "-0.000000000000001E-383"),
                testCase("8000000000000001", "-1E-398"),
                // next is smallest all-nines
                testCase("e400ff3fcff3fcff", "-9999999999999999E-398"),
                testCase("e400ff3fcff3fcff", "-9.999999999999999E-383"),
                // and a tricky subnormal
                testCase("00009124491246a4", "1.11111111111524E-384"),
                // Case missing in dectest?
                testCase("80009124491246a4", "-1.11111111111524E-384"),
                // near-underflows
                testCase("8000000000000001", "-1e-398"),
                testCase(null, "-1.0e-398", "8000000000000001"),
                // zeros
                testCase(null, "0E-500", "0000000000000000"),
                testCase(null, "0E-400", "0000000000000000"),
                testCase("0000000000000000", "0E-398"),
                testCase("0000000000000000", "0.000000000000000E-383"),
                testCase("2230000000000000", "0E-2"),
                testCase("2230000000000000", "0.00"),
                testCase("2238000000000000", "0"),
                testCase("2244000000000000", "0E+3"),
                testCase("43fc000000000000", "0E+369"),
                // clamped zeros...
                testCase(null, "0E+370", "43fc000000000000"),
                testCase(null, "0E+384", "43fc000000000000"),
                testCase(null, "0E+400", "43fc000000000000"),
                testCase(null, "0E+500", "43fc000000000000"),
                // negative zeros
                // clamped testcase doesn't work (implementation limit of dec test method; TODO Fix)
                testCase(null, "-0E-500", "8000000000000000"),
                testCase(null, "-0E-400", "8000000000000000"),
                testCase("8000000000000000", "-0E-398"),
                testCase("8000000000000000", "-0.000000000000000E-383"),
                testCase("a230000000000000", "-0E-2"),
                testCase("a238000000000000", "-0"),
                testCase("a244000000000000", "-0E+3"),
                testCase("c3fc000000000000", "-0E+369"),
                // clamped zeros...
                testCase(null, "-0E+370", "c3fc000000000000"),
                testCase(null, "-0E+384", "c3fc000000000000"),
                testCase(null, "-0E+400", "c3fc000000000000"),
                testCase(null, "-0E+500", "c3fc000000000000"),
                // exponents
                testCase("225c000000000007", "7E+9"),
                testCase("23c4000000000007", "7E+99"),
                //specials (may have overlap with earlier cases)
                testCase("Infinity", hexToBytes("7800000000000000"), Decimal64.POSITIVE_INFINITY),
                testCase("Infinity", hexToBytes("7878787878787878"), Decimal64.POSITIVE_INFINITY, null),
                testCase("Infinity", hexToBytes("7979797979797979"), Decimal64.POSITIVE_INFINITY, null),
                testCase("Infinity", hexToBytes("7900000000000000"), Decimal64.POSITIVE_INFINITY, null),
                testCase("Infinity", hexToBytes("7a7a7a7a7a7a7a7a"), Decimal64.POSITIVE_INFINITY, null),
                testCase("Infinity", hexToBytes("7a00000000000000"), Decimal64.POSITIVE_INFINITY, null),
                testCase("Infinity", hexToBytes("7b7b7b7b7b7b7b7b"), Decimal64.POSITIVE_INFINITY, null),
                testCase("Infinity", hexToBytes("7b00000000000000"), Decimal64.POSITIVE_INFINITY, null),
                testCase("NaN", hexToBytes("7c00000000000000"), Decimal64.POSITIVE_NAN),
                testCase("NaN", hexToBytes("7c7c7c7c7c7c7c7c"), Decimal64.POSITIVE_NAN, null),
                testCase("NaN", hexToBytes("7c007c7c7c7c7c7c"), Decimal64.POSITIVE_NAN, null),
                testCase("NaN", hexToBytes("7d7d7d7d7d7d7d7d"), Decimal64.POSITIVE_NAN, null),
                testCase("NaN", hexToBytes("7c017d7d7d7d7d7d"), Decimal64.POSITIVE_NAN, null),
                testCase("sNaN", hexToBytes("7e00000000000000"), Decimal64.POSITIVE_SIGNALING_NAN),
                testCase("sNaN", hexToBytes("7e7e7e7e7e7e7e7e"), Decimal64.POSITIVE_SIGNALING_NAN, null),
                testCase("sNaN", hexToBytes("7e007e7e7e7e7c7e"), Decimal64.POSITIVE_SIGNALING_NAN, null),
                testCase("sNaN", hexToBytes("7f7f7f7f7f7f7f7f"), Decimal64.POSITIVE_SIGNALING_NAN, null),
                testCase("sNaN", hexToBytes("7e007f7f7f7f7c7f"), Decimal64.POSITIVE_SIGNALING_NAN, null),
                testCase("sNaN", hexToBytes("7f00000000000000"), Decimal64.POSITIVE_SIGNALING_NAN, null),
                testCase("sNaN", hexToBytes("7fffffffffffffff"), Decimal64.POSITIVE_SIGNALING_NAN, null),
                testCase("sNaN", hexToBytes("7e00ff3fcff3fcff"), Decimal64.POSITIVE_SIGNALING_NAN, null),
                testCase("-Infinity", hexToBytes("f800000000000000"), Decimal64.NEGATIVE_INFINITY),
                testCase("-Infinity", hexToBytes("f878787878787878"), Decimal64.NEGATIVE_INFINITY, null),
                testCase("-Infinity", hexToBytes("f979797979797979"), Decimal64.NEGATIVE_INFINITY, null),
                testCase("-Infinity", hexToBytes("fa7a7a7a7a7a7a7a"), Decimal64.NEGATIVE_INFINITY, null),
                testCase("-Infinity", hexToBytes("fa00000000000000"), Decimal64.NEGATIVE_INFINITY, null),
                testCase("-Infinity", hexToBytes("fb7b7b7b7b7b7b7b"), Decimal64.NEGATIVE_INFINITY, null),
                testCase("-Infinity", hexToBytes("fb00000000000000"), Decimal64.NEGATIVE_INFINITY, null),
                testCase("-NaN", hexToBytes("fc00000000000000"), Decimal64.NEGATIVE_NAN),
                testCase("-NaN", hexToBytes("fc7c7c7c7c7c7c7c"), Decimal64.NEGATIVE_NAN, null),
                testCase("-NaN", hexToBytes("fc007c7c7c7c7c7c"), Decimal64.NEGATIVE_NAN, null),
                testCase("-NaN", hexToBytes("fd7d7d7d7d7d7d7d"), Decimal64.NEGATIVE_NAN, null),
                testCase("-NaN", hexToBytes("fc017d7d7d7d7d7d"), Decimal64.NEGATIVE_NAN, null),
                testCase("-sNaN", hexToBytes("fe00000000000000"), Decimal64.NEGATIVE_SIGNALING_NAN),
                testCase("-sNaN", hexToBytes("fe7e7e7e7e7e7e7e"), Decimal64.NEGATIVE_SIGNALING_NAN, null),
                testCase("-sNaN", hexToBytes("fe007e7e7e7e7c7e"), Decimal64.NEGATIVE_SIGNALING_NAN, null),
                testCase("-sNaN", hexToBytes("ff7f7f7f7f7f7f7f"), Decimal64.NEGATIVE_SIGNALING_NAN, null),
                testCase("-sNaN", hexToBytes("fe007f7f7f7f7c7f"), Decimal64.NEGATIVE_SIGNALING_NAN, null),
                testCase("-sNaN", hexToBytes("ff00000000000000"), Decimal64.NEGATIVE_SIGNALING_NAN, null),
                testCase("-sNaN", hexToBytes("ffffffffffffffff"), Decimal64.NEGATIVE_SIGNALING_NAN, null),
                testCase("-sNaN", hexToBytes("fe00ff3fcff3fcff"), Decimal64.NEGATIVE_SIGNALING_NAN, null),
                // diagnostic NaNs skipped, not supported, handled as 'normal' NaNs!
                // too many digits
                // fold-down full sequence
                testCase(null, "1E+384", "47fc000000000000"),
                testCase("47fc000000000000", "1.000000000000000E+384"),
                testCase(null, "1E+383", "43fc800000000000"),
                testCase("43fc800000000000", "1.00000000000000E+383"),
                testCase(null, "1E+382", "43fc100000000000"),
                testCase("43fc100000000000", "1.0000000000000E+382"),
                testCase(null, "1E+381", "43fc010000000000"),
                testCase("43fc010000000000", "1.000000000000E+381"),
                testCase(null, "1E+380", "43fc002000000000"),
                testCase("43fc002000000000", "1.00000000000E+380"),
                testCase(null, "1E+379", "43fc000400000000"),
                testCase("43fc000400000000", "1.0000000000E+379"),
                testCase(null, "1E+378", "43fc000040000000"),
                testCase("43fc000040000000", "1.000000000E+378"),
                testCase(null, "1E+377", "43fc000008000000"),
                testCase("43fc000008000000", "1.00000000E+377"),
                testCase(null, "1E+376", "43fc000001000000"),
                testCase("43fc000001000000", "1.0000000E+376"),
                testCase(null, "1E+375", "43fc000000100000"),
                testCase("43fc000000100000", "1.000000E+375"),
                testCase(null, "1E+374", "43fc000000020000"),
                testCase("43fc000000020000", "1.00000E+374"),
                testCase(null, "1E+373", "43fc000000004000"),
                testCase("43fc000000004000", "1.0000E+373"),
                testCase(null, "1E+372", "43fc000000000400"),
                testCase("43fc000000000400", "1.000E+372"),
                testCase(null, "1E+371", "43fc000000000080"),
                testCase("43fc000000000080", "1.00E+371"),
                testCase(null, "1E+370", "43fc000000000010"),
                testCase("43fc000000000010", "1.0E+370"),
                testCase("43fc000000000001", "1E+369"),
                testCase("43f8000000000001", "1E+368"),
                // same with 9s
                testCase(null, "9E+384", "77fc000000000000"),
                testCase("77fc000000000000", "9.000000000000000E+384"),
                testCase(null, "9E+383", "43fc8c0000000000"),
                testCase("43fc8c0000000000", "9.00000000000000E+383"),
                testCase(null, "9E+382", "43fc1a0000000000"),
                testCase("43fc1a0000000000", "9.0000000000000E+382"),
                testCase(null, "9E+381", "43fc090000000000"),
                testCase("43fc090000000000", "9.000000000000E+381"),
                testCase(null, "9E+380", "43fc002300000000"),
                testCase("43fc002300000000", "9.00000000000E+380"),
                testCase(null, "9E+379", "43fc000680000000"),
                testCase("43fc000680000000", "9.0000000000E+379"),
                testCase(null, "9E+378", "43fc000240000000"),
                testCase("43fc000240000000", "9.000000000E+378"),
                testCase(null, "9E+377", "43fc000008c00000"),
                testCase("43fc000008c00000", "9.00000000E+377"),
                testCase(null, "9E+376", "43fc000001a00000"),
                testCase("43fc000001a00000", "9.0000000E+376"),
                testCase(null, "9E+375", "43fc000000900000"),
                testCase("43fc000000900000", "9.000000E+375"),
                testCase(null, "9E+374", "43fc000000023000"),
                testCase("43fc000000023000", "9.00000E+374"),
                testCase(null, "9E+373", "43fc000000006800"),
                testCase("43fc000000006800", "9.0000E+373"),
                testCase(null, "9E+372", "43fc000000002400"),
                testCase("43fc000000002400", "9.000E+372"),
                testCase(null, "9E+371", "43fc00000000008c"),
                testCase("43fc00000000008c", "9.00E+371"),
                testCase(null, "9E+370", "43fc00000000001a"),
                testCase("43fc00000000001a", "9.0E+370"),
                testCase("43fc000000000009", "9E+369"),
                testCase("43f8000000000009", "9E+368"),
                // Selected DPD codes
                testCase("2238000000000000", "0"),
                testCase("2238000000000009", "9"),
                testCase("2238000000000010", "10"),
                testCase("2238000000000019", "19"),
                testCase("2238000000000020", "20"),
                testCase("2238000000000029", "29"),
                testCase("2238000000000030", "30"),
                testCase("2238000000000039", "39"),
                testCase("2238000000000040", "40"),
                testCase("2238000000000049", "49"),
                testCase("2238000000000050", "50"),
                testCase("2238000000000059", "59"),
                testCase("2238000000000060", "60"),
                testCase("2238000000000069", "69"),
                testCase("2238000000000070", "70"),
                testCase("2238000000000071", "71"),
                testCase("2238000000000072", "72"),
                testCase("2238000000000073", "73"),
                testCase("2238000000000074", "74"),
                testCase("2238000000000075", "75"),
                testCase("2238000000000076", "76"),
                testCase("2238000000000077", "77"),
                testCase("2238000000000078", "78"),
                testCase("2238000000000079", "79"),
                testCase("223800000000029e", "994"),
                testCase("223800000000029f", "995"),
                testCase("22380000000002a0", "520"),
                testCase("22380000000002a1", "521"),
                // from telco test data
                testCase("2238000000000188", "308"),
                testCase("22380000000001a3", "323"),
                testCase("223800000000002a", "82"),
                testCase("22380000000001a9", "329"),
                testCase("2238000000000081", "101"),
                testCase("22380000000002a2", "522"),
                // DPD: one of each of the huffman groups
                testCase("22380000000003f7", "777"),
                testCase("22380000000003f8", "778"),
                testCase("22380000000003eb", "787"),
                testCase("223800000000037d", "877"),
                testCase("223800000000039f", "997"),
                testCase("22380000000003bf", "979"),
                testCase("22380000000003df", "799"),
                testCase("223800000000006e", "888"),
                // DPD all-highs cases (includes the 24 redundant codes)
                testCase("223800000000006e", "888"),
                testCase("223800000000016e", "888", "223800000000006e"),
                testCase("223800000000026e", "888", "223800000000006e"),
                testCase("223800000000036e", "888", "223800000000006e"),
                testCase("223800000000006f", "889"),
                testCase("223800000000016f", "889", "223800000000006f"),
                testCase("223800000000026f", "889", "223800000000006f"),
                testCase("223800000000036f", "889", "223800000000006f"),
                testCase("223800000000007e", "898"),
                testCase("223800000000017e", "898", "223800000000007e"),
                testCase("223800000000027e", "898", "223800000000007e"),
                testCase("223800000000037e", "898", "223800000000007e"),
                testCase("223800000000007f", "899"),
                testCase("223800000000017f", "899", "223800000000007f"),
                testCase("223800000000027f", "899", "223800000000007f"),
                testCase("223800000000037f", "899", "223800000000007f"),
                testCase("22380000000000ee", "988"),
                testCase("22380000000001ee", "988", "22380000000000ee"),
                testCase("22380000000002ee", "988", "22380000000000ee"),
                testCase("22380000000003ee", "988", "22380000000000ee"),
                testCase("22380000000000ef", "989"),
                testCase("22380000000001ef", "989", "22380000000000ef"),
                testCase("22380000000002ef", "989", "22380000000000ef"),
                testCase("22380000000003ef", "989", "22380000000000ef"),
                testCase("22380000000000fe", "998"),
                testCase("22380000000001fe", "998", "22380000000000fe"),
                testCase("22380000000002fe", "998", "22380000000000fe"),
                testCase("22380000000003fe", "998", "22380000000000fe"),
                testCase("22380000000000ff", "999"),
                testCase("22380000000001ff", "999", "22380000000000ff"),
                testCase("22380000000002ff", "999", "22380000000000ff"),
                testCase("22380000000003ff", "999", "22380000000000ff"),
                // values around [u]int32 edges (zeros done earlier)
                testCase("a23800008c78af46", "-2147483646"),
                testCase("a23800008c78af47", "-2147483647"),
                testCase("a23800008c78af48", "-2147483648"),
                testCase("a23800008c78af49", "-2147483649"),
                testCase("223800008c78af46", "2147483646"),
                testCase("223800008c78af47", "2147483647"),
                testCase("223800008c78af48", "2147483648"),
                testCase("223800008c78af49", "2147483649"),
                testCase("2238000115afb55a", "4294967294"),
                testCase("2238000115afb55b", "4294967295"),
                testCase("2238000115afb57a", "4294967296"),
                testCase("2238000115afb57b", "4294967297"),
                // for narrowing
                testCase("2870000000000000", "2.000000000000000E-99"),
                // some miscellaneous
                testCase("0004070000000000", "7.000000000000E-385"),
                testCase("0008000000020000", "1.00000E-391")
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
     * For test cases that don't round-trip, use {@link #testCase(String, byte[], Decimal64, byte[])}
     * </p>
     *
     * @param sourceBytes
     *         Binary encoding of decimal (source and target)
     * @param decimal64Value
     *         Decimal64 value
     * @return Test case data
     */
    private static Object[] testCase(String description, byte[] sourceBytes, Decimal64 decimal64Value) {
        return testCase(description, sourceBytes, decimal64Value, sourceBytes);
    }

    /**
     * Test case from source bytes, to decimal string, and from decimal string to source bytes.
     * <p>
     * For round-trippable conversions, use {@link #testCase(String, byte[], Decimal64)}
     * </p>
     *
     * @param sourceBytes
     *         Binary encoding of decimal (source)
     * @param decimal64Value
     *         Decimal64 value
     * @param targetBytes
     *         Binary encoding of decimal (target)
     * @return Test case data
     */
    private static Object[] testCase(String description, byte[] sourceBytes, Decimal64 decimal64Value,
            byte[] targetBytes) {
        return new Object[] { description, sourceBytes, decimal64Value, targetBytes };
    }

    private static Decimal64 dec(String decimalString) {
        return Decimal64.valueOf(decimalString);
    }
}
