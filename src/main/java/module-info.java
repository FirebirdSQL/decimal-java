import org.jspecify.annotations.NullMarked;

/**
 * Decimal-java is a library to convert {@link java.math.BigDecimal} to and from IEEE-754r (IEEE-754-2008) decimal byte
 * representations.
 * <p>
 * For more information, consult the project's README on
 * <a href="https://github.com/FirebirdSQL/decimal-java">https://github.com/FirebirdSQL/decimal-java</a>.
 * </p>
 */
@NullMarked
module org.firebirdsql.decimal {
    requires static org.jspecify;
    exports org.firebirdsql.decimal;
}