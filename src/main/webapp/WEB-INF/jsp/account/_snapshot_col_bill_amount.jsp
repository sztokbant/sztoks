<script type="text/javascript">
$(document).ready(function() {
var data = {
  snapshotId: ${snapshot.id},
  entityId: ${entity.accountId},
};

prepareUpdateForm($("#form_account_bill_amount_${entity.accountId}"),
  $("#account_bill_amount_${entity.accountId}"),
  $("#new_account_bill_amount_${entity.accountId}"),
  "snapshot/updateAccountBillAmount",
  data,
  accountBillAmountUpdateSuccessCallback,
);
})
</script>

<div class="col col-cell align-right ${editableClass}">
    <form id="form_account_bill_amount_${entity.accountId}">
        <span id="account_bill_amount_${entity.accountId}">${entity.billAmount}</span>
        <span><input id="new_account_bill_amount_${entity.accountId}" name="amount" type="number" step="0.01" style="display: none;"/></span>
    </form>
</div>
