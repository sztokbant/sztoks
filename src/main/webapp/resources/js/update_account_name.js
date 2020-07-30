function submitName(accountId) {
  var formData = {
    accountId: accountId,
    name: $("#new_account_name_" + accountId).val().trim(),
  }

  var postUrl = window.location.origin + "/accountname" + "?_csrf=" + $("#_csrf").val();

  $.ajax({
    type: "POST",
    contentType: "application/json",
    url: postUrl,
    data: JSON.stringify(formData),
    dataType: 'json',
    success: function(result) {
      if (!result.hasError) {
        $("#account_name_" + accountId).html(result.name);
      } else {
        alert('Result has error.');
      }
    },
    error: function(e) {
      alert('Error updating account name.');
      console.log("Error: ", e);
    }
  });
}

function prepareAccountNameUpdateForm(theForm, accountId, nameSpan, newNameInput) {
  theForm.submit(function(event) {
    event.preventDefault();
    newNameInput.hide();
    nameSpan.show();
  });

  newNameInput.focusout(function() {
    var currentName = nameSpan.text();
    var newName = newNameInput.val();

    if (newName && newName.trim() != "" && newName.trim() != currentName) {
        submitName(accountId);
    }

    newNameInput.hide();
    nameSpan.show();
  });

  nameSpan.click(function() {
    nameSpan.hide();
    newNameInput.val(nameSpan.text());
    newNameInput.show();
    newNameInput.focus();
  });
}
