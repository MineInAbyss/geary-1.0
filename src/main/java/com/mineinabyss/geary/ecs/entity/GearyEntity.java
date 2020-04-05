package com.mineinabyss.geary.ecs.entity;

import com.mineinabyss.geary.ecs.component.Component;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;

/**
 * An entity in geary's ECS.
 */
public interface GearyEntity {

  /**
   * Get the underlying data holder for this entity.
   */
  PersistentDataHolder getDataHolder();

  /**
   * Gets the contained component of type {@code T} or null if this entity does not contain {@code
   * T}
   */
  <T extends Component> T getComponent(Class<T> componentClass);

  /**
   * Adds the provided component to this entity. Adding the same type of component a second time
   * overwrites the first one.
   */
  void addComponent(Component component);

  /**
   * Removes the component with this type from this entity. Noop if the component is not contained.
   */
  void removeComponent(Class<? extends Component> componentClass);

  /**
   * Returns true if this entity has a component with this type.
   */
  boolean hasComponent(Class<? extends Component> componentClass);

  /**
   * Gets the ItemStack this entity is attached to, if it exists.
   */
  Optional<ItemStack> getItemStack();

  /**
   * Gets this entities UUID.
   */
  UUID getUUID();

  /**
   * Gets the Player this entity is held by, if it exists.
   */
  Optional<Player> getHoldingPlayer();

  /**
   * Gets all components within this entity.
   */
  Set<Component> getComponents();
}
