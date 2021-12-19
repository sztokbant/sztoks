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

<div class="col col-cell-${deviceType} text-center width-70px">
    <form id="form_account_future_tithing_${entity.accountId}">
        <select id="select_account_future_tithing_${entity.accountId}" name="account_future_tithing_${entity.accountId}">
            <c:forEach items="${futureTithingPolicies}" var="policy">
                <c:choose>
                    <c:when test="${policy eq entity.futureTithingPolicy}">
                        <option value="${policy}" selected="true">${policy}</option>
                    </c:when>
                    <c:otherwise>
                        <option value="${policy}">${policy}</option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </select>
    </form>
</div>
