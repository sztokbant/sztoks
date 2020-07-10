<div class="text-center">
    <h3>My Equity</h3>
    <c:if test="${not empty pageContext.request.userPrincipal}">
        ${user.email} (<a href="#" onclick="document.forms['logoutForm'].submit()">Logout</a>)
    </c:if>
    <hr/>
</div>
