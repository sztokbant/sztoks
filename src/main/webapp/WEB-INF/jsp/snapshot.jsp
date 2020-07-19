<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Snapshot ${snapshot.date}</title>
    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%@ include file="_header.jsp" %>

<div class="container">

    <div class="text-center">
        <h4>Snapshot ${snapshot.date}</h4>
        <%@ include file="_snapshot_net_worth.jsp" %>
    </div>

    <hr/>

    <h5>Assets</h5>
    <b>Total</b>
    <c:forEach items="${snapshot.assetsBalance}" var="entry">
        <div>
            ${entry.key}: ${entry.value}<br>
        </div>
    </c:forEach>

    <br/>

    <c:forEach var="account" items="${assetAccounts}">
        <div>
            ${account.name}: ${account.balanceCurrencyUnit} ${account.balanceAmount}
            <%@ include file="_update_snapshot_account_balance.jsp" %>
        </div>
    </c:forEach>

    <hr/>

    <h5>Liabilities</h5>
    <b>Total</b>
    <c:forEach items="${snapshot.liabilitiesBalance}" var="entry">
        <div>
            ${entry.key}: ${entry.value}<br>
        </div>
    </c:forEach>

    <br/>

    <c:forEach var="account" items="${liabilityAccounts}">
        <div>
            ${account.name}: ${account.balanceCurrencyUnit} ${account.balanceAmount}
            <%@ include file="_update_snapshot_account_balance.jsp" %>
        </div>
    </c:forEach>

    <hr/>

    <div class="text-center">
        <a class="btn btn-default" href="/workspace/${workspace.id}">Back</a>
    </div>

</div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
</body>
</html>
