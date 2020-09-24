function accountNameUpdateSuccessCallback(data, result) {
  $("#account_name_" + data.id).html(result.name);
}

function snapshotNameUpdateSuccessCallback(data, result) {
  $("#snapshot_name_" + data.id).html(result.name);
}

function accountBalanceUpdateSuccessCallback(data, result) {
  $("#account_balance_amount_" + data.accountId).html(result.currencyUnitSymbol + result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}

function creditCardTotalCreditUpdateSuccessCallback(data, result) {
  $("#credit_card_total_credit_" + data.accountId).html(result.currencyUnitSymbol + result.totalCredit);
  $("#credit_card_used_credit_percentage_" + data.accountId).html(result.usedCreditPercentage);
  $("#credit_card_total_" + data.accountId).html(result.currencyUnitSymbol + result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}

function creditCardAvailableCreditUpdateSuccessCallback(data, result) {
  $("#credit_card_available_credit_" + data.accountId).html(result.currencyUnitSymbol + result.availableCredit);
  $("#credit_card_used_credit_percentage_" + data.accountId).html(result.usedCreditPercentage);
  $("#credit_card_total_" + data.accountId).html(result.currencyUnitSymbol + result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}

function investmentSharesUpdateSuccessCallback(data, result) {
  $("#investment_shares_" + data.accountId).html(result.shares);
  $("#investment_total_" + data.accountId).html(result.currencyUnitSymbol + result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}

function investmentOriginalShareValueUpdateSuccessCallback(data, result) {
  $("#investment_original_share_value_" + data.accountId).html(result.currencyUnitSymbol + result.originalShareValue);
  $("#investment_profit_percentage_" + data.accountId).html(result.profitPercentage);
}

function investmentCurrentShareValueUpdateSuccessCallback(data, result) {
  $("#investment_current_share_value_" + data.accountId).html(result.currencyUnitSymbol + result.currentShareValue);
  $("#investment_profit_percentage_" + data.accountId).html(result.profitPercentage);
  $("#investment_total_" + data.accountId).html(result.currencyUnitSymbol + result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}

function payableDueDateUpdateSuccessCallback(data, result) {
  $("#payable_due_date_" + data.accountId).html(result.dueDate);
}

function receivableDueDateUpdateSuccessCallback(data, result) {
  $("#receivable_due_date_" + data.accountId).html(result.dueDate);
}