<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    accountId: ${account.accountId},
  };

  prepareUpdateForm($("#form_investment_shares_${account.accountId}"),
    $("#investment_shares_${account.accountId}"),
    $("#new_investment_shares_${account.accountId}"),
    "snapshot/updateInvestmentShares",
    "${account.currencyUnitSymbol}",
    data,
    investmentSharesUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_investment_original_share_value_${account.accountId}"),
    $("#investment_original_share_value_${account.accountId}"),
    $("#new_investment_original_share_value_${account.accountId}"),
    "snapshot/updateInvestmentOriginalShareValue",
    "${account.currencyUnitSymbol}",
    data,
    investmentOriginalShareValueUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_investment_current_share_value_${account.accountId}"),
    $("#investment_current_share_value_${account.accountId}"),
    $("#new_investment_current_share_value_${account.accountId}"),
    "snapshot/updateInvestmentCurrentShareValue",
    "${account.currencyUnitSymbol}",
    data,
    investmentCurrentShareValueUpdateSuccessCallback,
  );
})
</script>

<div class="row border-1px-bottom" id="account_row_${account.accountId}">
    <%@ include file="_snapshot_col_account_name.jsp" %>

    <div class="col col-cell align-right editable-asset">
        <form id="form_investment_shares_${account.accountId}">
            <span id="investment_shares_${account.accountId}">${account.shares}</span>
            <span><input id="new_investment_shares_${account.accountId}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-cell align-right editable-asset">
        <form id="form_investment_original_share_value_${account.accountId}">
            <span id="investment_original_share_value_${account.accountId}">${account.currencyUnitSymbol}${account.originalShareValue}</span>
            <span><input id="new_investment_original_share_value_${account.accountId}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-cell align-right editable-asset">
        <form id="form_investment_current_share_value_${account.accountId}">
            <span id="investment_current_share_value_${account.accountId}">${account.currencyUnitSymbol}${account.currentShareValue}</span>
            <span><input id="new_investment_current_share_value_${account.accountId}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-cell align-right">
        <span id="investment_profit_percentage_${account.accountId}">${account.profitPercentage}</span>
    </div>

    <div class="col col-cell align-right">
        <span id="investment_balance_${account.accountId}">${account.currencyUnitSymbol}${account.balance}</span>
    </div>
</div>