package gigaherz.util.gddl2.config;

public class StringGenerationOptions
{
    public static final StringGenerationOptions COMPACT = new StringGenerationOptions(); // Default
    public static final StringGenerationOptions NICE = new StringGenerationOptions();

    static {
        NICE.writeComments = true;
        NICE.lineBreaksAfterOpeningBrace = 1;
        NICE.lineBreaksBeforeClosingBrace = 1;
        NICE.lineBreaksAfterClosingBrace = 1;
        NICE.lineBreaksAfterValues = 1;
        NICE.spacesBeforeOpeningBrace = 0;
        NICE.spacesAfterOpeningBrace = 1;
        NICE.spacesBeforeClosingBrace = 1;
        NICE.spacesAfterClosingBrace = 0;
        NICE.spacesBetweenElements = 1;
        NICE.oneElementPerLineThreshold = 10;
        NICE.spacesPerIndent = 4;
    }

    // Sets
    public int lineBreaksBeforeOpeningBrace = 0;
    public int lineBreaksAfterOpeningBrace = 0;
    public int lineBreaksBeforeClosingBrace = 0;
    public int lineBreaksAfterClosingBrace = 0;
    public int spacesBeforeOpeningBrace = 0;
    public int spacesAfterOpeningBrace = 0;
    public int spacesBeforeClosingBrace = 0;
    public int spacesAfterClosingBrace = 0;
    public int oneElementPerLineThreshold = Integer.MAX_VALUE;
    public int spacesBetweenElements = 1;

    // Values
    public int lineBreaksAfterValues = 0;

    // Naming
    public boolean alwaysQuoteNames = false;
    public int lineBreaksAfterName = 0;

    // Typing
    public boolean alwaysQuoteTypes = false;
    public int lineBreaksAfterType = 0;

    // Indentation
    public boolean indentUsingTabs = false;
    public int spacesPerIndent = 2;
    public int indentSetContents = 0;
    public int indentExtraLines = 0;

    // Comments
    public boolean writeComments = false;

    // Uncategorized
    public boolean omitCommaAfterClosingBrace = false;
    public FloatMode floatFormattingStyle = FloatMode.AUTO;

    public enum FloatMode
    {
        DECIMAL, SCIENTIFIC, AUTO;
    }
}
