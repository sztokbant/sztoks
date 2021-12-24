<c:set var="ccTotals" value="${snapshot.creditCardTotals[currentCurrency]}"/>
<div class="row border-1px-bottom bg-light-yellow">
    <div class="col col-cell-${deviceType} col-account-name">Credit Cards TOTAL ${currentCurrency}</div>
    <div class="col col-cell-${deviceType} short-${deviceType}"></div>
    <div class="col col-cell-${deviceType} align-right"><b><span id="snapshot_credit_card_total_credit_${currentCurrency}">${ccTotals.totalCredit}</span></b>
    </div>
    <div class="col col-cell-${deviceType} align-right"><b><span id="snapshot_credit_card_available_credit_${currentCurrency}">${ccTotals.availableCredit}</span></b>
    </div>

    <fmt:parseNumber var="ccUsageValue" value="${ccTotals.usedCreditPercentage}" integerOnly="false"/>
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

    <div class="col col-cell-${deviceType} align-right">
        <b><span id="snapshot_credit_card_used_credit_percentage_${currentCurrency}"
                 class="${ccUsageStyle}">${ccTotals.usedCreditPercentage}</span></b>
    </div>

    <div class="col col-cell-${deviceType} align-right"><b><span id="snapshot_credit_card_statement_${currentCurrency}">${ccTotals.statement}</span></b>
    </div>
    <div class="col col-cell-${deviceType} align-right"><b><span id="snapshot_credit_card_remaining_balance_${currentCurrency}">${ccTotals.remainingBalance}</span></b>
    </div>
    <div class="col col-cell-${deviceType} align-right"><b><span id="snapshot_credit_card_balance_${currentCurrency}">${ccTotals.balance}</span></b>
    </div>
</div>
