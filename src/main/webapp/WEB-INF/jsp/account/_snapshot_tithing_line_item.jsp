<div class="row border-1px-bottom" id="account_row_${entity.accountId}">
    <div class="col col-cell short">
        &nbsp;
    </div>

    <%@ include file="_snapshot_col_account_name.jsp" %>

    <div class="col col-cell">
        &nbsp;
    </div>

    <div class="col col-cell">
        &nbsp;
    </div>

    <div class="col col-cell">
        &nbsp;
    </div>

    <div class="col col-cell">
        &nbsp;
    </div>

    <div class="col col-cell">
        &nbsp;
    </div>

    <div class="col col-cell align-right">
        <span id="tithing_balance_${entity.currencyUnit}">${entity.balance}</span>
    </div>
</div>