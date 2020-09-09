<div class="col col-account-name">
    <form id="remove_account_${snapshot.id}_${account.id}">
        <a href="#" onclick="removeAccountFromSnapshot('${account.name}', ${account.id}, ${snapshot.id});" style="text-decoration: none;">&#x26D4;</a> ${account.name}
        <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
</div>
