<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Create New Account</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
</head>

<body>

<%@ include file="_header.jsp" %>

<div class="container">
    <form:form method="POST" action="${contextPath}/newaccount" modelAttribute="accountForm" class="form-signin">
        <h4 class="form-signin-heading">Create New Account</h4>

        <spring:bind path="name">
            <div class="form-group ${status.error ? 'has-error' : ''}">
                <form:input type="text" path="name" class="form-control" placeholder="Account Name"
                            autofocus="true"></form:input>
                <form:errors path="name"/>
            </div>
        </spring:bind>

        <spring:bind path="accountType">
            <div class="form-group ${status.error ? 'has-error' : ''}">
                <div>
                    <form:radiobutton path="accountType" value="ASSET" id="assetRadio" checked="checked"/>
                    <label for="assetRadio">Asset</label>
                </div>
                <div>
                    <form:radiobutton path="accountType" value="LIABILITY" id="liabilityRadio"/>
                    <label for="liabilityRadio">Liability</label>
                </div>
                <form:errors path="accountType"/>
            </div>
        </spring:bind>

        <spring:bind path="currencyUnit">
            <div class="form-group ${status.error ? 'has-error' : ''}">
                <form:input type="text" path="currencyUnit" class="form-control" placeholder="Currency"
                            autofocus="true"></form:input>
                <form:errors path="currencyUnit"/>
            </div>
        </spring:bind>

        <button class="btn btn-lg btn-primary btn-block" type="submit">Submit</button>
        <div class="text-center"><a href="${contextPath}/">Back</a></div>
    </form:form>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
</body>
</html>
