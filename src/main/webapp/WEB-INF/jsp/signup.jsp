<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Sign Up</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">

    <script src="${contextPath}/resources/js/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script>
</head>

<body>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<div class="full-width">
    <c:choose>
        <c:when test="${deviceType ne 'MOBILE'}">
            <div class="center-w640">
        </c:when>
    </c:choose>

    <form:form method="post" modelAttribute="userForm" class="form-signin">
        <div class="row form-group">
            <div class="col col-form-label-${deviceType}">
                <label for="email">E-mail</label>
            </div>
            <div class="col">
                <spring:bind path="email">
                    <form:input type="text" id="email" path="email" class="form-control form-entry-${deviceType}"
                                placeholder="E-mail"
                                autofocus="true"/>
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:errors path="email"/>
                    </div>
                </spring:bind>
            </div>
        </div>

        <div class="row form-group">
            <div class="col col-form-label-${deviceType}">
                <label for="firstName">First Name</label>
            </div>
            <div class="col">
                <spring:bind path="firstName">
                    <form:input type="text" id="firstName" path="firstName" class="form-control form-entry-${deviceType}"
                                placeholder="First Name"
                                autofocus="true"/>
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:errors path="firstName"/>
                    </div>
                </spring:bind>
            </div>
        </div>

        <div class="row form-group">
            <div class="col col-form-label-${deviceType}">
                <label for="lastName">Last Name</label>
            </div>
            <div class="col">
                <spring:bind path="lastName">
                    <form:input type="text" id="lastName" path="lastName" class="form-control form-entry-${deviceType}"
                                placeholder="Last Name"
                                autofocus="true"/>
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:errors path="lastName"/>
                    </div>
                </spring:bind>
            </div>
        </div>

        <div class="row form-group">
            <div class="col col-form-label-${deviceType}">
                <label for="currencyUnit">Base Currency</label>
            </div>
            <spring:bind path="currencyUnit">
                <%@ include file="/WEB-INF/jsp/_currency_select.jsp" %>
            </spring:bind>
        </div>

        <div class="row form-group">
            <div class="col col-form-label-${deviceType}">
                <label for="tithingPercentage"><span>Default Tithing Percentage</span><br/><span class="col-form-sub-label-${deviceType}">E.g.: "10.00" for 10%</span></label>
            </div>
            <div class="col">
                <spring:bind path="tithingPercentage">
                    <form:input type="number" min="0" max="100" step="0.01" id="tithingPercentage" path="tithingPercentage"
                                class="form-control form-entry-${deviceType}"
                                placeholder="Default Tithing Percentage"
                                autofocus="true"/>
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:errors path="tithingPercentage"/>
                    </div>
                </spring:bind>
            </div>
        </div>

        <div class="row form-group">
            <div class="col col-form-label-${deviceType}">
                <label for="password">Password</label>
            </div>
            <div class="col">
                <spring:bind path="password">
                    <form:input type="password" id="password" path="password"
                                class="form-control form-entry-${deviceType}"
                                placeholder="Password"/>
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:errors path="password"/>
                    </div>
                </spring:bind>
            </div>
        </div>

        <div class="row form-group">
            <div class="col col-form-label-${deviceType}">
                <label for="passwordConfirm">Password Confirmation</label>
            </div>
            <div class="col">
                <spring:bind path="passwordConfirm">
                    <form:input type="password" id="passwordConfirm" path="passwordConfirm"
                                class="form-control form-entry-${deviceType}"
                                placeholder="Confirm your password"/>
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:errors path="passwordConfirm"/>
                    </div>
                </spring:bind>
            </div>
        </div>

        <div class="row form-group">
            <div class="col paragraph-${deviceType}">
                By signing up you confirm that you <b>understand</b> and <b>agree</b> that <b>Sztoks</b> is provided for educational purposes only with <b>no guarantees</b>. Use it at your own discretion.
            </div>
        </div>

        <div class="text-center">
            <button class="btn btn-lg btn-primary btn-block btn-sztoks btn-form-${deviceType}" type="submit"
                    onclick="this.form.submit(); this.disabled=true; this.innerText='Signing up...';">Sign up</button>
        </div>
        <div class="text-center paragraph-${deviceType}">Already have an account? <a href="${contextPath}/login">Log in</a>.</div>
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
