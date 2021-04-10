<div class="row">
    <div class="col">
        <div class="row border-1px-bottom">
            <div class="col col-cell"><b>Transactions</b></div>
        </div>
    </div>
</div>
<div class="row">
    <div class="col col-section income-transactions">
        <div class="row border-1px-bottom">
            <div class="col col-cell text-center"><b>INCOME</b>
                <a href="/snapshot/${snapshot.id}/newIncomeTransaction" style="text-decoration: none;">&#x271A;</a>
            </div>
        </div>

        <div class="row border-1px-bottom bg-light-yellow">
            <div class="col col-cell col-account-name">TOTAL</div>
            <div class="col col-cell align-right">
                <b><span id="total_INCOME">${snapshot.incomeTransactionsTotal}</span></b>
            </div>
        </div>

        <c:choose>
            <c:when test="${not empty snapshot.incomes}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title short">&nbsp;</div>
                    <div class="col col-cell col-title short">&#128260;</div>
                    <div class="col col-cell col-title col-account-name">Description</div>
                    <div class="col col-cell col-title">Date</div>
                    <div class="col col-cell col-title">Tithing Percentage</div>
                    <div class="col col-cell col-title">Amount</div>
                </div>
                <c:forEach var="entity" items="${snapshot.incomes}">
                    <%@ include file="_snapshot_income_transaction_line_item.jsp" %>
                </c:forEach>
            </c:when>
        </c:choose>

    </div>

    <div class="col col-section investment-transactions">
        <div class="row border-1px-bottom">
            <div class="col col-cell text-center"><b>INVESTMENTS</b>
                <a href="/snapshot/${snapshot.id}/newInvestmentTransaction" style="text-decoration: none;">&#x271A;</a>
            </div>
        </div>

        <div class="row border-1px-bottom bg-light-yellow">
            <div class="col col-cell col-account-name">TOTAL</div>
            <div class="col col-cell align-right">
                <b><span id="total_INVESTMENT">${snapshot.investmentTransactionsTotal}</span></b>
            </div>
        </div>

        <c:choose>
            <c:when test="${not empty snapshot.investments}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title short">&nbsp;</div>
                    <div class="col col-cell col-title short">&#128260;</div>
                    <div class="col col-cell col-title col-account-name">Description</div>
                    <div class="col col-cell col-title">Date</div>
                    <div class="col col-cell col-title">Category</div>
                    <div class="col col-cell col-title">Amount</div>
                </div>
                <c:forEach var="entity" items="${snapshot.investments}">
                    <%@ include file="_snapshot_investment_transaction_line_item.jsp" %>
                </c:forEach>
            </c:when>
        </c:choose>

    </div>

    <div class="col col-section donation-transactions">
        <div class="row border-1px-bottom">
            <div class="col col-cell text-center"><b>DONATIONS</b>
                <a href="/snapshot/${snapshot.id}/newDonationTransaction" style="text-decoration: none;">&#x271A;</a>
            </div>
        </div>

        <div class="row border-1px-bottom bg-light-yellow">
            <div class="col col-cell col-account-name">TOTAL</div>
            <div class="col col-cell align-right">
                <b><span id="total_DONATION">${snapshot.donationTransactionsTotal}</span></b>
            </div>
        </div>

        <c:choose>
            <c:when test="${not empty snapshot.donations}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title short">&nbsp;</div>
                    <div class="col col-cell col-title short">&#128260;</div>
                    <div class="col col-cell col-title col-account-name">Description</div>
                    <div class="col col-cell col-title">Date</div>
                    <div class="col col-cell col-title">Tax deductible?</div>
                    <div class="col col-cell col-title">Amount</div>
                </div>
                <c:forEach var="entity" items="${snapshot.donations}">
                    <%@ include file="_snapshot_donation_transaction_line_item.jsp" %>
                </c:forEach>
            </c:when>
        </c:choose>

    </div>
</div>