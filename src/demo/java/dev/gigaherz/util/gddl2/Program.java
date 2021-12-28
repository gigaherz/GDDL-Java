package dev.gigaherz.util.gddl2;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.gigaherz.util.gddl2.dynamic.GDDLOps;
import dev.gigaherz.util.gddl2.exceptions.ParserException;
import dev.gigaherz.util.gddl2.serialization.Formatter;

import java.io.IOException;
import java.util.List;

public class Program
{
    public static void main(String[] args)
    {
        try
        {
            var doc = GDDL.fromFile("sample.json");
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
