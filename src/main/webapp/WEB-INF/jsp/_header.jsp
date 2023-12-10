<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<nav class="navbar fixed-top navbar-dark bg-dark navbar-sztoks-${deviceType}">
    <div class="container-fluid">
        <div class="navbar-header sztoks-navbar">
            <a class="navbar-item" href="#" class="navbar-toggle collapsed" data-toggle="dropdown" data-target="#bs-example-navbar-collapse-1">
                <c:choose>
                    <c:when test="${sztoksEnv eq 'localhost'}">
                        <c:set var="envDiscriminator" value=" <span style='color: #00ff00;'>LOCALHOST</span>" />
                    </c:when>
                    <c:when test="${sztoksEnv eq 'beta'}">
                        <c:set var="envDiscriminator" value=" <span style='color: #00ff00;'>BETA</span>" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="envDiscriminator" value="" />
                    </c:otherwise>
                </c:choose>

                <b>&#9776;&nbsp; Sztoks</b>${envDiscriminator}
            </a>
        </div>

        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="dropdown-menu bg-dark navbar-menu navbar-sztoks-${deviceType}">
                <c:choose>
                    <c:when test="${not empty pageContext.request.userPrincipal}">
                        <li class="navbar-entry-${deviceType}">
                            <a class="navbar-item" href="/">Snapshot List</a>
                        </li>
                        <li class="navbar-entry-${deviceType} divider"></li>
                        <li class="navbar-entry-${deviceType}">
                            <span class="navbar-text">Balance Sheet</span>
                        </li>
                        <li class="navbar-entry-${deviceType}">
                            <a class="navbar-item" href="/snapshot/${snapshotId}/newAssetAccount" style="text-decoration: none;">&#x271A; Asset Account</a>
                        </li>
                        <li class="navbar-entry-${deviceType}">
                            <a class="navbar-item" href="/snapshot/${snapshotId}/newLiabilityAccount" style="text-decoration: none;">&#x271A; Liability Account</a>
                        </li>
                        <li class="navbar-entry-${deviceType} divider"></li>
                        <li class="navbar-entry-${deviceType}">
                            <span class="navbar-text">Transactions</span>
                        </li>
                        <li class="navbar-entry-${deviceType}">
                            <a class="navbar-item" href="/snapshot/${snapshotId}/newIncomeTransaction" style="text-decoration: none;">&#x271A; Income Transaction</a>
                        </li>
                        <li class="navbar-entry-${deviceType}">
                            <a class="navbar-item" href="/snapshot/${snapshotId}/newInvestmentTransaction" style="text-decoration: none;">&#x271A; Investing Activity Transaction</a>
                        </li>
                        <li class="navbar-entry-${deviceType}">
                            <a class="navbar-item" href="/snapshot/${snapshotId}/newDonationTransaction" style="text-decoration: none;">&#x271A; Donation Transaction</a>
                        </li>
                        <li class="navbar-entry-${deviceType} divider">

                        </li>
                        <li class="navbar-entry-${deviceType} navbar-entry">
                            <form:form id="logoutForm" method="post" action="${contextPath}/logout" />
                            <a class="navbar-item" href="#" onclick="document.forms['logoutForm'].submit()">
                                Logout (${user.fullName})
                            </a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="navbar-entry-${deviceType} navbar-entry">
                            <a class="navbar-item" href="/">Log In</a>
                        </li>
                        <li class="navbar-entry-${deviceType} navbar-entry">
                            <a class="navbar-item" href="/signup">Sign Up</a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>

<input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>

<div class="navbar-margin-bottom-${deviceType}" />
