<div class="row">
    <div class="col col-section asset-accounts">
        <div class="row border-1px-bottom">
            <div class="col col-cell-${deviceType} text-center"><b>ASSETS</b></div>
        </div>

        <div class="row border-1px-bottom bg-light-yellow">
            <div class="col col-cell-${deviceType} col-account-name">ASSETS TOTAL</div>
            <div class="col col-cell-${deviceType} align-right">
                <b><span id="total_ASSET">${snapshot.assetsTotal}</span></b>
            </div>
        </div>

        <c:choose>
            <c:when test="${not empty snapshot.simpleAssetAccounts}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell-${deviceType} col-title col-account-name align-left"><i>Simple Assets</i></div>
                    <div class="col col-cell-${deviceType} col-title short-${deviceType}">&nbsp;</div>
                    <div class="col col-cell-${deviceType} col-title width-total-${deviceType}">Future Tithing</div>
                    <div class="col col-cell-${deviceType} col-title align-right">Balance</div>
                </div>
                <c:set var="count" value="0"/>
                <c:forEach var="entity" items="${snapshot.simpleAssetAccounts}">
                    <%@ include file="_zebra_asset.jsp" %>
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
                    <div class="col col-cell-${deviceType} col-title col-account-name align-left"><i>Gift Certificates</i></div>
                    <div class="col col-cell-${deviceType} col-title short-${deviceType}">&nbsp;</div>
                    <div class="col col-cell-${deviceType} col-title width-total-${deviceType}">Future Tithing</div>
                    <div class="col col-cell-${deviceType} col-title">Shares</div>
                    <div class="col col-cell-${deviceType} col-title">Current Share Value</div>
                    <div class="col col-cell-${deviceType} col-title align-right">Balance</div>
                </div>
                <c:set var="count" value="0"/>
                <c:forEach var="entity" items="${snapshot.giftCertificateAccounts}">
                    <%@ include file="_zebra_asset.jsp" %>
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
                    <div class="col col-cell-${deviceType} col-title col-account-name align-left"><i>Receivables</i></div>
                    <div class="col col-cell-${deviceType} col-title short-${deviceType}">&nbsp;</div>
                    <div class="col col-cell-${deviceType} col-title width-total-${deviceType}">Future Tithing</div>
                    <div class="col col-cell-${deviceType} col-title">Due Date</div>
                    <div class="col col-cell-${deviceType} col-title width-70px">Is paid?</div>
                    <div class="col col-cell-${deviceType} col-title">Amount</div>
                    <div class="col col-cell-${deviceType} col-title align-right">Balance</div>
                </div>
                <c:set var="count" value="0"/>
                <c:forEach var="entity" items="${snapshot.receivableAccounts}">
                    <%@ include file="_zebra_asset.jsp" %>
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
                    <div class="col col-cell-${deviceType} col-title col-account-name align-left"><i>Shared Bill Receivables</i></div>
                    <div class="col col-cell-${deviceType} col-title short-${deviceType}">&nbsp;</div>
                    <div class="col col-cell-${deviceType} col-title width-total-${deviceType}">Future Tithing</div>
                    <div class="col col-cell-${deviceType} col-title">Due Day</div>
                    <div class="col col-cell-${deviceType} col-title">Number of Partners</div>
                    <div class="col col-cell-${deviceType} col-title width-70px">Is paid?</div>
                    <div class="col col-cell-${deviceType} col-title">Bill Amount</div>
                    <div class="col col-cell-${deviceType} col-title align-right">Balance</div>
                </div>
                <c:set var="count" value="0"/>
                <c:forEach var="entity" items="${snapshot.sharedBillReceivableAccounts}">
                    <%@ include file="_zebra_asset.jsp" %>
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
                    <div class="col col-cell-${deviceType} col-title col-account-name align-left"><i>Investments</i></div>
                    <div class="col col-cell-${deviceType} col-title short-${deviceType}">&nbsp;</div>
                </div>
                <div class="row border-1px-bottom">
                    <div class="col col-cell-${deviceType} col-title width-total-${deviceType}">Future Tithing</div>
                    <div class="col col-cell-${deviceType} col-title">Shares</div>
                    <div class="col col-cell-${deviceType} col-title">Amount Invested</div>
                    <div class="col col-cell-${deviceType} col-title">Share Purchase Price</div>
                    <div class="col col-cell-${deviceType} col-title">Current Share Price</div>
                    <div class="col col-cell-${deviceType} col-title width-total-${deviceType} align-right">Balance, Unrealized Gain/Loss</div>
                </div>
                <c:set var="count" value="0"/>
                <c:forEach var="entity" items="${snapshot.investmentAccounts}">
                    <%@ include file="_zebra_asset.jsp" %>
                    <%@ include file="_snapshot_investment_line_item.jsp" %>
                </c:forEach>
                <c:if test="${snapshot.investmentAccounts.size() gt 1}">
                    <%@ include file="_snapshot_investments_total.jsp" %>
                </c:if>
            </c:when>
        </c:choose>
    </div>

<c:choose>
    <c:when test="${deviceType eq 'MOBILE'}">
        </div>
        <div class="row">
    </c:when>
</c:choose>

    <div class="col col-section liability-accounts">
        <div class="row border-1px-bottom">
            <div class="col col-cell-${deviceType} text-center"><b>LIABILITIES</b></div>
        </div>

        <div class="row border-1px-bottom bg-light-yellow">
            <div class="col col-cell-${deviceType} col-account-name">LIABILITIES TOTAL</div>
            <div class="col col-cell-${deviceType} align-right">
                <b><span id="total_LIABILITY">${snapshot.liabilitiesTotal}</span></b>
            </div>
        </div>

        <div class="row border-1px-bottom">
            <div class="col col-cell-${deviceType} col-title col-account-name align-left"><i>Tithing</i></div>
            <div class="col col-cell-${deviceType} col-title short-${deviceType}">&nbsp;</div>
            <div class="col col-cell-${deviceType} col-title align-right">Balance</div>
        </div>

        <%@ include file="_snapshot_tithing_line_item.jsp" %>
        <%@ include file="_snapshot_future_tithing_line_item.jsp" %>
        <%@ include file="_snapshot_tithing_total.jsp" %>

        <div class="row border-1px-bottom">
            <div class="col col-cell-${deviceType} col-title col-account-name align-left"><i>Simple Liabilities</i></div>
            <div class="col col-cell-${deviceType} col-title short-${deviceType}">&nbsp;</div>
            <div class="col col-cell-${deviceType} col-title align-right">Balance</div>
        </div>

        <c:choose>
            <c:when test="${not empty snapshot.simpleLiabilityAccounts}">
                <c:set var="count" value="0"/>
                <c:forEach var="entity" items="${snapshot.simpleLiabilityAccounts}">
                    <%@ include file="_zebra_liability.jsp" %>
                    <%@ include file="_snapshot_simple_liability_line_item.jsp" %>
                </c:forEach>
            </c:when>
        </c:choose>
        <c:if test="${snapshot.simpleLiabilityAccounts.size() gt 1}">
            <%@ include file="_snapshot_simple_liabilities_total.jsp" %>
        </c:if>

        <c:choose>
            <c:when test="${not empty snapshot.payableAccounts}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell-${deviceType} col-title col-account-name align-left"><i>Payables</i></div>
                    <div class="col col-cell-${deviceType} col-title short-${deviceType}">&nbsp;</div>
                    <div class="col col-cell-${deviceType} col-title">Due Date</div>
                    <div class="col col-cell-${deviceType} col-title width-70px">Is paid?</div>
                    <div class="col col-cell-${deviceType} col-title">Amount</div>
                    <div class="col col-cell-${deviceType} col-title align-right">Balance</div>
                </div>
                <c:set var="count" value="0"/>
                <c:forEach var="entity" items="${snapshot.payableAccounts}">
                    <%@ include file="_zebra_liability.jsp" %>
                    <%@ include file="_snapshot_payable_line_item.jsp" %>
                </c:forEach>
                <c:if test="${snapshot.payableAccounts.size() gt 1}">
                    <%@ include file="_snapshot_payables_total.jsp" %>
                </c:if>
            </c:when>
        </c:choose>

        <c:choose>
            <c:when test="${not empty snapshot.sharedBillPayableAccounts}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell-${deviceType} col-title col-account-name align-left"><i>Shared Bill Payables</i></div>
                    <div class="col col-cell-${deviceType} col-title short-${deviceType}">&nbsp;</div>
                    <div class="col col-cell-${deviceType} col-title">Due Day</div>
                    <div class="col col-cell-${deviceType} col-title">Number of Partners</div>
                    <div class="col col-cell-${deviceType} col-title width-70px">Is paid?</div>
                    <div class="col col-cell-${deviceType} col-title">Bill Amount</div>
                    <div class="col col-cell-${deviceType} col-title align-right">Balance</div>
                </div>
                <c:set var="count" value="0"/>
                <c:forEach var="entity" items="${snapshot.sharedBillPayableAccounts}">
                    <%@ include file="_zebra_liability.jsp" %>
                    <%@ include file="_snapshot_shared_bill_payable_line_item.jsp" %>
                </c:forEach>
                <c:if test="${snapshot.sharedBillPayableAccounts.size() gt 1}">
                    <%@ include file="_snapshot_shared_bill_payables_total.jsp" %>
                </c:if>
            </c:when>
        </c:choose>

        <c:choose>
            <c:when test="${not empty snapshot.creditCardAccounts}">
                <div class="row border-1px-bottom">
                    <div class="col col-cell-${deviceType} col-title col-account-name align-left"><i>Credit Cards</i></div>
                    <div class="col col-cell-${deviceType} col-title short-${deviceType}">&nbsp;</div>
                    <div class="col col-cell-${deviceType} col-title short-${deviceType}">&nbsp;</div>
                    <div class="col col-cell-${deviceType} col-title">Total Credit</div>
                    <div class="col col-cell-${deviceType} col-title">Available Credit</div>
                    <div class="col col-cell-${deviceType} col-title">Used Credit</div>
                    <div class="col col-cell-${deviceType} col-title">Statement</div>
                    <div class="col col-cell-${deviceType} col-title">Remaining Balance</div>
                    <div class="col col-cell-${deviceType} col-title align-right">Balance</div>
                </div>

                <c:set var="currentCurrency" value=""/>
                <c:set var="count" value="0"/>
                <c:forEach var="entity" items="${snapshot.creditCardAccounts}">
                    <c:if test="${currentCurrency ne '' && currentCurrency ne entity.currencyUnit}">
                        <c:set var="count" value="0"/>
                        <%@ include file="_snapshot_credit_card_total_for_currency.jsp" %>
                    </c:if>
                    <c:set var="currentCurrency" value="${entity.currencyUnit}"/>
                    <%@ include file="_zebra_liability.jsp" %>
                    <%@ include file="_snapshot_credit_card_line_item.jsp" %>
                </c:forEach>
                <c:if test="${snapshot.creditCardAccounts.size() gt 1}">
                    <%@ include file="_snapshot_credit_card_total_for_currency.jsp" %>
                </c:if>
            </c:when>
        </c:choose>
    </div>
</div>
