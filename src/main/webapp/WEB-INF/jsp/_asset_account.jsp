<script type="text/javascript">
$(document).ready(function() {
  var data = {
    id: ${account.id},
  }
  prepareUpdateForm($("#form_account_name_${account.id}"),
    $("#account_name_${account.id}"),
    $("#new_account_name_${account.id}"),
    "account/updateName",
    "",
    data,
    accountNameUpdateSuccessCallback,
  );
})

</script>

<%@ include file="_delete_account.jsp" %>
<form id="form_account_name_${account.id}">
    <span id="account_name_${account.id}" class="editable-asset">${account.name}</span>
    <input id="new_account_name_${account.id}" name="name" type="text" style="display: none;"/>
    <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>
