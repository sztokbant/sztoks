<div class="col mid-col">
    <div class="text-center snapshot-header">
        <span id="snapshot_name_${snapshot.id}" class="snapshot-name page-title-${deviceType}">${snapshot.name}</span>

        <div class="full-width border-1px">
            <div class="row bg-light-yellow">
                <div class="align-left-p7 col col-net-worth-${deviceType}">
                    <b>NET WORTH</b>
                </div>

                <c:choose>
                    <c:when test="${deviceType eq 'MOBILE'}">
                        </div>
                        <div class="row bg-light-yellow">
                    </c:when>
                </c:choose>

                <c:choose>
                    <c:when test="${snapshot.netWorthIncreased}">
                        <c:set var="netWorthDiffStyle" value="cell-green" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="netWorthDiffStyle" value="cell-red" />
                    </c:otherwise>
                </c:choose>

                <div class="align-right-p7 col col-net-worth-${deviceType}">
                    <span id="snapshot_net_worth">${snapshot.netWorth}</span>
                    <br/>
                    <span id="snapshot_net_worth_increase" class="${netWorthDiffStyle}">${snapshot.netWorthIncrease}</span>
                    <br/>
                    <span id="snapshot_net_worth_increase_percentage" class="${netWorthDiffStyle}">${snapshot.netWorthIncreasePercentage}</span>
                </div>
            </div>
        </div>
    </div>
</div>
