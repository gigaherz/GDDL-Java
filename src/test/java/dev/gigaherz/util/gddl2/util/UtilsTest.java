package dev.gigaherz.util.gddl2.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsTest
{
    @Test
    public void unescapeWorks()
    {
        assertEquals("\r\n", Utility.unescapeString("'\\\r\n'"));
    }
}
