package br.net.du.myequity.exception;

public class UserNotFoundException extends MyEquityException {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException() {
        this("User not found");
    }

    public UserNotFoundException(final String message) {
        super(message);
    }
}
