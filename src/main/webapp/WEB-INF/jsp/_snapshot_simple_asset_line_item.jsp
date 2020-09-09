<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    accountId: ${account.id},
  }
  prepareUpdateForm($("#form_account_balance_${account.id}"),
    $("#account_balance_amount_${account.id}"),
    $("#new_account_balance_amount_${account.id}"),
    "updateAccountBalance",
    "${account.currencyUnitSymbol}",
    data,
    accountBalanceUpdateSuccessCallback,
  );
})
</script>

<div class="col col-account-name">
    ${account.name}
</div>

<div class="col">
    &nbsp;
</div>

<div class="col">
    &nbsp;
</div>

<div class="col">
    &nbsp;
</div>

<div class="col">
    &nbsp;
</div>

<div class="col col-value editable-asset">
    <form id="form_account_balance_${account.id}">
        <span id="account_balance_amount_${account.id}">${account.currencyUnitSymbol}${account.total}</span>
        <span><input id="new_account_balance_amount_${account.id}" name="amount" type="number" min="0"
                     step="0.01" style="display: none;"/></span>
        <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
</div>
