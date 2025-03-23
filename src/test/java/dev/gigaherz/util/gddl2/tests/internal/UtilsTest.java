package dev.gigaherz.util.gddl2.tests.internal;

import dev.gigaherz.util.gddl2.internal.Utility;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsTest
{
    @Test
    public void unescapeWorks()
    {
        assertEquals("\r", Utility.unescapeString("'\\\r'"));
        assertEquals("\n", Utility.unescapeString("'\\\n'"));
        assertEquals("\r\n", Utility.unescapeString("'\\\r\\\n'"));
        assertEquals("\r\n", Utility.unescapeString("'\\\r\n'"));
        assertEquals("\r\n", Utility.unescapeString("'\r\\\n'"));
    }
}
