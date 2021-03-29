package br.net.du.myequity.model.account;

import java.time.LocalDate;

public interface DueDateUpdateable {
    LocalDate getDueDate();

    void setDueDate(LocalDate date);
}
