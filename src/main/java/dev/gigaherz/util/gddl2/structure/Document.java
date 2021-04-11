package dev.gigaherz.util.gddl2.structure;

import dev.gigaherz.util.gddl2.serialization.Formatter;
import dev.gigaherz.util.gddl2.util.Utility;

import java.util.Objects;

public final class Document
{
    //region API
    public Document()
    {
    }

    public Document(Element<?> root)
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

    public Element<?> getRoot()
    {
        return root;
    }

    public void setRoot(Element<?> root)
    {
        this.root = root;
    }
    //endregion

    //region Implementation
    private Element<?> root;
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
        return equalsImpl((Document) other);
    }

    public boolean equals(Document other)
    {
        if (this == other) return true;
        if (other == null) return false;
        return equalsImpl(other);
    }

    private boolean equalsImpl(Document other)
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
