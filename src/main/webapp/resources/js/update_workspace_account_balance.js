function submitAccountBalance(accountId) {
  var formData = {
    id: accountId,
    balance: $("#new_amount_" + accountId).val(),
  }

  var postUrl = window.location.origin + "/account" + "?_csrf=" + $("#_csrf").val();

  $.ajax({
    type: "POST",
    contentType: "application/json",
    url: postUrl,
    data: JSON.stringify(formData),
    dataType: 'json',
    success: function(result) {
      if (!result.hasError) {
        $("#amount_" + accountId).html(result.balance);
        $("#ws_nw_" + result.currencyUnit).html(result.netWorth);
        $("#ws_" + result.accountType + "_" + result.currencyUnit).html(result.totalForAccountType);
      } else {
        alert('Error updating amount.')
      }
    },
    error: function(e) {
      alert('Error updating amount.')
      console.log("Error: ", e);
    }
  });
}

function prepareAccountBalanceUpdateForm(theForm, accountId, amountSpan, newAmountInput) {
  theForm.submit(function(event) {
    event.preventDefault();
    submitAccountBalance(accountId);
  });

  newAmountInput.focusout(function() {
    var currentAmount = amountSpan.text();
    var newAmount = newAmountInput.val();

    if (currentAmount != newAmount) {
      theForm.trigger('submit');
    } else {
      newAmountInput.hide();
      amountSpan.show();
    }
  });

  theForm.submit(function() {
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
