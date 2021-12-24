<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.accountId},
  };

  prepareUpdateForm($("#form_account_balance_${entity.accountId}"),
    $("#account_balance_${entity.accountId}"),
    $("#new_account_balance_${entity.accountId}"),
    "snapshot/updateAccountBalance",
    data,
    accountBalanceUpdateSuccessCallback,
  );
})

</script>

<div class="row border-1px-bottom" id="account_row_${entity.accountId}">
    <%@ include file="_snapshot_col_remove_account.jsp" %>

    <%@ include file="_snapshot_col_account_name.jsp" %>

    <div class="col col-cell-${deviceType} width-total-${deviceType} align-right ${editableClass}">
        <form id="form_account_balance_${entity.accountId}">
            <span id="account_balance_${entity.accountId}">${entity.balance}</span>
            <span><input id="new_account_balance_${entity.accountId}" name="amount" type="number" step="0.01" style="display: none;"/></span>
        </form>
    </div>
</div>
