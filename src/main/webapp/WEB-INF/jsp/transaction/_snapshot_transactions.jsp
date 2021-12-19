<div class="row">
    <div class="col">
        <div class="row border-1px-bottom">
            <div class="col col-cell-${deviceType}"><b>Transactions</b></div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col col-section income-transactions">
        <div class="row border-1px-bottom">
            <div class="col col-cell-${deviceType} text-center"><b>INCOME</b></div>
        </div>

        <div class="row border-1px-bottom bg-light-yellow">
            <div class="col col-cell-${deviceType} col-account-name">TOTAL</div>
            <div class="col col-cell-${deviceType} align-right">
                <b><span id="total_INCOME">${snapshot.incomeTransactionsTotal}</span></b>
            </div>
        </div>

        <c:set var="editableClass" value="editable-income"/>

        <div class="row border-1px-bottom bg-transaction-subcategory">
            <div class="col col-cell-${deviceType} col-title">&nbsp;</div>
            <div class="col col-cell-${deviceType} col-title">YTD Total</div>
            <div class="col col-cell-${deviceType} col-title">12mo Total</div>
        </div>

        <div class="row border-1px-bottom bg-transaction-subcategory">
            <div class="col col-cell-${deviceType} transaction-subcategory-label">Cumulative</div>
            <div class="col col-cell-${deviceType} text-center">
                <span id="ytd_incomes_total">${ytdTotals.incomesTotal}</span>
            </div>
            <div class="col col-cell-${deviceType} text-center">
                <span id="twelve_months_incomes_total">${twelveMonthsTotals.incomesTotal}</span>
            </div>
        </div>

        <%@ include file="_snapshot_income_categories_totals.jsp" %>

        <c:choose>
            <c:when test="${not empty snapshot.incomes}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell-${deviceType} col-title short">&nbsp;</div>
                    <div class="col col-cell-${deviceType} col-title">&#128260;</div>
                    <div class="col col-cell-${deviceType} col-title col-account-name">Description</div>
                    <div class="col col-cell-${deviceType} col-title">Date</div>
                    <div class="col col-cell-${deviceType} col-title">Category</div>
                    <div class="col col-cell-${deviceType} col-title">Tithing %</div>
                    <div class="col col-cell-${deviceType} col-title">Amount</div>
                </div>
                <c:forEach var="entity" items="${snapshot.incomes}">
                    <%@ include file="_snapshot_income_transaction_line_item.jsp" %>
                </c:forEach>
            </c:when>
        </c:choose>
    </div>
</div>

<div class="row">
    <div class="col col-section investment-transactions">
        <div class="row border-1px-bottom">
            <div class="col col-cell-${deviceType} text-center"><b>INVESTMENTS</b></div>
        </div>

        <div class="row border-1px-bottom bg-light-yellow">
            <div class="col col-cell-${deviceType} col-account-name">TOTAL</div>
            <div class="col col-cell-${deviceType} align-right">
                <b><span id="total_INVESTMENT">${snapshot.investmentTransactionsTotal}</span></b>
            </div>
        </div>

        <c:set var="editableClass" value="editable-investment"/>

        <div class="row border-1px-bottom bg-transaction-subcategory">
            <div class="col col-cell-${deviceType} col-title">&nbsp;</div>
            <div class="col col-cell-${deviceType} col-title">YTD Total</div>
            <div class="col col-cell-${deviceType} col-title">YTD Avg</div>
            <div class="col col-cell-${deviceType} col-title">12mo Total</div>
            <div class="col col-cell-${deviceType} col-title">12mo Avg</div>
        </div>

        <div class="row border-1px-bottom bg-transaction-subcategory">
            <div class="col col-cell-${deviceType} transaction-subcategory-label">Cumulative</div>
            <div class="col col-cell-${deviceType} text-center">
                <span id="ytd_investments_total">${ytdTotals.investmentsTotal}</span>
            </div>
            <div class="col col-cell-${deviceType} text-center">
                <span id="ytd_investment_avg">${ytdTotals.investmentAvg}</span>
            </div>
            <div class="col col-cell-${deviceType} text-center">
                <span id="twelve_months_investments_total">${twelveMonthsTotals.investmentsTotal}</span>
            </div>
            <div class="col col-cell-${deviceType} text-center">
                <span id="twelve_months_investment_avg">${twelveMonthsTotals.investmentAvg}</span>
            </div>
        </div>

        <%@ include file="_snapshot_investment_categories_totals.jsp" %>

        <c:choose>
            <c:when test="${not empty snapshot.investments}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell-${deviceType} col-title short">&nbsp;</div>
                    <div class="col col-cell-${deviceType} col-title">&#128260;</div>
                    <div class="col col-cell-${deviceType} col-title col-account-name">Description</div>
                    <div class="col col-cell-${deviceType} col-title">Date</div>
                    <div class="col col-cell-${deviceType} col-title">Category</div>
                    <div class="col col-cell-${deviceType} col-title">Amount</div>
                </div>
                <c:forEach var="entity" items="${snapshot.investments}">
                    <%@ include file="_snapshot_investment_transaction_line_item.jsp" %>
                </c:forEach>
            </c:when>
        </c:choose>

    </div>

<c:choose>
    <c:when test="${deviceType eq 'MOBILE'}">
        </div>
        <div class="row">
    </c:when>
</c:choose>

    <div class="col col-section donation-transactions">
        <div class="row border-1px-bottom">
            <div class="col col-cell-${deviceType} text-center"><b>DONATIONS</b></div>
        </div>

        <div class="row border-1px-bottom bg-light-yellow">
            <div class="col col-cell-${deviceType} col-account-name">TOTAL</div>
            <div class="col col-cell-${deviceType} align-right">
                <b><span id="total_DONATION">${snapshot.donationTransactionsTotal}</span></b>
            </div>
        </div>

        <c:set var="editableClass" value="editable-donation"/>

        <div class="row border-1px-bottom bg-transaction-subcategory">
            <div class="col col-cell-${deviceType} col-title">&nbsp;</div>
            <div class="col col-cell-${deviceType} col-title">YTD Total</div>
            <div class="col col-cell-${deviceType} col-title">YTD Avg</div>
            <div class="col col-cell-${deviceType} col-title">12mo Total</div>
            <div class="col col-cell-${deviceType} col-title">12mo Avg</div>
        </div>

        <div class="row border-1px-bottom bg-transaction-subcategory">
            <div class="col col-cell-${deviceType} transaction-subcategory-label">Cumulative</div>
            <div class="col col-cell-${deviceType} text-center">
                <span id="ytd_donations_total">${ytdTotals.donationsTotal}</span>
            </div>
            <div class="col col-cell-${deviceType} text-center">
                <span id="ytd_donation_avg">${ytdTotals.donationAvg}</span>
            </div>
            <div class="col col-cell-${deviceType} text-center">
                <span id="twelve_months_donations_total">${twelveMonthsTotals.donationsTotal}</span>
            </div>
            <div class="col col-cell-${deviceType} text-center">
                <span id="twelve_months_donation_avg">${twelveMonthsTotals.donationAvg}</span>
            </div>
        </div>

        <div class="row border-1px-bottom bg-transaction-subcategory">
            <div class="col col-cell-${deviceType} col-title">&nbsp;</div>
            <div class="col col-cell-${deviceType} col-title">YTD Total</div>
            <div class="col col-cell-${deviceType} col-title">&nbsp;</div>
            <div class="col col-cell-${deviceType} col-title">12mo Total</div>
            <div class="col col-cell-${deviceType} col-title">This Month</div>
        </div>

        <div class="row border-1px-bottom bg-transaction-subcategory">
            <div class="col col-cell-${deviceType} transaction-subcategory-label">Tax Deductible</div>
            <div class="col col-cell-${deviceType} text-center">
                <span id="ytd_tax_deductible_donations_total">${ytdTotals.taxDeductibleDonationsTotal}</span>
            </div>
            <div class="col col-cell-${deviceType}">&nbsp;</div>
            <div class="col col-cell-${deviceType} text-center">
                <span id="twelve_months_tax_deductible_donations_total">${twelveMonthsTotals.taxDeductibleDonationsTotal}</span>
            </div>
            <div class="col col-cell-${deviceType} text-center">
                <span id="tax_deductible_donations_total">${snapshot.taxDeductibleDonationTransactionsTotal}</span>
            </div>
        </div>

        <%@ include file="_snapshot_donation_categories_totals.jsp" %>

        <c:choose>
            <c:when test="${not empty snapshot.donations}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell-${deviceType} col-title short">&nbsp;</div>
                    <div class="col col-cell-${deviceType} col-title">&#128260;</div>
                    <div class="col col-cell-${deviceType} col-title col-account-name">Description</div>
                    <div class="col col-cell-${deviceType} col-title">Date</div>
                    <div class="col col-cell-${deviceType} col-title">Category</div>
                    <div class="col col-cell-${deviceType} col-title">Tax deductible?</div>
                    <div class="col col-cell-${deviceType} col-title">Amount</div>
                </div>
                <c:forEach var="entity" items="${snapshot.donations}">
                    <%@ include file="_snapshot_donation_transaction_line_item.jsp" %>
                </c:forEach>
            </c:when>
        </c:choose>
    </div>
</div>
