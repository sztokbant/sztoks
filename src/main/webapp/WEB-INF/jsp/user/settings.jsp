<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Settings</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">

    <script src="${contextPath}/resources/js/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script></head>
</head>

<body>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<div class="full-width">
    <c:choose>
        <c:when test="${deviceType ne 'MOBILE'}">
            <div class="center-w640">
        </c:when>
    </c:choose>

    <div class="text-center page-title-${deviceType}">Settings</div>

    <div class="text-center page-subtitle-${deviceType}">
        <b>${user.fullName}</b><br/>
        ${user.email}
    </div>


    <div class="text-center">
        <div class="form-group ${message != null ? 'info-message' : ''}">
            <span>${message}</span>
        </div>

        <form>
            <button class="btn btn-lg btn-primary btn-block btn-sztoks btn-form-${deviceType}"
                    type="submit" formaction="/settings/name">
                Change Name
            </button>
        </form>

        <form>
            <button class="btn btn-lg btn-primary btn-block btn-sztoks btn-form-${deviceType}"
                    type="submit" formaction="/settings/email">
                Update E-mail Address
            </button>
        </form>

        <form>
            <button class="btn btn-lg btn-primary btn-block btn-sztoks btn-form-${deviceType}"
                    type="submit" formaction="/settings/password">
                Change Password
            </button>
        </form>

        <form>
            <button class="btn btn-lg btn-primary btn-block btn-sztoks btn-red btn-form-${deviceType}"
                    type="submit" formaction="/settings/delete_user">
                Permanently Delete My Account
            </button>
        </form>

        <div class="text-center paragraph-${deviceType}"><a href="${contextPath}/">Return to Snapshots</a></div>
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
