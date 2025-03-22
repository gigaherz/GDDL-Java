package dev.gigaherz.util.gddl2;

import dev.gigaherz.util.gddl2.exceptions.ParserException;
import dev.gigaherz.util.gddl2.formatting.Formatter;
import dev.gigaherz.util.gddl2.formatting.FormatterOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Program
{
    public static void main(String[] args)
    {
        try
        {
            var doc = GDDL.fromFile("Test.txt");
            var value = doc.getRoot();

            doc.write(Paths.get("./output1.txt"), FormatterOptions.NICE_HUMAN);

            var simple = value.simplify();

            var result = Formatter.formatNice(simple);
            System.out.println(result);
            Files.writeString(Paths.get("./output2.txt"), result);

            var v3 = doc.getRoot().query("'named list'/[1]/[0]");
            System.out.println(v3.toList());
        }
        catch (IOException | ParserException e)
        {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
