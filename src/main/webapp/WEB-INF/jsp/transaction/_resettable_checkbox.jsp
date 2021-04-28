<script type="text/javascript">
$(document).ready(function() {
  prepareCheckbox(
    "TRANSACTION_${entity.id}_resettable",
    ${snapshot.id},
    ${entity.id},
    ${entity.resettable} == true,
    "transaction/setResettable",
    function(data, result){});
})
</script>

<div class="col col-cell align-center short">
    <input id="TRANSACTION_${entity.id}_resettable" type="checkbox"/>
</div>
