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
</head>

<body>

<%@ include file="_header.jsp" %>

<div class="full-width">
    <div class="center-w640">
        <form:form method="POST" modelAttribute="userForm" class="form-signin">
            <h4 class="form-signin-heading">Sign Up</h4>

            <div class="row form-group">
                <div class="col col-form-label">
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
                <div class="col col-form-label">
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
                <div class="col col-form-label">
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
                <div class="col col-form-label">
                    <label for="currencyUnit">Default Currency</label>
                </div>
                <spring:bind path="currencyUnit">
                    <%@ include file="/WEB-INF/jsp/_currency_select.jsp" %>
                </spring:bind>
            </div>

            <div class="row form-group">
                <div class="col" style="max-width: 40%;">
                    <label for="tithingPercentage"><span><b>Default Tithing Percentage</b></span><br/><i>E.g.: "10.00" for 10%</i></label>
                </div>
                <div class="col">
                    <spring:bind path="tithingPercentage">
                        <div class="${status.error ? 'has-error' : ''}">
                            <form:input type="number" min="0" step="0.01" id="tithingPercentage" path="tithingPercentage" class="form-control"
                                        placeholder="Default Tithing Percentage"
                                        autofocus="true"></form:input>
                            <form:errors path="tithingPercentage"/>
                        </div>
                    </spring:bind>
                </div>
            </div>

            <div class="row form-group">
                <div class="col col-form-label">
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
                <div class="col col-form-label">
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
                <div class="col">
                    By signing up you confirm that you understand that <b>Sztoks</b> is provided for educational purposes only with <b>no guarantees</b>. Use it at your own risk.
                </div>
            </div>

            <button class="btn btn-lg btn-primary btn-block" type="submit"
                    onClick="this.form.submit(); this.disabled=true; this.innerText='Signing up...';">Sign up</button>
            <div class="text-center">Already have an account? <a href="${contextPath}/login">Log in</a>.</div>
        </form:form>
    </div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
</body>
</html>
