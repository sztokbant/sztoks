<div class="text-center">
    <h3>My Equity</h3>
    <c:if test="${not empty pageContext.request.userPrincipal}">
        <form id="logoutForm" method="POST" action="${contextPath}/logout">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
        ${user.fullName} - ${user.email} (<a href="#" onclick="document.forms['logoutForm'].submit()">Logout</a>)
    </c:if>
    <hr/>
</div>
