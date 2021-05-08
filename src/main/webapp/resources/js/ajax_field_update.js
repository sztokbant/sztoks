function prepareUpdateForm(theForm, currentValueSpan, newValueInput, endpoint, data, successCallback) {
  theForm.submit(function(event) {
    event.preventDefault();
    newValueInput.hide();
    currentValueSpan.show();
  });

  newValueInput.focusout(function() {
    var currentValue = (newValueInput[0].type != "text")
      ? stripDecimal(currentValueSpan)
      : currentValueSpan.text();

    var newValue = newValueInput.val();

    if (newValue && newValue.trim() != "" && newValue.trim() != currentValue) {
      data.newValue = newValue.trim();
      ajaxPost(endpoint, data, successCallback);
    }

    newValueInput.hide();
    currentValueSpan.show();
  });

  currentValueSpan.click(function() {
    var currentValue = (newValueInput[0].type != "text")
      ? stripDecimal(currentValueSpan)
      : currentValueSpan.text();
    currentValueSpan.hide();
    newValueInput.val(currentValue);
    newValueInput.show();
    newValueInput.focus();
    newValueInput.select();
  });
}

function stripDecimal(currentValueSpan) {
  return currentValueSpan
    .text()
    .replace(/^[^\d-]+/g, '')
    .replaceAll(',', '')
    .replaceAll('%', '')
    .trim();
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

function prepareCheckbox(elementId, snapshotId, entityId, isChecked, endpoint, successCallback) {
  const checkbox = document.getElementById(elementId);

  if (isChecked) {
    checkbox.setAttribute('checked', true);
  }

  checkbox.addEventListener('change', (event) => {
    var data = {
      snapshotId: snapshotId,
      entityId: entityId,
    };

    if (event.currentTarget.checked) {
      data['newValue'] = true;
    } else {
      data['newValue'] = false;
    }

    ajaxPost(endpoint, data, successCallback);
  })
}
