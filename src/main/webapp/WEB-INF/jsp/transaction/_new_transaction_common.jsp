<spring:bind path="description">
    <div class="row form-group">
        <div class="col col-form-label">
            <label for="description">Description</label>
        </div>
        <div class="col">
            <div class="${status.error ? 'has-error' : ''}">
                <form:input type="text" id="description" path="description" class="form-control" placeholder="Description"
                            autofocus="true"></form:input>
                <form:errors path="description"/>
            </div>
        </div>
    </div>
</spring:bind>

<jsp:useBean id="nowAsDate" class="java.util.Date"/>
<fmt:formatDate var="today" value="${nowAsDate}" pattern="yyyy-MM-dd" />

<spring:bind path="date">
    <div class="row form-group">
        <div class="col col-form-label">
            <label for="date">Date</label>
        </div>
        <div class="col">
            <div class="${status.error ? 'has-error' : ''}">
                <form:input type="date" id="date" path="date" class="form-control" placeholder="Date"
                            value="${today}" autofocus="true"></form:input>
                <form:errors path="date"/>
            </div>
        </div>
    </div>
</spring:bind>

<spring:bind path="currencyUnit">
    <div class="row form-group">
        <div class="col col-form-label">
            <label for="currencyUnit">Currency</label>
        </div>
        <%@ include file="/WEB-INF/jsp/_currency_select.jsp" %>
    </div>
</spring:bind>

<spring:bind path="amount">
    <div class="row form-group">
        <div class="col col-form-label">
            <label for="amount">Amount</label>
        </div>
        <div class="col">
            <div class="${status.error ? 'has-error' : ''}">
                <form:input type="number" step="0.00000001" id="amount" path="amount" class="form-control" placeholder="Amount"
                            autofocus="true"></form:input>
                <form:errors path="amount"/>
            </div>
        </div>
    </div>
</spring:bind>

<spring:bind path="recurrencePolicy">
    <div class="row form-group">
        <div class="col col-form-label">
            Recurrence
        </div>
        <div class="col">
            <div class="${status.error ? 'has-error' : ''}">
                <div>
                    <form:radiobutton path="recurrencePolicy" value="NONE" id="notRecurringRadio"
                                      checked="checked"/>
                    <label for="notRecurringRadio">Not recurring</label>
                </div>
                <div>
                    <form:radiobutton path="recurrencePolicy" value="RECURRING" id="recurringRadio"/>
                    <label for="recurringRadio">Recurring</label>
                </div>
                <div>
                    <form:radiobutton path="recurrencePolicy" value="RESETTABLE" id="resettableRadio"/>
                    <label for="resettableRadio">Recurring + reset to zero every month</label>
                </div>
                <form:errors path="recurrencePolicy"/>
            </div>
        </div>
    </div>
</spring:bind>
