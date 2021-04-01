function accountNameUpdateSuccessCallback(data, result) {
  $("#account_name_" + data.id).html(result.name);
}

function snapshotNameUpdateSuccessCallback(data, result) {
  $("#snapshot_name_" + data.id).html(result.name);
}

function accountBalanceUpdateSuccessCallback(data, result) {
  $("#account_balance_" + data.entityId).html(result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}

function creditCardTotalCreditUpdateSuccessCallback(data, result) {
  $("#credit_card_total_credit_" + data.entityId).html(result.totalCredit);
  $("#credit_card_used_credit_percentage_" + data.entityId).html(result.usedCreditPercentage);
  $("#credit_card_balance_" + data.entityId).html(result.balance);
  $("#credit_card_remaining_balance_" + data.entityId).html(result.remainingBalance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
  updateSnapshotCreditCardTotals(result);
}

function creditCardAvailableCreditUpdateSuccessCallback(data, result) {
  $("#credit_card_available_credit_" + data.entityId).html(result.availableCredit);
  $("#credit_card_used_credit_percentage_" + data.entityId).html(result.usedCreditPercentage);
  $("#credit_card_remaining_balance_" + data.entityId).html(result.remainingBalance);
  $("#credit_card_balance_" + data.entityId).html(result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
  updateSnapshotCreditCardTotals(result);
}

function creditCardStatementUpdateSuccessCallback(data, result) {
  $("#credit_card_statement_" + data.entityId).html(result.statement);
  $("#credit_card_remaining_balance_" + data.entityId).html(result.remainingBalance);
  updateSnapshotCreditCardTotals(result);
}

function investmentSharesUpdateSuccessCallback(data, result) {
  $("#investment_shares_" + data.entityId).html(result.shares);
  $("#investment_average_purchase_price_" + data.entityId).html(result.averagePurchasePrice);
  $("#investment_balance_" + data.entityId).html(result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}

function investmentAmountInvestedUpdateSuccessCallback(data, result) {
  $("#investment_amount_invested_" + data.entityId).html(result.amountInvested);
  $("#investment_average_purchase_price_" + data.entityId).html(result.averagePurchasePrice);
  $("#investment_profit_percentage_" + data.entityId).html(result.profitPercentage);
}

function investmentCurrentShareValueUpdateSuccessCallback(data, result) {
  $("#investment_current_share_value_" + data.entityId).html(result.currentShareValue);
  $("#investment_profit_percentage_" + data.entityId).html(result.profitPercentage);
  $("#investment_balance_" + data.entityId).html(result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}

function payableDueDateUpdateSuccessCallback(data, result) {
  $("#payable_due_date_" + data.entityId).html(result.dueDate);
}

function receivableDueDateUpdateSuccessCallback(data, result) {
  $("#receivable_due_date_" + data.entityId).html(result.dueDate);
}

function transactionAmountUpdateSuccessCallback(data, result) {
  $("#txn_amount_" + data.entityId).html(result.amount);
  $("#total_" + result.type + "_" + result.currencyUnit).html(result.totalForTransactionType);
}
