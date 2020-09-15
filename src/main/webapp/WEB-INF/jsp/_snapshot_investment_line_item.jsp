<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    accountId: ${account.id},
  };

  prepareUpdateForm($("#form_investment_shares_${account.id}"),
    $("#investment_shares_${account.id}"),
    $("#new_investment_shares_${account.id}"),
    "snapshot/updateInvestmentShares",
    "${account.currencyUnitSymbol}",
    data,
    investmentSharesUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_investment_original_share_value_${account.id}"),
    $("#investment_original_share_value_${account.id}"),
    $("#new_investment_original_share_value_${account.id}"),
    "snapshot/updateInvestmentOriginalShareValue",
    "${account.currencyUnitSymbol}",
    data,
    investmentOriginalShareValueUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_investment_current_share_value_${account.id}"),
    $("#investment_current_share_value_${account.id}"),
    $("#new_investment_current_share_value_${account.id}"),
    "snapshot/updateInvestmentCurrentShareValue",
    "${account.currencyUnitSymbol}",
    data,
    investmentCurrentShareValueUpdateSuccessCallback,
  );
})
</script>

<div class="row" id="account_row_${account.id}">
    <%@ include file="_snapshot_col_account_name.jsp" %>

    <div class="col col-value editable-asset">
        <form id="form_investment_shares_${account.id}">
            <span id="investment_shares_${account.id}">${account.shares}</span>
            <span><input id="new_investment_shares_${account.id}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-value editable-asset">
        <form id="form_investment_original_share_value_${account.id}">
            <span id="investment_original_share_value_${account.id}">${account.currencyUnitSymbol}${account.originalShareValue}</span>
            <span><input id="new_investment_original_share_value_${account.id}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-value editable-asset">
        <form id="form_investment_current_share_value_${account.id}">
            <span id="investment_current_share_value_${account.id}">${account.currencyUnitSymbol}${account.currentShareValue}</span>
            <span><input id="new_investment_current_share_value_${account.id}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-value">
        <span id="investment_profit_percentage_${account.id}">${account.profitPercentage}%</span>
    </div>

    <div class="col">
        <span id="investment_total_${account.id}">${account.currencyUnitSymbol}${account.total}</span>
    </div>
</div>