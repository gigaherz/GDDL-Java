package dev.gigaherz.util.gddl2.parsing;

public enum TokenType
{
    COMMA,
    EQUAL_SIGN,
    COLON,
    SLASH,
    DOT,
    DOUBLE_DOT,
    TRIPLE_DOT,
    L_BRACE,
    R_BRACE,
    L_BRACKET,
    R_BRACKET,
    PERCENT,
    CARET,

    HEX_INT_LITERAL,
    INTEGER_LITERAL,
    DECIMAL_LITERAL,
    STRING_LITERAL,

    IDENTIFIER,

    // identifiers
    NIL,
    NULL,
    TRUE,
    FALSE,

    // type identifiers
    BOOLEAN,
    STRING,
    INTEGER,
    DECIMAL,

    // end
    END
}