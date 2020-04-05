package com.mineinabyss.geary.ecs.system.systems;

import com.mineinabyss.geary.ecs.Family;
import com.mineinabyss.geary.ecs.component.components.control.Activated;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.system.IteratingSystem;

/**
 * Deactivates anything that has not been deactivated by another system.
 */
public class DeactivationSystem extends IteratingSystem {

  public DeactivationSystem() {
    super(Family.builder().setAll(Activated.class).build());
  }

  @Override
  protected void update(GearyEntity gearyEntity) {
    gearyEntity.removeComponent(Activated.class);
  }
}
