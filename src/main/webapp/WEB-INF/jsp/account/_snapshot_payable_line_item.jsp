<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.accountId},
  };

  prepareUpdateForm($("#form_payable_due_date_${entity.accountId}"),
    $("#payable_due_date_${entity.accountId}"),
    $("#new_payable_due_date_${entity.accountId}"),
    "snapshot/updateAccountDueDate",
    data,
    payableDueDateUpdateSuccessCallback,
  );
})

</script>

<div class="row border-1px-bottom" id="account_row_${entity.accountId}">
    <%@ include file="_snapshot_col_remove_account.jsp" %>

    <%@ include file="_snapshot_col_account_name.jsp" %>

    <div class="col col-cell-${deviceType} text-center ${editableClass}">
        <form id="form_payable_due_date_${entity.accountId}">
            <span id="payable_due_date_${entity.accountId}">${entity.dueDate}</span>
            <span><input id="new_payable_due_date_${entity.accountId}" name="amount" type="date"
                         class="hidden-input"/></span>
        </form>
    </div>

    <%@ include file="_snapshot_col_is_paid.jsp" %>

    <%@ include file="_snapshot_col_bill_amount.jsp" %>

    <div class="col col-cell-${deviceType} align-right">
        <span id="account_balance_${entity.accountId}">${entity.balance}</span>
    </div>
</div>
