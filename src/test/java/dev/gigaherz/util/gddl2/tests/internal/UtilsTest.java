package dev.gigaherz.util.gddl2.tests.internal;

import dev.gigaherz.util.gddl2.internal.Utility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilsTest
{
    @Test
    public void unescapeWorks()
    {
        Assertions.assertEquals("\r\n", Utility.unescapeString("'\\\r\n'"));
    }
}
