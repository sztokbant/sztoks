<%@ include file="_account_name_update.jsp" %>

<%@ include file="_delete_account.jsp" %>
<form id="form_account_name_${account.id}">
    <span id="account_name_${account.id}" class="editable-liability">${account.name}</span>
    <input id="new_account_name_${account.id}" name="name" type="text" style="display: none;"/>
    <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>
