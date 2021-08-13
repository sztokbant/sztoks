<fmt:parseNumber var="amountValue" value="${szt:replaceAll(entity.amount, '[^0-9.-]+', '')}" type="NUMBER"
                 integerOnly="false"/>
<c:choose>
    <c:when test="${amountValue gt 0}">
        <c:set var="amountStyle" value="cell-green"/>
    </c:when>
    <c:when test="${amountValue lt 0}">
        <c:set var="amountStyle" value="cell-red"/>
    </c:when>
    <c:otherwise>
        <c:set var="amountStyle" value=""/>
    </c:otherwise>
</c:choose>

<form id="form_txn_amount_${entity.id}">
    <span id="txn_amount_${entity.id}" class="${amountStyle}">${entity.amount}</span>
    <span><input id="new_txn_amount_${entity.id}" name="amount" type="number"
                 step="0.01" style="display: none;"/></span>
</form>
