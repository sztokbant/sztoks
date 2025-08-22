<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Delete My Account</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">

    <script src="${contextPath}/resources/js/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script></head>
    <script>
    function validateDeleteUser() {
        const confirmation = document.getElementById("confirmation").value.trim();

        if (confirmation.toLocaleLowerCase() !== "PERMANENTLY DELETE".toLowerCase()) {
            alert("Type 'PERMANENTLY DELETE' to delete your account.");
            return false;
        }

        return confirm("Are you sure you want to PERMANENTLY DELETE your account? This action cannot be undone.");
    }
    </script>
</head>

<body>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<div class="full-width">
    <c:choose>
        <c:when test="${deviceType ne 'MOBILE'}">
            <div class="center-w640">
        </c:when>
    </c:choose>

    <div class="text-center page-title-${deviceType}">Delete My Account</div>

    <div class="text-center page-subtitle-${deviceType}">
        <b>${user.fullName}</b><br/>
        ${user.email}
    </div>

    <div>
        <form:form modelAttribute="deleteUserInput" method="post" action="${contextPath}/settings/delete_user" class="form-signin">
            <div class="form-group ${message != null ? 'info-message' : ''}">
                <span>${message}</span>
            </div>

            <div class="form-group has-error">
                Type 'PERMANENTLY DELETE' below to delete your account.<br/>
                WARNING: this action cannot be undone!
            </div>

            <spring:bind path="confirmation">
                <div class="row form-group">
                    <div class="col">
                        <form:input type="text" id="confirmation" path="confirmation" class="form-control form-entry-${deviceType}" placeholder="Confirmation"
                                    autofocus="true"/>
                        <div class="${status.error ? 'has-error' : ''}">
                            <form:errors path="confirmation"/>
                        </div>
                    </div>
                </div>
            </spring:bind>

            <div class="form-group ${error != null ? 'has-error' : ''}">
                <span>${error}</span>
            </div>

            <div class="text-center">
                <button class="btn btn-lg btn-primary btn-block btn-sztoks btn-red btn-form-${deviceType}" type="submit"
                        onclick="if (validateDeleteUser()) { this.form.submit(); this.disabled=true; this.innerText='...'; return true; } else { return false; }">
                DELETE MY ACCOUNT
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
