<script type="text/javascript">
$(document).ready(function() {
  prepareCheckbox(
    "TRANSACTION_${entity.id}_recurring",
    ${snapshot.id},
    ${entity.id},
    ${entity.recurring} == true,
    "transaction/setRecurring",
    function(data, result){});
})
</script>

<div class="col col-cell align-center short">
    <input id="TRANSACTION_${entity.id}_recurring" type="checkbox"/>
</div>
