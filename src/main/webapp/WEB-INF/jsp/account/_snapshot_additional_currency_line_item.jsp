<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    isOldSnapshot: ${snapshot.old},
    entityId: ${entity.accountId},
  };

  prepareUpdateForm($("#form_additional_currency_shares_${entity.accountId}"),
    $("#additional_currency_shares_${entity.accountId}"),
    $("#new_additional_currency_shares_${entity.accountId}"),
    "snapshot/updateAdditionalCurrencyShares",
    data,
    additionalCurrencySharesUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_additional_currency_amount_invested_${entity.accountId}"),
    $("#additional_currency_amount_invested_${entity.accountId}"),
    $("#new_additional_currency_amount_invested_${entity.accountId}"),
    "snapshot/updateAdditionalCurrencyAmountInvested",
    data,
    additionalCurrencyAmountInvestedUpdateSuccessCallback,
  );
})
</script>

<div class="row border-1px-bottom" id="account_row_${entity.accountId}">
    <%@ include file="_snapshot_col_remove_account.jsp" %>

    <%@ include file="_snapshot_col_account_name.jsp" %>

    <c:set var="includeProfitFutureTithing" value="true"/>
    <%@ include file="_account_future_tithing_select.jsp" %>

    <div class="col col-cell-${deviceType} align-right ${editableClass}">
        <form id="form_additional_currency_shares_${entity.accountId}">
            <span id="additional_currency_shares_${entity.accountId}">${entity.shares}</span>
            <span><input id="new_additional_currency_shares_${entity.accountId}" name="amount" type="number" min="0"
                         step="0.00000001" class="hidden-input"/></span>
        </form>
    </div>

    <div class="col col-cell-${deviceType} align-right ${editableClass}">
        <form id="form_additional_currency_amount_invested_${entity.accountId}">
            <span id="additional_currency_amount_invested_${entity.accountId}">${entity.amountInvested}</span>
            <span><input id="new_additional_currency_amount_invested_${entity.accountId}" name="amount" type="number"
                         min="0" step="0.01" class="hidden-input"/></span>
        </form>
    </div>

    <div class="col col-cell-${deviceType} align-right ${regularClass}">
        <span id="additional_currency_average_purchase_price_${entity.accountId}">${entity.averagePurchasePrice}</span>
    </div>

    <fmt:parseNumber var="profitValue" value="${entity.profitPercentage}" integerOnly="false" />
    <c:choose>
        <c:when test="${profitValue ge 0}">
            <c:set var="profitStyle" value="cell-green" />
        </c:when>
        <c:when test="${profitValue lt 0}">
            <c:set var="profitStyle" value="cell-red" />
        </c:when>
        <c:otherwise>
            <c:set var="profitStyle" value="" />
        </c:otherwise>
    </c:choose>

    <div class="col col-cell-${deviceType} width-total-${deviceType} align-right ${regularClass}">
        <div>
            <span id="additional_currency_value_in_base_${entity.accountId}">${entity.valueInBaseCurrency}</span>
        </div>
        <div>
            <span id="additional_currency_profit_percentage_${entity.accountId}"
                  class="${profitStyle}">${entity.profitPercentage}</span>
        </div>
    </div>
</div>
