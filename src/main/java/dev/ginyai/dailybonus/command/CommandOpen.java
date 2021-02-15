package dev.ginyai.dailybonus.command;

import dev.ginyai.dailybonus.api.view.DailyBonusView;
import dev.ginyai.dailybonus.api.view.DailyBonusViewManager;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.util.UtilMethods;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@NonnullByDefault
public class CommandOpen extends AbstractCommand {

    public CommandOpen(DailyBonusMain dailyBonus) {
        super("open", dailyBonus);
    }

    private boolean testOther(CommandSource source) {
        return source.hasPermission(getPermissionString("other"));
    }

    @Override
    public Text getUsage(CommandSource source) {
        if (source instanceof Player) {
            if (testOther(source)) {
                return getTranslation("usage.other");
            } else {
                return getTranslation("usage.player");
            }
        } else {
            return getTranslation("usage.no_player");
        }
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        String[] args = arguments.split(" ");
        Player player;
        String viewId;
        if (args.length == 1 && source instanceof Player) {
            player = (Player) source;
            viewId = args[0];
        } else if (args.length == 2 && testOther(source)) {
            player = Sponge.getServer().getPlayer(args[0]).orElseThrow(() -> new CommandException(getTranslation("player_not_find", "name", args[0])));
            viewId = args[1];
        } else {
            throw new CommandException(getUsage(source));
        }
        DailyBonusViewManager viewManager = dailyBonus.getViewManager();
        DailyBonusView view = viewManager.createView(player, viewId).orElseThrow(() -> new CommandException(getTranslation("no_such_view", "id", viewId)));
        view.open();
        return CommandResult.success();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        String[] args = arguments.split(" ");
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0];
            Stream<String> stream = Stream.of();
            if (testOther(source)) {
                stream = Sponge.getServer().getOnlinePlayers().stream().map(Player::getName);
            }
            if (source instanceof Player) {
                stream = Stream.concat(stream, dailyBonus.getViewManager().getUsableViews().stream());
            }
            return UtilMethods.startWith(prefix, stream);
        } else if (args.length == 2) {
            return UtilMethods.startWith(args[1], dailyBonus.getViewManager().getUsableViews().stream());
        } else {
            return Collections.emptyList();
        }
    }
}
