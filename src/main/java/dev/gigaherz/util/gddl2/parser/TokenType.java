package dev.gigaherz.util.gddl2.parser;

public enum TokenType
{
    COMMA,
    EQUALS,
    COLON,
    SLASH,
    DOT,
    DOUBLE_DOT,
    L_BRACE,
    R_BRACE,
    L_BRACKET,
    R_BRACKET,
    PERCENT,

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

    END
}