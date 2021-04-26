package br.net.du.myequity.controller.util;

import org.joda.money.CurrencyUnit;

public class ControllerConstants {
    public static final String REDIRECT_SNAPSHOT_TEMPLATE = "redirect:/snapshot/%d";
    public static final String REDIRECT_TO_HOME = "redirect:/";
    public static final String REDIRECT_TO_LOGIN = "redirect:/login";

    public static final String ACCOUNT_TYPE_KEY = "accountType";
    public static final String ID = "id";
    public static final String SNAPSHOT_BASE_CURRENCY_KEY = "baseCurrency";
    public static final String SNAPSHOT_ID_KEY = "snapshotId";
    public static final String SNAPSHOT_KEY = "snapshot";
    public static final String SNAPSHOTS_KEY = "snapshots";
    public static final String TRANSACTION_TYPE_KEY = "transactionType";
    public static final String USER_KEY = "user";

    public static final String TWELVE_MONTHS_TOTALS = "twelveMonthsTotals";
    public static final String YTD_TOTALS = "ytdTotals";

    public static final String CURRENCIES = "currencies";
    public static final CurrencyUnit DEFAULT_CURRENCY_UNIT = CurrencyUnit.USD;
    public static final String SELECTED_CURRENCY = "selectedCurrency";
}
