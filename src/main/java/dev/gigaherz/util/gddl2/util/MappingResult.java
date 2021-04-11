package dev.gigaherz.util.gddl2.util;

import dev.gigaherz.util.gddl2.structure.Collection;
import dev.gigaherz.util.gddl2.structure.Element;
import dev.gigaherz.util.gddl2.structure.Reference;
import dev.gigaherz.util.gddl2.structure.Value;

import java.util.Optional;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Supplier;

public abstract class MappingResult<T>
{
    public static <T> MappingResult<T> of(T value)
    {
        return new WithValue(value);
    }

    public static <T> MappingResult<T> remainder(Element<?> remainder)
    {
        return new WithRemainder(remainder);
    }

    /**
     * If this element is a Collection, applies a mapping that returns a new Element.
     * Otherwise, returns itself.
     *
     * @param mapping The function to apply if the current element
     * @return The mapped value, or itself.
     */
    public abstract MappingResult<T> mapCollection(Function<Collection, T> mapping);

    /**
     * If this element is a Collection, applies a mapping that returns a new Element.
     * Otherwise, returns itself.
     *
     * @param mapping The function to apply if the current element
     * @return The mapped value, or itself.
     */
    public abstract MappingResult<T> mapValue(Function<Value, T> mapping);

    /**
     * If this element is a Collection, applies a mapping that returns a new Element.
     * Otherwise, returns itself.
     *
     * @param mapping The function to apply if the current element
     * @return The mapped value, or itself.
     */
    public abstract MappingResult<T> mapReference(Function<Reference, T> mapping);

    public abstract MappingResult<T> mapNull(Supplier<T> mapping);

    public abstract MappingResult<T> mapString(Function<String, T> mapping);

    public abstract MappingResult<T> mapBoolean(BooleanFunction<T> mapping);

    public abstract MappingResult<T> mapInteger(LongFunction<T> mapping);

    public abstract MappingResult<T> mapDouble(DoubleFunction<T> mapping);

    public abstract <R> MappingResult<R> map(Function<T, R> mapping);

    public abstract T get();

    public abstract T orElse(T value);

    public abstract T orElseGet(Supplier<T> supplier);

    public abstract T orElseMap(Function<Element<?>, T> mapping);

    public abstract <E extends Throwable> T orElseThrow(Supplier<E> exceptionFactory) throws E;

    public abstract Optional<T> asOptional();

    private static class WithValue<T> extends MappingResult<T>
    {
        private final T value;

        public WithValue(T value)
        {
            this.value = value;
        }

        @Override
        public MappingResult<T> mapCollection(Function<Collection, T> mapping)
        {
            return this;
        }

        @Override
        public MappingResult<T> mapValue(Function<Value, T> mapping)
        {
            return this;
        }

        @Override
        public MappingResult<T> mapReference(Function<Reference, T> mapping)
        {
            return this;
        }

        @Override
        public MappingResult<T> mapNull(Supplier<T> mapping)
        {
            return this;
        }

        @Override
        public MappingResult<T> mapString(Function<String, T> mapping)
        {
            return this;
        }

        @Override
        public MappingResult<T> mapBoolean(BooleanFunction<T> mapping)
        {
            return this;
        }

        @Override
        public MappingResult<T> mapInteger(LongFunction<T> mapping)
        {
            return this;
        }

        @Override
        public MappingResult<T> mapDouble(DoubleFunction<T> mapping)
        {
            return this;
        }

        @Override
        public <R> MappingResult<R> map(Function<T, R> mapping)
        {
            return of(mapping.apply(value));
        }

        @Override
        public T get()
        {
            return value;
        }

        @Override
        public T orElse(T value)
        {
            return this.value;
        }

        @Override
        public T orElseGet(Supplier<T> supplier)
        {
            return this.value;
        }

        @Override
        public T orElseMap(Function<Element<?>, T> mapping)
        {
            return value;
        }

        @Override
        public <E extends Throwable> T orElseThrow(Supplier<E> exceptionFactory) throws E
        {
            return this.value;
        }

        @Override
        public Optional<T> asOptional()
        {
            return Optional.of(value);
        }
    }

    private static class WithRemainder<T> extends MappingResult<T>
    {
        private final Element<?> remainder;

        public WithRemainder(Element<?> remainder)
        {
            this.remainder = remainder;
        }

        @Override
        public MappingResult<T> mapCollection(Function<Collection, T> mapping)
        {
            if (remainder.isCollection())
                return of(mapping.apply(remainder.asCollection()));
            return this;
        }

        @Override
        public MappingResult<T> mapValue(Function<Value, T> mapping)
        {
            if (remainder.isValue())
                return of(mapping.apply(remainder.asValue()));
            return this;
        }

        @Override
        public MappingResult<T> mapReference(Function<Reference, T> mapping)
        {
            if (remainder.isReference())
                return of(mapping.apply(remainder.asReference()));
            return this;
        }

        @Override
        public MappingResult<T> mapNull(Supplier<T> mapping)
        {
            if (remainder.isNull())
                return of(mapping.get());
            return this;
        }

        @Override
        public MappingResult<T> mapString(Function<String, T> mapping)
        {
            if (remainder.isString())
                return of(mapping.apply(remainder.asString()));
            return this;
        }

        @Override
        public MappingResult<T> mapBoolean(BooleanFunction<T> mapping)
        {
            if (remainder.isBoolean())
                return of(mapping.apply(remainder.asBoolean()));
            return this;
        }

        @Override
        public MappingResult<T> mapInteger(LongFunction<T> mapping)
        {
            if (remainder.isInteger())
                return of(mapping.apply(remainder.asInteger()));
            return this;
        }

        @Override
        public MappingResult<T> mapDouble(DoubleFunction<T> mapping)
        {
            if (remainder.isDouble())
                return of(mapping.apply(remainder.asDouble()));
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R> MappingResult<R> map(Function<T, R> mapping)
        {
            return (MappingResult<R>) this;
        }

        @Override
        public T get()
        {
            throw new IllegalStateException("This mapping was not resolved to a value.");
        }

        @Override
        public T orElse(T value)
        {
            return value;
        }

        @Override
        public T orElseGet(Supplier<T> supplier)
        {
            return supplier.get();
        }

        @Override
        public T orElseMap(Function<Element<?>, T> mapping)
        {
            return mapping.apply(remainder);
        }

        @Override
        public <E extends Throwable> T orElseThrow(Supplier<E> exceptionFactory) throws E
        {
            throw exceptionFactory.get();
        }

        @Override
        public Optional<T> asOptional()
        {
            return Optional.empty();
        }
    }
}
