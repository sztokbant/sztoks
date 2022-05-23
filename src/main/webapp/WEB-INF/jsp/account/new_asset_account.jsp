<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>New Asset</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">

    <script src="${contextPath}/resources/js/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script></head>
<head>

<body>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<div class="full-width">
    <c:choose>
        <c:when test="${deviceType ne 'MOBILE'}">
            <div class="center-w640">
        </c:when>
    </c:choose>

    <div class="text-center page-title-${deviceType}">New Asset Account</div>

    <div class="text-center page-subtitle-${deviceType}">Snapshot: ${snapshotTitle}</div>

    <form:form method="POST" action="${contextPath}/snapshot/${snapshotId}/newAccount" modelAttribute="accountForm" class="form-signin">
        <form:input type="hidden" path="accountType" value="${accountType}"/>

        <spring:bind path="name">
            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    <label for="name">Account Name</label>
                </div>
                <div class="col">
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:input type="text" id="name" path="name" class="form-control form-entry-${deviceType}" placeholder="Account Name"
                                    autofocus="true"></form:input>
                        <form:errors path="name"/>
                    </div>
                </div>
            </div>
        </spring:bind>

        <spring:bind path="subtypeName">
            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    Asset Type
                </div>
                <div class="col">
                    <div class="${status.error ? 'has-error' : ''}">
                        <div>
                            <form:radiobutton path="subtypeName" value="SimpleAssetAccount" id="simpleAssetRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="simpleAssetRadio" class="form-entry-${deviceType}">Simple Asset</label>
                        </div>
                        <div>
                            <form:radiobutton path="subtypeName" value="GiftCertificateAccount" id="giftCertificateRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="giftCertificateRadio" class="form-entry-${deviceType}">Gift Certificate</label>
                        </div>
                        <div>
                            <form:radiobutton path="subtypeName" value="ReceivableAccount" id="receivableRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="receivableRadio" class="form-entry-${deviceType}">Receivable</label>
                        </div>
                        <div>
                            <form:radiobutton path="subtypeName" value="SharedBillReceivableAccount" id="sharedBillReceivableRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="sharedBillReceivableRadio" class="form-entry-${deviceType}">Shared Bill Receivable</label>
                        </div>
                        <div>
                            <form:radiobutton path="subtypeName" value="InvestmentAccount" id="investmentRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="investmentRadio" class="form-entry-${deviceType}">Investment</label>
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

        <spring:bind path="futureTithingPolicy">
            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    Future Tithing Policy
                </div>
                <div class="col">
                    <div class="${status.error ? 'has-error' : ''}">
                        <div>
                            <form:radiobutton path="futureTithingPolicy" value="NONE" id="noneRadio" checked="checked"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="noneRadio" class="form-entry-${deviceType}">&#128683; &nbsp;None</label>
                        </div>
                        <div>
                            <form:radiobutton path="futureTithingPolicy" value="PROFITS_ONLY" id="profitsOnlyRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="profitsOnlyRadio" class="form-entry-${deviceType}">&#128200; &nbsp;Profits Only</label>
                        </div>
                        <div>
                            <form:radiobutton path="futureTithingPolicy" value="ALL" id="allRadio"
                                              class="radio-sztoks-${deviceType}"/>
                            <label for="allRadio" class="form-entry-${deviceType}">&#9989; &nbsp;All</label>
                        </div>
                        <form:errors path="futureTithingPolicy"/>
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

