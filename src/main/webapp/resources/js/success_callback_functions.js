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

// BALANCE UPDATABLE ACCOUNT

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

// SHARED BILL ACCOUNT

function sharedBillDueDayUpdateSuccessCallback(data, result) {
  $("#shared_bill_due_day_" + data.entityId).html(result.dueDay);
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

  let profit_percentage_span = $("#snapshot_investments_profit_percentage");
  profit_percentage_span.html(result.investmentTotals.profitPercentage);

  let percentage = stripDecimalForText(result.investmentTotals.profitPercentage);
  colorizeProfitPercentage(percentage, profit_percentage_span);

  $("#snapshot_investments_balance").html(result.investmentTotals.balance);
}

function investmentSharesUpdateSuccessCallback(data, result) {
  $("#investment_shares_" + data.entityId).html(result.shares);
  $("#investment_average_purchase_price_" + data.entityId).html(result.averagePurchasePrice);
  updateProfitPercentage(data.entityId, result.profitPercentage);
  $("#investment_balance_" + data.entityId).html(result.balance);
  updateTotalForAccountType(result);
  updateSnapshotInvestmentTotals(result);
  updateTithingBalance(result);
  updateNetWorth(result);
}

function investmentAmountInvestedUpdateSuccessCallback(data, result) {
  $("#investment_amount_invested_" + data.entityId).html(result.amountInvested);
  $("#investment_average_purchase_price_" + data.entityId).html(result.averagePurchasePrice);
  updateProfitPercentage(data.entityId, result.profitPercentage);
  updateSnapshotInvestmentTotals(result);
  updateTithingBalance(result);
  updateNetWorth(result);
}

function investmentCurrentShareValueUpdateSuccessCallback(data, result) {
  $("#investment_current_share_value_" + data.entityId).html(result.currentShareValue);
  updateProfitPercentage(data.entityId, result.profitPercentage);
  $("#investment_balance_" + data.entityId).html(result.balance);
  updateTotalForAccountType(result);
  updateSnapshotInvestmentTotals(result);
  updateTithingBalance(result);
  updateNetWorth(result);
}

function updateProfitPercentage(entityId, profitPercentage) {
  let profit_percentage_span = $("#investment_profit_percentage_" + entityId);
  profit_percentage_span.html(profitPercentage);

  let percentage = stripDecimalForText(profitPercentage);
  colorizeProfitPercentage(percentage, profit_percentage_span);
}

function colorizeProfitPercentage(percentage, element) {
  if (percentage > 0) {
    element.removeClass('cell-red');
    element.addClass('cell-green');
  } else if (percentage < 0) {
    element.removeClass('cell-green');
    element.addClass('cell-red');
  } else {
    element.removeClass('cell-red');
    element.removeClass('cell-green');
  }
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

  let used_credit_percentage_span = $("#snapshot_credit_card_used_credit_percentage_" + result.currencyUnit);
  used_credit_percentage_span.html(result.creditCardTotalsForCurrencyUnit.usedCreditPercentage);

  let percentage = stripDecimalForText(result.creditCardTotalsForCurrencyUnit.usedCreditPercentage);
  colorizeCreditCardPercentage(percentage, used_credit_percentage_span);

  $("#snapshot_credit_card_statement_" + result.currencyUnit)
    .html(result.creditCardTotalsForCurrencyUnit.statement);
  $("#snapshot_credit_card_remaining_balance_" + result.currencyUnit)
    .html(result.creditCardTotalsForCurrencyUnit.remainingBalance);
  $("#snapshot_credit_card_balance_" + result.currencyUnit)
    .html(result.creditCardTotalsForCurrencyUnit.balance);
}

function creditCardTotalCreditUpdateSuccessCallback(data, result) {
  $("#credit_card_total_credit_" + data.entityId).html(result.totalCredit);
  updateUsedCreditPercentage(data.entityId, result.usedCreditPercentage);
  $("#credit_card_balance_" + data.entityId).html(result.balance);
  $("#credit_card_remaining_balance_" + data.entityId).html(result.remainingBalance);
  updateTotalForAccountType(result);
  updateSnapshotCreditCardTotals(result);
  updateNetWorth(result);
}

function creditCardAvailableCreditUpdateSuccessCallback(data, result) {
  $("#credit_card_available_credit_" + data.entityId).html(result.availableCredit);
  updateUsedCreditPercentage(data.entityId, result.usedCreditPercentage);
  $("#credit_card_remaining_balance_" + data.entityId).html(result.remainingBalance);
  $("#credit_card_balance_" + data.entityId).html(result.balance);
  updateTotalForAccountType(result);
  updateSnapshotCreditCardTotals(result);
  updateNetWorth(result);
}

function updateUsedCreditPercentage(entityId, usedCreditPercentage) {
  let used_credit_percentage_span = $("#credit_card_used_credit_percentage_" + entityId);
  used_credit_percentage_span.html(usedCreditPercentage);

  let percentage = stripDecimalForText(usedCreditPercentage);
  colorizeCreditCardPercentage(percentage, used_credit_percentage_span);
}

function colorizeCreditCardPercentage(percentage, element) {
  if (percentage >= 30) {
    element.removeClass('cell-orange');
    element.addClass('cell-red');
  } else if (percentage >= 10) {
    element.removeClass('cell-red');
    element.addClass('cell-orange');
  } else {
    element.removeClass('cell-red');
    element.removeClass('cell-orange');
  }
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

function transactionDateUpdateSuccessCallback(data, result) {
  $("#transaction_date_" + data.entityId).html(result.date);
}

function transactionAmountUpdateSuccessCallback(data, result) {
  let txn_amount = $("#txn_amount_" + data.entityId);
  txn_amount.html(result.amount);
  let amount = stripDecimalForText(result.amount);
  if (amount > 0) {
    txn_amount.removeClass('cell-red');
    txn_amount.addClass('cell-green');
  } else if (amount < 0) {
    txn_amount.removeClass('cell-green');
    txn_amount.addClass('cell-red');
  } else {
    txn_amount.removeClass('cell-red');
    txn_amount.removeClass('cell-green');
  }

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
