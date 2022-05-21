<%@ include file="_transaction_amount_update_callback.jsp" %>

<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.id},
  };

  prepareUpdateForm($("#form_txn_tithing_percentage_${entity.id}"),
    $("#txn_tithing_percentage_${entity.id}"),
    $("#new_txn_tithing_percentage_${entity.id}"),
    "transaction/updateTithingPercentage",
    data,
    transactionTithingPercentageUpdateSuccessCallback,
  );

  document.getElementById("select_txn_income_category_${entity.id}").onchange =
    (evt) => {
      data.newValue = evt.srcElement.value;
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
        <form id="form_txn_income_category_${entity.id}">
            <select id="select_txn_income_category_${entity.id}" name="income_category">
                <c:forEach items="${incomeCategories}" var="category">
                    <c:choose>
                        <c:when test="${category eq entity.category}">
                            <option value="${category}" selected="true">${fn:replace(category, '_', ' ')}</option>
                        </c:when>
                        <c:otherwise>
                            <option value="${category}">${fn:replace(category, '_', ' ')}</option>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </select>
        </form>
    </div>

    <div class="col col-cell-${deviceType} text-center ${editableClass}">
        <form id="form_txn_tithing_percentage_${entity.id}">
            <span id="txn_tithing_percentage_${entity.id}">${entity.tithingPercentage}</span>
            <span><input id="new_txn_tithing_percentage_${entity.id}" name="amount" type="number" min="0" max="100"
                         step="0.01" class="hidden-input"/></span>
        </form>
    </div>

    <div class="col col-cell-${deviceType} align-right ${editableClass}">
        <%@ include file="_transaction_amount_form.jsp" %>
    </div>
</div>
