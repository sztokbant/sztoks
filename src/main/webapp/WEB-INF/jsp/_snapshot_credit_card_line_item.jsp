<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    accountId: ${account.accountId},
  };

  prepareUpdateForm($("#form_credit_card_total_credit_${account.accountId}"),
    $("#credit_card_total_credit_${account.accountId}"),
    $("#new_credit_card_total_credit_${account.accountId}"),
    "snapshot/updateCreditCardTotalCredit",
    "${account.currencyUnitSymbol}",
    data,
    creditCardTotalCreditUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_credit_card_available_credit_${account.accountId}"),
    $("#credit_card_available_credit_${account.accountId}"),
    $("#new_credit_card_available_credit_${account.accountId}"),
    "snapshot/updateCreditCardAvailableCredit",
    "${account.currencyUnitSymbol}",
    data,
    creditCardAvailableCreditUpdateSuccessCallback,
  );
})
</script>

<div class="row" id="account_row_${account.accountId}">
    <%@ include file="_snapshot_col_account_name.jsp" %>

    <div class="col col-value editable-liability">
        <form id="form_credit_card_total_credit_${account.accountId}">
            <span id="credit_card_total_credit_${account.accountId}">${account.currencyUnitSymbol}${account.totalCredit}</span>
            <span><input id="new_credit_card_total_credit_${account.accountId}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-value editable-liability">
        <form id="form_credit_card_available_credit_${account.accountId}">
            <span id="credit_card_available_credit_${account.accountId}">${account.currencyUnitSymbol}${account.availableCredit}</span>
            <span><input id="new_credit_card_available_credit_${account.accountId}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-value">
        <span id="credit_card_used_credit_percentage_${account.accountId}">${account.usedCreditPercentage}</span>
    </div>

    <div class="col col-value">
        <span id="credit_card_balance_${account.accountId}">${account.currencyUnitSymbol}${account.balance}</span>
    </div>
</div>