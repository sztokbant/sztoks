<script type="text/javascript">
$(document).ready(function() {
  prepareCheckbox(
    "TRANSACTION_${entity.id}_recurring",
    ${snapshot.id},
    ${entity.id},
    ${entity.recurring} == true,
    "transaction/setRecurring");
})
</script>

<div class="col col-cell">
    <input id="TRANSACTION_${entity.id}_recurring" type="checkbox"/>
</div>
