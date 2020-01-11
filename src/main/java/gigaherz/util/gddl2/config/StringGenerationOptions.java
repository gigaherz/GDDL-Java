package gigaherz.util.gddl2.config;

public class StringGenerationOptions
{
    public static final StringGenerationOptions Compact = new StringGenerationOptions(); // Default
    public static final StringGenerationOptions Nice = new StringGenerationOptions();

    static {
        Nice.lineBreaksAfterOpeningBrace = 1;
        Nice.lineBreaksAfterClosingBrace = 1;
        Nice.lineBreaksAfterValues = 1;
        Nice.writeComments = true;
    }

    // Sets
    public int lineBreaksBeforeOpeningBrace = 0;
    public int lineBreaksAfterOpeningBrace = 0;
    public int lineBreaksBeforeClosingBrace = 0;
    public int lineBreaksAfterClosingBrace = 0;

    // Values
    public int lineBreaksAfterValues = 0;

    // Naming
    public boolean alwaysQuoteNames = false;
    public int lineBreaksAfterName = 0;

    // Typing
    public boolean alwaysQuoteTypes = false;
    public int lineBreaksAfterType = 0;

    // Indentation
    public int indentSetContents = 0;
    public int indentExtraLines = 0;

    // Comments
    public boolean writeComments = false;
}
