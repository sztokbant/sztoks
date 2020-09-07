function prepareUpdateForm(theForm, currentValueSpan, newValueInput, endpoint, data, successCallback) {
  theForm.submit(function(event) {
    event.preventDefault();
    newValueInput.hide();
    currentValueSpan.show();
  });

  newValueInput.focusout(function() {
    var currentValue = currentValueSpan.text();
    var newValue = newValueInput.val();

    if (newValue && newValue.trim() != "" && newValue.trim() != currentValue) {
      data.newValue = newValue.trim();
      ajaxPost(endpoint, data, successCallback);
    }

    newValueInput.hide();
    currentValueSpan.show();
  });

  currentValueSpan.click(function() {
    currentValueSpan.hide();
    newValueInput.val(currentValueSpan.text());
    newValueInput.show();
    newValueInput.focus();
  });
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
      if (!result.hasError) {
        successCallback(data, result);
      } else {
        alert('Result has error.');
      }
    },
    error: function(e) {
      alert('Error updating field.');
      console.log("Error: ", e);
    }
  });
}
