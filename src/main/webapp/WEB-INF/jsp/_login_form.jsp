<div>
    <form method="POST" action="${contextPath}/login" class="form-signin">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <div class="form-group ${error != null ? 'has-error' : ''}">
            <span>${message}</span>

            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    <label for="username">E-mail</label>
                </div>
                <div class="col">
                    <input id="username" name="username" type="text" class="form-control" placeholder="E-mail" autofocus="true"/>
                </div>
            </div>

            <div class="row form-group">
                <div class="col col-form-label-${deviceType}">
                    <label for="password">Password</label>
                </div>
                <div class="col">
                    <input id="password" name="password" type="password" class="form-control" placeholder="Password"/>
                </div>
            </div>

            <span>${error}</span>

            <div class="text-center">
                <button class="btn btn-lg btn-primary btn-block btn-sztoks-${deviceType}" type="submit"
                        onClick="this.form.submit(); this.disabled=true; this.innerText='Logging in...';">Log in</button>
            </div>
            <div class="text-center paragraph-${deviceType}">Don't have an account? <a href="${contextPath}/signup">Sign up for Sztoks</a>.</div>
        </div>
    </form>
</div>
