package br.net.du.myequity.controller.viewmodel;

import lombok.Data;

import java.util.List;

@Data
public class AddAccountsToSnapshotViewModelInput {
    private List<Long> accounts;
}
