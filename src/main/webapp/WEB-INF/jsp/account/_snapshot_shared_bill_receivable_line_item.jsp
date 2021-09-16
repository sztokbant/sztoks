<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.accountId},
  };

  prepareUpdateForm($("#form_receivable_due_day_${entity.accountId}"),
    $("#receivable_due_day_${entity.accountId}"),
    $("#new_receivable_due_day_${entity.accountId}"),
    "snapshot/updateAccountDueDay",
    data,
    receivableDueDayUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_account_number_of_partners_${entity.accountId}"),
    $("#account_number_of_partners_${entity.accountId}"),
    $("#new_account_number_of_partners_${entity.accountId}"),
    "snapshot/updateAccountNumberOfPartners",
    data,
    accountNumberOfPartnersUpdateSuccessCallback,
  );

  prepareCheckbox(
    "ACCOUNT_${entity.accountId}_is_paid",
    ${snapshot.id},
    ${entity.accountId},
    ${entity.isPaid} == true,
    "snapshot/updateBillIsPaid",
    accountPaymentReceivedUpdateSuccessCallback);

  prepareUpdateForm($("#form_account_bill_amount_${entity.accountId}"),
    $("#account_bill_amount_${entity.accountId}"),
    $("#new_account_bill_amount_${entity.accountId}"),
    "snapshot/updateAccountBillAmount",
    data,
    accountBillAmountUpdateSuccessCallback,
  );
})
</script>

<div class="row border-1px-bottom" id="account_row_${entity.accountId}">
    <%@ include file="_snapshot_col_remove_account.jsp" %>

    <%@ include file="_snapshot_col_account_name.jsp" %>

    <%@ include file="_account_future_tithing_select.jsp" %>

    <div class="col col-cell align-center ${editableClass}">
        <form id="form_receivable_due_day_${entity.accountId}">
            <span id="receivable_due_day_${entity.accountId}">${entity.dueDay}</span>
            <span><input id="new_receivable_due_day_${entity.accountId}" name="amount" type="number" step="1" min="1" max="31"
                         style="display: none;"/></span>
        </form>
    </div>

    <div class="col col-cell">
        &nbsp;
    </div>

    <div class="col col-cell align-center ${editableClass}">
        <form id="form_account_number_of_partners_${entity.accountId}">
            <span id="account_number_of_partners_${entity.accountId}">${entity.numberOfPartners}</span>
            <span><input id="new_account_number_of_partners_${entity.accountId}" name="number_of_partners" type="number" step="1" min="1" style="display: none;"/></span>
        </form>
    </div>

    <div class="col col-cell align-center">
        <input id="ACCOUNT_${entity.accountId}_is_paid" type="checkbox"/>
    </div>

    <div class="col col-cell align-right ${editableClass}">
        <form id="form_account_bill_amount_${entity.accountId}">
            <span id="account_bill_amount_${entity.accountId}">${entity.billAmount}</span>
            <span><input id="new_account_bill_amount_${entity.accountId}" name="amount" type="number" step="0.01" style="display: none;"/></span>
        </form>
    </div>

    <div class="col col-cell align-right">
        <span id="account_balance_${entity.accountId}">${entity.balance}</span>
    </div>
</div>
