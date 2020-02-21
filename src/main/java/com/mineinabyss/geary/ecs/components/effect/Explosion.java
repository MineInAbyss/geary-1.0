package com.mineinabyss.geary.ecs.components.effect;

import com.badlogic.ashley.core.Component;

/**
 * Creates an explosion effect at the target.
 */
public class Explosion implements Component {

  public enum Size {
    SMALL,
    MEDIUM,
    LARGE
  }

  private Size size;

  public Explosion(Size size) {
    this.size = size;
  }

  public Size getSize() {
    return size;
  }
}
