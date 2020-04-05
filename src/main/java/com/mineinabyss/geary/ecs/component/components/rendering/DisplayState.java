package com.mineinabyss.geary.ecs.component.components.rendering;

import com.mineinabyss.geary.ecs.component.Component;

/**
 * Information about what model to display for an item or entity.
 */
public class DisplayState implements Component {

  private int modelNo;

  public DisplayState() {
  }

  public DisplayState(int modelNo) {
    this.modelNo = modelNo;
  }

  public int getModelNo() {
    return modelNo;
  }

  public void setModelNo(int modelNo) {
    this.modelNo = modelNo;
  }
}
