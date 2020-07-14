<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>${workspace.name}</title>
    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%@ include file="_header.jsp" %>

<div class="container">

    <div class="text-center">
        <h4>${workspace.name}</h4>
        <%@ include file="_workspace_net_worth.jsp" %>
    </div>

    <hr/>

    <h5>Snapshots</h5>
    <c:choose>
        <c:when test="${empty workspace.snapshots}">
            No saved snapshots.
        </c:when>
        <c:otherwise>
            <c:forEach var="snapshot" items="${workspace.snapshots}">
                <div>
                    <a href="/snapshot/${snapshot.id}">${snapshot.date}</a>
                    <%@ include file="_snapshot_net_worth.jsp" %>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>

    <hr/>

    <h5>Assets</h5>
    <b>Total</b>
    <c:forEach items="${workspace.assetsTotal}" var="entry">
        <div>
            ${entry.key}: ${entry.value}<br>
        </div>
    </c:forEach>

    <br/>

    <c:forEach var="account" items="${workspace.accounts[assetMapKey]}">
        <div>
            ${account.name}: ${account.balance}
            <%@ include file="_update_workspace_account_balance.jsp" %>
        </div>
    </c:forEach>

    <hr/>

    <h5>Liabilities</h5>
    <b>Total</b>
    <c:forEach items="${workspace.liabilitiesTotal}" var="entry">
        <div>
            ${entry.key}: ${entry.value}<br>
        </div>
    </c:forEach>

    <br/>

    <c:forEach var="account" items="${workspace.accounts[liabilityMapKey]}">
        <div>
            ${account.name}: ${account.balance}
            <%@ include file="_update_workspace_account_balance.jsp" %>
        </div>
    </c:forEach>

    <hr/>

    <div class="text-center">
        <a class="btn btn-default" href="/">Back</a>
    </div>
</div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
</body>
</html>
