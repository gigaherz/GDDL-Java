package gigaherz.util.gddl2.config;

public class StringGenerationOptions
{
    public static final StringGenerationOptions Compact = new StringGenerationOptions(); // Default
    public static final StringGenerationOptions Nice = new StringGenerationOptions();

    static {
        Nice.writeComments = true;
        Nice.lineBreaksAfterOpeningBrace = 1;
        Nice.lineBreaksBeforeClosingBrace = 1;
        Nice.lineBreaksAfterClosingBrace = 1;
        Nice.lineBreaksAfterValues = 1;
        Nice.spacesBeforeOpeningBrace = 0;
        Nice.spacesAfterOpeningBrace = 1;
        Nice.spacesBeforeClosingBrace = 1;
        Nice.spacesAfterClosingBrace = 0;
        Nice.spacesBetweenElements = 1;
        Nice.oneElementPerLineThreshold = 10;
        Nice.spacesPerIndent = 4;
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
}
