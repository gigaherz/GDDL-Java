package gigaherz.utils.GDDL;

public class StringGenerationContext
{
    public enum StringGenerationOptions
    {
        Compact, // Default
        Nice
    }

    public StringGenerationOptions Options;

    public int IndentLevel = 1;

    public StringGenerationContext(StringGenerationOptions options)
    {
        Options = options;
    }
}
