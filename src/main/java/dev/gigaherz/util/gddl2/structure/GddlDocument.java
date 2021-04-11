package dev.gigaherz.util.gddl2.structure;

import dev.gigaherz.util.gddl2.serialization.Formatter;
import dev.gigaherz.util.gddl2.util.Utility;

import java.util.Objects;

public final class GddlDocument
{
    //region API
    public GddlDocument()
    {
    }

    public GddlDocument(GddlElement<?> root)
    {
        this.root = root;
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

    public void setRoot(GddlElement<?> root)
    {
        this.root = root;
    }
    //endregion

    //region Implementation
    private GddlElement<?> root;
    private String danglingComment;
    //endregion

    //region toString
    @Override
    public final String toString()
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
