package dev.ginyai.dailybonus.command;

import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@NonnullByDefault
public class TreeCommand extends AbstractCommand {

    private final Map<String, ICommand> childrenMap = new LinkedHashMap<>();

    public TreeCommand(String name, DailyBonusMain dailyBonus) {
        super(name, dailyBonus);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // TODO: 2021/2/15 Show something?
        return CommandResult.success();
    }

    @Override
    protected CommandElement getArgs() {
        return GenericArguments.none();
    }

    @Override
    public CommandCallable toCallable() {
        if (callable == null) {
            CommandSpec.Builder builder = CommandSpec.builder()
                .permission(getPermissionString("base"))
                .arguments(getArgs())
                .executor(this);
            for (ICommand child: childrenMap.values()) {
                List<String> alias = new ArrayList<>();
                alias.add(child.getName());
                alias.addAll(child.getAlias());
                builder.child(child.toCallable(), alias);
            }
            callable = builder.build();
        }
        return callable;
    }

    public void addChild(ICommand child) {
        String commandName = child.getName();
        if (childrenMap.containsKey(commandName)) {
            throw new IllegalArgumentException("Child command " + commandName + " is already exists.");
        }
        childrenMap.put(commandName, child);
        callable = null;
    }

    public void addHelp() {
        addChild(new CommandHelp(dailyBonus, this));
    }

    public Map<String, ICommand> getChildrenMap() {
        return childrenMap;
    }

    @Override
    protected String getTranslationKey() {
        return "command.tree.";
    }

    Stream<ICommand> getUsableCommands(CommandSource src) {
        return childrenMap.values().stream().filter(c -> c.toCallable().testPermission(src));
    }
}
