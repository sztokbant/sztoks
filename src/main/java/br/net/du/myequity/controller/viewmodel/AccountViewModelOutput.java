package br.net.du.myequity.controller.viewmodel;

import br.net.du.myequity.model.account.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class AccountViewModelOutput implements Comparable<AccountViewModelOutput> {
    private final Long id;
    private final String name;

    public static AccountViewModelOutput of(final Account account) {
        return AccountViewModelOutput.builder().id(account.getId()).name(account.getName()).build();
    }

    @Override
    public int compareTo(final AccountViewModelOutput other) {
        return name.compareTo(other.name);
    }
}
