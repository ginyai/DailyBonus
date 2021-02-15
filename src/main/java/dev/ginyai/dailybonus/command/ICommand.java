package dev.ginyai.dailybonus.command;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@NonnullByDefault
public interface ICommand extends CommandCallable {

    String getName();

    @Override
    CommandResult process(CommandSource source, String arguments) throws CommandException;

    @Override
    List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws org.spongepowered.api.command.CommandException;

    @Override
    boolean testPermission(CommandSource source);

    @Override
    Optional<Text> getShortDescription(CommandSource source);

    @Override
    default Optional<Text> getHelp(CommandSource source) {
        return Optional.of(getUsage(source));
    }

    @Override
    Text getUsage(CommandSource source);
}
