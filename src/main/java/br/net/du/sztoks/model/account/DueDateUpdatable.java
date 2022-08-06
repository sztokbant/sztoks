package br.net.du.sztoks.model.account;

import java.time.LocalDate;

public interface DueDateUpdatable {
    LocalDate getDueDate();

    void setDueDate(LocalDate date);
}
