package dev.gigaherz.util.gddl2.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilsTest
{
    @Test
    public void unescapeWorks()
    {
        assertEquals("\r\n", Utility.unescapeString("'\\\r\n'"));
    }
}
