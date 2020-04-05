package com.mineinabyss.geary.ecs;

import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.system.GearySystem;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.plugin.Plugin;

public interface GearyEngine {

  void update();

  void addSystem(GearySystem gearySystem, Plugin plugin);

  Optional<GearyEntity> getEntity(UUID uuid);

  void addEntity(GearyEntity gearyEntity);

  void removeEntity(GearyEntity gearyEntity);
}
