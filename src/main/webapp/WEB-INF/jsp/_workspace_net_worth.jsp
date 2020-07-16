<div>
    <b>Net Worth</b>
</div>
<c:choose>
    <c:when test="${not empty workspace.accounts}">
        <div>
            <c:forEach items="${workspace.netWorth}" var="entry">
                ${entry.key} <span id="ws_nw_${entry.key}">${entry.value}</span><br>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        0.00
    </c:otherwise>
</c:choose>
