<c:if test="${snapshot.nextId ne null || snapshot.previousId ne null}">
    <div class="navigation-buttons-padding-bottom">
        <c:if test="${snapshot.previousId ne null}">
            <a class="btn btn-myequity" href="${contextPath}/snapshot/${snapshot.previousId}">&#x23EA;&nbsp;${snapshot.previousName}</a>
        </c:if>
        <c:if test="${snapshot.nextId ne null}">
            <a class="btn btn-myequity"
               href="${contextPath}/snapshot/${snapshot.nextId}">${snapshot.nextName}&nbsp;&#x23E9;</a>
        </c:if>
    </div>
</c:if>
