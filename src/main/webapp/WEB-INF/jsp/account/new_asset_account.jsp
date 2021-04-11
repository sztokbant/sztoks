<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>New Asset</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
</head>

<body>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<div class="full-width">
    <div class="center-w640">
        <form:form method="POST" action="${contextPath}/snapshot/${snapshotId}/newAccount" modelAttribute="accountForm" class="form-signin">
            <h4 class="form-signin-heading">New Asset</h4>

            <form:input type="hidden" path="accountType" value="${accountType}"/>

            <spring:bind path="name">
                <div class="row form-group">
                    <div class="col col-form-label">
                        <label for="name">Account Name</label>
                    </div>
                    <div class="col">
                        <div class="${status.error ? 'has-error' : ''}">
                            <form:input type="text" id="name" path="name" class="form-control" placeholder="Account Name"
                                        autofocus="true"></form:input>
                            <form:errors path="name"/>
                        </div>
                    </div>
                </div>
            </spring:bind>

            <spring:bind path="subtypeName">
                <div class="row form-group">
                    <div class="col col-form-label">
                        Asset Type
                    </div>
                    <div class="col">
                        <div class="${status.error ? 'has-error' : ''}">
                            <div>
                                <form:radiobutton path="subtypeName" value="SimpleAssetAccount" id="simpleAssetRadio"/>
                                <label for="simpleAssetRadio">Simple Asset</label>
                            </div>
                            <div>
                                <form:radiobutton path="subtypeName" value="ReceivableAccount" id="receivableRadio"/>
                                <label for="receivableRadio">Receivable</label>
                            </div>
                            <div>
                                <form:radiobutton path="subtypeName" value="InvestmentAccount" id="investmentRadio"/>
                                <label for="investmentRadio">Investment</label>
                            </div>
                            <form:errors path="subtypeName"/>
                        </div>
                    </div>
                </div>
            </spring:bind>

            <spring:bind path="currencyUnit">
                <div class="row form-group">
                    <div class="col col-form-label">
                        <label for="currencyUnit">Currency</label>
                    </div>
                    <%@ include file="/WEB-INF/jsp/_currency_select.jsp" %>
                </div>
            </spring:bind>

            <button class="btn btn-lg btn-primary btn-block" type="submit">Submit</button>
            <div class="text-center"><a href="${contextPath}/snapshot/${snapshotId}">Back</a></div>
        </form:form>
    </div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
</body>
</html>

