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

  prepareUpdateForm($("#form_investment_original_share_value_${entity.accountId}"),
    $("#investment_original_share_value_${entity.accountId}"),
    $("#new_investment_original_share_value_${entity.accountId}"),
    "snapshot/updateInvestmentOriginalShareValue",
    "${entity.currencyUnitSymbol}",
    data,
    investmentOriginalShareValueUpdateSuccessCallback,
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
        <form id="form_investment_original_share_value_${entity.accountId}">
            <span id="investment_original_share_value_${entity.accountId}">${entity.originalShareValue}</span>
            <span><input id="new_investment_original_share_value_${entity.accountId}" name="amount" type="number"
                         min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
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

    <div class="col col-cell align-right">
        <span id="investment_profit_percentage_${entity.accountId}">${entity.profitPercentage}</span>
    </div>

    <div class="col col-cell align-right">
        <span id="investment_balance_${entity.accountId}">${entity.balance}</span>
    </div>
</div>