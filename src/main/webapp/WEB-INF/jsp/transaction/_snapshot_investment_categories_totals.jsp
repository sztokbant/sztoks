<div class="row border-1px-bottom bg-transaction-subcategory">
    <div class="col col-cell col-title">Category</div>
    <div class="col col-cell col-title">YTD Total</div>
    <div class="col col-cell col-title"></div>
    <div class="col col-cell col-title">12mo Total</div>
    <div class="col col-cell col-title"></div>
</div>

<c:forEach var="categories" items="${transactionCategoryTotals.investmentCategories}">
    <c:forEach var="byCurrency" items="${categories.value}">
        <div class="row border-1px-bottom bg-transaction-subcategory">
            <c:choose>
                <c:when test="${categories.value.size() gt 1}">
                    <c:set var="categoryName" value="${categories.key} (${byCurrency.key})" />
                </c:when>
                <c:otherwise>
                    <c:set var="categoryName" value="${categories.key}" />
                </c:otherwise>
            </c:choose>
            <div class="col col-cell transaction-subcategory-label">${categoryName}</div>
            <div class="col col-cell text-center">${byCurrency.value.ytd}</div>
            <div class="col col-cell text-center"></div>
            <div class="col col-cell text-center">${byCurrency.value.twelveMonths}</div>
            <div class="col col-cell text-center"></div>
        </div>
    </c:forEach>
</c:forEach>
