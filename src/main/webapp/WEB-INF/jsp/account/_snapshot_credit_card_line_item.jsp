<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.accountId},
  };

  prepareUpdateForm($("#form_credit_card_total_credit_${entity.accountId}"),
    $("#credit_card_total_credit_${entity.accountId}"),
    $("#new_credit_card_total_credit_${entity.accountId}"),
    "snapshot/updateCreditCardTotalCredit",
    data,
    creditCardTotalCreditUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_credit_card_available_credit_${entity.accountId}"),
    $("#credit_card_available_credit_${entity.accountId}"),
    $("#new_credit_card_available_credit_${entity.accountId}"),
    "snapshot/updateCreditCardAvailableCredit",
    data,
    creditCardAvailableCreditUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_credit_card_statement_${entity.accountId}"),
    $("#credit_card_statement_${entity.accountId}"),
    $("#new_credit_card_statement_${entity.accountId}"),
    "snapshot/updateCreditCardStatement",
    data,
    creditCardStatementUpdateSuccessCallback,
  );
})
</script>

<div class="row border-1px-bottom" id="account_row_${entity.accountId}">
    <%@ include file="_snapshot_col_remove_account.jsp" %>

    <%@ include file="_snapshot_col_account_name.jsp" %>

    <div class="col col-cell align-right editable-liability">
        <form id="form_credit_card_total_credit_${entity.accountId}">
            <span id="credit_card_total_credit_${entity.accountId}">${entity.totalCredit}</span>
            <span><input id="new_credit_card_total_credit_${entity.accountId}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
        </form>
    </div>

    <div class="col col-cell align-right editable-liability">
        <form id="form_credit_card_available_credit_${entity.accountId}">
            <span id="credit_card_available_credit_${entity.accountId}">${entity.availableCredit}</span>
            <span><input id="new_credit_card_available_credit_${entity.accountId}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
        </form>
    </div>

    <fmt:parseNumber var="ccUsageValue" value="${entity.usedCreditPercentage}" integerOnly="false"/>
    <c:choose>
        <c:when test="${ccUsageValue ge 30}">
            <c:set var="ccUsageStyle" value="cell-red"/>
        </c:when>
        <c:when test="${ccUsageValue ge 10}">
            <c:set var="ccUsageStyle" value="cell-orange"/>
        </c:when>
        <c:otherwise>
            <c:set var="ccUsageStyle" value=""/>
        </c:otherwise>
    </c:choose>

    <div class="col col-cell align-right">
        <span id="credit_card_used_credit_percentage_${entity.accountId}" class="${ccUsageStyle}">${entity.usedCreditPercentage}</span>
    </div>

    <div class="col col-cell align-right editable-liability">
        <form id="form_credit_card_statement_${entity.accountId}">
            <b><span id="credit_card_statement_${entity.accountId}">${entity.statement}</span></b>
            <span><input id="new_credit_card_statement_${entity.accountId}" name="amount" type="number" step="0.01" style="display: none;"/></span>
        </form>
    </div>

    <div class="col col-cell align-right">
        <span id="credit_card_remaining_balance_${entity.accountId}">${entity.remainingBalance}</span>
    </div>

    <div class="col col-cell align-right">
        <span id="credit_card_balance_${entity.accountId}">${entity.balance}</span>
    </div>
</div>