<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
    <body>
        <h1>Hello, World!</h1>
        <p>
            Today's Date: <%= (new java.util.Date()).toLocaleString() %>
        </p>
    </body>
</html>
