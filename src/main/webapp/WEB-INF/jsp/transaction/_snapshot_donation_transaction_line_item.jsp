<%@ include file="_transaction_amount_update_callback.jsp" %>

<script type="text/javascript">
$(document).ready(function() {
  prepareCheckbox(
    "TRANSACTION_${entity.id}_taxDeductible",
    ${snapshot.id},
    ${entity.id},
    ${entity.taxDeductible} == true,
    "transaction/setTaxDeductible",
    donationIsDeductibleUpdateSuccessCallback);

  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.id},
  };

  document.getElementById("select_txn_donation_category_${entity.id}").onchange =
    (evt) => {
      data.newValue = evt.srcElement.value;
      ajaxPost("transaction/updateCategory", data, transactionCategoryUpdateSuccessCallback);
    };
});
</script>

<div class="row border-1px-bottom" id="txn_row_${entity.id}">
    <%@ include file="_remove_transaction.jsp" %>

    <%@ include file="_recurrence_select.jsp" %>

    <div class="col col-cell col-account-name">
        ${entity.description}
    </div>


    <div class="col col-cell align-center">
        ${entity.date}
    </div>

    <div class="col col-cell align-center">
        <form id="form_txn_donation_category_${entity.id}">
            <select id="select_txn_donation_category_${entity.id}" name="donation_category">
                <c:forEach items="${donationCategories}" var="category">
                    <c:choose>
                        <c:when test="${category eq entity.category}">
                            <option value="${category}" selected="true">${category}</option>
                        </c:when>
                        <c:otherwise>
                            <option value="${category}">${category}</option>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </select>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    <div class="col col-cell align-center">
        <input id="TRANSACTION_${entity.id}_taxDeductible" type="checkbox"/>
    </div>

    <div class="col col-cell align-right editable-donation">
        <form id="form_txn_amount_${entity.id}">
            <span id="txn_amount_${entity.id}">${entity.amount}</span>
            <span><input id="new_txn_amount_${entity.id}" name="amount" type="number"
                         step="0.01" style="display: none;"/></span>
            <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
</div>