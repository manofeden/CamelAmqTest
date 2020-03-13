package ru.gazpromneft.camelamqtest.exception;

public class ApplicationException extends RuntimeException{

    private static final long serialVersionUID = -6525179994769307219L;
    private static final String PREFIX = "My application: ";

    ApplicationException(String message) {
        super(PREFIX + message);
    }
}
