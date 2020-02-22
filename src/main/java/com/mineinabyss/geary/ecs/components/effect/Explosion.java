package com.mineinabyss.geary.ecs.components.effect;

import com.badlogic.ashley.core.Component;

/**
 * Creates an explosion effect at the target.
 */
public class Explosion implements Component {
  private final float power;
  private final boolean setFire;
  private final boolean breakBlocks;

  public Explosion(float power, boolean setFire, boolean breakBlocks) {
    this.power = power;
    this.setFire = setFire;
    this.breakBlocks = breakBlocks;
  }

  public float getPower() {
    return power;
  }

  public boolean isSetFire() {
    return setFire;
  }

  public boolean isBreakBlocks() {
    return breakBlocks;
  }
}
