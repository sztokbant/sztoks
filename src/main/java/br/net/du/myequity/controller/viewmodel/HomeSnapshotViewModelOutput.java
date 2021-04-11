package br.net.du.myequity.controller.viewmodel;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class HomeSnapshotViewModelOutput {
    private final long id;
    private final String name;
    private final String netWorth;
}
