package br.net.du.myequity.viewmodel;

import lombok.Data;

import java.util.List;

@Data
public class AddAccountsToSnapshotViewModelInput {
    private List<Long> accounts;
}
