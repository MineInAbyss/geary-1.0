package com.mineinabyss.geary.ecs.system;

import com.mineinabyss.geary.ecs.Family;
import com.mineinabyss.geary.ecs.GearyEngine;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import java.util.Collection;

public interface GearySystem {

  Family getFamily();

  void update(Collection<GearyEntity> gearyEntities);

  GearyEngine getEngine();
}
