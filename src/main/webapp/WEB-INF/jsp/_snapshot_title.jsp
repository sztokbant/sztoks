<div class="col mid-col">
    <div class="text-center snapshot-header">
        <span id="snapshot_name_${snapshot.id}" class="snapshot-name page-title-${deviceType}">${snapshot.name}</span>

        <div class="full-width border-1px">
            <div class="row bg-light-yellow">
                <div class="col col-net-worth-${deviceType}">
                    <b>NET WORTH</b>
                </div>

                <c:choose>
                    <c:when test="${deviceType eq 'MOBILE'}">
                        </div>
                        <div class="row bg-light-yellow">
                    </c:when>
                </c:choose>

                <div class="col col-net-worth-${deviceType}">
                    <span id="snapshot_net_worth">${snapshot.netWorth}</span>
                </div>
            </div>
        </div>
    </div>
</div>
