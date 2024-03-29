<div class="row border-1px-bottom" id="account_row_${entity.accountId}">
    <%@ include file="_snapshot_col_remove_account.jsp" %>

    <%@ include file="_snapshot_col_account_name.jsp" %>

    <c:set var="includeProfitFutureTithing" value="false"/>
    <%@ include file="_account_future_tithing_select.jsp" %>

    <%@ include file="_snapshot_col_due_day.jsp" %>

    <%@ include file="_snapshot_col_number_of_partners.jsp" %>

    <%@ include file="_snapshot_col_is_paid.jsp" %>

    <%@ include file="_snapshot_col_bill_amount.jsp" %>

    <div class="col col-cell-${deviceType} align-right ${regularClass}">
        <span id="account_balance_${entity.accountId}">${entity.balance}</span>
    </div>
</div>
