decimal-java
============

Decimal-java is a library to convert `java.math.BigDecimal` to and from 
IEEE-754r (IEEE-754-2008) decimal byte representations.

[![Java CI with Gradle](https://github.com/FirebirdSQL/decimal-java/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/FirebirdSQL/decimal-java/actions/workflows/gradle.yml?query=branch%3Amaster)
[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/org.firebirdsql/decimal-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.firebirdsql/decimal-java/)

Maven
-----

Get decimal-java from Maven:

```xml
<dependency>
    <groupId>org.firebirdsql</groupId>
    <artifactId>decimal-java</artifactId>
    <version>1.0.2</version>
</dependency>
```

License
-------

This library is licensed under the [MIT license](https://opensource.org/licenses/MIT),
see also LICENSE.md.

Status
------

The API is stable and is not expected to change.

Version 2.0.0 and higher require Java 17 or higher and is modularized with
module name `org.firebirdsql.decimal`. Version 1.0.2 requires Java 7 or higher,
and declares the automatic module name `org.firebirdsql.decimal`.

Goals
-----

This library provides conversion between `java.math.BigDecimal` and  IEEE-754r 
decimal formats.

Specifically supported are byte representations of:

- decimal32
- decimal64
- decimal128

Other formats (arbitrary precision decimal) are not planned to be supported.

Non-goals
---------

This library explicitly does not include mathematical operations on decimal.
As an alternative, consider using `BigDecimal` with `MathContext.DECIMAL128`,
`MathContext.DECIMAL64`, or `MathContext.DECIMAL32`.

Usage
-----

For full javadoc (of latest build), see https://firebirdsql.github.io/decimal-java/javadoc/

Decoding a 4-byte Decimal32 to a `java.math.BigDecimal`:

```java
byte[] bytes = {(byte) 0xc7, (byte) 0xf4, (byte) 0xd2, (byte) 0xe7};
Decimal32 decimal32 = Decimal32.parseBytes(bytes);
BigDecimal bigDecimal = decimal32.toBigDecimal();
assertEquals(new BigDecimal("-1.234567E+96"), bigDecimal);
```

The method `toBigDecimal` throws `DecimalInconvertibleException` if the decimal
value is an infinity or NaN value. The actual type and sign can be obtained from
the exception.

Encoding a `java.math.BigDecimal` to Decimal32 byte array:

```java
BigDecimal bigDecimal = new BigDecimal("-7.50E-7");
Decimal32 decimal32 = Decimal32.valueOf(bigDecimal);
byte[] bytes = decimal32.toBytes();
assertArrayEquals(new byte[] {(byte) 0xa1, (byte) 0xc0, 0x03, (byte) 0xd0}, bytes);
```

This will apply rounding if `bigDecimal` value doesn't fit a Decimal32, and
overflow will 'round' to infinity.

If overflow to infinity is unwanted, then use:

```java
BigDecimal bigDecimal = new BigDecimal("-7.50E-7");
Decimal32 decimal32 = Decimal32.valueOf(bigDecimal, OverflowHandling.THROW_EXCEPTION);
byte[] bytes = decimal32.toBytes();
assertArrayEquals(new byte[] {(byte) 0xa1, (byte) 0xc0, 0x03, (byte) 0xd0}, bytes);
```

Conversion works the same for `Decimal64` and `Decimal128`.

The `valueOf` methods exists for:

- `BigDecimal`
- `BigInteger`
  - In addition, there is `valueOfExact(BigInteger)` which throws 
 `DecimalOverflowException` if the `BigInteger` needs to be rounded to fit the
 target decimal type.
- `String`
- `double`
- `Decimal` (parent class of `Decimal32`, `Decimal64` and `Decimal128`) to allow
conversion between decimal types

The `valueOf` methods will round values to fit the target decimal type, and -
depending on the specified overflow handling - will either return +/- infinity
or throw an exception on overflow.

Conversion to a type is provided by:

- `toBytes()`
- `toBigDecimal()` - will throw `DecimalInconvertibleException` if the value is
an infinity or NaN value
- `toString()`
- `doubleValue()`
- `toDecimal(Class)` and `toDecimal(Class, OverflowHandling)`

To obtain a `BigInteger`, use `toBigDecimal().toBigInteger()` but be aware that 
large values (especially of `Decimal128`) can result in significant memory use. 

Background
----------

The reason this library exists is that [Firebird](https://www.firebirdsql.org/) 
version 4.0 added support for the SQL `DECFLOAT` type (with a precision of 16 
and 34 digits) using _decimal64_ and _decimal128_ in the protocol, and this 
support needed to be added to [Jaybird](https://github.com/FirebirdSQL/jaybird), 
the Firebird JDBC driver.

As I was unable to find an existing library for this (maybe my Google-fu is
lacking though), I created one. As I believe others may have similar needs, I 
decided to release it as a separate library.

I have tried to keep this library generically usable, but I'm sure this 
_raison d'être_ has informed a number of design and implementation decisions. 
Pull requests and suggestions for improvements are welcome.

Jaybird does not directly depend on this library, but instead contains a copy to
reduce the number of dependencies. External dependencies for JDBC drivers are
an annoyance in non-Maven/Gradle environments.

References
----------

-   [General Decimal Arithmetic](http://speleotrove.com/decimal/)
    -   [Decimal Arithmetic Encodings](http://speleotrove.com/decimal/decbits.html)
    -   [A Summary of Densely Packed Decimal encoding](http://speleotrove.com/decimal/DPDecimal.html)
    -   [The decNumber Library](http://speleotrove.com/decimal/decnumber.html)
-   [Firebird 4.0 Release Notes](https://www.firebirdsql.org/file/documentation/release_notes/html/en/4_0/rlsnotes40.html)

SPDX-License-Identifier: MIT 