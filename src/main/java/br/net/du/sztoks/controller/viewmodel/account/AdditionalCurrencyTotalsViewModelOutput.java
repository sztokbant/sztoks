package br.net.du.sztoks.controller.viewmodel.account;

import static br.net.du.sztoks.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.sztoks.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.sztoks.controller.util.ControllerUtils.toDecimal;
import static br.net.du.sztoks.controller.util.MoneyFormatUtils.format;

import br.net.du.sztoks.model.totals.AdditionalCurrenciesTotal;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@AllArgsConstructor
@Data
@Builder
public class AdditionalCurrencyTotalsViewModelOutput {
    private final String currencyUnit;
    private final String shares;
    private final String amountInvested;
    private final String averagePurchasePrice;
    private final String profitPercentage;
    private final String balance;

    /**
     * @param baseCurrency snapshot base (amountInvested, avg price, and balance are in base)
     */
    public static AdditionalCurrencyTotalsViewModelOutput of(
            final AdditionalCurrenciesTotal total, final CurrencyUnit baseCurrency) {
        return AdditionalCurrencyTotalsViewModelOutput.builder()
                .currencyUnit(total.getCurrencyUnit().getCode())
                .shares(new BigDecimal(formatAsDecimal(total.getShares())).toString())
                .amountInvested(format(baseCurrency, toDecimal(total.getAmountInvested())))
                .averagePurchasePrice(
                        format(baseCurrency, toDecimal(total.getAveragePurchasePrice())))
                .profitPercentage(formatAsPercentage(total.getProfitPercentage()))
                .balance(format(baseCurrency, toDecimal(total.getBalance())))
                .build();
    }
}
