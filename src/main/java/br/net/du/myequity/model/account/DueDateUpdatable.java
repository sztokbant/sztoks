package br.net.du.myequity.model.account;

import java.time.LocalDate;

public interface DueDateUpdatable {
    LocalDate getDueDate();

    void setDueDate(LocalDate date);
}
