<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
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

<div class="col col-cell text-center ${editableClass}">
    <form id="form_transaction_date_${entity.id}">
        <span id="transaction_date_${entity.id}">${entity.date}</span>
        <span><input id="new_transaction_date_${entity.id}" type="date" style="display: none;"/></span>
    </form>
</div>
