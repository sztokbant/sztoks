<div class="col">
    <div class="${status.error ? 'has-error' : ''}">
        <form:select path="currencyUnit" class="form-control" id="currency_select">
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

        <form:errors path="currencyUnit"/>
    </div>
</div>
