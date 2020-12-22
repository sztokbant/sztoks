package br.net.du.myequity.controller.viewmodel;

import java.util.List;
import lombok.Data;

@Data
public class AddAccountsToSnapshotViewModelInput {
    private List<Long> accounts;
}
