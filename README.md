decimal-java
============

Decimal-java is a library to convert `java.math.BigDecimal` to and from 
IEEE-754r (IEEE-754-2008) decimal byte representations.

License
-------

This library is licensed under the [MIT license](https://opensource.org/licenses/MIT),
see also LICENSE.md.

Status
------

At the moment, the API and underlying implementation is still in a state of 
flux, this is expected to stabilize mid-January 2018.

Goals
-----

This library provides conversion between `java.math.BigDecimal` and  IEEE-754r 
decimal formats.

Specifically supported are byte representations of:

- decimal32
- decimal64
- decimal128

Other formats (arbitrary precision decimal) are not planned to be supported.

For the time being, the minimum supported Java version is Java 7.

Non-goals
---------

This library explicitly does not include mathematical operations on decimal.
As an alternative, consider using `BigDecimal` with `MathContext.DECIMAL128`,
`MathContext.DECIMAL64`, or `MathContext.DECIMAL32`.

Background
----------

The reason this library exists is that [Firebird](https://www.firebirdsql.org/) 
version 4.0 added support for the SQL `DECFLOAT` type (with a precision of 16 
and 34 digits) using _decimal64_ and _decimal128_ in the protocol, and this 
support needed to be added to [Jaybird](https://github.com/FirebirdSQL/jaybird), 
the Firebird JDBC driver.

Unfortunately, I ([Mark Rotteveel](https://github.com/mrotteveel/)) was unable 
to find an existing library for this (maybe my Google-fu is lacking though), so 
I created one. As I believe others may have similar needs, I decided to release 
it as a separate library.

I have tried to keep this library generically usable, but I'm sure this 
_raison d'être_ has informed a number of design and implementation decisions. 
Pull requests and suggestions for improvements are welcome.

Jaybird does not directly depend on this library, but instead contains a copy to
reduce the number of dependencies. External dependencies for JDBC drivers are
an annoyance in non-maven/gradle environments, and Jaybird already has three 
(although two are optional).

References
----------

-   [General Decimal Arithmetic](http://speleotrove.com/decimal/)
    -   [Decimal Arithmetic Encodings](http://speleotrove.com/decimal/decbits.html)
    -   [A Summary of Densely Packed Decimal encoding](http://speleotrove.com/decimal/DPDecimal.html)
    -   [The decNumber Library](http://speleotrove.com/decimal/decnumber.html)
-   [Firebird 4 alpha 1 release notes](http://web.firebirdsql.org/downloads/prerelease/v40alpha1/Firebird-4.0.0_Alpha1-ReleaseNotes.pdf)

 