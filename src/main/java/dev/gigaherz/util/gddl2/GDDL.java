package dev.gigaherz.util.gddl2;

import dev.gigaherz.util.gddl2.exceptions.ParserException;
import dev.gigaherz.util.gddl2.parser.Lexer;
import dev.gigaherz.util.gddl2.parser.Parser;
import dev.gigaherz.util.gddl2.structure.GddlDocument;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GDDL
{
    //region Factory Methods

    /**
     * Constructs a Parser instance that reads from the given filename.
     *
     * @param filename The filename to read from.
     * @return A parser ready to process the file.
     * @throws IOException When accessing the file.
     */
    public static GddlDocument fromFile(String filename) throws ParserException, IOException
    {
        return fromFile(filename, StandardCharsets.UTF_8);
    }

    /**
     * Constructs a Parser instance that reads from the given filename.
     *
     * @param filename The filename to read from.
     * @param charset  The charset.
     * @return A parser ready to process the file.
     * @throws IOException When accessing the file.
     */
    public static GddlDocument fromFile(String filename, Charset charset) throws ParserException, IOException
    {
        return fromReader(new FileReader(filename, charset), filename);
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     *
     * @param file The file to read from.
     * @return A parser ready to process the file.
     * @throws IOException When accessing the file.
     */
    public static GddlDocument fromFile(File file) throws ParserException, IOException
    {
        return fromFile(file, StandardCharsets.UTF_8);
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     *
     * @param file    The file to read from.
     * @param charset The charset.
     * @return A parser ready to process the file.
     * @throws IOException When accessing the file.
     */
    public static GddlDocument fromFile(File file, Charset charset) throws ParserException, IOException
    {
        return fromReader(new FileReader(file, charset), file.getAbsolutePath());
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     *
     * @param path The file to read from.
     * @return A parser ready to process the file.
     * @throws IOException When accessing the file.
     */
    public static GddlDocument fromFile(Path path) throws ParserException, IOException
    {
        return fromFile(path, StandardCharsets.UTF_8);
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     *
     * @param path    The file to read from.
     * @param charset The charset.
     * @return A parser ready to process the file.
     * @throws IOException When accessing the file.
     */
    public static GddlDocument fromFile(Path path, Charset charset) throws ParserException, IOException
    {
        return fromReader(Files.newBufferedReader(path, charset), path.toString());
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     *
     * @param stream The file to read from.
     * @return A parser ready to process the file.
     */
    public static GddlDocument fromStream(InputStream stream) throws ParserException, IOException
    {
        return fromStream(stream, StandardCharsets.UTF_8);
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     *
     * @param stream The file to read from.
     * @return A parser ready to process the file.
     */
    public static GddlDocument fromStream(InputStream stream, String sourceName) throws ParserException, IOException
    {
        return fromStream(stream, StandardCharsets.UTF_8, sourceName);
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     *
     * @param stream  The file to read from.
     * @param charset The charset.
     * @return A parser ready to process the file.
     */
    public static GddlDocument fromStream(InputStream stream, Charset charset) throws ParserException, IOException
    {
        return fromStream(stream, charset, "UNKNOWN");
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     *
     * @param stream  The file to read from.
     * @param charset The charset.
     * @return A parser ready to process the file.
     */
    public static GddlDocument fromStream(InputStream stream, Charset charset, String sourceName) throws ParserException, IOException
    {
        return fromReader(new InputStreamReader(stream, charset), sourceName);
    }

    /**
     * Constructs a Parser instance that reads from the given string.
     *
     * @param text The text to parse.
     * @return A parser ready to process the file.
     */
    public static GddlDocument fromString(String text) throws ParserException, IOException
    {
        return fromString(text, "UNKNOWN");
    }

    /**
     * Constructs a Parser instance that reads from the given string.
     *
     * @param text       The text to parse.
     * @param sourceName The filename to display in parse errors.
     * @return A parser ready to process the file.
     */
    public static GddlDocument fromString(String text, String sourceName) throws ParserException, IOException
    {
        return fromReader(new StringReader(text), sourceName);
    }

    /**
     * Constructs a Parser instance that reads from the given reader.
     *
     * @param reader The stream to read from.
     * @return A parser ready to process the file.
     */
    public static GddlDocument fromReader(java.io.Reader reader) throws ParserException, IOException
    {
        return fromReader(reader, "UNKNOWN");
    }

    /**
     * Constructs a Parser instance that reads from the given file.
     *
     * @param reader     The stream to read from.
     * @param sourceName The filename to display in parse errors.
     * @return A parser ready to process the file.
     */
    public static GddlDocument fromReader(java.io.Reader reader, String sourceName) throws ParserException, IOException
    {
        var parser = new Parser(new Lexer(new dev.gigaherz.util.gddl2.parser.Reader(reader, sourceName)));
        return parser.parse();
    }
    //endregion
}
