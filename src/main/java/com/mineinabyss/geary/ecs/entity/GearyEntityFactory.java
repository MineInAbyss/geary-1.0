package com.mineinabyss.geary.ecs.entity;

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

  /**
   * Current entity version for geary. Increment on breaking change and make sure to create a new
   * {@link com.mineinabyss.geary.ecs.entity.migration.GearyEntityMigration} that upgrades old
   * entities.
   */
  public static final long ENTITY_VERSION = 1;

  public GearyEntity createEntity(Entity entity) {
    return new GearyEntityImpl(entity, ENTITY_VERSION, UUID.randomUUID());
  }

  public GearyEntity createEntity(ItemStack itemStack, Player player) {
    return new GearyEntityImpl(itemStack, player, ENTITY_VERSION, UUID.randomUUID());
  }

  public GearyEntity createEntity(Entity entity, long version, UUID uuid) {
    return new GearyEntityImpl(entity, version, uuid);
  }

  public GearyEntity createEntity(ItemStack itemStack, Player player, long version, UUID uuid) {
    return new GearyEntityImpl(itemStack, player, version, uuid);
  }

  public static class GearyEntityImpl implements GearyEntity {

    private ItemStack itemStack = null;
    private final PersistentDataHolder persistentDataHolder;
    private final long version;
    private final Map<Class<? extends Component>, Component> components;
    private final Map<String, PlaceholderComponent> placeholders;
    private final UUID uuid;
    private Player player;

    private GearyEntityImpl(ItemStack itemStack, Player player, long version, UUID uuid) {
      this(itemStack.getItemMeta(), version, uuid);
      this.itemStack = itemStack;
      this.player = player;
    }

    private GearyEntityImpl(PersistentDataHolder persistentDataHolder, long version, UUID uuid) {
      this.persistentDataHolder = persistentDataHolder;
      this.version = version;
      this.uuid = uuid;
      this.components = new HashMap<>();
      this.placeholders = new HashMap<>();
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
    public long getVersion() {
      return version;
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
