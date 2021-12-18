<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>New Income</title>

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

    <form:form method="POST" action="${contextPath}/snapshot/${snapshotId}/newTransaction" modelAttribute="transactionForm" class="form-signin">
        <h4 class="form-signin-heading">New Income Transaction</h4>

        <form:input type="hidden" path="typeName" value="${transactionType}" />

        <%@ include file="_new_transaction_common.jsp" %>

        <spring:bind path="tithingPercentage">
            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    <label for="tithingPercentage">Tithing Percentage</label>
                </div>
                <div class="col">
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:input type="number" min="0" max="100" step="0.01" id="tithingPercentage" path="tithingPercentage" class="form-control" placeholder="Tithing Percentage"
                                    autofocus="true"></form:input>
                        <form:errors path="tithingPercentage"/>
                    </div>
                </div>
            </div>
        </spring:bind>

        <spring:bind path="category">
            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    Income Category
                </div>
                <div class="col">
                    <div class="${status.error ? 'has-error' : ''}">
                        <div>
                            <form:radiobutton path="category" value="JOB" id="jobRadio"/>
                            <label for="jobRadio">Job</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="BUSINESS" id="businessRadio"/>
                            <label for="businessRadio">Business</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="CAPITAL_GAIN" id="capitalGainRadio"/>
                            <label for="capitalGainRadio">Capital Gain</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="SIDE_GIG" id="sideGigRadio"/>
                            <label for="sideGigRadio">Side Gig</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="GIFT" id="giftRadio"/>
                            <label for="giftRadio">Gift</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="PROMO" id="promoRadio"/>
                            <label for="promoRadio">Promo</label>
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
