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
    <script src="${contextPath}/resources/js/success_callback_functions.js"></script>
    <script src="${contextPath}/resources/js/remove_account_from_snapshot.js"></script>
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/snapshot.css" rel="stylesheet">
</head>
<body>

<%@ include file="_header.jsp" %>

<div class="text-center">
    <%@ include file="_snapshot_name.jsp" %>
    <%@ include file="_snapshot_net_worth.jsp" %>
</div>

<div class="full-width">
    <div class="row">
        <div class="col" style="background: lightpink;">
            <div class="text-center">
                <h5>Assets</h5>
                <b>Total</b>
                <c:choose>
                    <c:when test="${not empty snapshot.assetsBalance}">
                        <c:forEach items="${snapshot.assetsBalance}" var="entry">
                            <div>
                                ${entry.key} <span id="total_ASSET_${entry.key}">${entry.value}</span><br>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        0.00
                    </c:otherwise>
                </c:choose>
            </div>

            <br/>

            <c:choose>
                <c:when test="${not empty simpleAssetAccounts}">
                    <div class="row">
                        <div class="col col-title"><i>Simple Assets</i></div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">Balance</div>
                    </div>
                    <c:forEach var="account" items="${simpleAssetAccounts}">
                        <%@ include file="_snapshot_simple_asset_line_item.jsp" %>
                    </c:forEach>
                </c:when>
            </c:choose>

            <c:choose>
                <c:when test="${not empty receivableAccounts}">
                    <div class="row">
                        <div class="col col-title"><i>Receivables</i></div>
                        <div class="col col-title">Due Date</div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">Balance</div>
                    </div>
                    <c:forEach var="account" items="${receivableAccounts}">
                        <%@ include file="_snapshot_receivable_line_item.jsp" %>
                    </c:forEach>
                </c:when>
            </c:choose>

            <c:choose>
                <c:when test="${not empty investmentAccounts}">
                    <div class="row">
                        <div class="col col-title"><i>Investments</i></div>
                        <div class="col col-title">Shares</div>
                        <div class="col col-title">Original Share Value</div>
                        <div class="col col-title">Current Share Value</div>
                        <div class="col col-title">Profit</div>
                        <div class="col col-title">Balance</div>
                    </div>
                    <c:forEach var="account" items="${investmentAccounts}">
                        <%@ include file="_snapshot_investment_line_item.jsp" %>
                    </c:forEach>
                </c:when>
            </c:choose>
        </div>
        <div class="col" style="background: lightblue;">
            <div class="text-center">
                <h5>Liabilities</h5>
                <b>Total</b>
                <c:choose>
                    <c:when test="${not empty snapshot.liabilitiesBalance}">
                        <c:forEach items="${snapshot.liabilitiesBalance}" var="entry">
                            <div>
                                ${entry.key} <span id="total_LIABILITY_${entry.key}">${entry.value}</span><br>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        0.00
                    </c:otherwise>
                </c:choose>
            </div>

            <br/>

            <c:choose>
                <c:when test="${not empty simpleLiabilityAccounts}">
                    <div class="row">
                        <div class="col col-title"><i>Simple Liabilities</i></div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">Balance</div>
                    </div>
                    <c:forEach var="account" items="${simpleLiabilityAccounts}">
                        <%@ include file="_snapshot_simple_liability_line_item.jsp" %>
                    </c:forEach>
                </c:when>
            </c:choose>

            <c:choose>
                <c:when test="${not empty payableAccounts}">
                    <div class="row">
                        <div class="col col-title"><i>Payables</i></div>
                        <div class="col col-title">Due Date</div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">&nbsp;</div>
                        <div class="col col-title">Balance</div>
                    </div>
                    <c:forEach var="account" items="${payableAccounts}">
                        <%@ include file="_snapshot_payable_line_item.jsp" %>
                    </c:forEach>
                </c:when>
            </c:choose>

            <c:choose>
                <c:when test="${not empty creditCardAccounts}">
                    <div class="row">
                        <div class="col col-title"><i>Credit Cards</i></div>
                        <div class="col col-title">Total Credit</div>
                        <div class="col col-title">Available Credit</div>
                        <div class="col col-title">Used Credit</div>
                        <div class="col col-title">Statement</div>
                        <div class="col col-title">Remaining Balance</div>
                        <div class="col col-title">Balance</div>
                    </div>
                    <c:forEach var="account" items="${creditCardAccounts}">
                        <%@ include file="_snapshot_credit_card_line_item.jsp" %>
                    </c:forEach>
                </c:when>
            </c:choose>
        </div>
    </div>

    <br/>

    <div class="text-center">
        <a class="btn btn-myequity" href="${contextPath}/snapshot/addAccounts/${snapshot.id}">Add Accounts to Snapshot</a>
    </div>

    <div class="text-center"><a href="/">Back</a></div>
</div>

</body>
</html>
