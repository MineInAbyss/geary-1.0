package com.mineinabyss.geary.ecs.components.equipment;

import com.badlogic.ashley.core.Component;

public class Durability implements Component {

  private int maxUses;
  private int consumeOnUse;
  private int currentUses;

  public Durability(int maxUses, int consumeOnUse) {
    this.maxUses = maxUses;
    this.currentUses = maxUses;
    this.consumeOnUse = consumeOnUse;
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

  public int getConsumeOnUse() {
    return consumeOnUse;
  }
}
