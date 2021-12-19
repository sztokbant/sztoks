<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.accountId},
  };

  prepareUpdateForm($("#form_investment_shares_${entity.accountId}"),
    $("#investment_shares_${entity.accountId}"),
    $("#new_investment_shares_${entity.accountId}"),
    "snapshot/updateAccountShares",
    data,
    investmentSharesUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_investment_amount_invested_${entity.accountId}"),
    $("#investment_amount_invested_${entity.accountId}"),
    $("#new_investment_amount_invested_${entity.accountId}"),
    "snapshot/updateInvestmentAmountInvested",
    data,
    investmentAmountInvestedUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_investment_current_share_value_${entity.accountId}"),
    $("#investment_current_share_value_${entity.accountId}"),
    $("#new_investment_current_share_value_${entity.accountId}"),
    "snapshot/updateAccountCurrentShareValue",
    data,
    investmentCurrentShareValueUpdateSuccessCallback,
  );
})

</script>

<div class="row border-1px-bottom" id="account_row_${entity.accountId}">
    <%@ include file="_snapshot_col_remove_account.jsp" %>

    <%@ include file="_snapshot_col_account_name.jsp" %>

    <%@ include file="_account_future_tithing_select.jsp" %>

    <div class="col col-cell-${deviceType} align-right width-70px ${editableClass}">
        <form id="form_investment_shares_${entity.accountId}">
            <span id="investment_shares_${entity.accountId}">${entity.shares}</span>
            <span><input id="new_investment_shares_${entity.accountId}" name="amount" type="number" min="0"
                         step="0.00000001" style="display: none;"/></span>
        </form>
    </div>

    <div class="col col-cell-${deviceType} align-right ${editableClass}">
        <form id="form_investment_amount_invested_${entity.accountId}">
            <span id="investment_amount_invested_${entity.accountId}">${entity.amountInvested}</span>
            <span><input id="new_investment_amount_invested_${entity.accountId}" name="amount" type="number"
                         min="0"
                         step="0.01" style="display: none;"/></span>
        </form>
    </div>


    <div class="col col-cell-${deviceType} align-right">
        <span id="investment_average_purchase_price_${entity.accountId}">${entity.averagePurchasePrice}</span>
    </div>

    <div class="col col-cell-${deviceType} align-right ${editableClass}">
        <form id="form_investment_current_share_value_${entity.accountId}">
            <span id="investment_current_share_value_${entity.accountId}">${entity.currentShareValue}</span>
            <span><input id="new_investment_current_share_value_${entity.accountId}" name="amount" type="number"
                         min="0"
                         step="0.01" style="display: none;"/></span>
        </form>
    </div>

    <fmt:parseNumber var="profitValue" value="${entity.profitPercentage}" integerOnly="false" />
    <c:choose>
        <c:when test="${profitValue gt 0}">
            <c:set var="profitStyle" value="cell-green" />
        </c:when>
        <c:when test="${profitValue lt 0}">
            <c:set var="profitStyle" value="cell-red" />
        </c:when>
        <c:otherwise>
            <c:set var="profitStyle" value="" />
        </c:otherwise>
    </c:choose>

    <div class="col col-cell-${deviceType} align-right width-70px">
        <span id="investment_profit_percentage_${entity.accountId}"
              class="${profitStyle}">${entity.profitPercentage}</span>
    </div>

    <div class="col col-cell-${deviceType} align-right">
        <span id="investment_balance_${entity.accountId}">${entity.balance}</span>
    </div>
</div>
