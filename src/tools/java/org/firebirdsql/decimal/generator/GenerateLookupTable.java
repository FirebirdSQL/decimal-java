/*
 * Copyright (c) 2017-2023 Firebird development team and individual contributors
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
package org.firebirdsql.decimal.generator;

import static org.firebirdsql.decimal.generator.LookupConstants.DPD2BIN;

/**
 * Generates a lookup table from DPD encoding of three digits to those three digits as characters.
 *
 * @author Mark Rotteveel
 */
public class GenerateLookupTable {

    public static void main(String[] args) {
        for (int index = 0; index < DPD2BIN.length; index++) {
            if (index % 12 == 0) {
                System.out.println();
            }
            System.out.print(toThreeDigitString(DPD2BIN[index]));
            if (index < DPD2BIN.length - 1) {
                System.out.print(" + ");
            } else {
                System.out.println();
            }
        }
    }

    private static String toThreeDigitString(int value) {
        return String.format("\"%03d\"", value);
    }

}
