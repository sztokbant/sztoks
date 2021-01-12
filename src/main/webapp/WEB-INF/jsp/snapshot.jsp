<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>${snapshot.name}</title>
    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script>
    <script src="${contextPath}/resources/js/ajax_field_update.js"></script>
    <script src="${contextPath}/resources/js/update_snapshot_credit_card_totals.js"></script>
    <script src="${contextPath}/resources/js/success_callback_functions.js"></script>
    <script src="${contextPath}/resources/js/remove_account_from_snapshot.js"></script>
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/snapshot.css" rel="stylesheet">
</head>
<body>

<%@ include file="_header.jsp" %>

<div class="full-width">
    <div class="row">
        <div class="col">
            <div class="left-button">
                <c:if test="${snapshot.previousId ne null}">
                    <a class="btn btn-myequity" href="${contextPath}/snapshot/${snapshot.previousId}">&#x23EA;&nbsp;&nbsp;${snapshot.previousName}</a>
                </c:if>
            </div>
        </div>

        <div class="col">
            <div class="text-center snapshot-header">
                <%@ include file="_snapshot_name.jsp" %>

                <table class="full-width">
                    <tr class="border-1px bg-light-yellow">
                        <td class="align-left-p7 valign-top"><b>NET WORTH</b></td>
                        <td class="align-right-p7 valign-top"><%@ include file="_snapshot_net_worth.jsp" %></td>
                    </tr>
                </table>
            </div>
        </div>

        <div class="col">
            <div class="right-button">
                <c:if test="${snapshot.nextId ne null}">
                    <a class="btn btn-myequity"
                       href="${contextPath}/snapshot/${snapshot.nextId}">${snapshot.nextName}&nbsp;&#x23E9;</a>
                </c:if>
            </div>
        </div>
    </div>
</div>

<div class="full-width">
    <div class="row">
        <div class="col" style="background: lightpink;">
            <div class="row border-1px-bottom">
                <div class="col col-cell text-center"><b>ASSETS</b></div>
            </div>
            <c:choose>
                <c:when test="${not empty snapshot.assetsBalance}">
                    <c:forEach items="${snapshot.assetsBalance}" var="entry">
                        <div class="row border-1px-bottom bg-light-yellow">
                            <div class="col col-cell col-account-name">TOTAL ${entry.key}</div>
                            <div class="col col-cell">&nbsp;</div>
                            <div class="col col-cell">&nbsp;</div>
                            <div class="col col-cell">&nbsp;</div>
                            <div class="col col-cell">&nbsp;</div>
                            <div class="col col-cell align-right"><b><span
                                    id="total_ASSET_${entry.key}">${entry.value}</span></b></div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="row border-1px-bottom bg-light-yellow">
                        <div class="col col-cell col-account-name">TOTAL</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell align-right"><b>0.00</b></div>
                    </div>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${not empty snapshot.simpleAssetAccounts}">
                    <div class="row border-1px-bottom">
                        <div class="col col-cell col-title"><i>Simple Assets</i></div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">Balance</div>
                    </div>
                    <c:forEach var="account" items="${snapshot.simpleAssetAccounts}">
                        <%@ include file="_snapshot_simple_asset_line_item.jsp" %>
                    </c:forEach>
                </c:when>
            </c:choose>

            <c:choose>
                <c:when test="${not empty snapshot.receivableAccounts}">
                    <div class="row border-1px-bottom">
                        <div class="col col-cell col-title"><i>Receivables</i></div>
                        <div class="col col-cell col-title">Due Date</div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">Balance</div>
                    </div>
                    <c:forEach var="account" items="${snapshot.receivableAccounts}">
                        <%@ include file="_snapshot_receivable_line_item.jsp" %>
                    </c:forEach>
                </c:when>
            </c:choose>

            <c:choose>
                <c:when test="${not empty snapshot.investmentAccounts}">
                    <div class="row border-1px-bottom">
                        <div class="col col-cell col-title"><i>Investments</i></div>
                        <div class="col col-cell col-title">Shares</div>
                        <div class="col col-cell col-title">Original Share Value</div>
                        <div class="col col-cell col-title">Current Share Value</div>
                        <div class="col col-cell col-title">Profit</div>
                        <div class="col col-cell col-title">Balance</div>
                    </div>
                    <c:forEach var="account" items="${snapshot.investmentAccounts}">
                        <%@ include file="_snapshot_investment_line_item.jsp" %>
                    </c:forEach>
                </c:when>
            </c:choose>
        </div>
        <div class="col" style="background: lightblue;">
            <div class="row border-1px-bottom">
                <div class="col col-cell text-center"><b>LIABILITIES</b></div>
            </div>

            <c:choose>
                <c:when test="${not empty snapshot.liabilitiesBalance}">
                    <c:forEach items="${snapshot.liabilitiesBalance}" var="entry">
                        <div class="row border-1px-bottom bg-light-yellow">
                            <div class="col col-cell col-account-name">TOTAL ${entry.key}</div>
                            <div class="col col-cell">&nbsp;</div>
                            <div class="col col-cell">&nbsp;</div>
                            <div class="col col-cell">&nbsp;</div>
                            <div class="col col-cell">&nbsp;</div>
                            <div class="col col-cell">&nbsp;</div>
                            <div class="col col-cell align-right"><b><span id="total_LIABILITY_${entry.key}">${entry.value}</span></b>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="row border-1px-bottom bg-light-yellow">
                        <div class="col col-cell col-account-name">TOTAL</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell">&nbsp;</div>
                        <div class="col col-cell align-right"><b>0.00</b></div>
                    </div>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${not empty snapshot.simpleLiabilityAccounts}">
                    <div class="row border-1px-bottom">
                        <div class="col col-cell col-title"><i>Simple Liabilities</i></div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">Balance</div>
                    </div>
                    <c:forEach var="account" items="${snapshot.simpleLiabilityAccounts}">
                        <%@ include file="_snapshot_simple_liability_line_item.jsp" %>
                    </c:forEach>
                </c:when>
            </c:choose>

            <c:choose>
                <c:when test="${not empty snapshot.payableAccounts}">
                    <div class="row border-1px-bottom">
                        <div class="col col-cell col-title"><i>Payables</i></div>
                        <div class="col col-cell col-title">Due Date</div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">&nbsp;</div>
                        <div class="col col-cell col-title">Balance</div>
                    </div>
                    <c:forEach var="account" items="${snapshot.payableAccounts}">
                        <%@ include file="_snapshot_payable_line_item.jsp" %>
                    </c:forEach>
                </c:when>
            </c:choose>

            <c:choose>
                <c:when test="${not empty snapshot.creditCardAccounts}">
                    <div class="row border-1px-bottom">
                        <div class="col col-cell col-title"><i>Credit Cards</i></div>
                        <div class="col col-cell col-title">Total Credit</div>
                        <div class="col col-cell col-title">Available Credit</div>
                        <div class="col col-cell col-title">Used Credit</div>
                        <div class="col col-cell col-title">Statement</div>
                        <div class="col col-cell col-title">Remaining Balance</div>
                        <div class="col col-cell col-title">Balance</div>
                    </div>

                    <c:set var="currentCurrency" value=""/>
                    <c:forEach var="account" items="${snapshot.creditCardAccounts}">
                        <c:if test="${currentCurrency ne '' && currentCurrency ne account.currencyUnit}">
                            <%@ include file="_snapshot_credit_card_total_for_currency.jsp" %>
                        </c:if>
                        <c:set var="currentCurrency" value="${account.currencyUnit}"/>
                        <%@ include file="_snapshot_credit_card_line_item.jsp" %>
                    </c:forEach>
                    <%@ include file="_snapshot_credit_card_total_for_currency.jsp" %>
                </c:when>
            </c:choose>
        </div>
    </div>

    <div class="navigation-buttons-padding-top">
        <a class="btn btn-myequity" href="${contextPath}/snapshot/addAccounts/${snapshot.id}">Add Accounts to
            Snapshot</a>
    </div>

    <div class="text-center"><a href="/">Back</a></div>
</div>

</body>
</html>
