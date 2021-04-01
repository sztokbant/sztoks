<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.accountId},
  };

  prepareUpdateForm($("#form_investment_shares_${entity.accountId}"),
    $("#investment_shares_${entity.accountId}"),
    $("#new_investment_shares_${entity.accountId}"),
    "snapshot/updateInvestmentShares",
    "${entity.currencyUnitSymbol}",
    data,
    investmentSharesUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_investment_amount_invested_${entity.accountId}"),
    $("#investment_amount_invested_${entity.accountId}"),
    $("#new_investment_amount_invested_${entity.accountId}"),
    "snapshot/updateInvestmentAmountInvested",
    "${entity.currencyUnitSymbol}",
    data,
    investmentAmountInvestedUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_investment_current_share_value_${entity.accountId}"),
    $("#investment_current_share_value_${entity.accountId}"),
    $("#new_investment_current_share_value_${entity.accountId}"),
    "snapshot/updateInvestmentCurrentShareValue",
    "${entity.currencyUnitSymbol}",
    data,
    investmentCurrentShareValueUpdateSuccessCallback,
  );
})

</script>

<div class="row border-1px-bottom" id="account_row_${entity.accountId}">
    <%@ include file="_snapshot_col_account_name.jsp" %>

    <div class="col col-cell align-right editable-asset">
        <form id="form_investment_shares_${entity.accountId}">
            <span id="investment_shares_${entity.accountId}">${entity.shares}</span>
            <span><input id="new_investment_shares_${entity.accountId}" name="amount" type="number" min="0"
                         step="0.00000001" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-cell align-right editable-asset">
        <form id="form_investment_amount_invested_${entity.accountId}">
            <span id="investment_amount_invested_${entity.accountId}">${entity.amountInvested}</span>
            <span><input id="new_investment_amount_invested_${entity.accountId}" name="amount" type="number"
                         min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>


    <div class="col col-cell align-right">
        <span id="investment_average_purchase_price_${entity.accountId}">${entity.averagePurchasePrice}</span>
    </div>

    <div class="col col-cell align-right editable-asset">
        <form id="form_investment_current_share_value_${entity.accountId}">
            <span id="investment_current_share_value_${entity.accountId}">${entity.currentShareValue}</span>
            <span><input id="new_investment_current_share_value_${entity.accountId}" name="amount" type="number"
                         min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
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

    <div class="col col-cell align-right ${profitStyle}">
        <span id="investment_profit_percentage_${entity.accountId}">${entity.profitPercentage}</span>
    </div>

    <div class="col col-cell align-right">
        <span id="investment_balance_${entity.accountId}">${entity.balance}</span>
    </div>
</div>