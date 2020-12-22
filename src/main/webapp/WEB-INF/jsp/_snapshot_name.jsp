<script type="text/javascript">
$(document).ready(function() {
  var data = {
    id: ${snapshot.id},
  }
  prepareUpdateForm($("#form_snapshot_name_${snapshot.id}"),
    $("#snapshot_name_${snapshot.id}"),
    $("#new_snapshot_name_${snapshot.id}"),
    "snapshot/updateName",
    "",
    data,
    snapshotNameUpdateSuccessCallback,
  );
})

</script>

<form id="form_snapshot_name_${snapshot.id}">
    <span id="snapshot_name_${snapshot.id}" class="editable-snapshot-name">${snapshot.name}</span>
    <input id="new_snapshot_name_${snapshot.id}" name="name" type="text" style="display: none;"/>
    <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>
