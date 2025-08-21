<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Change E-mail Address</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">

    <script src="${contextPath}/resources/js/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script></head>
<script src="${contextPath}/resources/js/ajax_field_update.js"></script>
<script>
    function validateEmailChange() {
        const currentEmail = document.getElementById("currentEmail").value.trim();
        const email = document.getElementById("email").value.trim();
        const emailConfirmation = document.getElementById("emailConfirmation").value.trim();
        const password = document.getElementById("password").value;

        if (email === "") {
            alert("New e-mail cannot be empty.");
            return false;
        }
        if (email !== emailConfirmation) {
            alert("E-mails do not match.");
            return false; // prevent submission
        }
        if (email === currentEmail) {
            alert("New e-mail must be different from current e-mail.");
            return false; // prevent submission
        }
        if (password === "") {
            alert("Password missing.");
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

    <div class="text-center page-title-${deviceType}">Change E-mail Address</div>

    <div class="text-center page-subtitle-${deviceType}">Current E-mail Address: <b>${user.email}</b></div>

    <div>
        <form:form modelAttribute="emailUpdateViewModelInput" method="post" action="${contextPath}/settings/email" class="form-signin">
            <form:input type="hidden" id="currentEmail" path="currentEmail" value="${user.email}"/>

            <div class="form-group ${message != null ? 'info-message' : ''}">
                <span>${message}</span>
            </div>

            <spring:bind path="email">
                <div class="row form-group">
                    <div class="col col-form-label-${deviceType}">
                        <label for="email">New E-mail</label>
                    </div>
                    <div class="col">
                        <form:input type="text" id="email" path="email" class="form-control form-entry-${deviceType}" placeholder="New E-mail Address"
                                    autofocus="true"/>
                        <div class="${status.error ? 'has-error' : ''}">
                            <form:errors path="email"/>
                        </div>
                    </div>
                </div>
            </spring:bind>

            <spring:bind path="emailConfirmation">
                <div class="row form-group">
                    <div class="col col-form-label-${deviceType}">
                        <label for="emailConfirmation">New E-mail Confirmation</label>
                    </div>
                    <div class="col">
                        <form:input type="text" id="emailConfirmation" path="emailConfirmation" class="form-control form-entry-${deviceType}" placeholder="New E-mail Address Confirmation" />
                        <div class="${status.error ? 'has-error' : ''}">
                            <form:errors path="emailConfirmation"/>
                        </div>
                    </div>
                </div>
            </spring:bind>

            <spring:bind path="password">
                <div class="row form-group">
                    <div class="col col-form-label-${deviceType}">
                        <label for="password">Your Current Password</label>
                    </div>
                    <div class="col">
                        <form:input type="password" id="password" path="password" class="form-control form-entry-${deviceType}" placeholder="Password" />
                        <div class="${status.error ? 'has-error' : ''}">
                            <form:errors path="password"/>
                        </div>
                    </div>
                </div>
            </spring:bind>

            <div class="form-group ${error != null ? 'has-error' : ''}">
                <span>${error}</span>
            </div>

            <div class="text-center">
                <button class="btn btn-lg btn-primary btn-block btn-sztoks btn-form-${deviceType}" type="submit"
                        onclick="if (validateEmailChange()) { this.form.submit(); this.disabled=true; this.innerText='...'; return true; } else { return false; }">
                Save
                </button>
                <div class="text-center paragraph-${deviceType}"><a href="${contextPath}/">Return to Snapshots</a></div>
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
