<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>My Equity</title>
    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script>
    <script src="${contextPath}/resources/js/ajax_field_update.js"></script>
    <script src="${contextPath}/resources/js/success_callback_functions.js"></script>
    <script src="${contextPath}/resources/js/delete_account.js"></script>
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/home.css" rel="stylesheet">
</head>
<body>

<%@ include file="_header.jsp" %>

<div class="full-width">
    <div class="text-center"><h4>Snapshots</h4></div>

    <div class="navigation-buttons-padding-bottom">
        <form method="post" action="${contextPath}/snapshot/new">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <input type="submit" class="btn btn-myequity" value="&#x2795;  Snapshot"/>
        </form>
    </div>

    <div class="center-w640">
        <table class="full-width">
            <tr class="border-1px">
                <th class="snapshots-title">Snapshot Name</th>
                <th class="snapshots-title">Net Worth</th>
            </tr>
            <c:forEach var="snapshot" items="${snapshots}">
                <tr class="border-1px bg-light-yellow">
                    <td class="valign-top">
                        <c:if test="${snapshots.size() gt 1}">
                            <div class="delete-icon">
                                <form method="post" action="${contextPath}/snapshot/delete/${snapshot.id}"
                                      onsubmit="return confirm('Are you sure you want to permanently delete snapshot \'${snapshot.name}\'?');">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                    <input type="submit" value="&#x26D4" class="transparent-button"/>
                                </form>
                            </div>
                        </c:if>
                        <a href="/snapshot/${snapshot.id}">${snapshot.name}</a>
                    </td>
                    <td class="align-right-p7"><%@ include file="_snapshot_net_worth.jsp" %></td>
                </tr>
            </c:forEach>
        </table>
    </div>

    <hr/>

    <h4 class="text-center">Accounts</h4>

    <div class="navigation-buttons-padding-bottom">
        <a class="btn btn-myequity" href="${contextPath}/newaccount">&#x2795;&nbsp; Account</a>
    </div>

    <div class="center-w640">
        <div class="row">
            <div class="col" style="background: lightpink;">
                <h5 class="text-center">Assets</h5>
                <c:choose>
                    <c:when test="${empty assetAccounts}">
                        <div>No assets.</div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="account" items="${assetAccounts}">
                            <div id="account_row_${account.id}">
                                <%@ include file="_asset_account.jsp" %>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                <br/>
            </div>
            <div class="col" style="background: lightblue;">
                <h5 class="text-center">Liabilities</h5>
                <c:choose>
                    <c:when test="${empty liabilityAccounts}">
                        <div>No liabilities.</div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="account" items="${liabilityAccounts}">
                            <div id="account_row_${account.id}">
                                <%@ include file="_liability_account.jsp" %>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                <br/>
            </div>
        </div>
    </div>
</div>

</body>
</html>
