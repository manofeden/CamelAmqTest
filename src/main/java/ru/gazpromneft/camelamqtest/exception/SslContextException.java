package ru.gazpromneft.camelamqtest.exception;

public class SslContextException extends ApplicationException {
    public SslContextException(String message) {
        super("Unable to create ssl context: "+ message);
    }
}
