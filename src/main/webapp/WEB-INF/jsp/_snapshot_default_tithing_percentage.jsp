<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    isOldSnapshot: ${snapshot.old},
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

<div class="text-center default-tithing-${deviceType}">
    <span><b>Default Tithing</b></span>

    <div class="full-width">
        <form id="form_default_tithing_percentage">
            <span id="default_tithing_percentage" class="editable-income-even">${snapshot.defaultTithingPercentage}</span>
            <span><input id="new_default_tithing_percentage" type="number"
                         min="0" max="100"
                         step="0.01" class="hidden-input"/></span>
        </form>
    </div>
</div>
