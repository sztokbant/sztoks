<div>
    <b>Net Worth</b>
</div>
<c:choose>
    <c:when test="${not empty snapshot.netWorth}">
        <div>
            <c:forEach items="${snapshot.netWorth}" var="entry">
                ${entry.key} <span id="snapshot_networth_${entry.key}">${entry.value}</span><br>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        0.00
    </c:otherwise>
</c:choose>
