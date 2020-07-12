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
    <c:forEach var="accountToBalance" items="${snapshot.accountsByType[assetMapKey]}">
        <div>
            ${accountToBalance.key.name}: ${accountToBalance.value}
        </div>
    </c:forEach>

    <hr/>

    <h5>Liabilities</h5>
    <c:forEach var="accountToBalance" items="${snapshot.accountsByType[liabilityMapKey]}">
        <div>
            ${accountToBalance.key.name}: ${accountToBalance.value}
        </div>
    </c:forEach>

    <hr/>

    <div class="text-center">
        <a class="btn btn-default" href="/workspace/${snapshot.workspace.id}">Back</a>
    </div>
</div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
</body>
</html>
