<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Add New Currency</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">

    <script src="${contextPath}/resources/js/jquery.min.js"></script>
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

    <form:form method="POST" action="${contextPath}/snapshot/${snapshotId}/newCurrency" modelAttribute="newCurrencyForm" class="form-signin">
        <h4 class="form-signin-heading">Add New Currency</h4>

        <div class="row form-group">
            <div class="col col-form-label-${deviceType}">
                <label for="currencyUnit">Currency</label>
            </div>
            <%@ include file="/WEB-INF/jsp/_currency_select.jsp" %>
        </div>

        <div class="row form-group">
            <div class="col" style="max-width: 40%;">
                <label for="conversionRate"><b>Conversion Rate</b><br/><i>1 ${baseCurrency} to <span id="selected_currency"></span></i></label>
            </div>
            <div class="col">
                <spring:bind path="conversionRate">
                    <div class="form-group ${status.error ? 'has-error' : ''}">
                        <form:input type="number" min="0" step="0.0001" id="conversionRate" path="conversionRate" class="form-control" placeholder="Conversion Rate"
                                    autofocus="true"></form:input>
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
