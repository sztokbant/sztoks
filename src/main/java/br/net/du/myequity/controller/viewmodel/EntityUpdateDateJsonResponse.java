package br.net.du.myequity.controller.viewmodel;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityUpdateDateJsonResponse {
    private LocalDate date;
}
