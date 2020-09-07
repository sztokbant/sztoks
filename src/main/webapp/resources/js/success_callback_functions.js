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
  $("#credit_card_total_" + data.accountId).html(result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}

function creditCardAvailableCreditUpdateSuccessCallback(data, result) {
  $("#credit_card_available_credit_" + data.accountId).html(result.availableCredit);
  $("#credit_card_total_" + data.accountId).html(result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}
