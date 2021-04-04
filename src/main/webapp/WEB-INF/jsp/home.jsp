<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>My Equity</title>
    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script>
    <script src="${contextPath}/resources/js/ajax_field_update.js"></script>
    <script src="${contextPath}/resources/js/success_callback_functions.js"></script>
    <script src="${contextPath}/resources/js/delete_account.js"></script>
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/home.css" rel="stylesheet">
</head>
<body>

<%@ include file="_header.jsp" %>

<div class="full-width">
    <div class="text-center"><h4>Snapshots</h4></div>

    <div class="navigation-buttons-padding-bottom">
        <form method="post" action="${contextPath}/snapshot/new">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <input type="submit" class="btn btn-myequity" value="&#x2795;  Snapshot"/>
        </form>
    </div>

    <div class="center-w640">
        <table class="full-width">
            <tr class="border-1px">
                <th class="snapshots-title">Snapshot Name</th>
                <th class="snapshots-title">Net Worth</th>
            </tr>
            <c:forEach var="snapshot" items="${snapshots}">
                <tr class="border-1px bg-light-yellow">
                    <td class="valign-top">
                        <c:if test="${snapshots.size() gt 1}">
                            <div class="delete-icon">
                                <form method="post" action="${contextPath}/snapshot/delete/${snapshot.id}"
                                      onsubmit="return confirm('Are you sure you want to permanently delete snapshot \'${snapshot.name}\'?');">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                    <input type="submit" value="&#x26D4" class="transparent-button"/>
                                </form>
                            </div>
                        </c:if>
                        <a href="/snapshot/${snapshot.id}">${snapshot.name}</a>
                    </td>
                    <td class="align-right-p7">${snapshot.netWorth}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>

</body>
</html>
