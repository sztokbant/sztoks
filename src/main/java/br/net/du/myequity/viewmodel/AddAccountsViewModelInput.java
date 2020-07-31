package br.net.du.myequity.viewmodel;

import lombok.Data;

import java.util.List;

@Data
public class AddAccountsViewModelInput {
    private List<Long> accounts;
}
