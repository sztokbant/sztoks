<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>New Investment</title>

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

    <div class="text-center page-title-${deviceType}">New Investment Transaction</div>

    <div class="text-center page-subtitle-${deviceType}">Snapshot: ${snapshotTitle}</div>

    <form:form method="POST" action="${contextPath}/snapshot/${snapshotId}/newTransaction"
               modelAttribute="transactionForm" class="form-signin">
        <form:input type="hidden" path="typeName" value="${transactionType}"/>

        <%@ include file="_new_transaction_common.jsp" %>

        <spring:bind path="category">
            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    Investment Category
                </div>
                <div class="col">
                    <div class="${status.error ? 'has-error' : ''}">
                        <div>
                            <form:radiobutton path="category" value="LONG_TERM" id="longTermRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="longTermRadio" class="form-entry-${deviceType}">Long Term</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="MID_TERM" id="midTermRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="midTermRadio" class="form-entry-${deviceType}">Mid Term</label>
                        </div>
                        <div>
                            <form:radiobutton path="category" value="SHORT_TERM" id="shortTermRadio" checked="checked"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="shortTermRadio" class="form-entry-${deviceType}">Short Term</label>
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
