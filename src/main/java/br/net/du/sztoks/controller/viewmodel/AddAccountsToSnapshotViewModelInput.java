package br.net.du.sztoks.controller.viewmodel;

import java.util.List;
import lombok.Data;

@Data
public class AddAccountsToSnapshotViewModelInput {
    private List<Long> accounts;
}
