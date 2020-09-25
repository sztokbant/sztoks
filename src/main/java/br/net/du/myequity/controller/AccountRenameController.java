package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.EntityRenameJsonRequest;
import br.net.du.myequity.controller.model.EntityRenameJsonResponse;
import br.net.du.myequity.model.account.Account;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountRenameController extends AccountControllerBase {

    @PostMapping("/account/updateName")
    public EntityRenameJsonResponse post(final Model model,
                                         @RequestBody final EntityRenameJsonRequest entityNameJsonRequest) {
        final Account account = getAccount(model, entityNameJsonRequest.getId());

        account.setName(entityNameJsonRequest.getNewValue().trim());
        accountRepository.save(account);

        return EntityRenameJsonResponse.builder().name(account.getName()).build();
    }
}
