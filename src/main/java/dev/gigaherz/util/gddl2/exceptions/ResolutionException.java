package dev.gigaherz.util.gddl2.exceptions;

public class ResolutionException extends RuntimeException
{
    public ResolutionException(String message)
    {
        super(message);
    }

    public ResolutionException(String message, Throwable innerException)
    {
        super(message, innerException);
    }
}
