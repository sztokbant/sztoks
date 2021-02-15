function removeTransaction(snapshotId, entityId, type, description) {
    var doRemove = confirm('Are you sure you want to remove ' + type + ' transaction "' + description + '" from this snapshot?');

    if (doRemove) {
      var data = {
        snapshotId: snapshotId,
        entityId: entityId,
      };

      ajaxPost('snapshot/removeTransaction', data, removeTransactionSuccessCallback);
    }
}

function removeTransactionSuccessCallback(data, result) {
  $("#total_" + result.type + "_" + result.currencyUnit).html(result.totalForType);
  $("#txn_row_" + result.entityId).hide();
}
