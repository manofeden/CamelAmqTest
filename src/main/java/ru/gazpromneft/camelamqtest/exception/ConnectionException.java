package ru.gazpromneft.camelamqtest.exception;

public class ConnectionException extends ApplicationException {
    private static final long serialVersionUID = 5989180019403837279L;
    private static final String PREFIX = "unable to create Jms Connection Factory: ";

    public ConnectionException(final String message) {
        super(PREFIX + message);
    }
}
