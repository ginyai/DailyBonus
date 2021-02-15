package dev.ginyai.dailybonus.command;

import dev.ginyai.dailybonus.api.data.PlayerData;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.placeholder.PlaceholderVisitor;
import dev.ginyai.dailybonus.util.UtilMethods;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@NonnullByDefault
public class CommandListParms extends AbstractCommand {
    public CommandListParms(DailyBonusMain dailyBonus) {
        super("list_parms", dailyBonus);
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        Player player = Sponge.getServer().getPlayer(arguments).orElseThrow(() -> new CommandException(getTranslation("player_not_find", "name", arguments)));
        PlayerData playerData = dailyBonus.getPlayerDataManager().getOrCreatePlayerData(player);
        PlaceholderVisitor visitor = new PlaceholderVisitor();
        playerData.visitData(visitor);
        visitor.getValues().entrySet().stream().sorted(Map.Entry.comparingByKey())
            .map(e -> getTranslation("line", "key", e.getKey(), "value", e.getValue()))
            .forEach(source::sendMessage);
        return CommandResult.success();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        return UtilMethods.startWith(arguments, Sponge.getServer().getOnlinePlayers().stream().map(Player::getName));
    }
}
