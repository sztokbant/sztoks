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

    <div class="col col-cell align-right editable-asset">
        <form id="form_gift_certificate_shares_${entity.accountId}">
            <span id="gift_certificate_shares_${entity.accountId}">${entity.shares}</span>
            <span><input id="new_gift_certificate_shares_${entity.accountId}" name="amount" type="number" min="0"
                         step="0.00000001" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-cell">
        &nbsp;
    </div>

    <div class="col col-cell">
        &nbsp;
    </div>

    <div class="col col-cell align-right editable-asset">
        <form id="form_gift_certificate_current_share_value_${entity.accountId}">
            <span id="gift_certificate_current_share_value_${entity.accountId}">${entity.currentShareValue}</span>
            <span><input id="new_gift_certificate_current_share_value_${entity.accountId}" name="amount" type="number"
                         min="0"
                         step="0.0001" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-cell">
        &nbsp;
    </div>

    <div class="col col-cell align-right">
        <span id="gift_certificate_balance_${entity.accountId}">${entity.balance}</span>
    </div>
</div>