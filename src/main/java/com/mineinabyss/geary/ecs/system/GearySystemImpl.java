package com.mineinabyss.geary.ecs.system;

import com.mineinabyss.geary.ecs.Family;
import com.mineinabyss.geary.ecs.GearyEngine;

public abstract class GearySystemImpl implements GearySystem {

  protected Family family;
  private GearyEngine engine;

  public GearySystemImpl(Family family) {
    this.family = family;
  }

  public final Family getFamily() {
    return family;
  }

  public final void setEngine(GearyEngine gearyEngine) {
    this.engine = gearyEngine;
  }

  @Override
  public GearyEngine getEngine() {
    return engine;
  }
}
