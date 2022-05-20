<c:set var="count" value="${count + 1}"/>
<c:choose>
    <c:when test="${count % 2 eq 0}">
        <c:set var="editableClass" value="editable-investment-even"/>
        <c:set var="regularClass" value="regular-investment-even"/>
    </c:when>
    <c:otherwise>
        <c:set var="editableClass" value="editable-investment-odd"/>
        <c:set var="regularClass" value="regular-investment-odd"/>
    </c:otherwise>
</c:choose>
