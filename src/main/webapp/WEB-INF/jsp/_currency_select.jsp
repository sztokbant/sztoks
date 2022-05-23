<div class="col">
    <form:select path="currencyUnit" class="form-control form-entry-${deviceType}" id="currency_select">
        <c:forEach items="${currencies}" var="currency">
            <c:choose>
                <c:when test="${currency eq selectedCurrency}">
                    <option value="${currency}" selected="true">${currency}</option>
                </c:when>
                <c:otherwise>
                    <option value="${currency}">${currency}</option>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </form:select>

    <div class="${status.error ? 'has-error' : ''}">
        <form:errors path="currencyUnit"/>
    </div>
</div>
