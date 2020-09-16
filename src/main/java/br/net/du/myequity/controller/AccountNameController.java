package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.EntityNameJsonRequest;
import br.net.du.myequity.controller.model.EntityNameJsonResponse;
import br.net.du.myequity.model.account.Account;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountNameController extends AccountControllerBase {

    @PostMapping("/account/updateName")
    public EntityNameJsonResponse post(final Model model,
                                       @RequestBody final EntityNameJsonRequest entityNameJsonRequest) {
        final Account account = getAccount(model, entityNameJsonRequest.getId());

        account.setName(entityNameJsonRequest.getNewValue().trim());
        accountRepository.save(account);

        return EntityNameJsonResponse.builder().name(account.getName()).build();
    }
}
