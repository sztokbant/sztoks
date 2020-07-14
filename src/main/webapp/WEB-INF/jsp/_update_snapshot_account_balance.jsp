<form method="POST" action="${contextPath}/snapshot/${snapshot.id}">
    <input name="account_id" value="${accountToBalance.key.id}" type="hidden"/>
    <input name="balance_amount" type="text"/>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <button type="submit">Update</button>
</form>
