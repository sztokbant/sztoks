<script type="text/javascript">
$(document).ready(function() {
  var data = {
    id: ${account.id},
    isOldSnapshot: ${snapshot.old},
  }
  prepareUpdateForm($("#form_account_name_${account.id}"),
    $("#account_name_${account.id}"),
    $("#new_account_name_${account.id}"),
    "account/updateName",
    data,
    accountNameUpdateSuccessCallback,
  );
})

</script>
