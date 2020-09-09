<script type="text/javascript">
$(document).ready(function() {
  var data = {
    accountId: ${account.id},
  }
  prepareUpdateForm($("#form_account_name_${account.id}"),
    $("#account_name_${account.id}"),
    $("#new_account_name_${account.id}"),
    "updateAccountName",
    "${account.currencyUnitSymbol}",
    data,
    accountNameUpdateSuccessCallback,
  );
})
</script>

<li>
    <form id="form_account_name_${account.id}">
        <span id="account_name_${account.id}" class="editable-liability">${account.name}</span>
        <c:if test="${account.closed}">(Closed)</c:if>
        <input id="new_account_name_${account.id}" name="name" type="text" style="display: none;"/>
        <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
</li>
