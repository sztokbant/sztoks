<div class="col col-account-name">
    <form method="post" id="remove_account_${snapshot.id}_${account.id}" action="/removeAccountFromSnapshot/${snapshot.id}/${account.id}">
        <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
    <a href="#" onclick="remove_account_from_snapshot('${account.name}', ${account.id}, ${snapshot.id});" style="text-decoration: none;">&#x26D4;</a> ${account.name}
</div>