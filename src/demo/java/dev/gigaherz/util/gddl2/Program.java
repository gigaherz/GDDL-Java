package dev.gigaherz.util.gddl2;

import dev.gigaherz.util.gddl2.exceptions.ParserException;
import dev.gigaherz.util.gddl2.parser.Parser;
import dev.gigaherz.util.gddl2.serialization.Formatter;

import java.io.IOException;

public class Program
{
    public static void main(String[] args)
    {
        try
        {
            var parser = GDDL.fromFile("Test.txt");
            var doc = parser.parse();
            var e = doc.getRoot();
            var result = Formatter.formatNice(e);
            System.out.println(result);
            //Files.writeString(Paths.get("./Test.output.txt"), result);
        }
        catch (IOException | ParserException e)
        {
            e.printStackTrace();
        }
    }
}
