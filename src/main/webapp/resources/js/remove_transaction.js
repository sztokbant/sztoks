function removeTransaction(snapshotId, entityId, type, description) {
    var doRemove = confirm('Are you sure you want to remove ' + type + ' transaction "' + description + '" from this snapshot?');

    if (doRemove) {
      var data = {
        snapshotId: snapshotId,
        entityId: entityId,
      };

      ajaxPost('transaction/remove', data, removeTransactionSuccessCallback);
    }
}

function removeTransactionSuccessCallback(data, result) {
  $("#total_" + result.type).html(result.totalForTransactionType);
  if (result.type == "INCOME" || result.type == "DONATION") {
    $("#tithing_balance").html(result.tithingBalance);
    $("#total_LIABILITY").html(result.totalLiability);
    $("#snapshot_net_worth").html(result.netWorth);
  }
  $("#txn_row_" + result.entityId).hide();
}
