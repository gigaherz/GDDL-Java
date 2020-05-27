package gigaherz.util.gddl2.serialization;

public class FormatterOptions
{
    public static final FormatterOptions COMPACT = new FormatterOptions(); // Default
    public static final FormatterOptions NICE = new FormatterOptions();

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

    // Collections
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
    public boolean omitCommaAfterClosingBrace = false;

    // Values
    public int lineBreaksAfterValues = 0;
    public DoubleFormattingStyle floatFormattingStyle = DoubleFormattingStyle.AUTO;
    public boolean alwaysShowNumberSign = false;
    public boolean alwaysShowExponentSign = false;
    public int autoScientificNotationUpper = 5;
    public int autoScientificNotationLower = -2;
    public int floatSignificantFigures = 15;

    // Indentation
    public boolean indentUsingTabs = false;
    public int spacesPerIndent = 2;

    // Comments
    public boolean writeComments = false;

}
