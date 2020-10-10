function updateSnapshotCreditCardTotals(result) {
  $("#snapshot_credit_card_total_credit_" + result.currencyUnit)
    .html(result.currencyUnitSymbol + result.creditCardTotalsForCurrencyUnit.totalCredit);
  $("#snapshot_credit_card_available_credit_" + result.currencyUnit)
    .html(result.currencyUnitSymbol + result.creditCardTotalsForCurrencyUnit.availableCredit);
  $("#snapshot_credit_card_used_credit_percentage_" + result.currencyUnit)
    .html(result.creditCardTotalsForCurrencyUnit.usedCreditPercentage);
  $("#snapshot_credit_card_statement_" + result.currencyUnit)
    .html(result.currencyUnitSymbol + result.creditCardTotalsForCurrencyUnit.statement);
  $("#snapshot_credit_card_remaining_balance_" + result.currencyUnit)
    .html(result.currencyUnitSymbol + result.creditCardTotalsForCurrencyUnit.remainingBalance);
  $("#snapshot_credit_card_balance_" + result.currencyUnit)
    .html(result.currencyUnitSymbol + result.creditCardTotalsForCurrencyUnit.balance);
}
