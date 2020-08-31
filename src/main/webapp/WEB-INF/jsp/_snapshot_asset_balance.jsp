<script type="text/javascript">
$(document).ready(function() {
  prepareAccountBalanceUpdateForm($("#form_account_balance_${account.id}"),
    ${snapshot.id},
    ${account.id},
    $("#account_balance_amount_${account.id}"),
    $("#new_account_balance_amount_${account.id}"));
})
</script>

<div class="col">
    ${account.name}
</div>

<c:choose>
    <c:when test="${empty account.shares}">
        <div class="col">
            &nbsp;
        </div>

        <div class="col">
            &nbsp;
        </div>

        <div class="col">
            <form id="form_account_balance_${account.id}">
                ${account.balanceCurrencyUnit}
                <span id="account_balance_amount_${account.id}">${account.balanceAmount}</span>
                <span><input id="new_account_balance_amount_${account.id}" name="amount" type="number" min="0"
                             step="0.01" style="display: none;"/></span>
                <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </div>
    </c:when>
    <c:otherwise>
        <div class="col">
            ${account.shares}
        </div>

        <div class="col">
            <form id="form_account_balance_${account.id}">
                ${account.balanceCurrencyUnit}
                <span id="account_balance_amount_${account.id}">${account.balanceAmount}</span>
                <span><input id="new_account_balance_amount_${account.id}" name="amount" type="number" min="0"
                             step="0.01" style="display: none;"/></span>
                <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </div>

        <div class="col">
            ${account.total}
        </div>
    </c:otherwise>
</c:choose>
