package com.mineinabyss.geary.ecs.component.components.grappling;

import com.mineinabyss.geary.ecs.component.Component;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import java.util.UUID;

/**
 * Stores information about an extended grappling hook.
 */
public class GrapplingHookExtended implements Component {

  private UUID extendedEntityId;

  public GrapplingHookExtended() {
  }

  public GrapplingHookExtended(GearyEntity extendedEntity) {
    this.extendedEntityId = extendedEntity.getUUID();
  }

  /**
   * Reference to the projectile that was fired by this entity.
   */
  public UUID getExtendedEntity() {
    return extendedEntityId;
  }
}
