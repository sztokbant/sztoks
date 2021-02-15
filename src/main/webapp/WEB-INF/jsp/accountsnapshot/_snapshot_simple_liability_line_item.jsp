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
    "${entity.currencyUnitSymbol}",
    data,
    accountBalanceUpdateSuccessCallback,
  );
})

</script>

<div class="row border-1px-bottom" id="account_row_${entity.accountId}">
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
        <form id="form_account_balance_${entity.accountId}">
            <span id="account_balance_${entity.accountId}">${entity.balance}</span>
            <span><input id="new_account_balance_${entity.accountId}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
</div>