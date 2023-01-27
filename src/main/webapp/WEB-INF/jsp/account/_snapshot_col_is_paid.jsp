<script type="text/javascript">
$(document).ready(function() {
  prepareCheckbox(
    "ACCOUNT_${entity.accountId}_is_paid",
    ${snapshot.id},
    ${snapshot.old},
    ${entity.accountId},
    ${entity.isPaid} == true,
    "snapshot/updateBillIsPaid",
    accountPaymentReceivedUpdateSuccessCallback);
})
</script>

<div class="col col-cell-${deviceType} text-center width-70px ${regularClass}">
    <input id="ACCOUNT_${entity.accountId}_is_paid" type="checkbox"
           class="checkbox-sztoks-${deviceType}"/>
</div>
