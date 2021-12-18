<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Sztoks</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/home.css" rel="stylesheet">

    <script src="${contextPath}/resources/js/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script>
    <script src="${contextPath}/resources/js/ajax_field_update.js"></script>
    <script src="${contextPath}/resources/js/success_callback_functions.js"></script>
</head>
<body>

<c:choose>
    <c:when test="${deviceType eq 'MOBILE'}">
        <c:set var="divClass" value="center-w100pct" />
    </c:when>
    <c:otherwise>
        <c:set var="divClass" value="center-w640" />
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<div class="full-width">
    <div class="text-center page-title-${deviceType}">Snapshots</div>

    <div class="${divClass}">
        <table class="full-width">
            <tr class="border-1px">
                <th class="col-snapshots-title col-snapshot-${deviceType}">Snapshot Name</th>
                <th class="col-snapshots-title col-snapshot-${deviceType}">Net Worth</th>
            </tr>

            <c:set var="isFirst" value="true"/>
            <c:forEach var="snapshot" items="${snapshots}">
                    <c:set var="rowClass" value="bg-light-yellow"/>
                    <c:set var="aClass" value=""/>
                    <c:if test='${snapshot.name.endsWith("-01")}'>
                        <c:set var="rowClass" value="bg-red"/>
                        <c:set var="aClass" value="bg-red-link"/>
                    </c:if>

                    <tr class="border-1px ${rowClass}">
                    <td class="valign-top col-snapshot-${deviceType}">
                        <div class="row">
                            <div class="col col-snapshot-delete-${deviceType}">
                                <c:if test="${isFirst and snapshots.size() gt 1}">
                                    <form method="post" action="${contextPath}/snapshot/delete/${snapshot.id}"
                                          onsubmit="return confirm('Are you sure you want to PERMANENTLY delete snapshot \'${snapshot.name}\'?');">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        <input type="submit" value="&#x26D4" class="transparent-button"/>
                                    </form>
                                    <c:set var="isFirst" value="false"/>
                                </c:if>
                            </div>
                            <div class="col col-account-name">
                                <a class="${aClass}" href="/snapshot/${snapshot.id}">${snapshot.name}</a>
                            </div>
                        </div>
                    </td>
                    <td class="align-right-p7 col-snapshot-${deviceType}">
                        ${snapshot.netWorth}
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/_footer.jsp" %>

</body>
</html>
