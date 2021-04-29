<div class="row">
    <div class="col col-section asset-accounts">
        <div class="row border-1px-bottom">
            <div class="col col-cell text-center"><b>ASSETS</b>
                <a href="/snapshot/${snapshot.id}/newAssetAccount" style="text-decoration: none;">&#x271A;</a>
            </div>
        </div>

        <div class="row border-1px-bottom bg-light-yellow">
            <div class="col col-cell col-account-name">ASSETS TOTAL</div>
            <div class="col col-cell align-right">
                <b><span id="total_ASSET">${snapshot.assetsTotal}</span></b>
            </div>
        </div>

        <c:choose>
            <c:when test="${not empty snapshot.simpleAssetAccounts}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title short">&nbsp;</div>
                    <div class="col col-cell col-title col-account-name"><i>Simple Assets</i></div>
                    <div class="col col-cell col-title">Future Tithing</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">Balance</div>
                </div>
                <c:forEach var="entity" items="${snapshot.simpleAssetAccounts}">
                    <%@ include file="_snapshot_simple_asset_line_item.jsp" %>
                </c:forEach>
                <c:if test="${snapshot.simpleAssetAccounts.size() gt 1}">
                    <%@ include file="_snapshot_simple_assets_total.jsp" %>
                </c:if>
            </c:when>
        </c:choose>

        <c:choose>
            <c:when test="${not empty snapshot.giftCertificateAccounts}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title short">&nbsp;</div>
                    <div class="col col-cell col-title col-account-name"><i>Gift Certificates</i></div>
                    <div class="col col-cell col-title">Future Tithing</div>
                    <div class="col col-cell col-title">Shares</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">Current Share Value</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">Balance</div>
                </div>
                <c:forEach var="entity" items="${snapshot.giftCertificateAccounts}">
                    <%@ include file="_snapshot_gift_certificate_line_item.jsp" %>
                </c:forEach>
                <c:if test="${snapshot.giftCertificateAccounts.size() gt 1}">
                    <%@ include file="_snapshot_gift_certificates_total.jsp" %>
                </c:if>
            </c:when>
        </c:choose>

        <c:choose>
            <c:when test="${not empty snapshot.receivableAccounts}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title short">&nbsp;</div>
                    <div class="col col-cell col-title col-account-name"><i>Receivables</i></div>
                    <div class="col col-cell col-title">Future Tithing</div>
                    <div class="col col-cell col-title">Due Date</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">Balance</div>
                </div>
                <c:forEach var="entity" items="${snapshot.receivableAccounts}">
                    <%@ include file="_snapshot_receivable_line_item.jsp" %>
                </c:forEach>
                <c:if test="${snapshot.receivableAccounts.size() gt 1}">
                    <%@ include file="_snapshot_receivables_total.jsp" %>
                </c:if>
            </c:when>
        </c:choose>

        <c:choose>
            <c:when test="${not empty snapshot.sharedBillReceivableAccounts}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title short">&nbsp;</div>
                    <div class="col col-cell col-title col-account-name"><i>Shared Bill Receivables</i></div>
                    <div class="col col-cell col-title">Future Tithing</div>
                    <div class="col col-cell col-title">Due Day</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">Number of Partners</div>
                    <div class="col col-cell col-title">Payment received?</div>
                    <div class="col col-cell col-title">Bill Amount</div>
                    <div class="col col-cell col-title">Balance</div>
                </div>
                <c:forEach var="entity" items="${snapshot.sharedBillReceivableAccounts}">
                    <%@ include file="_snapshot_shared_bill_receivable_line_item.jsp" %>
                </c:forEach>
                <c:if test="${snapshot.sharedBillReceivableAccounts.size() gt 1}">
                    <%@ include file="_snapshot_shared_bill_receivables_total.jsp" %>
                </c:if>
            </c:when>
        </c:choose>

        <c:choose>
            <c:when test="${not empty snapshot.investmentAccounts}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title short">&nbsp;</div>
                    <div class="col col-cell col-title col-account-name"><i>Investments</i></div>
                    <div class="col col-cell col-title">Future Tithing</div>
                    <div class="col col-cell col-title">Shares</div>
                    <div class="col col-cell col-title">Amount Invested</div>
                    <div class="col col-cell col-title">Average Purchase Price</div>
                    <div class="col col-cell col-title">Current Share Value</div>
                    <div class="col col-cell col-title">Profit</div>
                    <div class="col col-cell col-title">Balance</div>
                </div>
                <c:forEach var="entity" items="${snapshot.investmentAccounts}">
                    <%@ include file="_snapshot_investment_line_item.jsp" %>
                </c:forEach>
                <c:if test="${snapshot.investmentAccounts.size() gt 1}">
                    <%@ include file="_snapshot_investments_total.jsp" %>
                </c:if>
            </c:when>
        </c:choose>
    </div>
    <div class="col col-section liability-accounts">
        <div class="row border-1px-bottom">
            <div class="col col-cell text-center"><b>LIABILITIES</b>
                <a href="/snapshot/${snapshot.id}/newLiabilityAccount" style="text-decoration: none;">&#x271A;</a>
            </div>
        </div>

        <div class="row border-1px-bottom bg-light-yellow">
            <div class="col col-cell col-account-name">LIABILITIES TOTAL</div>
            <div class="col col-cell align-right">
                <b><span id="total_LIABILITY">${snapshot.liabilitiesTotal}</span></b>
            </div>
        </div>

        <div class="row border-1px-bottom">
            <div class="col col-cell col-title short">&nbsp;</div>
            <div class="col col-cell col-title col-account-name"><i>Simple Liabilities</i></div>
            <div class="col col-cell col-title">&nbsp;</div>
            <div class="col col-cell col-title">&nbsp;</div>
            <div class="col col-cell col-title">&nbsp;</div>
            <div class="col col-cell col-title">&nbsp;</div>
            <div class="col col-cell col-title">&nbsp;</div>
            <div class="col col-cell col-title">Balance</div>
        </div>

        <%@ include file="_snapshot_tithing_line_item.jsp" %>
        <%@ include file="_snapshot_future_tithing_line_item.jsp" %>

        <c:choose>
            <c:when test="${not empty snapshot.simpleLiabilityAccounts}">
                <c:forEach var="entity" items="${snapshot.simpleLiabilityAccounts}">
                    <%@ include file="_snapshot_simple_liability_line_item.jsp" %>
                </c:forEach>
            </c:when>
        </c:choose>
        <%-- Tithing accounts are counted grouped along with SimpleLiability accounts --%>
        <%@ include file="_snapshot_simple_liabilities_total.jsp" %>

        <c:choose>
            <c:when test="${not empty snapshot.payableAccounts}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title short">&nbsp;</div>
                    <div class="col col-cell col-title col-account-name"><i>Payables</i></div>
                    <div class="col col-cell col-title">Due Date</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">&nbsp;</div>
                    <div class="col col-cell col-title">Balance</div>
                </div>
                <c:forEach var="entity" items="${snapshot.payableAccounts}">
                    <%@ include file="_snapshot_payable_line_item.jsp" %>
                </c:forEach>
                <c:if test="${snapshot.payableAccounts.size() gt 1}">
                    <%@ include file="_snapshot_payables_total.jsp" %>
                </c:if>
            </c:when>
        </c:choose>

        <c:choose>
            <c:when test="${not empty snapshot.creditCardAccounts}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell col-title short">&nbsp;</div>
                    <div class="col col-cell col-title col-account-name"><i>Credit Cards</i></div>
                    <div class="col col-cell col-title">Total Credit</div>
                    <div class="col col-cell col-title">Available Credit</div>
                    <div class="col col-cell col-title">Used Credit</div>
                    <div class="col col-cell col-title">Statement</div>
                    <div class="col col-cell col-title">Remaining Balance</div>
                    <div class="col col-cell col-title">Balance</div>
                </div>

                <c:set var="currentCurrency" value=""/>
                <c:forEach var="entity" items="${snapshot.creditCardAccounts}">
                    <c:if test="${currentCurrency ne '' && currentCurrency ne entity.currencyUnit}">
                        <%@ include file="_snapshot_credit_card_total_for_currency.jsp" %>
                    </c:if>
                    <c:set var="currentCurrency" value="${entity.currencyUnit}"/>
                    <%@ include file="_snapshot_credit_card_line_item.jsp" %>
                </c:forEach>
                <c:if test="${snapshot.creditCardAccounts.size() gt 1}">
                    <%@ include file="_snapshot_credit_card_total_for_currency.jsp" %>
                </c:if>
            </c:when>
        </c:choose>
    </div>
</div>
