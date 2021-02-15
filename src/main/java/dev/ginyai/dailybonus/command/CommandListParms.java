package dev.ginyai.dailybonus.command;

import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.api.data.PlayerData;
import dev.ginyai.dailybonus.placeholder.PlaceholderVisitor;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import java.util.Map;

@NonnullByDefault
public class CommandListParms extends AbstractCommand {
    public CommandListParms(DailyBonusMain dailyBonus) {
        super("list_parms", dailyBonus);
    }

    @Override
    protected CommandElement getArgs() {
        return GenericArguments.seq(
            GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("player"))),
            GenericArguments.optionalWeak(GenericArguments.string(Text.of("prefix")))
        );
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = args.requireOne("player");
        String prefix = args.<String>getOne("prefix").orElse("");
        PlayerData playerData = dailyBonus.getPlayerDataManager().getOrCreatePlayerData(player);
        PlaceholderVisitor visitor = new PlaceholderVisitor();
        playerData.visitData(visitor);
        visitor.getValues().entrySet().stream()
            .filter(e -> e.getKey().startsWith(prefix))
            .sorted(Map.Entry.comparingByKey())
            .map(e -> getTranslation("line", "key", e.getKey(), "value", e.getValue()))
            .forEach(src::sendMessage);
        return CommandResult.success();
    }
}
