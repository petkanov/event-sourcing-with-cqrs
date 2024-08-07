package com.techbank.account.cmd.peter.model.command;

import com.techbank.account.common.dto.AccountType;
import com.techbank.account.common.commands.BaseCommand;
import lombok.Data;

@Data
public class OpenAccountCommand extends BaseCommand {
    private String accountHolder;
    private AccountType accountType;
    private double openingBalance;
}