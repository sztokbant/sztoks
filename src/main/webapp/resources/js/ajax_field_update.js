function prepareUpdateForm(theForm, currentValueSpan, newValueInput, endpoint, currencyUnitSymbol, data, successCallback) {
  theForm.submit(function(event) {
    event.preventDefault();
    newValueInput.hide();
    currentValueSpan.show();
  });

  newValueInput.focusout(function() {
    var currentValueNoCurrencySymbol = stripCurrencySymbol(currentValueSpan, currencyUnitSymbol);
    var newValue = newValueInput.val();

    if (newValue && newValue.trim() != "" && newValue.trim() != currentValueNoCurrencySymbol) {
      data.newValue = newValue.trim();
      ajaxPost(endpoint, data, successCallback);
    }

    newValueInput.hide();
    currentValueSpan.show();
  });

  currentValueSpan.click(function() {
    var currentValueNoCurrencySymbol = stripCurrencySymbol(currentValueSpan, currencyUnitSymbol);
    currentValueSpan.hide();
    newValueInput.val(currentValueNoCurrencySymbol);
    newValueInput.show();
    newValueInput.focus();
  });
}

function stripCurrencySymbol(currentValueSpan, currencyUnitSymbol) {
  return currentValueSpan.text().replace(currencyUnitSymbol, '').trim();
}

function ajaxPost(endpoint, data, successCallback) {
  var postUrl = window.location.origin + "/" + endpoint + "?_csrf=" + $("#_csrf").val();

  $.ajax({
    type: "POST",
    contentType: "application/json",
    url: postUrl,
    data: JSON.stringify(data),
    dataType: 'json',
    success: function(result) {
      if (!result.errorMessage) {
        successCallback(data, result);
      } else {
        alert(result.errorMessage);
      }
    },
    error: function(e) {
      alert('Unexpected error.');
      console.log("Error: ", e);
    }
  });
}
