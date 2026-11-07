<c:set var="totals" value="${snapshot.additionalCurrencyTotals[currentCurrency]}"/>
<div class="row border-1px-bottom bg-light-yellow">
    <div class="col col-cell-${deviceType} col-account-name">TOTAL ${currentCurrency}</div>
    <div class="col col-cell-${deviceType} short-${deviceType}">&nbsp;</div>
    <div class="col col-cell-${deviceType} width-total-${deviceType}">&nbsp;</div>
    <div class="col col-cell-${deviceType} align-right">
        <b><span id="snapshot_additional_currency_shares_${currentCurrency}">${totals.shares}</span></b>
    </div>
    <div class="col col-cell-${deviceType} align-right">
        <b><span id="snapshot_additional_currency_amount_invested_${currentCurrency}">${totals.amountInvested}</span></b>
    </div>
    <div class="col col-cell-${deviceType} align-right">
        <b><span id="snapshot_additional_currency_average_purchase_price_${currentCurrency}">${totals.averagePurchasePrice}</span></b>
    </div>
    <fmt:parseNumber var="profitValue" value="${totals.profitPercentage}" integerOnly="false" />
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
    <div class="col col-cell-${deviceType} width-total-${deviceType} align-right">
        <div>
            <b><span id="snapshot_additional_currency_balance_${currentCurrency}">${totals.balance}</span></b>
        </div>
        <div>
            <b><span id="snapshot_additional_currency_profit_percentage_${currentCurrency}"
                     class="${profitStyle}">${totals.profitPercentage}</span></b>
        </div>
    </div>
</div>
