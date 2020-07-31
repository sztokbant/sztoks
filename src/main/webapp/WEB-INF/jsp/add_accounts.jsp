<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Add Accounts to Snapshot</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
</head>

<body>

<%@ include file="_header.jsp" %>

<div class="container">
    <form:form method="POST" action="${contextPath}/addaccounts/${snapshot.id}" modelAttribute="addAccountsForm" class="form-signin">
        <h4 class="form-signin-heading">Add Accounts to Snapshot</h4>

        <spring:bind path="accounts">
            <div class="form-group ${status.error ? 'has-error' : ''}">
                <h5>Assets</h5>

                <c:choose>
                    <c:when test="${empty assets}">
                        <div>No assets available.</div>
                        <br/>
                    </c:when>
                    <c:otherwise>
                        <ul>
                            <c:forEach var="account" items="${assets}">
                                <div>
                                    <form:checkbox path="accounts" value="${account.id}" id="checkbox_${account.id}"/>
                                    <label for="checkbox_${account.id}">${account.name}</label>
                                </div>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>

                <h5>Liabilities</h5>

                <c:choose>
                    <c:when test="${empty liabilities}">
                        <div>No liabilities available.</div>
                    </c:when>
                    <c:otherwise>
                        <ul>
                            <c:forEach var="account" items="${liabilities}">
                                <div>
                                    <form:checkbox path="accounts" value="${account.id}" id="checkbox_${account.id}"/>
                                    <label for="checkbox_${account.id}">${account.name}</label>
                                </div>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
                <form:errors path="accounts"/>
            </div>
        </spring:bind>

        <c:choose>
            <c:when test="${not empty assets || not empty liabilities}">
                <button class="btn btn-lg btn-primary btn-block" type="submit">Submit</button>
            </c:when>
        </c:choose>
        <div class="text-center"><a href="/snapshot/${snapshot.id}">Back</a></div>
    </form:form>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
</body>
</html>
