package com.mineinabyss.geary.ecs.components.control;


import com.badlogic.ashley.core.Component;

public class RetainOriginalProperties implements Component {

  private int durabilityReduction;

  public RetainOriginalProperties(int durabilityReduction) {
    this.durabilityReduction = durabilityReduction;
  }

  public int getDurabilityReduction() {
    return durabilityReduction;
  }
}
