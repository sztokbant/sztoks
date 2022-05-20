<c:set var="count" value="${count + 1}"/>
<c:choose>
    <c:when test="${count % 2 eq 0}">
        <c:set var="editableClass" value="editable-income-even"/>
        <c:set var="regularClass" value="regular-income-even"/>
    </c:when>
    <c:otherwise>
        <c:set var="editableClass" value="editable-income-odd"/>
        <c:set var="regularClass" value="regular-income-odd"/>
    </c:otherwise>
</c:choose>
