<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.accountId},
  };

  prepareUpdateForm($("#form_receivable_due_date_${entity.accountId}"),
    $("#receivable_due_date_${entity.accountId}"),
    $("#new_receivable_due_date_${entity.accountId}"),
    "snapshot/updateAccountDueDate",
    data,
    receivableDueDateUpdateSuccessCallback,
  );
})

</script>

<div class="row border-1px-bottom" id="account_row_${entity.accountId}">
    <%@ include file="_snapshot_col_remove_account.jsp" %>

    <%@ include file="_snapshot_col_account_name.jsp" %>

    <%@ include file="_account_future_tithing_select.jsp" %>

    <div class="col col-cell-${deviceType} text-center ${editableClass}">
        <form id="form_receivable_due_date_${entity.accountId}">
            <span id="receivable_due_date_${entity.accountId}">${entity.dueDate}</span>
            <span><input id="new_receivable_due_date_${entity.accountId}" name="amount" type="date"
                         style="display: none;"/></span>
        </form>
    </div>

    <%@ include file="_snapshot_col_is_paid.jsp" %>

    <%@ include file="_snapshot_col_bill_amount.jsp" %>

    <div class="col col-cell-${deviceType} width-total-${deviceType} align-right">
        <span id="account_balance_${entity.accountId}">${entity.balance}</span>
    </div>
</div>
