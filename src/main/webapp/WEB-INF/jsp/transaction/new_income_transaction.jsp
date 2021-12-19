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

    <div class="text-center page-title-${deviceType}">New Income Transaction</div>

    <div class="text-center page-subtitle-${deviceType}">Snapshot: ${snapshotTitle}</div>

    <form:form method="POST" action="${contextPath}/snapshot/${snapshotId}/newTransaction" modelAttribute="transactionForm" class="form-signin">
        <form:input type="hidden" path="typeName" value="${transactionType}" />

        <%@ include file="_new_transaction_common.jsp" %>

        <spring:bind path="tithingPercentage">
            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    <label for="tithingPercentage">Tithing Percentage</label>
                </div>
                <div class="col">
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:input type="number" min="0" max="100" step="0.01" id="tithingPercentage" path="tithingPercentage" class="form-control form-entry-${deviceType}" placeholder="Tithing Percentage"
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
                            <form:radiobutton path="category" value="JOB" id="jobRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="jobRadio" class="form-entry-${deviceType}">Job</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="BUSINESS" id="businessRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="businessRadio" class="form-entry-${deviceType}">Business</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="CAPITAL_GAIN" id="capitalGainRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="capitalGainRadio" class="form-entry-${deviceType}">Capital Gain</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="SIDE_GIG" id="sideGigRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="sideGigRadio" class="form-entry-${deviceType}">Side Gig</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="GIFT" id="giftRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="giftRadio" class="form-entry-${deviceType}">Gift</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="PROMO" id="promoRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="promoRadio" class="form-entry-${deviceType}">Promo</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="OTHER" id="otherRadio" checked="checked"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="otherRadio" class="form-entry-${deviceType}">Other</label>
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
