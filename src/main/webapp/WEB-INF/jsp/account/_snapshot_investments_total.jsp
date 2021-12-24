<div class="row border-1px-bottom bg-light-yellow">
    <div class="col col-cell-${deviceType} col-account-name">Investments TOTAL</div>
    <div class="col col-cell-${deviceType} width-total-${deviceType} align-right"><b><span id="snapshot_investments_balance">${snapshot.investmentTotals.balance}</span></b></div>
</div>
<div class="row border-1px-bottom bg-light-yellow">
    <div class="col col-cell-${deviceType} width-70px"></div>

    <div class="col col-cell-${deviceType}"></div>

    <div class="col col-cell-${deviceType} align-right"><b><span id="snapshot_investments_amount_invested">${snapshot.investmentTotals.amountInvested}</span></b>
    </div>

    <div class="col col-cell-${deviceType}"></div>

    <div class="col col-cell-${deviceType}"></div>

    <fmt:parseNumber var="profitValue" value="${snapshot.investmentTotals.profitPercentage}" integerOnly="false" />
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

    <div class="col col-cell-${deviceType} width-total-${deviceType} align-right">
        <b><span id="snapshot_investments_profit_percentage" class="${profitStyle}">${snapshot.investmentTotals.profitPercentage}</span></b>
    </div>
</div>
