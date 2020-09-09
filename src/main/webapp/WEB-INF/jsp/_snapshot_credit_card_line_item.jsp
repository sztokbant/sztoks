<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    accountId: ${account.id},
  }

  prepareUpdateForm($("#form_credit_card_total_credit_${account.id}"),
    $("#credit_card_total_credit_${account.id}"),
    $("#new_credit_card_total_credit_${account.id}"),
    "updateCreditCardTotalCredit",
    data,
    creditCardTotalCreditUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_credit_card_available_credit_${account.id}"),
    $("#credit_card_available_credit_${account.id}"),
    $("#new_credit_card_available_credit_${account.id}"),
    "updateCreditCardAvailableCredit",
    data,
    creditCardAvailableCreditUpdateSuccessCallback,
  );
})
</script>

<div class="col col-account-name">
    ${account.name}
</div>

<div class="col col-value editable-liability">
    <form id="form_credit_card_total_credit_${account.id}">
        ${account.balanceCurrencyUnit}
        <span id="credit_card_total_credit_${account.id}">${account.totalCredit}</span>
        <span><input id="new_credit_card_total_credit_${account.id}" name="amount" type="number" min="0"
                     step="0.01" style="display: none;"/></span>
        <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
</div>

<div class="col col-value editable-liability">
    <form id="form_credit_card_available_credit_${account.id}">
        ${account.balanceCurrencyUnit}
        <span id="credit_card_available_credit_${account.id}">${account.availableCredit}</span>
        <span><input id="new_credit_card_available_credit_${account.id}" name="amount" type="number" min="0"
                     step="0.01" style="display: none;"/></span>
        <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
</div>

<div class="col col-value">
    <span id="credit_card_used_credit_percentage_${account.id}">${account.usedCreditPercentage}%</span>
</div>

<div class="col col-value">
    ${account.balanceCurrencyUnit}
    <span id="credit_card_total_${account.id}">${account.total}</span>
</div>
