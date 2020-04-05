package com.mineinabyss.geary.ecs.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.mineinabyss.geary.core.nbt.ComponentArrayTagType.PlaceholderComponent;
import com.mineinabyss.geary.ecs.component.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;

public class GearyEntityFactory {

  public GearyEntity createEntity(Entity entity, UUID uuid) {
    return new GearyEntityImpl(entity, uuid);
  }

  public GearyEntity createEntity(ItemStack itemStack, UUID uuid, Player player) {
    return new GearyEntityImpl(itemStack, uuid, player);
  }

  private static class GearyEntityImpl implements GearyEntity {

    private ItemStack itemStack = null;
    private PersistentDataHolder persistentDataHolder;
    private Map<Class<? extends Component>, Component> components;
    private Map<String, PlaceholderComponent> placeholders;
    private UUID uuid;
    private Player player;

    private GearyEntityImpl(Entity entity, UUID uuid) {
      this((PersistentDataHolder) entity, uuid);
    }

    private GearyEntityImpl(ItemStack itemStack, UUID uuid, Player player) {
      this(itemStack.getItemMeta(), uuid);
      this.itemStack = itemStack;
      this.player = player;
    }

    private GearyEntityImpl(PersistentDataHolder persistentDataHolder, UUID uuid) {
      Preconditions.checkNotNull(uuid);
      this.persistentDataHolder = persistentDataHolder;
      this.components = new HashMap<>();
      this.placeholders = new HashMap<>();
      this.uuid = uuid;
    }

    @Override
    public PersistentDataHolder getDataHolder() {
      return persistentDataHolder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentClass) {
      if (componentClass.equals(PlaceholderComponent.class)) {
        throw new IllegalArgumentException("Placeholders can not be retrieved.");
      }
      return (T) components.get(componentClass);
    }

    @Override
    public void addComponent(Component component) {
      if (component instanceof PlaceholderComponent) {
        PlaceholderComponent placeholder = (PlaceholderComponent) component;
        placeholders.put(placeholder.getClassName(), placeholder);
      } else {
        components.put(component.getClass(), component);
      }
    }

    @Override
    public boolean hasComponent(Class<? extends Component> componentClass) {
      return components.containsKey(componentClass);
    }

    @Override
    public Optional<ItemStack> getItemStack() {
      return Optional.ofNullable(itemStack);
    }

    @Override
    public void removeComponent(Class<? extends Component> componentClass) {
      components.remove(componentClass);
    }

    @Override
    public UUID getUUID() {
      return uuid;
    }

    @Override
    public Optional<Player> getHoldingPlayer() {
      return Optional.ofNullable(player);
    }

    @Override
    public Set<Component> getComponents() {
      return ImmutableSet.<Component>builder()
          .addAll(components.values())
          .addAll(placeholders.values())
          .build();
    }
  }
}
