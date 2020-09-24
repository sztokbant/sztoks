<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    accountId: ${account.id},
  };

  prepareUpdateForm($("#form_credit_card_total_credit_${account.id}"),
    $("#credit_card_total_credit_${account.id}"),
    $("#new_credit_card_total_credit_${account.id}"),
    "snapshot/updateCreditCardTotalCredit",
    "${account.currencyUnitSymbol}",
    data,
    creditCardTotalCreditUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_credit_card_available_credit_${account.id}"),
    $("#credit_card_available_credit_${account.id}"),
    $("#new_credit_card_available_credit_${account.id}"),
    "snapshot/updateCreditCardAvailableCredit",
    "${account.currencyUnitSymbol}",
    data,
    creditCardAvailableCreditUpdateSuccessCallback,
  );
})
</script>

<div class="row" id="account_row_${account.id}">
    <%@ include file="_snapshot_col_account_name.jsp" %>

    <div class="col col-value editable-liability">
        <form id="form_credit_card_total_credit_${account.id}">
            <span id="credit_card_total_credit_${account.id}">${account.currencyUnitSymbol}${account.totalCredit}</span>
            <span><input id="new_credit_card_total_credit_${account.id}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-value editable-liability">
        <form id="form_credit_card_available_credit_${account.id}">
            <span id="credit_card_available_credit_${account.id}">${account.currencyUnitSymbol}${account.availableCredit}</span>
            <span><input id="new_credit_card_available_credit_${account.id}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-value">
        <span id="credit_card_used_credit_percentage_${account.id}">${account.usedCreditPercentage}%</span>
    </div>

    <div class="col col-value">
        <span id="credit_card_total_${account.id}">${account.currencyUnitSymbol}${account.total}</span>
    </div>
</div>