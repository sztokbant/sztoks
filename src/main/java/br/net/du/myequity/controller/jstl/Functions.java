package br.net.du.myequity.controller.jstl;

import lombok.NonNull;

public class Functions {
    private Functions() {}

    public static String replaceAll(
            @NonNull final String input,
            @NonNull final String pattern,
            @NonNull final String replacement) {
        return input.replaceAll(pattern, replacement);
    }
}
