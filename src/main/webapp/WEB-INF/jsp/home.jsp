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
</head>
<body>

<%@ include file="_header.jsp" %>

<div class="container">

    <div class="text-center"><h4>Snapshots</h4></div>

    <c:choose>
        <c:when test="${empty snapshots}">
            No snapshots.
        </c:when>
        <c:otherwise>
            <c:forEach var="snapshot" items="${snapshots}">
                <div>
                    <a href="/snapshot/${snapshot.id}">${snapshot.date}</a>
                </div>
                <%@ include file="_snapshot_net_worth.jsp" %>
                <br/>
            </c:forEach>
        </c:otherwise>
    </c:choose>

    <hr/>

    <h4 class="text-center">Accounts</h4>

    <div class="text-center"><a href="${contextPath}/newaccount">Create New Account</a></div>

    <div class="row">
        <div class="col" style="background: lightpink;">
            <h5 class="text-center">Assets</h5>
            <c:choose>
                <c:when test="${empty assetAccounts}">
                    <div>No assets.</div>
                </c:when>
                <c:otherwise>
                    <ul>
                        <c:forEach var="account" items="${assetAccounts}">
                            <%@ include file="_account.jsp" %>
                        </c:forEach>
                    </ul>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="col" style="background: lightblue;">
            <h5 class="text-center">Liabilities</h5>
            <c:choose>
                <c:when test="${empty liabilityAccounts}">
                    <div>No liabilities.</div>
                </c:when>
                <c:otherwise>
                    <ul>
                        <c:forEach var="account" items="${liabilityAccounts}">
                            <%@ include file="_account.jsp" %>
                        </c:forEach>
                    </ul>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
</body>
</html>
