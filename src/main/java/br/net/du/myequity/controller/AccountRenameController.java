package br.net.du.myequity.controller;

import br.net.du.myequity.controller.util.AccountUtils;
import br.net.du.myequity.controller.viewmodel.EntityRenameJsonRequest;
import br.net.du.myequity.controller.viewmodel.EntityRenameJsonResponse;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.service.AccountSnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountRenameController {

    @Autowired private AccountSnapshotService accountService;

    @Autowired private AccountUtils accountUtils;

    @PostMapping("/account/updateName")
    public EntityRenameJsonResponse post(
            final Model model, @RequestBody final EntityRenameJsonRequest entityNameJsonRequest) {
        final AccountSnapshot account =
                accountUtils.getAccount(model, entityNameJsonRequest.getId());

        account.setName(entityNameJsonRequest.getNewValue().trim());
        accountService.save(account);

        return EntityRenameJsonResponse.builder().name(account.getName()).build();
    }
}
