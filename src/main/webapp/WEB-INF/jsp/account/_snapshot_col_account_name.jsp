<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    isOldSnapshot: ${snapshot.old},
    entityId: ${entity.accountId},
  };

  prepareUpdateForm($("#form_account_name_${entity.accountId}"),
    $("#account_name_${entity.accountId}"),
    $("#new_account_name_${entity.accountId}"),
    "snapshot/renameAccount",
    data,
    accountRenameUpdateSuccessCallback,
  );
})
</script>

<div class="col col-cell-${deviceType} col-account-name ${editableClass}">
    <form id="form_account_name_${entity.accountId}">
        <span id="account_name_${entity.accountId}">${entity.name}</span>
        <span><input id="new_account_name_${entity.accountId}" type="text" class="hidden-input"/></span>
    </form>
</div>
