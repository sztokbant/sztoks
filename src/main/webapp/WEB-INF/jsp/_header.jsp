<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<nav class="navbar fixed-top navbar-dark bg-dark">
    <div class="container-fluid">
        <ul class="nav navbar-brand navbar-sztoks-${deviceType}">
            <li>
                <a href="/"><b>Sztoks</b></a>
            </li>
        </ul>
        <c:if test="${not empty pageContext.request.userPrincipal}">
            <ul class="nav navbar-brand navbar-sztoks-${deviceType}">
                <li>
                    <form id="logoutForm" method="POST" action="${contextPath}/logout">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                    <c:choose>
                        <c:when test="${deviceType ne 'MOBILE'}">
                            ${user.fullName} &ndash;
                        </c:when>
                    </c:choose>
                    <a href="#" onclick="document.forms['logoutForm'].submit()">Logout</a>
                </li>
            </ul>
        </c:if>
    </div>
</nav>

<div class="navbar-margin-bottom-${deviceType}" />
