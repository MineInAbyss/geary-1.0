package com.mineinabyss.geary.ecs.component.components.equipment;

import com.mineinabyss.geary.ecs.component.Component;

/**
 * Stores an entities current degredation levels, as well as maximum uses.
 */
public class Durability implements Component {

  private int maxUses;
  private int currentUses;

  public Durability() {
  }

  public Durability(int maxUses) {
    this.maxUses = maxUses;
    this.currentUses = maxUses;
  }

  public int getMaxUses() {
    return maxUses;
  }

  public int getCurrentUses() {
    return currentUses;
  }

  public void setCurrentUses(int currentUses) {
    this.currentUses = currentUses;
  }
}
