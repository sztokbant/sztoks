<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    accountId: ${account.accountId},
  };

  prepareUpdateForm($("#form_account_balance_${account.accountId}"),
    $("#account_balance_${account.accountId}"),
    $("#new_account_balance_${account.accountId}"),
    "snapshot/updateAccountBalance",
    "${account.currencyUnitSymbol}",
    data,
    accountBalanceUpdateSuccessCallback,
  );
})
</script>

<div class="row border-1px-bottom" id="account_row_${account.accountId}">
    <%@ include file="_snapshot_col_account_name.jsp" %>

    <div class="col col-cell">
        &nbsp;
    </div>

    <div class="col col-cell">
        &nbsp;
    </div>

    <div class="col col-cell">
        &nbsp;
    </div>

    <div class="col col-cell">
        &nbsp;
    </div>

    <div class="col col-cell">
        &nbsp;
    </div>

    <div class="col col-cell align-right editable-liability">
        <form id="form_account_balance_${account.accountId}">
            <span id="account_balance_${account.accountId}">${account.balance}</span>
            <span><input id="new_account_balance_${account.accountId}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
</div>