package dev.gigaherz.util.gddl2.util;

@FunctionalInterface
public interface BooleanFunction<T>
{
    T apply(boolean b);
}
