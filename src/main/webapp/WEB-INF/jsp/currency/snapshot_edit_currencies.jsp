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


    <div class="text-center page-title-${deviceType}">Edit Currencies</div>

    <div class="text-center page-subtitle-${deviceType}">Snapshot: ${snapshotTitle}</div>

    <div class="text-center paragraph-${deviceType}"><a href="${contextPath}/snapshot/${snapshotId}/newCurrency">Add New</a></div>

    <form:form method="POST" action="${contextPath}/snapshot/${snapshotId}/currencies" modelAttribute="editCurrenciesForm" class="form-signin">
        <c:forEach var="entry" items="${editCurrenciesForm.currencyConversionRates}">
            <spring:bind path="currencyConversionRates['${entry.key}']">
                <div class="form-group ${status.error ? 'has-error' : ''}">
                    <div class="row form-group">
                        <div class="col col-form-label-${deviceType}">
                            <label for="${entry.key}">1 ${baseCurrency} to <b>${entry.key}</b></label>
                        </div>
                        <div class="col">
                            <form:input type="number" min="0" step="0.0001" id="${entry.key}"
                                        path="currencyConversionRates['${entry.key}']"
                                        class="form-control form-entry-${deviceType}" placeholder="Conversion Rate"
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
