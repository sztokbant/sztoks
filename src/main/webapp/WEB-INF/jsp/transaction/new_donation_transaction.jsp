<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>New Donation</title>

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

    <div class="text-center page-title-${deviceType}">New Donation Transaction</div>

    <div class="text-center page-subtitle-${deviceType}">Snapshot: ${snapshotTitle}</div>

    <form:form method="post" action="${contextPath}/snapshot/${snapshotId}/newTransaction"
               modelAttribute="transactionForm" class="form-signin">
        <form:input type="hidden" path="typeName" value="${transactionType}"/>

        <%@ include file="_new_transaction_common.jsp" %>

        <spring:bind path="isTaxDeductible">
            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    Taxation
                </div>
                <div class="col">
                    <div>
                        <form:radiobutton path="isTaxDeductible" value="true" id="taxDeductibleRadio"
                                          class="radio-sztoks-${deviceType}"/>
                        <label for="taxDeductibleRadio" class="form-entry-${deviceType}">Tax Deductible</label>
                    </div>
                    <div>
                        <form:radiobutton path="isTaxDeductible" value="false" id="nondeductibleRadio" checked="checked"
                                          class="radio-sztoks-${deviceType}"/>
                        <label for="nondeductibleRadio" class="form-entry-${deviceType}">Nondeductible</label>
                    </div>
                    <div class="${status.error ? 'has-error' : ''}">
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
                    <div>
                        <form:radiobutton path="category" value="FAMILY" id="familyRadio"
                                          class="radio-sztoks-${deviceType}"/>
                        <label for="familyRadio" class="form-entry-${deviceType}">Family</label>
                    </div>
                    <div>
                        <form:radiobutton path="category" value="SPIRITUAL" id="spiritualRadio"
                                          class="radio-sztoks-${deviceType}"/>
                        <label for="spiritualRadio" class="form-entry-${deviceType}">Spiritual</label>
                    </div>
                    <div>
                        <form:radiobutton path="category" value="OTHER" id="otherRadio" checked="checked"
                                          class="radio-sztoks-${deviceType}"/>
                        <label for="otherRadio" class="form-entry-${deviceType}">Other</label>
                    </div>
                    <div class="${status.error ? 'has-error' : ''}">
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
