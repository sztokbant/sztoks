<c:set var="count" value="${count + 1}"/>
<c:choose>
    <c:when test="${count % 2 eq 0}">
        <c:set var="editableClass" value="editable-liability-even"/>
        <c:set var="regularClass" value="regular-liability-even"/>
    </c:when>
    <c:otherwise>
        <c:set var="editableClass" value="editable-liability-odd"/>
        <c:set var="regularClass" value="regular-liability-odd"/>
    </c:otherwise>
</c:choose>
