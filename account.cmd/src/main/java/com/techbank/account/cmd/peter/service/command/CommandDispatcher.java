package com.techbank.account.cmd.peter.service.command;

import com.techbank.account.common.commands.BaseCommand;
import com.techbank.account.common.handlers.CommandHandler;
import com.techbank.account.common.handlers.CommandHandlerMethod;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommandDispatcher {
    private final Map<Class<? extends BaseCommand>, CommandHandlerMethod<? extends BaseCommand>> routes = new HashMap<>();

    public CommandDispatcher(List<CommandHandler> handlers) {
        for (CommandHandler commandHandler : handlers) {

            for (Method method : commandHandler.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {

                    Command annotation = method.getAnnotation(Command.class);
                    Class<? extends BaseCommand> commandType = annotation.value();

                    CommandHandlerMethod<? extends BaseCommand> handler = command -> {
                        try {
                            method.invoke(commandHandler, command);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    };
                    routes.put(commandType, handler);
                }
            }
        }
    }

    public void dispatch(BaseCommand command) {
        var handler = routes.get(command.getClass());
        if (handler == null) {
            throw new RuntimeException("No command handler was registered!");
        }
        ((CommandHandlerMethod<BaseCommand>) handler).handle(command);
    }
}
