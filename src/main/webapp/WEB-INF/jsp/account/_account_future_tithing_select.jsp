<script type="text/javascript">
$(document).ready(function() {
  prepareSelect("select_account_future_tithing_${entity.accountId}",
    ${snapshot.id},
    ${snapshot.old},
    ${entity.accountId},
    "snapshot/updateFutureTithingPolicy",
    accountFutureTithingUpdateSuccessCallback
  );
})
</script>

<div class="col col-cell-${deviceType} text-center width-total-${deviceType} ${regularClass}">
    <form id="form_account_future_tithing_${entity.accountId}">
        <select id="select_account_future_tithing_${entity.accountId}" name="account_future_tithing_${entity.accountId}" class="emoji-select-${deviceType}">
            <c:forEach items="${futureTithingPolicies}" var="policy">
                <c:if test="${policy ne 'PROFITS_ONLY' or (policy eq 'PROFITS_ONLY' and includeProfitFutureTithing)}">
                    <c:choose>
                        <c:when test="${policy eq 'ALL'}">
                            <c:set var="policyLabel" value="&#9989;"/>
                        </c:when>
                        <c:when test="${policy eq 'NONE'}">
                            <c:set var="policyLabel" value="&#128683;"/>
                        </c:when>
                        <c:when test="${policy eq 'PROFITS_ONLY'}">
                            <c:set var="policyLabel" value="&#128200;"/>
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
