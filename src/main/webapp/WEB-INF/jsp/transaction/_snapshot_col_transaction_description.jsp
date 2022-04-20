<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.id},
  };

  prepareUpdateForm($("#form_transaction_description_${entity.id}"),
    $("#transaction_description_${entity.id}"),
    $("#new_transaction_description_${entity.id}"),
    "transaction/updateDescription",
    data,
    transactionDescriptionUpdateSuccessCallback,
  );
})
</script>

<div class="col col-cell-${deviceType} col-account-name ${editableClass}">
    <form id="form_transaction_description_${entity.id}">
        <span id="transaction_description_${entity.id}">${entity.description}</span>
        <span><input id="new_transaction_description_${entity.id}" type="text" class="hidden-input"/></span>
    </form>
</div>
