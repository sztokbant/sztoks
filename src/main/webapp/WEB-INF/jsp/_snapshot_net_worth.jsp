<c:if test="${not empty snapshot.accounts}">
    (Net Worth:
    <c:forEach items="${snapshot.netWorth}" var="entry">
        [${entry.key}: ${entry.value}]
    </c:forEach>
    )
</c:if>