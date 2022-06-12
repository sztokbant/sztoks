<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Add New Currency</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">

    <script src="${contextPath}/resources/js/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script></head>
</head>

<body>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<script type="text/javascript">
$(document).ready(function() {
  var currencySelect = document.getElementById("currency_select");

  $("#selected_currency").html(currencySelect.value);

  currencySelect.onchange =
    (evt) => {
      newValue = evt.srcElement.value;
      $("#selected_currency").html(newValue);
    };
});
</script>

<div class="full-width">
    <c:choose>
        <c:when test="${deviceType ne 'MOBILE'}">
            <div class="center-w640">
        </c:when>
    </c:choose>

    <div class="text-center page-title-${deviceType}">Add New Currency</div>

    <div class="text-center page-subtitle-${deviceType}">Snapshot: ${snapshotTitle}</div>

    <form:form method="post" action="${contextPath}/snapshot/${snapshotId}/newCurrency" modelAttribute="newCurrencyForm" class="form-signin">
        <div class="row form-group">
            <div class="col col-form-label-${deviceType}">
                <label for="currencyUnit">Currency</label>
            </div>
            <%@ include file="/WEB-INF/jsp/_currency_select.jsp" %>
        </div>

        <div class="row form-group">
            <div class="col col-form-label-${deviceType}">
                <label for="conversionRate">Conversion Rate
                    <br/>
                    <span class="col-form-sub-label-${deviceType}">1 ${baseCurrency} to <span id="selected_currency"></span></span></label>
            </div>
            <div class="col">
                <spring:bind path="conversionRate">
                    <form:input type="number" min="0" step="0.0001" id="conversionRate" path="conversionRate"
                                class="form-control form-entry-${deviceType}" placeholder="Conversion Rate"
                                autofocus="true"/>
                    <div class="form-group ${status.error ? 'has-error' : ''}">
                        <form:errors path="conversionRate"/>
                    </div>
                </spring:bind>
            </div>
        </div>

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
