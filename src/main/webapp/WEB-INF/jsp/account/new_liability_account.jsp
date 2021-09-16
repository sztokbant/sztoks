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
    <div class="center-w640">
        <form:form method="POST" action="${contextPath}/snapshot/${snapshotId}/newAccount" modelAttribute="accountForm" class="form-signin">
            <h4 class="form-signin-heading">New Liability</h4>

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
                    <div class="col col-form-label">
                        <label for="currencyUnit">Currency</label>
                    </div>
                    <%@ include file="/WEB-INF/jsp/_currency_select.jsp" %>
                </div>
            </spring:bind>

            <button class="btn btn-lg btn-primary btn-block" type="submit"
                    onClick="this.form.submit(); this.disabled=true; this.innerText='Saving...';">Save</button>
            <div class="text-center"><a href="${contextPath}/snapshot/${snapshotId}">Back</a></div>
        </form:form>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/_footer.jsp" %>

</body>
</html>
