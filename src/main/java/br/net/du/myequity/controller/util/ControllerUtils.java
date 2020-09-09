package br.net.du.myequity.controller.util;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Optional;

import static br.net.du.myequity.controller.interceptor.GlobalModelAttributes.LOGGED_USER;

public class ControllerUtils {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");
    private static final String PERCENTAGE_TEMPLATE = "%s%%";

    public static User getLoggedUser(final Model model) {
        final Optional<User> loggedUserOpt = getLoggedUserOpt(model);
        if (!loggedUserOpt.isPresent()) {
            throw new RuntimeException();
        }
        return loggedUserOpt.get();
    }

    public static Optional<User> getLoggedUserOpt(final Model model) {
        assert model.containsAttribute(LOGGED_USER);

        return (Optional<User>) model.getAttribute(LOGGED_USER);
    }

    public static boolean accountBelongsToUser(final User user, final Optional<Account> accountOpt) {
        return accountOpt.isPresent() && accountOpt.get().getUser().equals(user);
    }

    public static boolean snapshotBelongsToUser(final User user, final Optional<Snapshot> snapshotOpt) {
        return snapshotOpt.isPresent() && snapshotOpt.get().getUser().equals(user);
    }

    public static String formatAsPercentage(final BigDecimal value) {
        return String.format(PERCENTAGE_TEMPLATE, formatAsDecimal(value));
    }

    public static String formatAsDecimal(final BigDecimal value) {
        return new BigDecimal(DECIMAL_FORMAT.format(value.setScale(2))).toString();
    }
}
