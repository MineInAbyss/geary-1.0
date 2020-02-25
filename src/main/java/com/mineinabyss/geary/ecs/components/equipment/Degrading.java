package com.mineinabyss.geary.ecs.components.equipment;

import com.badlogic.ashley.core.Component;

public class Degrading implements Component {

  private int amount;

  public Degrading() {
    this(1);
  }

  public Degrading(int amount) {
    this.amount = amount;
  }

  public int getAmount() {
    return amount;
  }
}
