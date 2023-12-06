package dev.gigaherz.util.gddl2.structure;

import dev.gigaherz.util.gddl2.formatting.Formatter;
import dev.gigaherz.util.gddl2.formatting.FormatterOptions;
import dev.gigaherz.util.gddl2.internal.Utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class GddlDocument
{
    //region API
    public static GddlDocument create()
    {
        return new GddlDocument();
    }

    public static GddlDocument create(GddlElement<?> root)
    {
        return new GddlDocument(root);
    }

    public boolean hasDanglingComment()
    {
        return !Utility.isNullOrEmpty(danglingComment);
    }

    public String getDanglingComment()
    {
        return danglingComment;
    }

    public void setDanglingComment(String danglingComment)
    {
        this.danglingComment = danglingComment;
    }

    public GddlElement<?> getRoot()
    {
        return root;
    }

    public void write(Path path) throws IOException
    {
        Files.writeString(path, Formatter.format(root, FormatterOptions.COMPACT_HUMAN));
    }

    public void write(Path path, FormatterOptions formatterOptions) throws IOException
    {
        Files.writeString(path, Formatter.format(root, formatterOptions));
    }

    //endregion

    //region Implementation
    private final GddlElement<?> root;
    private String danglingComment;

    private GddlDocument()
    {
        this(GddlMap.empty());
    }

    private GddlDocument(GddlElement<?> root)
    {
        this.root = root;
    }
    //endregion

    //region toString
    @Override
    public String toString()
    {
        return Formatter.formatCompact(this);
    }
    //endregion

    //region Equality
    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsImpl((GddlDocument) other);
    }

    public boolean equals(GddlDocument other)
    {
        if (this == other) return true;
        if (other == null) return false;
        return equalsImpl(other);
    }

    private boolean equalsImpl(GddlDocument other)
    {
        return Objects.equals(root, other.root) && Objects.equals(danglingComment, other.danglingComment);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(root, danglingComment);
    }

    //endregion
}
