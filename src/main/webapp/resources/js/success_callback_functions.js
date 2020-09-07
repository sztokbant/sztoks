function accountNameUpdateSuccessCallback(data, result) {
  $("#account_name_" + data.accountId).html(result.name);
}

function accountBalanceUpdateSuccessCallback(data, result) {
  $("#account_balance_amount_" + data.accountId).html(result.balance);
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
}
