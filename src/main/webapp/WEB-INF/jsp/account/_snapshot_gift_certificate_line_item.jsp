<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.accountId},
  };

  prepareUpdateForm($("#form_gift_certificate_shares_${entity.accountId}"),
    $("#gift_certificate_shares_${entity.accountId}"),
    $("#new_gift_certificate_shares_${entity.accountId}"),
    "snapshot/updateAccountShares",
    data,
    giftCertificateSharesUpdateSuccessCallback,
  );

  prepareUpdateForm($("#form_gift_certificate_current_share_value_${entity.accountId}"),
    $("#gift_certificate_current_share_value_${entity.accountId}"),
    $("#new_gift_certificate_current_share_value_${entity.accountId}"),
    "snapshot/updateAccountCurrentShareValue",
    data,
    giftCertificateCurrentShareValueUpdateSuccessCallback,
  );
})

</script>

<div class="row border-1px-bottom" id="account_row_${entity.accountId}">
    <%@ include file="_snapshot_col_remove_account.jsp" %>

    <%@ include file="_snapshot_col_account_name.jsp" %>

    <%@ include file="_account_future_tithing_select.jsp" %>

    <div class="col col-cell-${deviceType} align-right ${editableClass}">
        <form id="form_gift_certificate_shares_${entity.accountId}">
            <span id="gift_certificate_shares_${entity.accountId}">${entity.shares}</span>
            <span><input id="new_gift_certificate_shares_${entity.accountId}" name="amount" type="number" min="0"
                         step="0.00000001" class="hidden-input"/></span>
        </form>
    </div>

    <div class="col col-cell-${deviceType} align-right ${editableClass}">
        <form id="form_gift_certificate_current_share_value_${entity.accountId}">
            <span id="gift_certificate_current_share_value_${entity.accountId}">${entity.currentShareValue}</span>
            <span><input id="new_gift_certificate_current_share_value_${entity.accountId}" name="amount" type="number"
                         min="0"
                         step="0.0001" class="hidden-input"/></span>
        </form>
    </div>

    <div class="col col-cell-${deviceType} align-right">
        <span id="gift_certificate_balance_${entity.accountId}">${entity.balance}</span>
    </div>
</div>
