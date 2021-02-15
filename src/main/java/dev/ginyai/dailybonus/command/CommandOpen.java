package dev.ginyai.dailybonus.command;

import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.api.view.DailyBonusView;
import dev.ginyai.dailybonus.api.view.DailyBonusViewManager;
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
import java.util.function.Function;

@NonnullByDefault
public class CommandOpen extends AbstractCommand {

    public CommandOpen(DailyBonusMain dailyBonus) {
        super("open", dailyBonus);
    }

    @Override
    protected CommandElement getArgs() {
        return GenericArguments.seq(
            new ArgPermissionOther(dailyBonus, Text.of("player"), getPermissionString("other")),
            GenericArguments.choices(Text.of("view"), () -> dailyBonus.getViewManager().getUsableViews(), Function.identity(), false)
        );
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String viewId = args.requireOne("view");
        Collection<Player> players = args.getAll("player");
        for (Player player: players) {
            DailyBonusViewManager viewManager = dailyBonus.getViewManager();
            DailyBonusView view = viewManager.createView(player, viewId).orElseThrow(() -> new CommandException(getTranslation("no_such_view", "id", viewId)));
            view.open();
        }
        return CommandResult.success();
    }
}
