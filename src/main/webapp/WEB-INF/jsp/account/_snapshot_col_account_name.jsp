<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
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

<div class="col col-cell col-account-name ${editableClass}">
    <form id="form_account_name_${entity.accountId}">
        <span id="account_name_${entity.accountId}">${entity.name}</span>
        <span><input id="new_account_name_${entity.accountId}" type="text" style="display: none;"/></span>
    </form>
</div>
