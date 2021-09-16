<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
  };

  prepareUpdateForm($("#form_default_tithing_percentage"),
    $("#default_tithing_percentage"),
    $("#new_default_tithing_percentage"),
    "snapshot/updateDefaultTithingPercentage",
    data,
    defaultTithingPercentageSuccessCallback,
  );
})
</script>

<div class="row center-w75pct">
    <div class="col">
        <b>Default Tithing</b>
    </div>
    <div class="col col-cell align-right">
        <form id="form_default_tithing_percentage">
            <span id="default_tithing_percentage" class="${editableClass}">${snapshot.defaultTithingPercentage}</span>
            <span><input id="new_default_tithing_percentage" type="number"
                         min="0" max="100"
                         step="0.01" style="display: none;"/></span>
        </form>
    </div>
</div>
