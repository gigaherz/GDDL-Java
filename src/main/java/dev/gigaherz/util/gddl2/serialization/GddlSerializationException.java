package dev.gigaherz.util.gddl2.serialization;

public class GddlSerializationException extends RuntimeException
{
    public GddlSerializationException()
    {
        super();
    }

    public GddlSerializationException(String message)
    {
        super(message);
    }

    public GddlSerializationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GddlSerializationException(Throwable cause)
    {
        super(cause);
    }
}
