package com.mineinabyss.geary;

import com.mineinabyss.geary.ecs.component.Component;
import com.mineinabyss.geary.ecs.system.GearySystem;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Service interface for {@link Geary}, exposed to other plugins.
 */
public interface GearyService {

  /**
   * Associates the provided components with the item stack.
   * <p>
   * Note that the components are associated by updating the {@link org.bukkit.inventory.meta.ItemMeta}
   * of the item stack.
   *
   * @param components Set of components to attach.
   * @param itemStack  The itemstack to attach to.
   */
  void attachToItemStack(Set<Component> components, ItemStack itemStack);

  /**
   * Associates the provided components with the entity.
   *
   * @param components Set of components to attach.
   * @param entity     The entity to attach to.
   */
  void attachToEntity(Set<Component> components, Entity entity);

  /**
   * Adds an additional system for processing geary entities.
   */
  void addSystem(GearySystem entitySystem, Plugin plugin);
}
