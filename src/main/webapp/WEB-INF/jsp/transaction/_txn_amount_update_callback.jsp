<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    txnId: ${txn.id},
  };

  prepareUpdateForm($("#form_txn_amount_${txn.id}"),
    $("#txn_amount_${txn.id}"),
    $("#new_txn_amount_${txn.id}"),
    "snapshot/updateTxnAmount",
    "${txn.currencyUnitSymbol}",
    data,
    txnAmountUpdateSuccessCallback,
  );
})
</script>
