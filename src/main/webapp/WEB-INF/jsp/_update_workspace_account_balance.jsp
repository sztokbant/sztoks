<script type="text/javascript">
$(document).ready(function() {
  prepareAccountBalanceUpdateForm($("#form_${account.id}"),
    ${account.id},
    $("#amount_${account.id}"),
    $("#new_amount_${account.id}"));
})
</script>

<form id="form_${account.id}">
    ${account.name}: ${account.balance.currencyUnit} <span id="amount_${account.id}">${account.balance.amount}</span>
    <input id="new_amount_${account.id}" name="amount" type="number" min="0" step="0.01" style="display: none;"/>
    <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>
