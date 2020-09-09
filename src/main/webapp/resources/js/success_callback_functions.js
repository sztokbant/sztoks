function accountNameUpdateSuccessCallback(data, result) {
  $("#account_name_" + data.accountId).html(result.name);
}

function accountBalanceUpdateSuccessCallback(data, result) {
  $("#account_balance_amount_" + data.accountId).html(result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}

function creditCardTotalCreditUpdateSuccessCallback(data, result) {
  $("#credit_card_total_credit_" + data.accountId).html(result.totalCredit);
  $("#credit_card_used_credit_percentage_" + data.accountId).html(result.usedCreditPercentage);
  $("#credit_card_total_" + data.accountId).html(result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}

function creditCardAvailableCreditUpdateSuccessCallback(data, result) {
  $("#credit_card_available_credit_" + data.accountId).html(result.availableCredit);
  $("#credit_card_used_credit_percentage_" + data.accountId).html(result.usedCreditPercentage);
  $("#credit_card_total_" + data.accountId).html(result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}

function investmentSharesUpdateSuccessCallback(data, result) {
  $("#investment_shares_" + data.accountId).html(result.shares);
  $("#investment_total_" + data.accountId).html(result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}

function investmentOriginalShareValueUpdateSuccessCallback(data, result) {
  $("#investment_original_share_value_" + data.accountId).html(result.originalShareValue);
  $("#investment_profit_percentage_" + data.accountId).html(result.profitPercentage);
}

function investmentCurrentShareValueUpdateSuccessCallback(data, result) {
  $("#investment_current_share_value_" + data.accountId).html(result.currentShareValue);
  $("#investment_profit_percentage_" + data.accountId).html(result.profitPercentage);
  $("#investment_total_" + data.accountId).html(result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}
