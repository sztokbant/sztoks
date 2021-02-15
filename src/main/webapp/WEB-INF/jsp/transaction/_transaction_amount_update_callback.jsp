<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${txn.id},
  };

  prepareUpdateForm($("#form_txn_amount_${txn.id}"),
    $("#txn_amount_${txn.id}"),
    $("#new_txn_amount_${txn.id}"),
    "transaction/updateAmount",
    "${txn.currencyUnitSymbol}",
    data,
    transactionAmountUpdateSuccessCallback,
  );
})
</script>
