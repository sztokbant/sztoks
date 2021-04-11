<%@ include file="_transaction_amount_update_callback.jsp" %>

<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.id},
  };

  prepareUpdateForm($("#form_txn_tithing_percentage_${entity.id}"),
    $("#txn_tithing_percentage_${entity.id}"),
    $("#new_txn_tithing_percentage_${entity.id}"),
    "transaction/updateTithingPercentage",
    data,
    transactionTithingPercentageUpdateSuccessCallback,
  );
})
</script>

<div class="row border-1px-bottom" id="txn_row_${entity.id}">

    <%@ include file="_remove_transaction.jsp" %>

    <%@ include file="_recurring_checkbox.jsp" %>

    <div class="col col-cell col-account-name">
        ${entity.description}
    </div>

    <div class="col col-cell align-center">
        ${entity.date}
    </div>

    <div class="col col-cell align-right editable-income">
        <form id="form_txn_tithing_percentage_${entity.id}">
            <span id="txn_tithing_percentage_${entity.id}">${entity.tithingPercentage}</span>
            <span><input id="new_txn_tithing_percentage_${entity.id}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-cell align-right editable-income">
        <form id="form_txn_amount_${entity.id}">
            <span id="txn_amount_${entity.id}">${entity.amount}</span>
            <span><input id="new_txn_amount_${entity.id}" name="amount" type="number"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
</div>