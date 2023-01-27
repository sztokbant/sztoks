function prepareUpdateForm(theForm, currentValueSpan, newValueInput, endpoint, data, successCallback) {
  theForm.submit(function(event) {
    event.preventDefault();
    newValueInput.hide();
    currentValueSpan.show();
  });

  newValueInput.focusout(function() {
    var currentValue = (newValueInput[0].type != "text")
      ? stripDecimalForElement(currentValueSpan)
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
      ? stripDecimalForElement(currentValueSpan)
      : currentValueSpan.text();
    currentValueSpan.hide();
    newValueInput.val(currentValue);
    newValueInput.show();
    newValueInput.focus();
    newValueInput.select();
  });
}

function stripDecimalForElement(currentValueSpan) {
  return stripDecimalForText(currentValueSpan.text());
}

function stripDecimalForText(text) {
  return text
    .replace(/^[^\d-]+/g, '')
    .replaceAll(',', '')
    .replaceAll('%', '')
    .trim();
}

function ajaxPost(endpoint, data, successCallback) {
  if (data.isOldSnapshot && !confirm('Are you sure you want to change an OLD snapshot?')) {
    return false;
  }

  var postUrl = window.location.origin + "/" + endpoint + "?_csrf=" + $("#_csrf").val();

  $.ajax({
    type: "post",
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

  return true;
}

function prepareCheckbox(elementId, snapshotId, isOldSnapshot, entityId, isChecked, endpoint, successCallback) {
  const checkbox = document.getElementById(elementId);

  // Initial value on page load
  checkbox.checked = isChecked;

  checkbox.addEventListener('change', (event) => {
    var data = {
      snapshotId: snapshotId,
      isOldSnapshot: isOldSnapshot,
      entityId: entityId,
      newValue: event.currentTarget.checked,
    };

    if (ajaxPost(endpoint, data, successCallback)) {
      checkbox.checked = data['newValue'];
    } else {
      checkbox.checked = !data['newValue'];
    }
  })
}

function prepareSelect(elementId, snapshotId, isOldSnapshot, entityId, endpoint, successCallback) {
  const element = document.getElementById(elementId)

  // Initial value on page load
  element.oldValue = element.value;

  var data = {
    snapshotId: snapshotId,
    isOldSnapshot: isOldSnapshot,
    entityId: entityId,
  };

  element.onfocus =
      () => {
        element.oldValue = element.value;
      };

  element.onchange =
      (evt) => {
        data.newValue = evt.target.value;
        if (!ajaxPost(endpoint, data, successCallback)) {
          element.value = element.oldValue;
        } else {
          element.oldValue = element.value;
        };
      };
}
