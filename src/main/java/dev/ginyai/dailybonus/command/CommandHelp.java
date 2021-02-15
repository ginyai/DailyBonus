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
import java.util.List;
import java.util.stream.Collectors;

@NonnullByDefault
public class CommandHelp extends AbstractCommand {
    private final TreeCommand parent;

    public CommandHelp(DailyBonusMain dailyBonus, TreeCommand parent) {
        super("help", dailyBonus);
        this.parent = parent;
    }

    @Override
    public Text getUsage(CommandSource source) {
        return getTranslation("usage",
            "children", parent.getChildrenMap().values().stream().filter(c -> c.testPermission(source)).map(ICommand::getName).collect(Collectors.joining(", "))
        );
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        String[] args = arguments.split(" ");
        String childName;
        if (args.length == 1) {
            childName = args[0];
        } else {
            childName = getName();
        }
        ICommand child = parent.getChildrenMap().values().stream()
            .filter(c -> c.testPermission(source))
            .filter(c -> childName.equals(c.getName()))
            .findAny().orElse(null);
        if (child == null) {
            throw new CommandException(getTranslation("not_find", "command", args[0]));
        }
        source.sendMessage(getUsage(source));
        return CommandResult.success();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        return UtilMethods.startWith(arguments, parent.getChildrenMap().values().stream()
            .filter(c -> c.testPermission(source))
            .map(ICommand::getName));
    }
}
