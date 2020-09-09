<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>${snapshot.date}</title>
    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script>
    <script src="${contextPath}/resources/js/ajax_field_update.js"></script>
    <script src="${contextPath}/resources/js/success_callback_functions.js"></script>
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
</head>
<body>

<%@ include file="_header.jsp" %>

<div class="container">

    <div class="text-center">
        <h4>${snapshot.date}</h4>
        <%@ include file="_snapshot_net_worth.jsp" %>
    </div>

    <hr/>

    <div class="text-center"><a href="${contextPath}/addaccounts/${snapshot.id}">Add Accounts to Snapshot</a></div>

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
                        <div class="col"><i>Simple Assets</i></div>
                        <div class="col">&nbsp;</div>
                        <div class="col">&nbsp;</div>
                        <div class="col">&nbsp;</div>
                        <div class="col">Balance</div>
                    </div>
                    <br/>
                    <c:forEach var="account" items="${simpleAssetAccounts}">
                        <div class="row">
                            <%@ include file="_snapshot_simple_asset_line_item.jsp" %>
                        </div>
                    </c:forEach>
                    <br/>
                </c:when>
            </c:choose>

            <c:choose>
                <c:when test="${not empty investmentAccounts}">
                    <div class="row">
                        <div class="col"><i>Investments</i></div>
                        <div class="col">Shares</div>
                        <div class="col">Original Share Value</div>
                        <div class="col">Current Share Value</div>
                        <div class="col">Profit</div>
                        <div class="col">Balance</div>
                    </div>
                    <br/>
                    <c:forEach var="account" items="${investmentAccounts}">
                        <div class="row">
                            <%@ include file="_snapshot_investment_line_item.jsp" %>
                        </div>
                    </c:forEach>
                    <br/>
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
                        <div class="col"><i>Simple Liabilities</i></div>
                        <div class="col">&nbsp;</div>
                        <div class="col">&nbsp;</div>
                        <div class="col">&nbsp;</div>
                        <div class="col">Balance</div>
                    </div>
                    <br/>
                    <c:forEach var="account" items="${simpleLiabilityAccounts}">
                        <div class="row">
                            <%@ include file="_snapshot_simple_liability_line_item.jsp" %>
                        </div>
                    </c:forEach>
                    <br/>
                </c:when>
            </c:choose>

            <c:choose>
                <c:when test="${not empty creditCardAccounts}">
                    <div class="row">
                        <div class="col"><i>Credit Cards</i></div>
                        <div class="col">Total Credit</div>
                        <div class="col">Available Credit</div>
                        <div class="col">Used Credit</div>
                        <div class="col">Balance</div>
                    </div>
                    <br/>
                    <c:forEach var="account" items="${creditCardAccounts}">
                        <div class="row">
                            <%@ include file="_snapshot_credit_card_line_item.jsp" %>
                        </div>
                    </c:forEach>
                    <br/>
                </c:when>
            </c:choose>
        </div>
    </div>

    <div class="text-center"><a href="/">Back</a></div>
</div>
</body>
</html>
