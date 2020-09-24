package br.net.du.myequity.model.snapshot;

import java.time.LocalDate;

public interface DueDateUpdateable {
    LocalDate getDueDate();

    void setDueDate(LocalDate date);
}
