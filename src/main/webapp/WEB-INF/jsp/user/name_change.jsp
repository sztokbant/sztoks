<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Change Name</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">

    <script src="${contextPath}/resources/js/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script></head>
    <script>
    function validateNameChange() {
        const firstName = document.getElementById("firstName").value.trim();
        const lastName = document.getElementById("lastName").value.trim();

        if (firstName === "") {
            alert("First name cannot be empty.");
            return false;
        }
        if (lastName === "") {
            alert("Last name cannot be empty.");
            return false;
        }
        return true; // allow submission
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

    <div class="text-center page-title-${deviceType}">Change Name</div>

    <div class="text-center page-subtitle-${deviceType}">Current Name: <b>${user.fullName}</b></div>

    <div>
        <form:form modelAttribute="nameChangeInput" method="post" action="${contextPath}/settings/name" class="form-signin">
            <div class="form-group ${message != null ? 'info-message' : ''}">
                <span>${message}</span>
            </div>

            <spring:bind path="firstName">
                <div class="row form-group">
                    <div class="col col-form-label-${deviceType}">
                        <label for="firstName">First Name</label>
                    </div>
                    <div class="col">
                        <form:input type="text" id="firstName" path="firstName" class="form-control form-entry-${deviceType}" placeholder="First Name"
                                    autofocus="true"/>
                        <div class="${status.error ? 'has-error' : ''}">
                            <form:errors path="firstName"/>
                        </div>
                    </div>
                </div>
            </spring:bind>

            <spring:bind path="lastName">
                <div class="row form-group">
                    <div class="col col-form-label-${deviceType}">
                        <label for="lastName">Last Name</label>
                    </div>
                    <div class="col">
                        <form:input type="text" id="lastName" path="lastName" class="form-control form-entry-${deviceType}" placeholder="Last Name" />
                        <div class="${status.error ? 'has-error' : ''}">
                            <form:errors path="lastName"/>
                        </div>
                    </div>
                </div>
            </spring:bind>

            <div class="form-group ${error != null ? 'has-error' : ''}">
                <span>${error}</span>
            </div>

            <div class="text-center">
                <button class="btn btn-lg btn-primary btn-block btn-sztoks btn-form-${deviceType}" type="submit"
                        onclick="if (validateNameChange()) { this.form.submit(); this.disabled=true; this.innerText='...'; return true; } else { return false; }">
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
