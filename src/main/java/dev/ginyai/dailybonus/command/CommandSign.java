package dev.ginyai.dailybonus.command;

import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.api.bonus.SignGroup;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

@NonnullByDefault
public class CommandSign extends AbstractCommand {

    public CommandSign(DailyBonusMain dailyBonus) {
        super("sign", dailyBonus);
    }

    @Override
    protected CommandElement getArgs() {
        return GenericArguments.seq(
            new ArgPermissionOther(dailyBonus, Text.of("player"), getPermissionString("other")),
            GenericArguments.choices(Text.of("sign"), dailyBonus::getSignGroupIds, s -> dailyBonus.getSignGroupById(s).orElse(null), false)
        );
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        SignGroup signGroup = args.requireOne("sign");
        for (Player player: args.<Player>getAll("player")) {
            signGroup.sign(player)
                .whenComplete((aBoolean, throwable) -> {
                    if (throwable != null) {
                        dailyBonus.getLogger().error("Error on sign ", throwable);
                        src.sendMessage(getTranslation("exception"));
                    } else {
                        if (aBoolean) {
                            src.sendMessage(getTranslation("success"));
                        } else {
                            src.sendMessage(getTranslation("already_signed"));
                        }
                    }
                });
        }
        return CommandResult.empty();
    }
}
