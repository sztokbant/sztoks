<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib  prefix="szt" uri="http://sztoks.com/functions" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>${snapshot.name}</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/snapshot.css" rel="stylesheet">

    <script src="${contextPath}/resources/js/jquery.min.js"></script>
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script>
    <script src="${contextPath}/resources/js/ajax_field_update.js"></script>
    <script src="${contextPath}/resources/js/success_callback_functions.js"></script>
</head>

<c:choose>
    <c:when test="${snapshot.nextId ne null}">
        <body style="background-color: #ccc;">
    </c:when>
    <c:otherwise>
        <body>
    </c:otherwise>
</c:choose>

<input type="hidden" id="${_csrf.parameterName}" name="${_csrf.parameterName}" value="${_csrf.token}"/>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<div class="full-width">
    <div class="row">
        <div class="col">
            <div class="row">
                <div class="col col-edge-${deviceType}">
                    <div class="left-button">
                        <c:if test="${snapshot.previousId ne null}">
                            <a class="btn btn-sztoks btn-adjacent-snapshot-common btn-adjacent-snapshot-${deviceType}" href="${contextPath}/snapshot/${snapshot.previousId}">
                                &#x23EA;&nbsp;&nbsp;${snapshot.previousName}
                            </a>
                        </c:if>
                    </div>

                    <c:choose>
                        <c:when test="${deviceType ne 'MOBILE'}">
                            </div>
                            <div class="col col-currencies">
                        </c:when>
                    </c:choose>

                    <%@ include file="_snapshot_default_tithing_percentage.jsp" %>
                </div>
            </div>
        </div>

        <%@ include file="_snapshot_title.jsp" %>

        <div class="col">
            <c:choose>
                <c:when test="${deviceType ne 'MOBILE'}">
                    <div class="row"">
                        <div class="col col-currencies">
                            <%@ include file="_snapshot_currency_conversion_rates.jsp" %>
                        </div>
                </c:when>
                <c:otherwise>
                    <div class="row" style="float: right;">
                </c:otherwise>
            </c:choose>

                <div class="col col-edge-${deviceType}">
                    <div class="right-button align-right">
                        <c:choose>
                            <c:when test="${snapshot.nextId ne null}">
                                <a class="btn btn-sztoks btn-adjacent-snapshot-common btn-adjacent-snapshot-${deviceType}"
                                href="${contextPath}/snapshot/${snapshot.nextId}">${snapshot.nextName} &#x23E9;</a>
                            </c:when>
                            <c:otherwise>
                                    <div class="navigation-buttons-padding-bottom">
                                <c:choose>
                                    <c:when test="${not snapshot.newSnapshotAllowed}">
                                        <a href="#" onclick="alert('It is too early to create a Snapshot for the upcoming month. Please, try again after the 15th.');" class="btn btn-sztoks btn-new-snapshot">&#x2795;&nbsp; Snapshot</a>
                                    </c:when>
                                    <c:otherwise>
                                            <form method="post" action="${contextPath}/snapshot/new" onSubmit="return confirm('Are you sure you want to create a new snapshot based on the current snapshot?')">
                                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                <button type="submit" class="btn btn-sztoks btn-new-snapshot-common btn-new-snapshot-${deviceType}">
                                                    &#x2795;&nbsp; Snapshot
                                                </button>
                                            </form>
                                    </c:otherwise>
                                </c:choose>
                                    </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <c:choose>
                        <c:when test="${deviceType eq 'MOBILE'}">
                            <%@ include file="_snapshot_currency_conversion_rates.jsp" %>
                        </c:when>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="full-width">
    <%@ include file="account/_snapshot_accounts.jsp" %>
    <%@ include file="transaction/_snapshot_transactions.jsp" %>
</div>

<%@ include file="/WEB-INF/jsp/_footer.jsp" %>

</body>
</html>
