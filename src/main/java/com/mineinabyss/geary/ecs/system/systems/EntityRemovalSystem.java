package com.mineinabyss.geary.ecs.system.systems;

import com.mineinabyss.geary.ecs.Family;
import com.mineinabyss.geary.ecs.component.components.Remove;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.system.IteratingSystem;
import org.bukkit.entity.Entity;

/**
 * Removes entities that are marked with {@link Remove}. This system should run last.
 */
public class EntityRemovalSystem extends IteratingSystem {

  public EntityRemovalSystem() {
    super(Family.builder().setAll(Remove.class).build());
  }

  @Override
  protected void update(GearyEntity gearyEntity) {
    getEngine().removeEntity(gearyEntity);

    if (gearyEntity.getDataHolder() instanceof Entity) {
      ((Entity) gearyEntity.getDataHolder()).remove();
    }
    if (gearyEntity.getItemStack().isPresent()) {
      gearyEntity.getItemStack().get().setAmount(0);
    }
  }
}
