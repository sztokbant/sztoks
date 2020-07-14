<form method="POST" action="${contextPath}/account/${account.id}">
    <input name="balance_amount" type="text"/>
    <input name="workspace_id" value="${workspace.id}" type="hidden"/>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <button type="submit">Update</button>
</form>
