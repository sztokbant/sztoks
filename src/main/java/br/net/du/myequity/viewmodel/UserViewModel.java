package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserViewModel {
    private final Long id;
    private final String fullName;
    private final String email;

    public static UserViewModel of(final User user) {
        return UserViewModel.builder().id(user.getId()).fullName(user.getFullName()).email(user.getEmail()).build();
    }
}
