function deleteAccount(accountName, accountId) {
    var doRemove = confirm('Are you sure you want to delete "' + accountName + '"?');

    if (doRemove) {
      var data = {
        accountId: accountId,
      };

      ajaxPost('account/delete', data, deleteAccountSuccessCallback);
    }
}

function deleteAccountSuccessCallback(data, result) {
  $("#account_row_" + result.accountId).hide();
}
