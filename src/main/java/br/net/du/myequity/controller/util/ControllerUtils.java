package br.net.du.myequity.controller.util;

import static br.net.du.myequity.controller.interceptor.GlobalModelAttributes.LOGGED_USER;

import br.net.du.myequity.exception.UserNotFoundException;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Optional;
import org.springframework.ui.Model;

public class ControllerUtils {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");
    private static final String PERCENTAGE_TEMPLATE = "%s%%";

    public static User getLoggedUser(final Model model) {
        if (!model.containsAttribute(LOGGED_USER)) {
            throw new UserNotFoundException();
        }

        final Optional<User> loggedUserOpt = (Optional<User>) model.getAttribute(LOGGED_USER);
        if (!loggedUserOpt.isPresent()) {
            throw new UserNotFoundException();
        }

        return loggedUserOpt.get();
    }

    public static boolean accountBelongsToUser(
            final User user, final Optional<Account> accountOpt) {
        return accountOpt.isPresent() && accountOpt.get().getUser().equals(user);
    }

    public static boolean snapshotBelongsToUser(
            final User user, final Optional<Snapshot> snapshotOpt) {
        return snapshotOpt.isPresent() && snapshotOpt.get().getUser().equals(user);
    }

    public static String formatAsPercentage(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }
        return String.format(PERCENTAGE_TEMPLATE, formatAsDecimal(value));
    }

    public static String formatAsDecimal(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }
        return toDecimal(value).toString();
    }

    public static BigDecimal toDecimal(final BigDecimal value) {
        return new BigDecimal(DECIMAL_FORMAT.format(value.setScale(2, RoundingMode.HALF_UP)));
    }
}
