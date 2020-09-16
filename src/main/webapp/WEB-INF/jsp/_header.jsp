<nav class="navbar fixed-top navbar-dark bg-dark">
    <div class="container-fluid">
        <ul class="nav navbar-brand">
            <li>
                <a href="/"><b>My Equity</b></a>
            </li>
        </ul>
        <c:if test="${not empty pageContext.request.userPrincipal}">
            <ul class="nav navbar-brand">
                <li>
                    <form id="logoutForm" method="POST" action="${contextPath}/logout">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                    ${user.fullName} &ndash; <a href="#" onclick="document.forms['logoutForm'].submit()"><b>Logout</b></a>
                </li>
            </ul>
        </c:if>
    </div>
</nav>

<br/>
<br/>
<br/>