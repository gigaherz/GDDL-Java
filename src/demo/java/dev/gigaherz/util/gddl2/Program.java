package dev.gigaherz.util.gddl2;

import dev.gigaherz.util.gddl2.exceptions.ParserException;
import dev.gigaherz.util.gddl2.formatting.Formatter;

import java.io.IOException;

public class Program
{
    public static void main(String[] args)
    {
        try
        {
            var doc = GDDL.fromFile("Test.txt");
            var e = doc.getRoot();
            var result = Formatter.formatNice(e);
            System.out.println(result);
            //Files.writeString(Paths.get("./Test.output.txt"), result);

            var v3 = doc.getRoot().query("'named list'/[1]/[0]");
            System.out.println(v3.toList());
        }
        catch (IOException | ParserException e)
        {
            e.printStackTrace();
        }
    }
}
