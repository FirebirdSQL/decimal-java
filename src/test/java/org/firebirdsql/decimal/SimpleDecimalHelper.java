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

import java.math.BigInteger;

/**
 * @author <a href="mailto:mark@lawinegevaar.nl">Mark Rotteveel</a>
 */
class SimpleDecimalHelper {

    static SimpleDecimal fromString(String decString) {
        switch (decString.toLowerCase()) {
        case "infinity":
        case "+infinity":
            return SimpleDecimal.POSITIVE_INFINITY;
        case "-infinity":
            return SimpleDecimal.NEGATIVE_INFINITY;
        case "nan":
        case "+nan":
            return SimpleDecimal.POSITIVE_NAN;
        case "-nan":
            return SimpleDecimal.NEGATIVE_NAN;
        case "snan":
        case "+snan":
            return SimpleDecimal.POSITIVE_SIGNALING_NAN;
        case "-snan":
            return SimpleDecimal.NEGATIVE_SIGNALING_NAN;
        default:
            // handle below
            break;
        }
        // for simplicity only support integerE+/-integer, not decimals
        String[] coefficientAndExponent = decString.split("[Ee]");
        if (coefficientAndExponent.length != 2) {
            throw new NumberFormatException("Value does not match format, was: " + decString);
        }
        BigInteger coefficient = new BigInteger(coefficientAndExponent[0]);
        int exponent = Integer.parseInt(coefficientAndExponent[1]);
        return new SimpleDecimal(coefficient, exponent);
    }

}
