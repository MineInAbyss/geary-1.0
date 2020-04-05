package com.mineinabyss.geary.ecs.system.systems;

import com.google.common.collect.ImmutableSet;
import com.mineinabyss.geary.ecs.Family;
import com.mineinabyss.geary.ecs.component.components.ProjectileHitComponents;
import com.mineinabyss.geary.ecs.component.components.ProjectileHitGround;
import com.mineinabyss.geary.ecs.component.components.Remove;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.system.IteratingSystem;

/**
 * When an entity has a {@link ProjectileHitGround} component, this system adds on hit components to
 * the entity.
 */
public class ProjectileCollisionSystem extends IteratingSystem {

  public ProjectileCollisionSystem() {
    super(Family.builder()
        .setAll(ImmutableSet.of(ProjectileHitComponents.class, ProjectileHitGround.class))
        .build());
  }

  @Override
  protected void update(GearyEntity gearyEntity) {
    ProjectileHitComponents projectileHitComponents = gearyEntity.getComponent(
        ProjectileHitComponents.class);
    gearyEntity.removeComponent(ProjectileHitComponents.class);

    projectileHitComponents.getCollisionComponents().get().forEach(gearyEntity::addComponent);

    // Projectiles are removed by default. If they have collision components they should persist.
    gearyEntity.removeComponent(Remove.class);
  }
}
