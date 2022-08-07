<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.id},
  };

  document.getElementById("select_txn_recurrence_${entity.id}").onchange =
    (evt) => {
      data.newValue = evt.target.value;
      ajaxPost("transaction/updateRecurrencePolicy", data, transactionRecurrenceUpdateSuccessCallback);
    };
})
</script>

<div class="col col-cell-${deviceType} text-center emoji-select-cell-${deviceType} ${regularClass}">
    <form id="form_txn_recurrence_${entity.id}">
        <select id="select_txn_recurrence_${entity.id}" name="txn_recurrence_${entity.id}" class="emoji-select-${deviceType}">
            <c:forEach items="${recurrencePolicies}" var="policy">
                <c:choose>
                    <c:when test="${policy eq 'RECURRING'}">
                        <c:set var="policyLabel" value="&#128257;"/>
                    </c:when>
                    <c:when test="${policy eq 'RESETTABLE'}">
                        <c:set var="policyLabel" value="&#48;&#65039;&#8419;"/>
                    </c:when>
                    <c:when test="${policy eq 'NONE'}">
                        <c:set var="policyLabel" value="&#128683;"/>
                    </c:when>
                </c:choose>
                <c:choose>
                    <c:when test="${policy eq entity.recurrencePolicy}">
                        <option value="${policy}" selected="true">${policyLabel}</option>
                    </c:when>
                    <c:otherwise>
                        <option value="${policy}">${policyLabel}</option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </select>
    </form>
</div>
