package br.net.du.myequity.exception;

public class MyEquityException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MyEquityException(final String message) {
        super(message);
    }
}
