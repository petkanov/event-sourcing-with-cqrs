package com.techbank.account.cmd.peter.model.command;

import com.techbank.account.common.commands.BaseCommand;
import lombok.Data;

@Data
public class DepositFundsCommand extends BaseCommand {
    private double amount;
}
