<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>${snapshot.name}</title>
    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script>
    <script src="${contextPath}/resources/js/ajax_field_update.js"></script>
    <script src="${contextPath}/resources/js/success_callback_functions.js"></script>
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/snapshot.css" rel="stylesheet">
</head>

<c:choose>
    <c:when test="${snapshot.nextId ne null}">
        <body style="background-color: #ccc;">
    </c:when>
    <c:otherwise>
        <body>
    </c:otherwise>
</c:choose>

<%@ include file="_header.jsp" %>

<div class="full-width">
    <div class="row">
        <div class="col">
            <div class="row">
                <div class="col col-edge">
                    <div class="left-button">
                        <c:if test="${snapshot.previousId ne null}">
                            <a class="btn btn-myequity" href="${contextPath}/snapshot/${snapshot.previousId}">&#x23EA;&nbsp;&nbsp;${snapshot.previousName}</a>
                        </c:if>
                    </div>
                </div>

                <div class="col">
                </div>
            </div>
        </div>

        <div class="col mid-col">
            <div class="text-center snapshot-header">
                <span id="snapshot_name_${snapshot.id}" class="snapshot-name">${snapshot.name}</span>

                <table class="full-width">
                    <tr class="border-1px bg-light-yellow">
                        <td class="align-left-p7 valign-top"><b>NET WORTH</b></td>
                        <td class="align-right-p7 valign-top">
                            <span id="snapshot_net_worth">${snapshot.netWorth}</span>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

        <div class="col">
            <div class="row">
                <div class="col col-currencies">
                    <%@ include file="_snapshot_currency_conversion_rates.jsp" %>
                </div>

                <div class="col col-edge">
                    <div class="right-button">
                        <c:choose>
                            <c:when test="${snapshot.nextId ne null}">
                                <a class="btn btn-myequity"
                                   href="${contextPath}/snapshot/${snapshot.nextId}">${snapshot.nextName}&nbsp;&#x23E9;</a>
                            </c:when>
                            <c:otherwise>
                                    <div class="navigation-buttons-padding-bottom">
                                <c:choose>
                                    <c:when test="${not snapshot.newSnapshotAllowed}">
                                        <a href="#" onclick="alert('It is too early to create a Snapshot for the upcoming month. Please, try again after the 15th.');" class="btn btn-newsnapshot">&#x2795;&nbsp; Snapshot</a>
                                    </c:when>
                                    <c:otherwise>
                                            <form method="post" action="${contextPath}/snapshot/new" onSubmit="return confirm('Are you sure you want to create a new snapshot based on the current snapshot?')">
                                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                <input type="submit" class="btn btn-newsnapshot" value="&#x2795;  Snapshot"/>
                                            </form>
                                    </c:otherwise>
                                </c:choose>
                                    </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="full-width">
    <%@ include file="account/_snapshot_accounts.jsp" %>
    <%@ include file="transaction/_snapshot_transactions.jsp" %>
</div>

</body>
</html>
