package gigaherz.util.gddl2;

import gigaherz.util.gddl2.exceptions.LexerException;

import java.io.IOException;

public interface TokenProvider extends ContextProvider, AutoCloseable
{
    TokenType peek() throws LexerException, IOException;
    TokenType peek(int index) throws LexerException, IOException;

    Token pop() throws LexerException, IOException;

    @Override
    void close() throws IOException;
}
