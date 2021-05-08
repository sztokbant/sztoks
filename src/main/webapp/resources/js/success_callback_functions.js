// SNAPSHOT

function defaultTithingPercentageSuccessCallback(data, result) {
  $("#default_tithing_percentage").html(result.defaultTithingPercentage);
  updateTotalForAccountType(result);
  updateTithingBalance(result);
  updateNetWorth(result);
}

// ACCOUNT (ALL)

function accountRenameUpdateSuccessCallback(data, result) {
  $("#account_name_" + data.entityId).html(result.name);
}

function removeAccountFromSnapshot(snapshotId, entityId, accountName) {
    var doRemove = confirm('Are you sure you want to remove "' + accountName + '" from this snapshot?');

    if (doRemove) {
      var data = {
        snapshotId: snapshotId,
        entityId: entityId,
      };

      ajaxPost('snapshot/removeAccount', data, removeAccountFromSnapshotSuccessCallback);
    }
}

function removeAccountFromSnapshotSuccessCallback(data, result) {
  $("#account_row_" + result.accountId).hide();

  if (result.accountSubtype != null) {
    updateTotalForAccountSubType(result);
  }

  if (result.investmentTotals != null) {
    updateSnapshotInvestmentTotals(result);
  }

  if (result.creditCardTotalsForCurrencyUnit != null) {
    updateSnapshotCreditCardTotals(result);
  }

  updateTotalForAccountType(result);
  updateTithingBalance(result);
  updateNetWorth(result);
}

// BALANCE UPDATEABLE ACCOUNT

function accountBalanceUpdateSuccessCallback(data, result) {
  $("#account_balance_" + data.entityId).html(result.balance);
  updateTotalForAccountSubType(result);
  updateTotalForAccountType(result);
  updateTithingBalance(result);
  updateNetWorth(result);
}

// FUTURE TITHING CAPABLE ACCOUNT

function accountFutureTithingUpdateSuccessCallback(data, result) {
  updateTithingBalance(result);
  updateNetWorth(result);
}

// RECEIVABLE ACCOUNT

function receivableDueDateUpdateSuccessCallback(data, result) {
  $("#receivable_due_date_" + data.entityId).html(result.dueDate);
}

// SHARED BILL RECEIVABLE ACCOUNT

function receivableDueDayUpdateSuccessCallback(data, result) {
  $("#receivable_due_day_" + data.entityId).html(result.dueDay);
}

function accountBillAmountUpdateSuccessCallback(data, result) {
  $("#account_bill_amount_" + data.entityId).html(result.billAmount);
  $("#account_balance_" + data.entityId).html(result.balance);
  updateTotalForAccountSubType(result);
  updateTotalForAccountType(result);
  updateTithingBalance(result);
  updateNetWorth(result);
}

function accountPaymentReceivedUpdateSuccessCallback(data, result) {
  $("#account_balance_" + data.entityId).html(result.balance);
  updateTotalForAccountSubType(result);
  updateTotalForAccountType(result);
  updateTithingBalance(result);
  updateNetWorth(result);
}

function accountNumberOfPartnersUpdateSuccessCallback(data, result) {
  $("#account_number_of_partners_" + data.entityId).html(result.numberOfPartners);
  $("#account_balance_" + data.entityId).html(result.balance);
  updateTotalForAccountSubType(result);
  updateTotalForAccountType(result);
  updateTithingBalance(result);
  updateNetWorth(result);
}

// GIFT CERTIFICATE ACCOUNT

function giftCertificateSharesUpdateSuccessCallback(data, result) {
  $("#gift_certificate_shares_" + data.entityId).html(result.shares);
  $("#gift_certificate_balance_" + data.entityId).html(result.balance);
  updateTotalForAccountSubType(result);
  updateTotalForAccountType(result);
  updateTithingBalance(result);
  updateNetWorth(result);
}

function giftCertificateCurrentShareValueUpdateSuccessCallback(data, result) {
  $("#gift_certificate_current_share_value_" + data.entityId).html(result.currentShareValue);
  $("#gift_certificate_balance_" + data.entityId).html(result.balance);
  updateTotalForAccountSubType(result);
  updateTotalForAccountType(result);
  updateTithingBalance(result);
  updateNetWorth(result);
}

// INVESTMENT ACCOUNT

function updateSnapshotInvestmentTotals(result) {
  $("#snapshot_investments_amount_invested").html(result.investmentTotals.amountInvested);
  $("#snapshot_investments_profit_percentage").html(result.investmentTotals.profitPercentage);
  $("#snapshot_investments_balance").html(result.investmentTotals.balance);
}

function investmentSharesUpdateSuccessCallback(data, result) {
  $("#investment_shares_" + data.entityId).html(result.shares);
  $("#investment_average_purchase_price_" + data.entityId).html(result.averagePurchasePrice);
  $("#investment_profit_percentage_" + data.entityId).html(result.profitPercentage);
  $("#investment_balance_" + data.entityId).html(result.balance);
  updateTotalForAccountType(result);
  updateSnapshotInvestmentTotals(result);
  updateTithingBalance(result);
  updateNetWorth(result);
}

function investmentAmountInvestedUpdateSuccessCallback(data, result) {
  $("#investment_amount_invested_" + data.entityId).html(result.amountInvested);
  $("#investment_average_purchase_price_" + data.entityId).html(result.averagePurchasePrice);
  $("#investment_profit_percentage_" + data.entityId).html(result.profitPercentage);
  updateSnapshotInvestmentTotals(result);
  updateTithingBalance(result);
  updateNetWorth(result);
}

function investmentCurrentShareValueUpdateSuccessCallback(data, result) {
  $("#investment_current_share_value_" + data.entityId).html(result.currentShareValue);
  $("#investment_profit_percentage_" + data.entityId).html(result.profitPercentage);
  $("#investment_balance_" + data.entityId).html(result.balance);
  updateTotalForAccountType(result);
  updateSnapshotInvestmentTotals(result);
  updateTithingBalance(result);
  updateNetWorth(result);
}

// PAYABLE ACCOUNT

function payableDueDateUpdateSuccessCallback(data, result) {
  $("#payable_due_date_" + data.entityId).html(result.dueDate);
}

// CREDIT CARD ACCOUNT

function updateSnapshotCreditCardTotals(result) {
  $("#snapshot_credit_card_total_credit_" + result.currencyUnit)
    .html(result.creditCardTotalsForCurrencyUnit.totalCredit);
  $("#snapshot_credit_card_available_credit_" + result.currencyUnit)
    .html(result.creditCardTotalsForCurrencyUnit.availableCredit);
  $("#snapshot_credit_card_used_credit_percentage_" + result.currencyUnit)
    .html(result.creditCardTotalsForCurrencyUnit.usedCreditPercentage);
  $("#snapshot_credit_card_statement_" + result.currencyUnit)
    .html(result.creditCardTotalsForCurrencyUnit.statement);
  $("#snapshot_credit_card_remaining_balance_" + result.currencyUnit)
    .html(result.creditCardTotalsForCurrencyUnit.remainingBalance);
  $("#snapshot_credit_card_balance_" + result.currencyUnit)
    .html(result.creditCardTotalsForCurrencyUnit.balance);
}

function creditCardTotalCreditUpdateSuccessCallback(data, result) {
  $("#credit_card_total_credit_" + data.entityId).html(result.totalCredit);
  $("#credit_card_used_credit_percentage_" + data.entityId).html(result.usedCreditPercentage);
  $("#credit_card_balance_" + data.entityId).html(result.balance);
  $("#credit_card_remaining_balance_" + data.entityId).html(result.remainingBalance);
  updateTotalForAccountType(result);
  updateSnapshotCreditCardTotals(result);
  updateNetWorth(result);
}

function creditCardAvailableCreditUpdateSuccessCallback(data, result) {
  $("#credit_card_available_credit_" + data.entityId).html(result.availableCredit);
  $("#credit_card_used_credit_percentage_" + data.entityId).html(result.usedCreditPercentage);
  $("#credit_card_remaining_balance_" + data.entityId).html(result.remainingBalance);
  $("#credit_card_balance_" + data.entityId).html(result.balance);
  updateTotalForAccountType(result);
  updateSnapshotCreditCardTotals(result);
  updateNetWorth(result);
}

function creditCardStatementUpdateSuccessCallback(data, result) {
  $("#credit_card_statement_" + data.entityId).html(result.statement);
  $("#credit_card_remaining_balance_" + data.entityId).html(result.remainingBalance);
  updateSnapshotCreditCardTotals(result);
}

// TRANSACTION (ALL)

function transactionDescriptionUpdateSuccessCallback(data, result) {
  $("#transaction_description_" + data.entityId).html(result.description);
}

function transactionAmountUpdateSuccessCallback(data, result) {
  $("#txn_amount_" + data.entityId).html(result.amount);
  $("#total_" + result.type).html(result.totalForTransactionType);

  if (result.type == "INCOME" || result.type == "DONATION") {
    if (result.type == "DONATION") {
      $("#tax_deductible_donations_total").html(result.taxDeductibleDonationsTotal);
    }
    updateTithingBalance(result);
    updateNetWorth(result);
  }
}

function transactionCategoryUpdateSuccessCallback(data, result) {
}

function transactionRecurrenceUpdateSuccessCallback(data, result) {
}

function removeTransaction(snapshotId, entityId, type, description) {
    var doRemove = confirm('Are you sure you want to remove ' + type + ' transaction "' + description + '" from this snapshot?');

    if (doRemove) {
      var data = {
        snapshotId: snapshotId,
        entityId: entityId,
      };

      ajaxPost('transaction/remove', data, removeTransactionSuccessCallback);
    }
}

function removeTransactionSuccessCallback(data, result) {
  $("#total_" + result.type).html(result.totalForTransactionType);
  if (result.type == "INCOME" || result.type == "DONATION") {
    $("#total_LIABILITY").html(result.totalLiability);
    updateTithingBalance(result);
    updateNetWorth(result);
  }
  $("#txn_row_" + result.entityId).hide();
}

// INCOME TRANSACTION

function transactionTithingPercentageUpdateSuccessCallback(data, result) {
  $("#txn_tithing_percentage_" + data.entityId).html(result.tithingPercentage) + "%";
  updateTithingBalance(result);
  updateNetWorth(result);
}

// DONATION TRANSACTION

function donationIsDeductibleUpdateSuccessCallback(data, result) {
  $("#tax_deductible_donations_total").html(result.taxDeductibleDonationsTotal);
}

// AUX FUNCTIONS

function updateTotalForAccountSubType(result) {
  $("#snapshot_" + result.accountSubtype + "_balance").html(result.totalForAccountSubtype);
}

function updateTotalForAccountType(result) {
  $("#total_" + result.accountType).html(result.totalForAccountType);
}

function updateTithingBalance(result) {
  if (result.tithingBalance != null) {
    $("#tithing_balance").html(result.tithingBalance);
  }

  if (result.futureTithingBalance != null) {
    $("#future_tithing_balance").html(result.futureTithingBalance);
  }

  if (result.totalTithingBalance != null) {
    $("#snapshot_TITHING_balance").html(result.totalTithingBalance);
  }

  if ((result.accountType == null || result.accountType != "LIABILITY") && result.totalLiability != null) {
    $("#total_LIABILITY").html(result.totalLiability);
  }
}

function updateNetWorth(result) {
  $("#snapshot_net_worth").html(result.netWorth);
}
