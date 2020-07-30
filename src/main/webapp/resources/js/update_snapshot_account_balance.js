function submitAccountBalance(snapshotId, accountId) {
  var formData = {
    snapshotId: snapshotId,
    accountId: accountId,
    balance: $("#new_account_balance_amount_" + accountId).val(),
  }

  var postUrl = window.location.origin + "/accountbalance" + "?_csrf=" + $("#_csrf").val();

  $.ajax({
    type: "POST",
    contentType: "application/json",
    url: postUrl,
    data: JSON.stringify(formData),
    dataType: 'json',
    success: function(result) {
      if (!result.hasError) {
        $("#account_balance_amount_" + accountId).html(result.balance);
        $("#snapshot_networth_" + result.currencyUnit).html(result.netWorth);
        $("#total_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
      } else {
        alert('Result has error.');
      }
    },
    error: function(e) {
      alert('Error updating amount.');
      console.log("Error: ", e);
    }
  });
}

function prepareAccountBalanceUpdateForm(theForm, snapshotId, accountId, amountSpan, newAmountInput) {
  theForm.submit(function(event) {
    event.preventDefault();
    newAmountInput.hide();
    amountSpan.show();
  });

  newAmountInput.focusout(function() {
    var currentAmount = amountSpan.text();
    var newAmount = newAmountInput.val();

    if (currentAmount != newAmount) {
      submitAccountBalance(snapshotId, accountId);
    }

    newAmountInput.hide();
    amountSpan.show();
  });

  amountSpan.click(function() {
    amountSpan.hide();
    newAmountInput.val(amountSpan.text());
    newAmountInput.show();
    newAmountInput.focus();
  });
}
