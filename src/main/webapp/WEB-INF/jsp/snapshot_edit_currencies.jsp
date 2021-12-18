<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Edit Snapshot Currencies</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
</head>

<body>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<div class="full-width">
    <c:choose>
        <c:when test="${deviceType ne 'MOBILE'}">
            <div class="center-w640">
        </c:when>
    </c:choose>

    <form:form method="POST" action="${contextPath}/snapshot/${snapshotId}/currencies" modelAttribute="editCurrenciesForm" class="form-signin">
        <div class="row">
            <div class="col">
                <h4 class="form-signin-heading">Edit Snapshot Currencies</h4>
            </div>
            <div class="col align-right-p7">
                <a href="${contextPath}/snapshot/${snapshotId}/newCurrency">Add New</a>
            </div>
        </div>

        <c:forEach var="entry" items="${editCurrenciesForm.currencyConversionRates}">
            <spring:bind path="currencyConversionRates['${entry.key}']">
                <div class="form-group ${status.error ? 'has-error' : ''}">
                    <div class="row form-group">
                        <div class="col">
                            <label for="${entry.key}">1 ${baseCurrency} to <span class="col-form-label-${deviceType}">${entry.key}</span></label>
                        </div>
                        <div class="col">
                            <form:input type="number" min="0" step="0.0001" id="${entry.key}" path="currencyConversionRates['${entry.key}']" class="form-control" placeholder="Conversion Rate"
                                        autofocus="true" value="${entry.value}"></form:input>
                            <form:errors path="currencyConversionRates['${entry.key}']"/>
                        </div>
                    </div>
                </div>
            </spring:bind>
        </c:forEach>

        <%@ include file="/WEB-INF/jsp/_default_submit_button.jsp" %>
    </form:form>

    <c:choose>
        <c:when test="${deviceType ne 'MOBILE'}">
            </div>
        </c:when>
    </c:choose>
</div>

<%@ include file="/WEB-INF/jsp/_footer.jsp" %>

</body>
</html>
