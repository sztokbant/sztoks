<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.id},
  };

  document.getElementById("select_txn_recurrence_${entity.id}").onchange =
    (evt) => {
      data.newValue = evt.srcElement.value;
      ajaxPost("transaction/updateRecurrencePolicy", data, transactionRecurrenceUpdateSuccessCallback);
    };
})
</script>

<div class="col col-cell align-center">
    <form id="form_txn_recurrence_${entity.id}">
        <select id="select_txn_recurrence_${entity.id}" name="txn_recurrence_${entity.id}">
            <c:forEach items="${recurrencePolicies}" var="policy">
                <c:choose>
                    <c:when test="${policy eq entity.recurrencePolicy}">
                        <option value="${policy}" selected="true">${policy}</option>
                    </c:when>
                    <c:otherwise>
                        <option value="${policy}">${policy}</option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </select>
        <input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
</div>
