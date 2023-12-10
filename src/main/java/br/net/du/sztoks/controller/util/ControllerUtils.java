package br.net.du.sztoks.controller.util;

import static br.net.du.sztoks.controller.interceptor.GlobalModelAttributes.LOGGED_USER;

import br.net.du.sztoks.exception.UserNotFoundException;
import br.net.du.sztoks.model.User;
import io.github.mngsk.devicedetector.DeviceDetector;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;

public class ControllerUtils {
    private static final String PERCENTAGE_TEMPLATE = "%s%%";
    private static final int DEFAULT_SCALE = 2;

    private static final DeviceDetector DEVICE_DETECTOR =
            new DeviceDetector.DeviceDetectorBuilder().build();

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

    public static String prepareTemplate(
            final String userAgent,
            @NonNull final Model model,
            @NonNull final String templateName) {
        model.addAttribute(
                "deviceType",
                userAgent == null || DEVICE_DETECTOR.detect(userAgent).isDesktop()
                        ? "DESKTOP"
                        : "MOBILE");
        model.addAttribute("sztoksEnv", System.getenv("SZTOKS_ENV"));
        return templateName;
    }

    public static String formatAsPercentage(final BigDecimal input) {
        final BigDecimal value = input == null ? BigDecimal.ZERO : input;
        return String.format(PERCENTAGE_TEMPLATE, toDecimal(value, DEFAULT_SCALE));
    }

    public static String formatAsDecimal(final BigDecimal input) {
        final BigDecimal value = input == null ? BigDecimal.ZERO : input;
        return toDecimal(value).toString();
    }

    public static BigDecimal toDecimal(final BigDecimal input) {
        final BigDecimal value = input == null ? BigDecimal.ZERO : input;
        final int scale = Math.max(getCurrentScale(value), DEFAULT_SCALE);
        return toDecimal(value, scale);
    }

    public static BigDecimal toDecimal(final BigDecimal input, int scale) {
        final BigDecimal value = input == null ? BigDecimal.ZERO : input;
        return new BigDecimal(value.toString()).setScale(scale, RoundingMode.HALF_UP);
    }

    private static int getCurrentScale(@NonNull final BigDecimal value) {
        if (!value.toString().contains(".")) {
            return 0;
        }
        final String decimals = value.toString().split("\\.")[1];
        return StringUtils.stripEnd(decimals, "0").length();
    }
}
