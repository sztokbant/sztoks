package br.net.du.sztoks.exception;

public class SztoksException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SztoksException(final String message) {
        super(message);
    }
}
