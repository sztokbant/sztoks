function deleteAccount(accountName, entityId) {
    var doRemove = confirm('Are you sure you want to delete "' + accountName + '"?');

    if (doRemove) {
      var data = {
        entityId: entityId,
      };

      ajaxPost('account/delete', data, deleteAccountSuccessCallback);
    }
}

function deleteAccountSuccessCallback(data, result) {
  $("#account_row_" + result.accountId).hide();
}
