package it.pyrox.directa.exception;

import it.pyrox.directa.enums.ErrorEnum;

public class ErrorMessageException extends Exception {

    private final ErrorEnum error;

    public ErrorMessageException(ErrorEnum error) {
        this.error = error;
    }

    public ErrorEnum getError() {
        return error;
    }
}