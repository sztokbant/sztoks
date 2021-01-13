<%@ include file="_txn_amount_update_callback.jsp" %>

<div class="row border-1px-bottom" id="txn_row_${txn.id}">
    <%@ include file="_remove_txn.jsp" %>

    <div class="col col-cell">
        ${txn.recurring}
    </div>

    <div class="col col-cell">
        ${txn.date}
    </div>

    <div class="col col-cell">
        ${txn.description}
    </div>

    <div class="col col-cell">
        ${txn.taxDeductible}
    </div>

    <div class="col col-cell align-right editable-asset">
        <form id="form_txn_amount_${txn.id}">
            <span id="txn_amount_${txn.id}">${txn.amount}</span>
            <span><input id="new_txn_amount_${txn.id}" name="amount" type="number" min="0"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
</div>