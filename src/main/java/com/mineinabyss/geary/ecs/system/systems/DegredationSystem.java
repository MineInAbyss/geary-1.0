package com.mineinabyss.geary.ecs.system.systems;

import com.google.common.collect.ImmutableSet;
import com.mineinabyss.geary.ecs.Family;
import com.mineinabyss.geary.ecs.component.components.Remove;
import com.mineinabyss.geary.ecs.component.components.equipment.Degrading;
import com.mineinabyss.geary.ecs.component.components.equipment.Durability;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.system.IteratingSystem;

/**
 * Lowers the durability of items that have the {@link Degrading} component.
 */
public class DegredationSystem extends IteratingSystem {

  public DegredationSystem() {
    super(Family.builder().setAll(ImmutableSet.of(Durability.class, Degrading.class)).build());
  }

  @Override
  protected void update(GearyEntity gearyEntity) {
    Durability durability = gearyEntity.getComponent(Durability.class);
    durability.setCurrentUses(durability.getCurrentUses() - 1);

    if (durability.getCurrentUses() <= 0) {
      gearyEntity.addComponent(new Remove());
    }

    gearyEntity.removeComponent(Degrading.class);
  }
}
