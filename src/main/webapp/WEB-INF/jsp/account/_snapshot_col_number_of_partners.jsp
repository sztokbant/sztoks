<script type="text/javascript">
$(document).ready(function() {
var data = {
  snapshotId: ${snapshot.id},
  entityId: ${entity.accountId},
};

prepareUpdateForm($("#form_account_number_of_partners_${entity.accountId}"),
  $("#account_number_of_partners_${entity.accountId}"),
  $("#new_account_number_of_partners_${entity.accountId}"),
  "snapshot/updateAccountNumberOfPartners",
  data,
  accountNumberOfPartnersUpdateSuccessCallback,
);
})
</script>

<div class="col col-cell-${deviceType} text-center ${editableClass}">
    <form id="form_account_number_of_partners_${entity.accountId}">
        <span id="account_number_of_partners_${entity.accountId}">${entity.numberOfPartners}</span>
        <span><input id="new_account_number_of_partners_${entity.accountId}" name="number_of_partners" type="number" step="1" min="1" class="hidden-input"/></span>
    </form>
</div>
