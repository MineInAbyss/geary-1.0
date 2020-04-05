package com.mineinabyss.geary.ecs.component.components.effect;

import com.mineinabyss.geary.ecs.component.Component;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import java.util.UUID;

/**
 * Pulls an entity to the location of this entity.
 */
public class PullToLocation implements Component {

  private UUID entityToPull;

  public PullToLocation() {
  }

  public PullToLocation(GearyEntity gearyEntity) {
    this.entityToPull = gearyEntity.getUUID();
  }

  public UUID getEntityToPull() {
    return entityToPull;
  }
}
