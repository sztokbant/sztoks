<table class="center-w50pct">
    <tr>
        <td colspan="2"><a href="#"><b>Additional Currencies</b></a></td>
    </tr>
    <c:forEach var="entry" items="${snapshot.currencyConversionRates}">
        <tr>
            <td class="align-left-p7 valign-top"><b>${entry.key}</b></td>
            <td class="align-right-p7 valign-top">${entry.value}</td>
        </tr>
    </c:forEach>
</table>
