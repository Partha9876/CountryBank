package com.cbank.services.impl;

import com.cbank.domain.Account;
import com.cbank.domain.RegistrationForm;
import com.cbank.domain.message.MessageTemplate;
import com.cbank.domain.security.BaseTokenType;
import com.cbank.services.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Podshivalov N.A.
 * @since 21.11.2017.
 */
@Slf4j
@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final UserService userService;
    private final TokenService tokenService;
    private final ClientService clientService;
    private final AccountService accountService;
    private final MessageService messageService;


    @Override
    public Account register(RegistrationForm form) {
        val user = userService.save(form.getUsername(), form.getPassword());
        val token = tokenService.create(user.getUsername(), BaseTokenType.REGISTRATION);

        val client  = form.toClient();
        clientService.save(client);

        val account = new Account(client.getId());
        accountService.save(account);

        messageService.send(client.getEmail(), MessageTemplate.REGISTRATION_CONFIRMATION,
                Map.of("user", user, "client", client,
                        "account", account,
                        "token", token) );

        log.debug("Account has been saved successfully: " + account);
        return account;
    }

    @Override
    public Account confirm() {
        return null;
    }
}
