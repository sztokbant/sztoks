<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="year" value="${now}" pattern="yyyy" />
<div class="copyright">
    Copyright &#169; 2020-${year} Eduardo Sztokbant, All Rights Reserved.
</div>