package com.mineinabyss.geary.ecs.system.systems.rendering;

import com.google.common.collect.ImmutableSet;
import com.mineinabyss.geary.ecs.Family;
import com.mineinabyss.geary.ecs.component.components.equipment.Durability;
import com.mineinabyss.geary.ecs.component.components.rendering.DisplayState;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.system.IteratingSystem;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * System that updates display models and durability for items associated with ECS entities.
 */
public class ItemDisplaySystem extends IteratingSystem {

  public ItemDisplaySystem() {
    super(Family.builder()
        .setOne(ImmutableSet.of(
            DisplayState.class,
            Durability.class))
        .build());
  }

  @Override
  protected void update(GearyEntity gearyEntity) {
    Optional<DisplayState> displayState = Optional
        .ofNullable(gearyEntity.getComponent(DisplayState.class));
    Optional<Durability> durability = Optional
        .ofNullable(gearyEntity.getComponent(Durability.class));

    Optional<ItemStack> equipped = gearyEntity.getItemStack();

    if (equipped.isPresent() && equipped.get().hasItemMeta()) {
      ItemMeta itemMeta = (ItemMeta) gearyEntity.getDataHolder();
      displayState
          .map(DisplayState::getModelNo)
          .ifPresent(itemMeta::setCustomModelData);
      durability
          .ifPresent(dur -> setDurability(dur, equipped.get(), itemMeta));
      equipped.get().setItemMeta(itemMeta);
    }
  }

  private void setDurability(Durability durability, ItemStack itemStack, ItemMeta itemMeta) {
    if (itemMeta instanceof Damageable) {
      Damageable damageable = (Damageable) itemMeta;

      int maxDurability = itemStack.getType().getMaxDurability();

      int maxUses = durability.getMaxUses();
      int currentUses = durability.getCurrentUses();

      double multiplier = 1 - currentUses / (1.0 * maxUses);

      damageable.setDamage((int) (maxDurability * multiplier));

      itemStack.setItemMeta(itemMeta);
    }
  }
}
