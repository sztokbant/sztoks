package br.net.du.myequity.model.util;

import br.net.du.myequity.model.User;

public class UserUtils {
    public static boolean equals(final User first, final User second) {
        return (first == null) ? (second == null) : first.equals(second);
    }
}
