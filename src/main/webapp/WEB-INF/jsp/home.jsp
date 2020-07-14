<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>My Equity</title>
    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%@ include file="_header.jsp" %>

<div class="container">

    <div class="text-center">
        <h4>Workspaces</h4>
    </div>

    <c:choose>
        <c:when test="${empty user.workspaces}">
            No workspaces.
        </c:when>
        <c:otherwise>
            <c:forEach var="workspace" items="${user.workspaces}">
                <div>
                    <a href="/workspace/${workspace.id}">${workspace.name}</a>
                </div>
                <%@ include file="_workspace_net_worth.jsp" %>
                <br/>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
</body>
</html>
