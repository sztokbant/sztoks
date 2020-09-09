function removeAccountFromSnapshot(accountName, accountId, snapshotId) {
    var doRemove = confirm('Are you sure you want to remove "' + accountName + '" from this snapshot?');

    if (doRemove) {
      var data = {
        snapshotId: snapshotId,
        accountId: accountId,
      };

      ajaxPost('removeAccountFromSnapshot', data, removeAccountFromSnapshotSuccessCallback);
    }
}

function removeAccountFromSnapshotSuccessCallback(data, result) {
  $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
  $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
  $("#account_row_" + result.accountId).hide();
}
