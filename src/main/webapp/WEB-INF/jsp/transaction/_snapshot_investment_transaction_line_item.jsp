<%@ include file="_transaction_amount_update_callback.jsp" %>

<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.id},
  };


  document.getElementById("select_txn_investment_category_${entity.id}").onchange =
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
        <form id="form_txn_investment_category_${entity.id}">
            <select id="select_txn_investment_category_${entity.id}" name="investment_category">
                <c:forEach items="${investmentCategories}" var="category">
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
        </form>
    </div>

    <div id="div_txn_amount_${entity.id}" class="col col-cell-${deviceType} align-right ${editableClass}">
        <%@ include file="_transaction_amount_form.jsp" %>
    </div>
</div>
