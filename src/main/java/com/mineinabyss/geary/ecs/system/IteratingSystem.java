package com.mineinabyss.geary.ecs.system;

import com.mineinabyss.geary.ecs.Family;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import java.util.Collection;

public abstract class IteratingSystem extends GearySystemImpl {

  public IteratingSystem(Family family) {
    super(family);
  }

  protected abstract void update(GearyEntity gearyEntity);

  @Override
  public final void update(Collection<GearyEntity> gearyEntities) {
    gearyEntities.forEach(this::update);
  }

}
