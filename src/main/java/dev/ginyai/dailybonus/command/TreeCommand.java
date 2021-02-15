package dev.ginyai.dailybonus.command;

import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.util.UtilMethods;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NonnullByDefault
public class TreeCommand extends AbstractCommand {

    private final Map<String, ICommand> childrenMap = new LinkedHashMap<>();

    public TreeCommand(String name, DailyBonusMain dailyBonus) {
        super(name, dailyBonus);
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        int indexOfSpace = arguments.indexOf(' ');
        String sub;
        String left;
        if (indexOfSpace < 0) {
            sub = arguments;
            left = "";
        } else {
            sub = arguments.substring(0, indexOfSpace);
            left = arguments.substring(indexOfSpace + 1);
        }
        ICommand child = childrenMap.get(sub);
        if (child == null || !child.testPermission(source)) {
            throw new CommandException(getUsage(source));
        }
        return child.process(source, left);
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        int indexOfSpace = arguments.indexOf(' ');
        if (indexOfSpace < 0) {
            return UtilMethods.startWith(arguments, getChildrenMap().values().stream()
                .filter(c -> c.testPermission(source))
                .map(ICommand::getName));
        }
        String sub = arguments.substring(0, indexOfSpace);
        ICommand child = childrenMap.get(sub);
        if (child == null || !child.testPermission(source)) {
            return Collections.emptyList();
        }
        return child.getSuggestions(source, arguments.substring(indexOfSpace + 1), targetPosition);
    }

    public void addChild(ICommand child) {
        String commandName = child.getName();
        if (childrenMap.containsKey(commandName)) {
            throw new IllegalArgumentException("Child command " + commandName + " is already exists.");
        }
        childrenMap.put(commandName, child);
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

    @Override
    public Text getUsage(CommandSource source) {
        return getTranslation("usage",
            "children", getChildrenMap().values().stream().filter(c -> c.testPermission(source)).map(ICommand::getName).collect(Collectors.joining(", "))
        );
    }
}
