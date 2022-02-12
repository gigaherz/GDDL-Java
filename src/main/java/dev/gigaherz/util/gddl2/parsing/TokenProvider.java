package dev.gigaherz.util.gddl2.parsing;

import dev.gigaherz.util.gddl2.exceptions.LexerException;

import java.io.IOException;

public interface TokenProvider extends ContextProvider, AutoCloseable
{
    /**
     * Returns the type of the first token in the lookahead buffer, reading new tokens from the Reader as necessary.
     *
     * @return The token type.
     * @throws LexerException When the lexing process encounters a problem
     * @throws IOException    When accessing the file for data
     */
    TokenType peek() throws LexerException, IOException;

    /**
     * Returns the type of the Nth token in the lookahead buffer, reading new tokens from the Reader as necessary.
     *
     * @param index The position from the lookahead buffer, starting at 0.
     * @return The token type at that position.
     * @throws LexerException When the lexing process encounters a problem
     * @throws IOException    When accessing the file for data
     */
    TokenType peek(int index) throws LexerException, IOException;

    /**
     * Removes the first token in the lookahead buffer, and returns it.
     *
     * @return The token.
     * @throws LexerException When the lexing process encounters a problem
     * @throws IOException    When accessing the file for data
     */
    Token pop() throws LexerException, IOException;

    /**
     * Gets the currently set whitespace processing mode.
     *
     * @return The current mode
     */
    WhitespaceMode getWhitespaceMode();

    /**
     * Changes the whitespace processing mode.
     *
     * @param whitespaceMode The new mode
     */
    void setWhitespaceMode(WhitespaceMode whitespaceMode);
}
