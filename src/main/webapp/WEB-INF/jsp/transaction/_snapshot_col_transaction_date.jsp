<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    isOldSnapshot: ${snapshot.old},
    entityId: ${entity.id},
  };

  prepareUpdateForm($("#form_transaction_date_${entity.id}"),
    $("#transaction_date_${entity.id}"),
    $("#new_transaction_date_${entity.id}"),
    "transaction/updateDate",
    data,
    transactionDateUpdateSuccessCallback,
  );
})
</script>

<div class="col col-cell-${deviceType} text-center ${editableClass}">
    <form id="form_transaction_date_${entity.id}">
        <span id="transaction_date_${entity.id}">${entity.date}</span>
        <span><input id="new_transaction_date_${entity.id}" type="date" class="hidden-input"/></span>
    </form>
</div>
