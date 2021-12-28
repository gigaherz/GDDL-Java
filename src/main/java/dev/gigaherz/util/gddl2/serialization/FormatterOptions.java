package dev.gigaherz.util.gddl2.serialization;

public final class FormatterOptions
{
    public static final FormatterOptions COMPACT = new Builder().build(); // Default
    public static final FormatterOptions NICE = new Builder()
            .writeComments(true)
            .lineBreaksAfterOpeningBrace(1)
            .lineBreaksBeforeClosingBrace(1)
            .lineBreaksAfterClosingBrace(1)
            .lineBreaksAfterValues(1)
            .spacesBeforeOpeningBrace(0)
            .spacesAfterOpeningBrace(1)
            .spacesBeforeClosingBrace(1)
            .spacesAfterClosingBrace(0)
            .spacesInEmptyCollection(1)
            .spacesAfterComma(1)
            .spacesBeforeEquals(0)
            .spacesAfterEquals(1)
            .spacesInEmptyCollection(1)
            .oneElementPerLineThreshold(10)
            .spacesPerIndent(4)
            .blankLinesBeforeComment(1)
            .preferJsonStyle(true)
            .build();

    // Collections
    public final int lineBreaksBeforeOpeningBrace;
    public final int lineBreaksAfterOpeningBrace;
    public final int lineBreaksBeforeClosingBrace;
    public final int lineBreaksAfterClosingBrace;
    public final int spacesBeforeOpeningBrace;
    public final int spacesAfterOpeningBrace;
    public final int spacesBeforeClosingBrace;
    public final int spacesAfterClosingBrace;
    public final int spacesBeforeComma;
    public final int spacesAfterComma;
    public final int spacesBeforeEquals;
    public final int spacesAfterEquals;
    public final int spacesInEmptyCollection;
    public final int oneElementPerLineThreshold;
    public final boolean omitCommaAfterClosingBrace;

    // Values
    public final int lineBreaksAfterValues;
    public final DoubleFormattingStyle floatFormattingStyle;
    public final boolean alwaysShowNumberSign;
    public final boolean alwaysShowExponentSign;
    public final int autoScientificNotationUpper;
    public final int autoScientificNotationLower;
    public final int floatSignificantFigures;

    // Indentation
    public final boolean indentUsingTabs;
    public final int spacesPerIndent;

    // Comments
    public final boolean writeComments;
    public final int blankLinesBeforeComment;
    public final boolean trimCommentLines;

    // Other
    public final boolean preferJsonStyle;

    // Internal Constructor
    private FormatterOptions(Builder builder)
    {
        lineBreaksBeforeOpeningBrace = builder.lineBreaksBeforeOpeningBrace;
        lineBreaksAfterOpeningBrace = builder.lineBreaksAfterOpeningBrace;
        lineBreaksBeforeClosingBrace = builder.lineBreaksBeforeClosingBrace;
        lineBreaksAfterClosingBrace = builder.lineBreaksAfterClosingBrace;
        spacesBeforeOpeningBrace = builder.spacesBeforeOpeningBrace;
        spacesAfterOpeningBrace = builder.spacesAfterOpeningBrace;
        spacesBeforeClosingBrace = builder.spacesBeforeClosingBrace;
        spacesAfterClosingBrace = builder.spacesAfterClosingBrace;
        spacesBeforeComma = builder.spacesBeforeComma;
        spacesAfterComma = builder.spacesAfterComma;
        spacesBeforeEquals = builder.spacesBeforeEquals;
        spacesAfterEquals = builder.spacesAfterEquals;
        spacesInEmptyCollection = builder.spacesInEmptyCollection;
        oneElementPerLineThreshold = builder.oneElementPerLineThreshold;
        omitCommaAfterClosingBrace = builder.omitCommaAfterClosingBrace;
        lineBreaksAfterValues = builder.lineBreaksAfterValues;
        floatFormattingStyle = builder.floatFormattingStyle;
        alwaysShowNumberSign = builder.alwaysShowNumberSign;
        alwaysShowExponentSign = builder.alwaysShowExponentSign;
        autoScientificNotationUpper = builder.autoScientificNotationUpper;
        autoScientificNotationLower = builder.autoScientificNotationLower;
        floatSignificantFigures = builder.floatSignificantFigures;
        indentUsingTabs = builder.indentUsingTabs;
        spacesPerIndent = builder.spacesPerIndent;
        writeComments = builder.writeComments;
        blankLinesBeforeComment = builder.blankLinesBeforeComment;
        trimCommentLines = builder.trimCommentLines;
        preferJsonStyle = builder.preferJsonStyle;
    }

    public static final class Builder
    {
        // Collections
        private int lineBreaksBeforeOpeningBrace = 0;
        private int lineBreaksAfterOpeningBrace = 0;
        private int lineBreaksBeforeClosingBrace = 0;
        private int lineBreaksAfterClosingBrace = 0;
        private int spacesBeforeOpeningBrace = 0;
        private int spacesAfterOpeningBrace = 0;
        private int spacesBeforeClosingBrace = 0;
        private int spacesAfterClosingBrace = 0;
        private int spacesBeforeComma = 0;
        private int spacesAfterComma = 0;
        private int spacesBeforeEquals = 0;
        private int spacesAfterEquals = 0;
        private int spacesInEmptyCollection = 0;
        private int oneElementPerLineThreshold = Integer.MAX_VALUE;
        private boolean omitCommaAfterClosingBrace = false;

        // Values
        private int lineBreaksAfterValues = 0;
        private DoubleFormattingStyle floatFormattingStyle = DoubleFormattingStyle.AUTO;
        private boolean alwaysShowNumberSign = false;
        private boolean alwaysShowExponentSign = false;
        private int autoScientificNotationUpper = 5;
        private int autoScientificNotationLower = -2;
        private int floatSignificantFigures = 15;

        // Indentation
        private boolean indentUsingTabs = false;
        private int spacesPerIndent = 2;

        // Comments
        private boolean writeComments = false;
        private int blankLinesBeforeComment = 0;
        private boolean trimCommentLines = true;

        // Other
        private boolean preferJsonStyle = false;

        public Builder lineBreaksBeforeOpeningBrace(int lineBreaksBeforeOpeningBrace)
        {
            this.lineBreaksBeforeOpeningBrace = lineBreaksBeforeOpeningBrace;
            return this;
        }

        public Builder lineBreaksAfterOpeningBrace(int lineBreaksAfterOpeningBrace)
        {
            this.lineBreaksAfterOpeningBrace = lineBreaksAfterOpeningBrace;
            return this;
        }

        public Builder lineBreaksBeforeClosingBrace(int lineBreaksBeforeClosingBrace)
        {
            this.lineBreaksBeforeClosingBrace = lineBreaksBeforeClosingBrace;
            return this;
        }

        public Builder lineBreaksAfterClosingBrace(int lineBreaksAfterClosingBrace)
        {
            this.lineBreaksAfterClosingBrace = lineBreaksAfterClosingBrace;
            return this;
        }

        public Builder spacesBeforeOpeningBrace(int spacesBeforeOpeningBrace)
        {
            this.spacesBeforeOpeningBrace = spacesBeforeOpeningBrace;
            return this;
        }

        public Builder spacesAfterOpeningBrace(int spacesAfterOpeningBrace)
        {
            this.spacesAfterOpeningBrace = spacesAfterOpeningBrace;
            return this;
        }

        public Builder spacesBeforeClosingBrace(int spacesBeforeClosingBrace)
        {
            this.spacesBeforeClosingBrace = spacesBeforeClosingBrace;
            return this;
        }

        public Builder spacesAfterClosingBrace(int spacesAfterClosingBrace)
        {
            this.spacesAfterClosingBrace = spacesAfterClosingBrace;
            return this;
        }

        public Builder spacesBeforeComma(int spacesBeforeComma)
        {
            this.spacesBeforeComma = spacesBeforeComma;
            return this;
        }

        public Builder spacesAfterComma(int spacesAfterComma)
        {
            this.spacesAfterComma = spacesAfterComma;
            return this;
        }

        public Builder spacesBeforeEquals(int spacesBeforeEquals)
        {
            this.spacesBeforeEquals = spacesBeforeEquals;
            return this;
        }

        public Builder spacesAfterEquals(int spacesAfterEquals)
        {
            this.spacesAfterEquals = spacesAfterEquals;
            return this;
        }

        public Builder spacesInEmptyCollection(int spacesInEmptyCollection)
        {
            this.spacesInEmptyCollection = spacesInEmptyCollection;
            return this;
        }

        public Builder oneElementPerLineThreshold(int oneElementPerLineThreshold)
        {
            this.oneElementPerLineThreshold = oneElementPerLineThreshold;
            return this;
        }

        public Builder omitCommaAfterClosingBrace(boolean omitCommaAfterClosingBrace)
        {
            this.omitCommaAfterClosingBrace = omitCommaAfterClosingBrace;
            return this;
        }

        public Builder lineBreaksAfterValues(int lineBreaksAfterValues)
        {
            this.lineBreaksAfterValues = lineBreaksAfterValues;
            return this;
        }

        public Builder floatFormattingStyle(DoubleFormattingStyle floatFormattingStyle)
        {
            this.floatFormattingStyle = floatFormattingStyle;
            return this;
        }

        public Builder alwaysShowNumberSign(boolean alwaysShowNumberSign)
        {
            this.alwaysShowNumberSign = alwaysShowNumberSign;
            return this;
        }

        public Builder alwaysShowExponentSign(boolean alwaysShowExponentSign)
        {
            this.alwaysShowExponentSign = alwaysShowExponentSign;
            return this;
        }

        public Builder autoScientificNotationUpper(int autoScientificNotationUpper)
        {
            this.autoScientificNotationUpper = autoScientificNotationUpper;
            return this;
        }

        public Builder autoScientificNotationLower(int autoScientificNotationLower)
        {
            this.autoScientificNotationLower = autoScientificNotationLower;
            return this;
        }

        public Builder floatSignificantFigures(int floatSignificantFigures)
        {
            this.floatSignificantFigures = floatSignificantFigures;
            return this;
        }

        public Builder indentUsingTabs(boolean indentUsingTabs)
        {
            this.indentUsingTabs = indentUsingTabs;
            return this;
        }

        public Builder spacesPerIndent(int spacesPerIndent)
        {
            this.spacesPerIndent = spacesPerIndent;
            return this;
        }

        public Builder writeComments(boolean writeComments)
        {
            this.writeComments = writeComments;
            return this;
        }

        public Builder blankLinesBeforeComment(int blankLinesBeforeComment)
        {
            this.blankLinesBeforeComment = blankLinesBeforeComment;
            return this;
        }

        public Builder trimCommentLines(boolean trimCommentLines)
        {
            this.trimCommentLines = trimCommentLines;
            return this;
        }

        public FormatterOptions build()
        {
            return new FormatterOptions(this);
        }

        public Builder preferJsonStyle(boolean prefer)
        {
            this.preferJsonStyle = prefer;
            return this;
        }
    }
}
