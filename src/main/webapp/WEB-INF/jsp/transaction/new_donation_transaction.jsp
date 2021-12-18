<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>New Donation</title>

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

    <form:form method="POST" action="${contextPath}/snapshot/${snapshotId}/newTransaction"
               modelAttribute="transactionForm" class="form-signin">
        <h4 class="form-signin-heading">New Donation Transaction</h4>

        <form:input type="hidden" path="typeName" value="${transactionType}"/>

        <%@ include file="_new_transaction_common.jsp" %>

        <spring:bind path="isTaxDeductible">
            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    Taxation
                </div>
                <div class="col">
                    <div class="${status.error ? 'has-error' : ''}">
                        <div>
                            <form:radiobutton path="isTaxDeductible" value="true" id="taxDeductibleRadio"/>
                            <label for="taxDeductibleRadio">Tax Deductible</label>
                        </div>
                        <div>
                            <form:radiobutton path="isTaxDeductible" value="false" id="nondeductibleRadio" checked="checked"/>
                            <label for="nondeductibleRadio">Nondeductible</label>
                        </div>
                        <form:errors path="isTaxDeductible"/>
                    </div>
                </div>
            </div>
        </spring:bind>

        <spring:bind path="category">
            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    Donation Category
                </div>
                <div class="col">
                    <div class="${status.error ? 'has-error' : ''}">
                        <div>
                            <form:radiobutton path="category" value="FAMILY" id="familyRadio"/>
                            <label for="familyRadio">Family</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="SPIRITUAL" id="spiritualRadio"/>
                            <label for="spiritualRadio">Spiritual</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="OTHER" id="otherRadio" checked="checked"/>
                            <label for="otherRadio">Other</label>
                        </div>
                        <form:errors path="category"/>
                    </div>
                </div>
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
