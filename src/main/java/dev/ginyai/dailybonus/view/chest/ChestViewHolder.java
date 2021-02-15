package dev.ginyai.dailybonus.view.chest;

import dev.ginyai.dailybonus.api.bonus.BonusSet;
import dev.ginyai.dailybonus.api.view.DailyBonusView;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.config.ChestViewDisplaySettings;
import dev.ginyai.dailybonus.data.TrackedPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ChestViewHolder implements DailyBonusView {

    private final DailyBonusMain dailyBonus;
    private final UUID ownerId;
    private final TrackedPlayer trackedPlayer;
    private final Inventory inventory;
    private final Map<Integer, ChestElement> elementMap;

    public ChestViewHolder(DailyBonusMain dailyBonus, Player owner, ChestViewDisplaySettings settings) {
        this.dailyBonus = dailyBonus;
        this.ownerId = owner.getUniqueId();
        this.inventory = Inventory.builder()
            .listener(ClickInventoryEvent.class, this::onClick)
            .property(InventoryTitle.of(Text.of(settings.getTitle())))
            .property(InventoryDimension.of(9, settings.getSize()/ 9))
            .build(dailyBonus.getPlugin());
        this.elementMap = settings.getElements();
        trackedPlayer = dailyBonus.getPlayerDataManager().getOrCreatePlayerData(owner);
        updateInventoryContent();
    }

    public void updateInventoryContent() {
        for (Inventory slot: inventory.slots()) {
            //noinspection ConstantConditions
            int i = slot.getProperties(SlotIndex.class).iterator().next().getValue();
            slot.set(Optional.ofNullable(elementMap.get(i)).map(e -> e.getDisplay(trackedPlayer)).orElse(ItemStack.empty()));
        }
    }

    private void onClick(ClickInventoryEvent event) {
        Optional<Player> optionalPlayer = getOwner();
        Optional<Slot> optionalSlot = event.getSlot();
        if (!optionalPlayer.isPresent() || !optionalSlot.isPresent()) {
            return;
        }
        event.setCancelled(true);
        Player player = optionalPlayer.get();
        Slot slot = optionalSlot.get();
        //noinspection ConstantConditions
        int slotIndex = slot.getProperties(SlotIndex.class).iterator().next().getValue();
        ChestElement chestElement = elementMap.get(slotIndex);
        if (chestElement != null) {
            chestElement.onClick(player).ifPresent(future ->
                future
                    .thenApplyAsync(o -> {
                        if (o instanceof BonusSet.GiveResult) {
                            ((BonusSet.GiveResult) o).getFailMessage().ifPresent(player::sendMessage);
                        }
                        return dailyBonus.getPlayerDataManager().updatePlayerData(player).join();
                    })
                    .thenAcceptAsync(p -> updateInventoryContent(), dailyBonus.getSyncExecutor())
                    .whenComplete((aVoid, throwable) -> {
                        if (throwable != null) {
                            dailyBonus.getLogger().warn("Exception on update player data.", throwable);
                        }
                    })
            );
        }
    }

    @Override
    public void open() {
        getOwner().ifPresent(player -> player.openInventory(inventory));
    }

    @Override
    public Optional<Player> getOwner() {
        return Sponge.getServer().getPlayer(ownerId);
    }
}
