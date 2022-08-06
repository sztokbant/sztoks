package br.net.du.sztoks.controller.viewmodel;

import br.net.du.sztoks.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserViewModelOutput {
    private final Long id;
    private final String fullName;
    private final String email;

    public static UserViewModelOutput of(final User user) {
        return UserViewModelOutput.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }
}
