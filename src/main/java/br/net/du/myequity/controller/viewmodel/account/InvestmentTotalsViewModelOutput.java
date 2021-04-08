package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.model.totals.InvestmentsTotal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@AllArgsConstructor
@Data
@Builder
public class InvestmentTotalsViewModelOutput {
    private final String amountInvested;
    private final String profitPercentage;
    private final String balance;

    public static InvestmentTotalsViewModelOutput of(final InvestmentsTotal investmentTotals) {
        final CurrencyUnit currencyUnit = investmentTotals.getCurrencyUnit();

        final String amountInvested =
                format(currencyUnit, toDecimal(investmentTotals.getAmountInvested()));

        final String profitPercentage = formatAsPercentage(investmentTotals.getProfitPercentage());

        final String balance = format(currencyUnit, toDecimal(investmentTotals.getBalance()));

        return new InvestmentTotalsViewModelOutput(amountInvested, profitPercentage, balance);
    }
}
