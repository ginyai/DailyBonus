package dev.ginyai.dailybonus.command;

import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.api.bonus.BonusSet;
import dev.ginyai.dailybonus.api.data.PlayerData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import java.util.Collection;

@NonnullByDefault
public class CommandBonus extends AbstractCommand {
    public CommandBonus(DailyBonusMain dailyBonus) {
        super("bonus", dailyBonus);
    }

    @Override
    protected CommandElement getArgs() {
        return GenericArguments.seq(
            new ArgPermissionOther(dailyBonus, Text.of("player"), getPermissionString("other")),
            GenericArguments.choices(Text.of("set"), () -> dailyBonus.getBonusSetMap().keySet(), s -> dailyBonus.getBonusSetById(s).orElse(null), false)
        );
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Collection<Player> players = args.getAll("player");
        BonusSet bonusSet = args.requireOne("set");
        for (Player player: players) {
            PlayerData playerData = dailyBonus.getPlayerDataManager().getOrCreatePlayerData(player);
            if (!bonusSet.getRequirements().stream().allMatch(r -> r.check(playerData))) {
                // TODO: 2021/2/15 Show requirement info
                throw new CommandException(getTranslation("requirement"));
            }
            bonusSet.give(player).whenComplete((giveResult, throwable) -> {
                if (throwable != null) {
                    dailyBonus.getLogger().error("Error on sign ", throwable);
                    src.sendMessage(getTranslation("exception"));
                } else {
                    if (giveResult.isSuccessful()) {
                        src.sendMessage(getTranslation("success"));
                    } else {
                        // TODO: 2021/2/15 Use  give result.getFailMessage
                        src.sendMessage(getTranslation("fail"));
                    }
                }
            });
        }
        return CommandResult.empty();
    }
}
