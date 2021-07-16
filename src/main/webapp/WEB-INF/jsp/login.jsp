<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Log in</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
</head>

<body>

<%@ include file="/WEB-INF/jsp/_header.jsp" %>

<div class="full-width">
    <div class="row">
        <div class="col">
            <div>
                <h4 class="form-heading">About</h4>
            </div>

            <div>
                <b>Sztoks</b> is a balance sheet-like web app which allows one to keep a monthly snapshot of
                their finances, including <i>assets</i>, <i>liabilities</i>, <i>income received</i>, <i>investment transactions</i>,
                as well as <i>donations</i> made.
            </div>
            <br />
            <div>
                Many financially successful people emphasize the importance of regularly saving a
                portion of one's income, as well as donating to charitable causes. <b>Sztoks</b> has built-in
                mechanisms that allow one to easily <i>"set the sail"</i> and visualize their progress on saving
                and on givivng to charity. Knowing where one stands is key to getting where one wants
                to get, in finances or anything else.
            </div>
            <br />
            <div>
                <b>Sztoks</b> is provided for educational purposes only with <b>no guarantees</b>. Use it at your own discretion.
            </div>
            <br/>
            <div>
                The source code for <b>Sztoks</b> is available on <a href="https://github.com/sztokbant/sztoks" target="_blank"><b>GitHub</b></a>.
            </div>
        </div>

        <div class="col">
            <div>
                <form method="POST" action="${contextPath}/login" class="form-signin">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                    <h4 class="form-heading">Log in</h4>

                    <div class="form-group ${error != null ? 'has-error' : ''}">
                        <span>${message}</span>

                        <div class="row form-group">
                            <div class="col col-form-label">
                                <label for="username">E-mail</label>
                            </div>
                            <div class="col">
                                <input id="username" name="username" type="text" class="form-control" placeholder="E-mail" autofocus="true"/>
                            </div>
                        </div>

                        <div class="row form-group">
                            <div class="col col-form-label">
                                <label for="password">Password</label>
                            </div>
                            <div class="col">
                                <input id="password" name="password" type="password" class="form-control" placeholder="Password"/>
                            </div>
                        </div>

                        <span>${error}</span>

                        <button class="btn btn-lg btn-primary btn-block" type="submit"
                                onClick="this.form.submit(); this.disabled=true; this.innerText='Logging in...';">Log in</button>
                        <div class="text-center">Don't have an account? <a href="${contextPath}/signup">Sign up for Sztoks</a>.
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/_footer.jsp" %>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
</body>
</html>
