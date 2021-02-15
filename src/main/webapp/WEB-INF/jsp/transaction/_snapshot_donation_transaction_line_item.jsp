<script type="text/javascript">
$(document).ready(function() {
  prepareCheckbox(
    "TRANSACTION_${entity.id}_taxDeductible",
    ${snapshot.id},
    ${entity.id},
    ${entity.taxDeductible} == true,
    "transaction/setTaxDeductible");
})
</script>

<%@ include file="_transaction_amount_update_callback.jsp" %>

<div class="row border-1px-bottom" id="txn_row_${entity.id}">
    <%@ include file="_remove_transaction.jsp" %>

    <%@ include file="/WEB-INF/jsp/_recurring_checkbox.jsp" %>

    <div class="col col-cell">
        ${entity.date}
    </div>

    <div class="col col-cell">
        ${entity.description}
    </div>

    <div class="col col-cell">
        <input id="TRANSACTION_${entity.id}_taxDeductible" type="checkbox"/>
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