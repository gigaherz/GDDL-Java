package gigaherz.util.gddl2;

import gigaherz.util.gddl2.exceptions.ParserException;
import gigaherz.util.gddl2.serialization.Formatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Program
{
    public static void main(String[] args)
    {
        try
        {
            var parser = Parser.fromFile("./samples/Test.txt");
            var e = parser.parse();
            var result = Formatter.formatNice(e);
            System.out.println(result);
            Files.writeString(Paths.get("./samples/Test.output.txt"), result);
        }
        catch (IOException | ParserException e)
        {
            e.printStackTrace();
        }
    }
}
