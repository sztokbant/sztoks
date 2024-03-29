<spring:bind path="description">
    <div class="row form-group">
        <div class="col col-form-label-${deviceType}">
            <label for="description">Description</label>
        </div>
        <div class="col">
            <form:input type="text" id="description" path="description" class="form-control form-entry-${deviceType}" placeholder="Description"
                        autofocus="true"/>
            <div class="${status.error ? 'has-error' : ''}">
                <form:errors path="description"/>
            </div>
        </div>
    </div>
</spring:bind>

<jsp:useBean id="nowAsDate" class="java.util.Date"/>
<fmt:formatDate var="today" value="${nowAsDate}" pattern="yyyy-MM-dd" />

<spring:bind path="date">
    <div class="row form-group">
        <div class="col col-form-label-${deviceType}">
            <label for="date">Date</label>
        </div>
        <div class="col">
            <form:input type="date" id="date" path="date" class="form-control form-entry-${deviceType}" placeholder="Date"
                        value="${today}" autofocus="true"/>
            <div class="${status.error ? 'has-error' : ''}">
                <form:errors path="date"/>
            </div>
        </div>
    </div>
</spring:bind>

<spring:bind path="currencyUnit">
    <div class="row form-group">
        <div class="col col-form-label-${deviceType}">
            <label for="currencyUnit">Currency</label>
        </div>
        <%@ include file="/WEB-INF/jsp/_currency_select.jsp" %>
    </div>
</spring:bind>

<spring:bind path="amount">
    <div class="row form-group">
        <div class="col col-form-label-${deviceType}">
            <label for="amount">Amount</label>
        </div>
        <div class="col">
            <form:input type="number" step="0.00000001" id="amount" path="amount" class="form-control form-entry-${deviceType}" placeholder="Amount"
                        autofocus="true"></form:input>
            <div class="${status.error ? 'has-error' : ''}">
                <form:errors path="amount"/>
            </div>
        </div>
    </div>
</spring:bind>

<spring:bind path="recurrencePolicy">
    <div class="row form-group">
        <div class="col col-form-label-${deviceType}">
            Recurrence
        </div>
        <div class="col">
            <div>
                <form:radiobutton path="recurrencePolicy" value="NONE" id="notRecurringRadio"
                                  checked="checked"
                                  class="radio-sztoks-${deviceType}"/>
                <label for="notRecurringRadio" class="form-entry-${deviceType}">&#128683; &nbsp;Not recurring</label>
            </div>
            <div>
                <form:radiobutton path="recurrencePolicy" value="RECURRING" id="recurringRadio"
                                  class="radio-sztoks-${deviceType}"/>
                <label for="recurringRadio" class="form-entry-${deviceType}">&#128257; &nbsp;Recurring</label>
            </div>
            <div>
                <form:radiobutton path="recurrencePolicy" value="RESETTABLE" id="resettableRadio"
                                  class="radio-sztoks-${deviceType}"/>
                <label for="resettableRadio" class="form-entry-${deviceType}">&#48;&#65039;&#8419; &nbsp;Recurring &amp; reset to zero every month</label>
            </div>
            <div class="${status.error ? 'has-error' : ''}">
                <form:errors path="recurrencePolicy"/>
            </div>
        </div>
    </div>
</spring:bind>
