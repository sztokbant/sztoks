package br.net.du.myequity.controller.util;

import java.text.DecimalFormat;

public class ControllerConstants {
    public static final String REDIRECT_TO_HOME = "redirect:/";

    public static final String ASSET_ACCOUNTS_KEY = "assetAccounts";
    public static final String CREDIT_CARD_ACCOUNTS_KEY = "creditCardAccounts";
    public static final String INVESTMENT_ACCOUNTS_KEY = "investmentAccounts";
    public static final String LIABILITY_ACCOUNTS_KEY = "liabilityAccounts";
    public static final String SIMPLE_ASSET_ACCOUNTS_KEY = "simpleAssetAccounts";
    public static final String SIMPLE_LIABILITY_ACCOUNTS_KEY = "simpleLiabilityAccounts";
    public static final String SNAPSHOT_KEY = "snapshot";
    public static final String SNAPSHOTS_KEY = "snapshots";
    public static final String USER_KEY = "user";

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");
}
