package br.net.du.sztoks.controller.viewmodel;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class SnapshotSummaryViewModelOutput {
    private final long id;
    private final int year;
    private final int month;
    private final String netWorth;

    public String getName() {
        return SnapshotViewModelOutput.getDisplayTitle(year, month);
    }
}
