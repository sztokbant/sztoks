<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>New Liability</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">

    <script src="${contextPath}/resources/js/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script></head>
</head>

<body>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<div class="full-width">
    <c:choose>
        <c:when test="${deviceType ne 'MOBILE'}">
            <div class="center-w640">
        </c:when>
    </c:choose>

    <div class="text-center page-title-${deviceType}">New Liability Account</div>

    <div class="text-center page-subtitle-${deviceType}">Snapshot: ${snapshotTitle}</div>

    <form:form method="POST" action="${contextPath}/snapshot/${snapshotId}/newAccount" modelAttribute="accountForm" class="form-signin">
        <form:input type="hidden" path="accountType" value="${accountType}"/>

        <spring:bind path="name">
            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    <label for="name">Account Name</label>
                </div>
                <div class="col">
                    <form:input type="text" id="name" path="name" class="form-control form-entry-${deviceType}" placeholder="Account Name"
                                autofocus="true"/>
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:errors path="name"/>
                    </div>
                </div>
            </div>
        </spring:bind>

        <spring:bind path="subtypeName">
            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    Liability Type
                </div>
                <div class="col">
                    <div>
                        <form:radiobutton path="subtypeName" value="SimpleLiabilityAccount" id="simpleLiabilityRadio"
                                          class="radio-sztoks-${deviceType}"/>
                        <label for="simpleLiabilityRadio" class="form-entry-${deviceType}">Simple Liability</label>
                    </div>
                    <div>
                        <form:radiobutton path="subtypeName" value="PayableAccount" id="payableRadio"
                                          class="radio-sztoks-${deviceType}"/>
                        <label for="payableRadio" class="form-entry-${deviceType}">Payable</label>
                    </div>
                    <div>
                        <form:radiobutton path="subtypeName" value="SharedBillPayableAccount" id="sharedBillPayableRadio"
                                          class="radio-sztoks-${deviceType}"/>
                        <label for="sharedBillPayableRadio" class="form-entry-${deviceType}">Shared Bill Payable</label>
                    </div>
                    <div>
                        <form:radiobutton path="subtypeName" value="CreditCardAccount" id="creditCardRadio"
                                          class="radio-sztoks-${deviceType}"/>
                        <label for="creditCardRadio" class="form-entry-${deviceType}">Credit Card</label>
                    </div>
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:errors path="subtypeName"/>
                    </div>
                </div>
            </div>
        </spring:bind>

        <spring:bind path="currencyUnit">
            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    <label for="currencyUnit">Currency</label>
                </div>
                <%@ include file="/WEB-INF/jsp/_currency_select.jsp" %>
            </div>
        </spring:bind>

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
