<spring:bind path="date">
    <div class="form-group ${status.error ? 'has-error' : ''}">
        <form:input type="text" path="date" class="form-control" placeholder="Date"
                    autofocus="true"></form:input>
        <form:errors path="date"/>
    </div>
</spring:bind>

<spring:bind path="currencyUnit">
    <div class="form-group ${status.error ? 'has-error' : ''}">
        <form:input type="text" path="currencyUnit" class="form-control" placeholder="Currency"
                    autofocus="true"></form:input>
        <form:errors path="currencyUnit"/>
    </div>
</spring:bind>

<spring:bind path="amount">
    <div class="form-group ${status.error ? 'has-error' : ''}">
        <form:input type="text" path="amount" class="form-control" placeholder="Amount"
                    autofocus="true"></form:input>
        <form:errors path="amount"/>
    </div>
</spring:bind>

<spring:bind path="description">
    <div class="form-group ${status.error ? 'has-error' : ''}">
        <form:input type="text" path="description" class="form-control" placeholder="Description"
                    autofocus="true"></form:input>
        <form:errors path="description"/>
    </div>
</spring:bind>

<spring:bind path="isRecurring">
    <div class="form-group ${status.error ? 'has-error' : ''}">
        <div>
            <form:radiobutton path="isRecurring" value="false" id="nonrecurringRadio"
                              checked="checked"/>
            <label for="nonrecurringRadio">Nonrecurring</label>
        </div>
        <div>
            <form:radiobutton path="isRecurring" value="true" id="recurringRadio"/>
            <label for="recurringRadio">Recurring</label>
        </div>
        <form:errors path="isRecurring"/>
    </div>
</spring:bind>
