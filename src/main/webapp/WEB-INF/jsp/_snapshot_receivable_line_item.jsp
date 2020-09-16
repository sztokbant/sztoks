<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    accountId: ${account.id},
  };

  prepareUpdateForm($("#form_receivable_due_date_${account.id}"),
    $("#receivable_due_date_${account.id}"),
    $("#new_receivable_due_date_${account.id}"),
    "snapshot/updateAccountDueDate",
    "",
    data,
    receivableDueDateUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_account_balance_${account.id}"),
    $("#account_balance_amount_${account.id}"),
    $("#new_account_balance_amount_${account.id}"),
    "snapshot/updateAccountBalance",
    "${account.currencyUnitSymbol}",
    data,
    accountBalanceUpdateSuccessCallback,
  );
})
</script>

<div class="row" id="account_row_${account.id}">
    <%@ include file="_snapshot_col_account_name.jsp" %>

    <div class="col col-value editable-asset">
        <form id="form_receivable_due_date_${account.id}">
            <span id="receivable_due_date_${account.id}">${account.dueDate}</span>
            <span><input id="new_receivable_due_date_${account.id}" name="amount" type="text" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col">
        &nbsp;
    </div>

    <div class="col">
        &nbsp;
    </div>

    <div class="col col-value editable-asset">
        <form id="form_account_balance_${account.id}">
            <span id="account_balance_amount_${account.id}">${account.currencyUnitSymbol}${account.total}</span>
            <span><input id="new_account_balance_amount_${account.id}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
</div>
