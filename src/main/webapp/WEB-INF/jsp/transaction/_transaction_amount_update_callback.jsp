<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    isOldSnapshot: ${snapshot.old},
    entityId: ${entity.id},
  };

  prepareUpdateForm($("#form_txn_amount_${entity.id}"),
    $("#txn_amount_${entity.id}"),
    $("#new_txn_amount_${entity.id}"),
    "transaction/updateAmount",
    data,
    transactionAmountUpdateSuccessCallback,
  );
})
</script>
