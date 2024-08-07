package com.techbank.account.cmd.peter.service.command;

import com.techbank.account.common.commands.BaseCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    Class<? extends BaseCommand> value();
}
