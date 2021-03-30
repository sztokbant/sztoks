<%@ include file="_transaction_amount_update_callback.jsp" %>

<div class="row border-1px-bottom" id="txn_row_${entity.id}">

    <%@ include file="_remove_transaction.jsp" %>

    <%@ include file="_recurring_checkbox.jsp" %>

    <div class="col col-cell col-account-name">
        ${entity.description}
    </div>

    <div class="col col-cell align-center">
        ${entity.date}
    </div>

    <div class="col col-cell align-center">
        ${entity.donationRatio}
    </div>

    <div class="col col-cell align-right editable-asset">
        <form id="form_txn_amount_${entity.id}">
            <span id="txn_amount_${entity.id}">${entity.amount}</span>
            <span><input id="new_txn_amount_${entity.id}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
</div>