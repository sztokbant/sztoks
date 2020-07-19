<c:if test="${not empty snapshot.netWorth}">
    (Net Worth:
    <c:forEach items="${snapshot.netWorth}" var="entry">
        [${entry.key}: ${entry.value}]
    </c:forEach>
    )
</c:if>