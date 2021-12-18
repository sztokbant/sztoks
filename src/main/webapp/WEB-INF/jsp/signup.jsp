<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Sign Up</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/signed-out.css" rel="stylesheet">
</head>

<body>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<div class="full-width">
    <c:choose>
        <c:when test="${deviceType ne 'MOBILE'}">
            <div class="center-w640">
        </c:when>
    </c:choose>

    <form:form method="POST" modelAttribute="userForm" class="form-signin">
        <div class="row form-group">
            <div class="col col-form-label-${deviceType}">
                <label for="email">E-mail</label>
            </div>
            <div class="col">
                <spring:bind path="email">
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:input type="text" id="email" path="email" class="form-control" placeholder="E-mail"
                                    autofocus="true"></form:input>
                        <form:errors path="email"></form:errors>
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
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:input type="text" id="firstName" path="firstName" class="form-control" placeholder="First Name"
                                    autofocus="true"></form:input>
                        <form:errors path="firstName"></form:errors>
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
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:input type="text" id="lastName" path="lastName" class="form-control" placeholder="Last Name"
                                    autofocus="true"></form:input>
                        <form:errors path="lastName"></form:errors>
                    </div>
                </spring:bind>
            </div>
        </div>

        <div class="row form-group">
            <div class="col col-form-label-${deviceType}">
                <label for="currencyUnit">Default Currency</label>
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
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:input type="number" min="0" max="100" step="0.01" id="tithingPercentage" path="tithingPercentage" class="form-control"
                                    placeholder="Default Tithing Percentage"
                                    autofocus="true"></form:input>
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
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:input type="password" id="password" path="password" class="form-control"
                                    placeholder="Password"></form:input>
                        <form:errors path="password"></form:errors>
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
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:input type="password" id="passwordConfirm" path="passwordConfirm" class="form-control"
                                    placeholder="Confirm your password"></form:input>
                        <form:errors path="passwordConfirm"></form:errors>
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
            <button class="btn btn-lg btn-primary btn-block btn-sztoks-${deviceType}" type="submit"
                    onClick="this.form.submit(); this.disabled=true; this.innerText='Signing up...';">Sign up</button>
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
