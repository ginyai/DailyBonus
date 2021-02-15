package dev.ginyai.dailybonus.command;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@NonnullByDefault
public class ArgChildCommand extends CommandElement {
    private final TreeCommand parent;

    protected ArgChildCommand(@Nullable Text key, TreeCommand parent) {
        super(key);
        this.parent = parent;
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String s = args.next();
        return Optional.ofNullable(parent.getChildrenMap().get(s))
            .filter(iCommand -> iCommand.toCallable().testPermission(source))
            .orElseThrow(() -> args.createError(parent.getTranslation("no_such_child", "name", s)));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        String prefix = args.nextIfPresent().orElse("").toLowerCase(Locale.ROOT);
        return parent.getUsableCommands(src)
            .map(ICommand::getName)
            .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(prefix))
            .collect(Collectors.toList());
    }
}
