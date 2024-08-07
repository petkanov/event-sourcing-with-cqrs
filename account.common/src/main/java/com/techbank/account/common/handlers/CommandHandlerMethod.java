package com.techbank.account.common.handlers;

import com.techbank.account.common.commands.BaseCommand;

@FunctionalInterface
public interface CommandHandlerMethod<T extends BaseCommand> {
    void handle(T command);
}
