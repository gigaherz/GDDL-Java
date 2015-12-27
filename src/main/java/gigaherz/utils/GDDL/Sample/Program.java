package gigaherz.utils.GDDL.sample;

import gigaherz.utils.GDDL.Parser;
import gigaherz.utils.GDDL.config.StringGenerationContext;
import gigaherz.utils.GDDL.config.StringGenerationOptions;
import gigaherz.utils.GDDL.exceptions.ParserException;
import gigaherz.utils.GDDL.structure.Element;

import java.io.IOException;

public class Program
{
    public static void main(String[] args)
    {
        try
        {
            Parser parser = Parser.fromFile("H:\\Projects\\GDDL-Java\\samples\\Test.txt");
            Element e = parser.parse();
            System.out.println(e.toString(new StringGenerationContext(StringGenerationOptions.Nice)));
        }
        catch (IOException | ParserException e)
        {
            e.printStackTrace();
        }
    }
}
