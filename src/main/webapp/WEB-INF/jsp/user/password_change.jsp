<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Change Password</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">

    <script src="${contextPath}/resources/js/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script></head>
<script>
    function validatePasswordChange() {
        const currentPassword = document.getElementById("currentPassword").value.trim();
        const password = document.getElementById("password").value;
        const passwordConfirmation = document.getElementById("passwordConfirm").value;

        if (currentPassword === "") {
            alert("Current password missing.");
            return false;
        }
        if (password === "") {
            alert("Password cannot be empty.");
            return false;
        }
        if (password !== passwordConfirmation) {
            alert("Passwords do not match.");
            return false;
        }
        if (password === currentPassword) {
            alert("Password must be different from current password.");
            return false;
        }
        return true; // allow submission
    }</script>
</head>

<body>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<div class="full-width">
    <c:choose>
        <c:when test="${deviceType ne 'MOBILE'}">
            <div class="center-w640">
        </c:when>
    </c:choose>

    <div class="text-center page-title-${deviceType}">Change Password</div>

    <div class="text-center page-subtitle-${deviceType}">
        <b>${user.fullName}</b><br/>
        ${user.email}
    </div>

    <div>
        <form:form modelAttribute="passwordChangeInput" method="post" action="${contextPath}/settings/password" class="form-signin">
            <form:input type="hidden" id="email" path="email" value="${user.email}"/>

            <div class="form-group ${message != null ? 'info-message' : ''}">
                <span>${message}</span>
            </div>

            <spring:bind path="currentPassword">
                <div class="row form-group">
                    <div class="col col-form-label-${deviceType}">
                        <label for="currentPassword">Your Current Password</label>
                    </div>
                    <div class="col">
                        <form:input type="password" id="currentPassword" path="currentPassword" class="form-control form-entry-${deviceType}" placeholder="Password" />
                        <div class="${status.error ? 'has-error' : ''}">
                            <form:errors path="currentPassword"/>
                        </div>
                    </div>
                </div>
            </spring:bind>

            <spring:bind path="password">
                <div class="row form-group">
                    <div class="col col-form-label-${deviceType}">
                        <label for="password">New Password</label>
                    </div>
                    <div class="col">
                        <form:input type="password" id="password" path="password" class="form-control form-entry-${deviceType}" placeholder="New Password" />
                        <div class="${status.error ? 'has-error' : ''}">
                            <form:errors path="password"/>
                        </div>
                    </div>
                </div>
            </spring:bind>

            <spring:bind path="passwordConfirm">
                <div class="row form-group">
                    <div class="col col-form-label-${deviceType}">
                        <label for="passwordConfirm">New Password Confirmation</label>
                    </div>
                    <div class="col">
                        <form:input type="password" id="passwordConfirm" path="passwordConfirm" class="form-control form-entry-${deviceType}" placeholder="New Password Confirmation" />
                        <div class="${status.error ? 'has-error' : ''}">
                            <form:errors path="passwordConfirm"/>
                        </div>
                    </div>
                </div>
            </spring:bind>

            <div class="form-group ${error != null ? 'has-error' : ''}">
                <span>${error}</span>
            </div>

            <div class="text-center">
                <button class="btn btn-lg btn-primary btn-block btn-sztoks btn-form-${deviceType}" type="submit"
                        onclick="if (validatePasswordChange()) { this.form.submit(); this.disabled=true; this.innerText='...'; return true; } else { return false; }">
                Save
                </button>
                <div class="text-center paragraph-${deviceType}"><a href="${contextPath}/settings">Return to Settings</a></div>
            </div>
        </form:form>
    </div>

    <c:choose>
    <c:when test="${deviceType ne 'MOBILE'}">
        </div>
    </c:when>
    </c:choose>
</div>

<%@ include file="/WEB-INF/jsp/_footer.jsp" %>

</body>
</html>
