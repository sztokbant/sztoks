<script type="text/javascript">
$(document).ready(function() {
  prepareCheckbox(
    "ACCOUNT_${entity.accountId}_is_paid",
    ${snapshot.id},
    ${entity.accountId},
    ${entity.isPaid} == true,
    "snapshot/updateBillIsPaid",
    accountPaymentReceivedUpdateSuccessCallback);
})
</script>

<div class="col col-cell-${deviceType} text-center width-70px">
    <input id="ACCOUNT_${entity.accountId}_is_paid" type="checkbox"/>
</div>
