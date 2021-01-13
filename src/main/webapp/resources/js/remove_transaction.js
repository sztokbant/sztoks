function removeTransaction(id, type, description) {
    var doRemove = confirm('Are you sure you want to remove ' + type + ' transaction "' + description + '" from this snapshot?');

    if (doRemove) {
      var data = {
        id: id,
      };

      ajaxPost('snapshot/removeTransaction', data, removeTransactionSuccessCallback);
    }
}

// TODO
function removeTransactionSuccessCallback(data, result) {
}
