<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    isOldSnapshot: ${snapshot.old},
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

    <c:set var="includeProfitFutureTithing" value="false"/>
    <%@ include file="_account_future_tithing_select.jsp" %>

    <div class="col col-cell-${deviceType} align-right ${editableClass}">
        <form id="form_account_balance_${entity.accountId}">
            <span id="account_balance_${entity.accountId}">${entity.balance}</span>
            <span><input id="new_account_balance_${entity.accountId}" name="amount" type="number"
                         step="0.01" class="hidden-input"/></span>
        </form>
    </div>
</div>
