<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Add New Currency</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
</head>

<body>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<div class="full-width">
    <div class="center-w640">
        <form:form method="POST" action="${contextPath}/snapshot/${snapshotId}/newCurrency" modelAttribute="newCurrencyForm" class="form-signin">
            <h4 class="form-signin-heading">Add New Currency</h4>

            <div class="row form-group">
                <div class="col col-form-label">
                    <label for="currencyUnit">Currency</label>
                </div>
                <div class="col">
                    <spring:bind path="currencyUnit">
                        <div class="${status.error ? 'has-error' : ''}">
                            <form:input type="text" id="currencyUnit" path="currencyUnit" class="form-control" placeholder="Currency"
                                        autofocus="true"></form:input>
                            <form:errors path="currencyUnit"/>
                        </div>
                    </spring:bind>
                </div>
            </div>

            <div class="row form-group">
                <div class="col col-form-label">
                    <label for="conversionRate">Conversion Rate</label>
                </div>
                <div class="col">
                    <spring:bind path="conversionRate">
                        <div class="form-group ${status.error ? 'has-error' : ''}">
                            <form:input type="number" min="0" step="0.0001" id="conversionRate" path="conversionRate" class="form-control" placeholder="Conversion Rate"
                                        autofocus="true"></form:input>
                            <form:errors path="conversionRate"/>
                        </div>
                    </spring:bind>
                </div>
            </div>

            <button class="btn btn-lg btn-primary btn-block" type="submit">Submit</button>
            <div class="text-center"><a href="${contextPath}/snapshot/${snapshotId}/currencies">Back</a></div>
        </form:form>
    </div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
</body>
</html>
