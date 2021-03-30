<div class="row">
    <div class="col" style="background: lightpink;">
        <div class="row border-1px-bottom">
            <div class="col col-cell text-center"><b>INCOME</b>
                <a href="/snapshot/${snapshot.id}/newIncomeTransaction" style="text-decoration: none;">&#x271A;</a>
            </div>
        </div>
        <c:choose>
            <c:when test="${not empty snapshot.incomeTransactionsTotal}">
                <c:forEach items="${snapshot.incomeTransactionsTotal}" var="entry">
                    <div class="row border-1px-bottom bg-light-yellow">
                        <div class="col col-cell col-account-name">TOTAL ${entry.key}</div>
                        <div class="col col-cell align-right"><b><span
                                id="total_INCOME_${entry.key}">${entry.value}</span></b></div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="row border-1px-bottom bg-light-yellow">
                    <div class="col col-cell col-account-name">TOTAL</div>
                    <div class="col col-cell align-right"><b>0.00</b></div>
                </div>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${not empty snapshot.incomes}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title short">&nbsp;</div>
                    <div class="col col-cell col-title short">&#128260;</div>
                    <div class="col col-cell col-title">Description</div>
                    <div class="col col-cell col-title">Date</div>
                    <div class="col col-cell col-title">Donation Ratio</div>
                    <div class="col col-cell col-title">Amount</div>
                </div>
                <c:forEach var="entity" items="${snapshot.incomes}">
                    <%@ include file="_snapshot_income_transaction_line_item.jsp" %>
                </c:forEach>
            </c:when>
        </c:choose>

    </div>

    <div class="col" style="background: lightpink;">
        <div class="row border-1px-bottom">
            <div class="col col-cell text-center"><b>INVESTMENTS</b>
                <a href="/snapshot/${snapshot.id}/newInvestmentTransaction" style="text-decoration: none;">&#x271A;</a>
            </div>
        </div>
        <c:choose>
            <c:when test="${not empty snapshot.investmentTransactionsTotal}">
                <c:forEach items="${snapshot.investmentTransactionsTotal}" var="entry">
                    <div class="row border-1px-bottom bg-light-yellow">
                        <div class="col col-cell col-account-name">TOTAL ${entry.key}</div>
                        <div class="col col-cell align-right"><b><span
                                id="total_INVESTMENT_${entry.key}">${entry.value}</span></b></div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="row border-1px-bottom bg-light-yellow">
                    <div class="col col-cell col-account-name">TOTAL</div>
                    <div class="col col-cell align-right"><b>0.00</b></div>
                </div>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${not empty snapshot.investments}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title short">&nbsp;</div>
                    <div class="col col-cell col-title short">&#128260;</div>
                    <div class="col col-cell col-title">Description</div>
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

    <div class="col" style="background: lightpink;">
        <div class="row border-1px-bottom">
            <div class="col col-cell text-center"><b>DONATIONS</b>
                <a href="/snapshot/${snapshot.id}/newDonationTransaction" style="text-decoration: none;">&#x271A;</a>
            </div>
        </div>
        <c:choose>
            <c:when test="${not empty snapshot.donationTransactionsTotal}">
                <c:forEach items="${snapshot.donationTransactionsTotal}" var="entry">
                    <div class="row border-1px-bottom bg-light-yellow">
                        <div class="col col-cell col-account-name">TOTAL ${entry.key}</div>
                        <div class="col col-cell align-right"><b><span
                                id="total_DONATION_${entry.key}">${entry.value}</span></b></div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="row border-1px-bottom bg-light-yellow">
                    <div class="col col-cell col-account-name">TOTAL</div>
                    <div class="col col-cell align-right"><b>0.00</b></div>
                </div>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${not empty snapshot.donations}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title short">&nbsp;</div>
                    <div class="col col-cell col-title short">&#128260;</div>
                    <div class="col col-cell col-title">Description</div>
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