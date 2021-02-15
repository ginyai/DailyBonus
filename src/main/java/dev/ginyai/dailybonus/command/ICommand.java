package dev.ginyai.dailybonus.command;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import java.util.Collection;
import java.util.Collections;

@NonnullByDefault
public interface ICommand extends CommandExecutor {

    String getName();

    default Collection<String> getAlias() {
        return Collections.emptyList();
    }

    @Override
    CommandResult execute(CommandSource src, CommandContext args) throws CommandException;

    CommandCallable toCallable();
}
