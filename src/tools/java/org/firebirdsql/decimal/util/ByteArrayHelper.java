package org.firebirdsql.decimal.util;

import java.util.HexFormat;

/**
 * Helper methods for byte arrays.
 *
 * @author Mark Rotteveel
 */
public final class ByteArrayHelper {

    private ByteArrayHelper() {
        // no instances
    }

    /**
     * Converts a hexadecimal string to a byte array.
     *
     * @param hexString
     *         Hexadecimal string or {@code null}
     * @return byte array, or {@code null} if {@code hexString} is {@code null}
     */
    public static byte[] hexToBytes(String hexString) {
        if (hexString == null) {
            return null;
        }
        return HexFormat.of().parseHex(hexString);
    }

}
