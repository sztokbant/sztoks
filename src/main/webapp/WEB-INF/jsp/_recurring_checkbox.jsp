<script type="text/javascript">
$(document).ready(function() {
  prepareCheckbox(
    "TRANSACTION_${txn.id}_recurring",
    ${snapshot.id},
    ${txn.id},
    ${txn.recurring} == true,
    "transaction/setRecurring");
})
</script>

<div class="col col-cell">
    <input id="TRANSACTION_${txn.id}_recurring" type="checkbox"/>
</div>
