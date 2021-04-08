function removeAccountFromSnapshot(snapshotId, entityId, accountName) {
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
  $("#snapshot_net_worth").html(result.netWorth);
  $("#total_" + result.accountType).html(result.totalForAccountType);
  $("#account_row_" + result.accountId).hide();

  if (result.accountSubtype != null) {
    $("#snapshot_" + result.accountSubtype + "_balance").html(result.totalForAccountSubtype);
  } if (result.investmentTotals != null) {
    updateSnapshotInvestmentTotals(result);
  } else if (result.creditCardTotalsForCurrencyUnit != null) {
    updateSnapshotCreditCardTotals(result);
  }
}
