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
      data.newValue = evt.target.value;
      ajaxPost("transaction/updateCategory", data, transactionCategoryUpdateSuccessCallback);
    };
});
</script>

<div class="row border-1px-bottom" id="txn_row_${entity.id}">
    <%@ include file="_remove_transaction.jsp" %>

    <%@ include file="_recurrence_select.jsp" %>

    <%@ include file="_snapshot_col_transaction_description.jsp" %>

    <%@ include file="_snapshot_col_transaction_date.jsp" %>

    <div class="col col-cell-${deviceType} text-center ${regularClass}">
        <form id="form_txn_donation_category_${entity.id}">
            <select id="select_txn_donation_category_${entity.id}" name="donation_category">
                <c:forEach items="${donationCategories}" var="category">
                    <c:choose>
                        <c:when test="${category eq entity.category}">
                            <option value="${category}" selected="true">${szt:capitalize(fn:replace(category, '_', ' '))}</option>
                        </c:when>
                        <c:otherwise>
                            <option value="${category}">${szt:capitalize(fn:replace(category, '_', ' '))}</option>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </select>
        </form>
    </div>

    <div class="col col-cell-${deviceType} text-center width-70px ${regularClass}">
        <input id="TRANSACTION_${entity.id}_taxDeductible" type="checkbox"
            class="checkbox-sztoks-${deviceType}"/>
    </div>

    <div class="col col-cell-${deviceType} align-right ${editableClass}">
        <%@ include file="_transaction_amount_form.jsp" %>
    </div>
</div>
