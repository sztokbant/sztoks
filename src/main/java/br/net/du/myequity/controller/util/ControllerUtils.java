package br.net.du.myequity.controller.util;

import static br.net.du.myequity.controller.interceptor.GlobalModelAttributes.LOGGED_USER;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.DEFAULT_SCALE;

import br.net.du.myequity.exception.UserNotFoundException;
import br.net.du.myequity.model.User;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mobile.device.Device;
import org.springframework.ui.Model;

public class ControllerUtils {
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

    public static String prepareTemplate(
            @NonNull final Model model,
            @NonNull final Device device,
            @NonNull final String templateName) {
        final String deviceType;
        if (device.isMobile()) {
            deviceType = "MOBILE";
        } else if (device.isTablet()) {
            deviceType = "TABLET";
        } else {
            deviceType = "DESKTOP";
        }

        model.addAttribute("deviceType", deviceType);
        model.addAttribute("devicePlatform", device.getDevicePlatform().toString());

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
