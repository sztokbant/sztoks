<script type="text/javascript">
$(document).ready(function() {
  var data = {
    snapshotId: ${snapshot.id},
    entityId: ${entity.accountId},
  };

  document.getElementById("select_account_future_tithing_${entity.accountId}").onchange =
    (evt) => {
      data.newValue = evt.srcElement.value;
      ajaxPost("snapshot/updateFutureTithingPolicy", data, accountFutureTithingUpdateSuccessCallback);
    };
})
</script>

<div class="col col-cell-${deviceType} text-center width-70px ${regularClass}">
    <form id="form_account_future_tithing_${entity.accountId}">
        <select id="select_account_future_tithing_${entity.accountId}" name="account_future_tithing_${entity.accountId}" class="emoji-select-${deviceType}">
            <c:forEach items="${futureTithingPolicies}" var="policy">
                <c:if test="${policy ne 'PROFIT' or (policy eq 'PROFIT' and includeProfitFutureTithing)}">
                    <c:choose>
                        <c:when test="${policy eq 'ALL'}">
                            <c:set var="policyLabel" value="&#9989;"/>
                        </c:when>
                        <c:when test="${policy eq 'NO'}">
                            <c:set var="policyLabel" value="&#128683;"/>
                        </c:when>
                        <c:when test="${policy eq 'PROFIT'}">
                            <c:set var="policyLabel" value="&#128178;"/>
                        </c:when>
                    </c:choose>
                    <c:choose>
                        <c:when test="${policy eq entity.futureTithingPolicy}">
                            <option value="${policy}" selected="true">${policyLabel}</option>
                        </c:when>
                        <c:otherwise>
                            <option value="${policy}">${policyLabel}</option>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </c:forEach>
        </select>
    </form>
</div>
