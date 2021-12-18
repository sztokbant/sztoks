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
</head>

<body>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<div class="full-width">
    <c:choose>
        <c:when test="${deviceType ne 'MOBILE'}">
            <div class="center-w640">
        </c:when>
    </c:choose>

    <form:form method="POST" action="${contextPath}/snapshot/${snapshotId}/newAccount" modelAttribute="accountForm" class="form-signin">
        <h4 class="form-signin-heading">New Liability Account</h4>

        <form:input type="hidden" path="accountType" value="${accountType}"/>

        <spring:bind path="name">
            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
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
                <div class="col col-form-label-${deviceType}">
                    Liability Type
                </div>
                <div class="col">
                    <div class="${status.error ? 'has-error' : ''}">
                        <div>
                            <form:radiobutton path="subtypeName" value="SimpleLiabilityAccount" id="simpleLiabilityRadio"/>
                            <label for="simpleLiabilityRadio">Simple Liability</label>
                        </div>
                        <div>
                            <form:radiobutton path="subtypeName" value="PayableAccount" id="payableRadio"/>
                            <label for="payableRadio">Payable</label>
                        </div>
                        <div>
                            <form:radiobutton path="subtypeName" value="SharedBillPayableAccount" id="sharedBillPayableRadio"/>
                            <label for="sharedBillPayableRadio">Shared Bill Payable</label>
                        </div>
                        <div>
                            <form:radiobutton path="subtypeName" value="CreditCardAccount" id="creditCardRadio"/>
                            <label for="creditCardRadio">Credit Card</label>
                        </div>
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
