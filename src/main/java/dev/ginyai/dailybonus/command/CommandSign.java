package dev.ginyai.dailybonus.command;

import dev.ginyai.dailybonus.api.bonus.SignGroup;
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
public class CommandSign extends AbstractCommand {

    public CommandSign(DailyBonusMain dailyBonus) {
        super("sign", dailyBonus);
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
        String groupId;
        if (args.length == 1 && (source instanceof Player)) {
            player = (Player) source;
            groupId = args[0];
        } else if (args.length == 2 && testOther(source)) {
            player = Sponge.getServer().getPlayer(args[0]).orElseThrow(() -> new CommandException(getTranslation("player_not_find", "name", args[0])));
            groupId = args[1];
        } else {
            throw new CommandException(getUsage(source));
        }
        SignGroup signGroup = dailyBonus.getSignGroupById(groupId).orElseThrow(() -> new CommandException(getTranslation("no_such_sign", "id", groupId)));;
        signGroup.sign(player)
            .whenComplete((aBoolean, throwable) -> {
                if (throwable != null) {
                    dailyBonus.getLogger().error("Error on sign ", throwable);
                    source.sendMessage(getTranslation("exception"));
                } else {
                    if (aBoolean) {
                        source.sendMessage(getTranslation("success"));
                    } else {
                        source.sendMessage(getTranslation("already_signed"));
                    }
                }
            });
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        String[] args = arguments.split(" ");
        if (args.length == 1) {
            String prefix = args[0];
            Stream<String> stream = Stream.of();
            if (testOther(source)) {
                stream = Sponge.getServer().getOnlinePlayers().stream().map(Player::getName);
            }
            if (source instanceof Player) {
                stream = Stream.concat(stream, dailyBonus.getSignGroupMap().keySet().stream());
            }
            return UtilMethods.startWith(prefix, stream);
        } else if (args.length == 2) {
            return UtilMethods.startWith(args[1], dailyBonus.getSignGroupMap().keySet());
        } else {
            return Collections.emptyList();
        }
    }
}
