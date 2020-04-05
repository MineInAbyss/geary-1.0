package com.mineinabyss.geary.ecs.component.components;

import com.mineinabyss.geary.ecs.component.Component;
import com.mineinabyss.geary.ecs.component.ComponentSupplier;

/**
 * Component storing additional components to be applied when a projectile hits.
 */
public class ProjectileHitComponents implements Component {

  private ComponentSupplier collisionComponents;

  public ProjectileHitComponents() {
  }

  public ProjectileHitComponents(ComponentSupplier onHitComponents) {
    this.collisionComponents = onHitComponents;
  }

  public ComponentSupplier getCollisionComponents() {
    return collisionComponents;
  }

  public void setCollisionComponents(
      ComponentSupplier collisionComponents) {
    this.collisionComponents = collisionComponents;
  }
}
