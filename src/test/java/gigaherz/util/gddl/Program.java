package gigaherz.util.gddl;

import gigaherz.util.gddl.config.StringGenerationContext;
import gigaherz.util.gddl.structure.Element;
import gigaherz.util.gddl.config.StringGenerationOptions;
import gigaherz.util.gddl.exceptions.ParserException;

import java.io.IOException;

public class Program
{
    public static void main(String[] args)
    {
        try
        {
            Parser parser = Parser.fromFile(".\\samples\\Test.txt");
            Element e = parser.parse();
            System.out.println(e.toString(new StringGenerationContext(StringGenerationOptions.Nice)));
        }
        catch (IOException | ParserException e)
        {
            e.printStackTrace();
        }
    }
}
