package gigaherz.util.gddl2.config;

import gigaherz.util.gddl2.util.BasicIntStack;

import java.util.Stack;

public class StringGenerationContext
{
    public StringGenerationOptions options;

    private final BasicIntStack indentLevels = new BasicIntStack();

    public int indentLevel = 0;

    public StringGenerationContext(StringGenerationOptions options)
    {
        this.options = options;
    }

    public void pushIndent()
    {
        indentLevels.push(indentLevel);
    }

    public void popIndent()
    {
        indentLevel = indentLevels.pop();
    }

    public void setIndent(int newIndent)
    {
        indentLevel = newIndent;
    }

    public void incIndent()
    {
        indentLevel++;
    }

    public void appendIndent(StringBuilder builder)
    {
        int tabsToGen = indentLevel;
        for (int i = 0; i < tabsToGen; i++)
        {
            if (options.indentUsingTabs)
            {
                builder.append("\t");
            }
            else
            {
                for (int j = 0; j < options.spacesPerIndent; j++)
                {
                    builder.append(" ");
                }
            }
        }
    }
}
