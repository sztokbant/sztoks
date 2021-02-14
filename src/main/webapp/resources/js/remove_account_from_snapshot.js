function removeAccountFromSnapshot(accountName, entityId, snapshotId) {
    var doRemove = confirm('Are you sure you want to remove "' + accountName + '" from this snapshot?');

    if (doRemove) {
      var data = {
        snapshotId: snapshotId,
        entityId: entityId,
      };

      ajaxPost('snapshot/removeAccount', data, removeAccountFromSnapshotSuccessCallback);
    }
}

function removeAccountFromSnapshotSuccessCallback(data, result) {
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
  $("#account_row_" + result.accountId).hide();

  if (result.creditCardTotalsForCurrencyUnit != null) {
    updateSnapshotCreditCardTotals(result);
  }
}
