<c:set var="count" value="${count + 1}"/>
<c:choose>
    <c:when test="${count % 2 eq 0}">
        <c:set var="editableClass" value="editable-donation-even"/>
        <c:set var="regularClass" value="regular-donation-even"/>
    </c:when>
    <c:otherwise>
        <c:set var="editableClass" value="editable-donation-odd"/>
        <c:set var="regularClass" value="regular-donation-odd"/>
    </c:otherwise>
</c:choose>
