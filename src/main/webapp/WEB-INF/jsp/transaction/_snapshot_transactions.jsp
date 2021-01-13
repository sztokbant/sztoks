<div class="row">
    <div class="col" style="background: lightpink;">
        <div class="row border-1px-bottom">
            <div class="col col-cell text-center"><b>INCOME</b></div>
        </div>
        <c:choose>
            <c:when test="${not empty snapshot.incomeTransactionsTotal}">
                <c:forEach items="${snapshot.incomeTransactionsTotal}" var="entry">
                    <div class="row border-1px-bottom bg-light-yellow">
                        <div class="col col-cell col-txn-name">TOTAL ${entry.key}</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell align-right"><b><span
                                id="total_INCOME_${entry.key}">${entry.value}</span></b></div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="row border-1px-bottom bg-light-yellow">
                    <div class="col col-cell col-txn-name">TOTAL</div>
                    <div class="col col-cell">&nbsp;</div>
                    <div class="col col-cell">&nbsp;</div>
                    <div class="col col-cell">&nbsp;</div>
                    <div class="col col-cell">&nbsp;</div>
                    <div class="col col-cell align-right"><b>0.00</b></div>
                </div>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${not empty snapshot.incomes}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title"></div>
                    <div class="col col-cell col-title">Recurring?</div>
                    <div class="col col-cell col-title">Date</div>
                    <div class="col col-cell col-title">Description</div>
                    <div class="col col-cell col-title">Donation Ratio</div>
                    <div class="col col-cell col-title">Amount</div>
                </div>
                <c:forEach var="txn" items="${snapshot.incomes}">
                    <%@ include file="_snapshot_income_txn_line_item.jsp" %>
                </c:forEach>
            </c:when>
        </c:choose>

    </div>

    <div class="col" style="background: lightpink;">
        <div class="row border-1px-bottom">
            <div class="col col-cell text-center"><b>INVESTMENTS</b></div>
        </div>
        <c:choose>
            <c:when test="${not empty snapshot.investmentTransactionsTotal}">
                <c:forEach items="${snapshot.investmentTransactionsTotal}" var="entry">
                    <div class="row border-1px-bottom bg-light-yellow">
                        <div class="col col-cell col-txn-name">TOTAL ${entry.key}</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell align-right"><b><span
                                id="total_INVESTMENT_${entry.key}">${entry.value}</span></b></div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="row border-1px-bottom bg-light-yellow">
                    <div class="col col-cell col-txn-name">TOTAL</div>
                    <div class="col col-cell">&nbsp;</div>
                    <div class="col col-cell">&nbsp;</div>
                    <div class="col col-cell">&nbsp;</div>
                    <div class="col col-cell">&nbsp;</div>
                    <div class="col col-cell align-right"><b>0.00</b></div>
                </div>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${not empty snapshot.investments}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title"></div>
                    <div class="col col-cell col-title">Recurring?</div>
                    <div class="col col-cell col-title">Date</div>
                    <div class="col col-cell col-title">Description</div>
                    <div class="col col-cell col-title">Category</div>
                    <div class="col col-cell col-title">Amount</div>
                </div>
                <c:forEach var="txn" items="${snapshot.investments}">
                    <%@ include file="_snapshot_investment_txn_line_item.jsp" %>
                </c:forEach>
            </c:when>
        </c:choose>

    </div>

    <div class="col" style="background: lightpink;">
        <div class="row border-1px-bottom">
            <div class="col col-cell text-center"><b>DONATIONS</b></div>
        </div>
        <c:choose>
            <c:when test="${not empty snapshot.donationTransactionsTotal}">
                <c:forEach items="${snapshot.donationTransactionsTotal}" var="entry">
                    <div class="row border-1px-bottom bg-light-yellow">
                        <div class="col col-cell col-txn-name">TOTAL ${entry.key}</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell align-right"><b><span
                                id="total_DONATION_${entry.key}">${entry.value}</span></b></div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="row border-1px-bottom bg-light-yellow">
                    <div class="col col-cell col-txn-name">TOTAL</div>
                    <div class="col col-cell">&nbsp;</div>
                    <div class="col col-cell">&nbsp;</div>
                    <div class="col col-cell">&nbsp;</div>
                    <div class="col col-cell">&nbsp;</div>
                    <div class="col col-cell align-right"><b>0.00</b></div>
                </div>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${not empty snapshot.donations}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title"></div>
                    <div class="col col-cell col-title">Recurring?</div>
                    <div class="col col-cell col-title">Date</div>
                    <div class="col col-cell col-title">Description</div>
                    <div class="col col-cell col-title">Tax deductible?</div>
                    <div class="col col-cell col-title">Amount</div>
                </div>
                <c:forEach var="txn" items="${snapshot.donations}">
                    <%@ include file="_snapshot_donation_txn_line_item.jsp" %>
                </c:forEach>
            </c:when>
        </c:choose>

    </div>
</div>