package com.techbank.account.cmd.peter.controller;

import com.techbank.account.cmd.peter.model.command.*;
import com.techbank.account.cmd.peter.model.dto.AccountResponse;
import com.techbank.account.cmd.peter.service.command.CommandDispatcher;
import com.techbank.account.common.exceptions.AggregateNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/bank-account")
public class AccountController {

    @Autowired
    private CommandDispatcher commandDispatcher;

    @PostMapping(path = "/open")
    public ResponseEntity<AccountResponse> openAccount(@RequestBody OpenAccountCommand command) {

        try {
            commandDispatcher.dispatch(command);
            return new ResponseEntity<>(new AccountResponse("Bank account creation request completed successfully!", command.getId()), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            log.error(MessageFormat.format("Client made a bad request - {0}.", e.toString()));
            return new ResponseEntity<>(new AccountResponse(null, e.toString()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            var safeErrorMessage = "Error while processing request to open a new bank account";
            log.error(safeErrorMessage, e);
            return new ResponseEntity<>(new AccountResponse(safeErrorMessage, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/deposit/{id}")
    public ResponseEntity<AccountResponse> depositFunds(@PathVariable(value = "id") String id,
                                                        @RequestBody DepositFundsCommand command) {
        try {
            command.setId(id);
            commandDispatcher.dispatch(command);
            return new ResponseEntity<>(new AccountResponse(null, "Deposit funds request completed successfully!"), HttpStatus.OK);
        } catch (IllegalStateException | AggregateNotFoundException e) {
            log.error(MessageFormat.format("Client made a bad request - {0}.", e.toString()));
            return new ResponseEntity<>(new AccountResponse(null, e.toString()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            var safeErrorMessage = MessageFormat.format("Error while processing request to deposit funds to bank account with id - {0}.", id);
            log.error(safeErrorMessage, e);
            return new ResponseEntity<>(new AccountResponse(null, safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/withdraw/{id}")
    public ResponseEntity<AccountResponse> withdrawFunds(@PathVariable(value = "id") String id,
                                                         @RequestBody WithdrawFundsCommand command) {
        try {
            command.setId(id);
            commandDispatcher.dispatch(command);
            return new ResponseEntity<>(new AccountResponse(null, "Withdraw funds request completed successfully!"), HttpStatus.OK);
        } catch (IllegalStateException | AggregateNotFoundException e) {
            log.error(MessageFormat.format("Client made a bad request - {0}.", e.toString()));
            return new ResponseEntity<>(new AccountResponse(null, e.toString()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            var safeErrorMessage = MessageFormat.format("Error while processing request to withdraw funds from bank account with id - {0}.", id);
            log.error(safeErrorMessage, e);
            return new ResponseEntity<>(new AccountResponse(null, safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/close/{id}")
    public ResponseEntity<AccountResponse> closeAccount(@PathVariable(value = "id") String id) {
        try {
            commandDispatcher.dispatch(new CloseAccountCommand(id));
            return new ResponseEntity<>(new AccountResponse(null, "Bank account closure request successfully completed!"), HttpStatus.OK);
        } catch (IllegalStateException | AggregateNotFoundException e) {
            log.error(MessageFormat.format("Client made a bad request - {0}.", e.toString()));
            return new ResponseEntity<>(new AccountResponse(null, e.toString()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            var safeErrorMessage = MessageFormat.format("Error while processing request to close bank account with id - {0}.", id);
            log.error(safeErrorMessage, e);
            return new ResponseEntity<>(new AccountResponse(null, safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/restore-read-db")
    public ResponseEntity<AccountResponse> restoreReadDb() {
        try {
            commandDispatcher.dispatch(new RestoreReadDbCommand());
            return new ResponseEntity<>(new AccountResponse(null, "Read database restore request completed successfully!"), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            log.error(MessageFormat.format("Client made a bad request - {0}.", e.toString()));
            return new ResponseEntity<>(new AccountResponse(null, e.toString()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            var safeErrorMessage = "Error while processing request to restore read database.";
            log.error(safeErrorMessage, e);
            return new ResponseEntity<>(new AccountResponse(null, safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
