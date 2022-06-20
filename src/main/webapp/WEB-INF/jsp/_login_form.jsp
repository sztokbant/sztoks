<div>

    <form:form method="post" action="${contextPath}/login" class="form-signin">
        <div class="form-group ${message != null ? 'info-message' : ''}">
            <span>${message}</span>
        </div>

        <div class="row form-group">
            <div class="col col-form-label-${deviceType}">
                <label for="username">E-mail</label>
            </div>
            <div class="col">
                <input id="username" name="username" type="text" class="form-control form-entry-${deviceType}" placeholder="E-mail" autofocus="true"/>
            </div>
        </div>

        <div class="row form-group">
            <div class="col col-form-label-${deviceType}">
                <label for="password">Password</label>
            </div>
            <div class="col">
                <input id="password" name="password" type="password" class="form-control form-entry-${deviceType}" placeholder="Password"/>
            </div>
        </div>

        <div class="form-group ${error != null ? 'has-error' : ''}">
            <span>${error}</span>
        </div>

        <div class="text-center">
            <button class="btn btn-lg btn-primary btn-block btn-sztoks btn-form-${deviceType}" type="submit"
                    onclick="this.form.submit(); this.disabled=true; this.innerText='Logging in...';">Log in</button>
        </div>
        <div class="text-center paragraph-${deviceType}">Don't have an account? <a href="${contextPath}/signup">Sign up for Sztoks</a>.</div>
    </form:form>
</div>
