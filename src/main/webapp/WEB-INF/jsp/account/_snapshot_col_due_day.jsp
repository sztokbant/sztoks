<script type="text/javascript">
$(document).ready(function() {
var data = {
  snapshotId: ${snapshot.id},
  entityId: ${entity.accountId},
};

prepareUpdateForm($("#form_shared_bill_due_day_${entity.accountId}"),
  $("#shared_bill_due_day_${entity.accountId}"),
  $("#new_shared_bill_due_day_${entity.accountId}"),
  "snapshot/updateAccountDueDay",
  data,
  sharedBillDueDayUpdateSuccessCallback,
);
})
</script>

<div class="col col-cell text-center ${editableClass}">
    <form id="form_shared_bill_due_day_${entity.accountId}">
        <span id="shared_bill_due_day_${entity.accountId}">${entity.dueDay}</span>
        <span><input id="new_shared_bill_due_day_${entity.accountId}" name="amount" type="number" step="1" min="1" max="31"
                     style="display: none;"/></span>
    </form>
</div>
