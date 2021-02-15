package dev.ginyai.dailybonus;

import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.serializer.TextParseException;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(
    id = DailyBonusPlugin.PLUGIN_ID,
    name = DailyBonusPlugin.NAME,
    version = DailyBonusPlugin.VERSION,
    description = DailyBonusPlugin.DESCRIPTION,
    authors = {"GiNYAi"}
)
public class DailyBonusPlugin {
    public static final String PLUGIN_ID = "dailybonus";
    public static final String NAME = "DailyBonus";
    public static final String VERSION = "@version@";
    public static final String DESCRIPTION = "DailyBonusPlugin";

    @Inject
    @ConfigDir(sharedRoot = true)
    private Path sharedConfigDir;

    private static DailyBonusPlugin instance;

    public static DailyBonusPlugin getInstance() {
        return instance;
    }

    private final DailyBonusMain dailyBonus;

    public DailyBonusPlugin() {
        instance = this;
        dailyBonus = new DailyBonusMain(this);
    }

    public DailyBonusMain getDailyBonus() {
        return dailyBonus;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        dailyBonus.onPreInit(sharedConfigDir.resolve(NAME), s -> {
            try {
                return TextSerializers.JSON.deserialize(s);
            } catch (TextParseException e) {
                return TextSerializers.FORMATTING_CODE.deserializeUnchecked(s);
            }
        });
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        dailyBonus.onInit();
    }

    @Listener
    public void onPostInit(GamePostInitializationEvent event) {
        dailyBonus.onPostInit();
    }

    @Listener
    public void onStartingServer(GameStartingServerEvent event) {
        Sponge.getCommandManager().register(this, dailyBonus.getRootCommand().toCallable(), "dailybonus", "sign");
    }

    @Listener
    public void onGameStop(GameStoppingServerEvent event) {
        dailyBonus.onClose();
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        Task.builder().interval(1, TimeUnit.SECONDS)
            .name("DailyBonusTickTask")
            .execute(dailyBonus::tick)
            .submit(this);
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        dailyBonus.onPlayerJoin(event.getTargetEntity());
    }

    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        dailyBonus.getPlayerDataManager().onPlayerLeave(event.getTargetEntity());
    }
}
