package com.mineinabyss.geary.ecs.system.systems.rendering;

import com.mineinabyss.geary.ecs.Family;
import com.mineinabyss.geary.ecs.component.components.Rope;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.system.IteratingSystem;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * System that draws ropes using particles.
 */
public class RopeDisplaySystem extends IteratingSystem {


  public RopeDisplaySystem() {
    super(Family.builder().setAll(Rope.class).build());
  }

  @Override
  protected void update(GearyEntity gearyEntity) {
    Rope rope = gearyEntity.getComponent(Rope.class);

    Optional<GearyEntity> startEntity = getEngine().getEntity(rope.getStart());
    Optional<GearyEntity> endEntity = getEngine().getEntity(rope.getEnd());

    if (startEntity.isPresent() && endEntity.isPresent()) {

      Location start = getEntityLocation(startEntity.get());
      Location end = getEntityLocation(endEntity.get());

      Vector step = end.toVector().subtract(start.toVector()).normalize().multiply(.1);
      Vector drawLocation = start.toVector();

      do {
        start.getWorld().spawnParticle(Particle.REDSTONE, drawLocation.getX(), drawLocation.getY(),
            drawLocation.getZ(), 1,
            new DustOptions(rope.getColor(), .2f));
        drawLocation.add(step);
      } while (drawLocation.distance(end.toVector()) > step.length());
    }
  }

  private Location getEntityLocation(GearyEntity gearyEntity) {
    if (gearyEntity.getHoldingPlayer().isPresent()) {
      return gearyEntity.getHoldingPlayer().get().getEyeLocation();
    } else if (gearyEntity.getDataHolder() instanceof Entity) {
      return ((Entity) gearyEntity.getDataHolder()).getLocation();
    } else {
      throw new RuntimeException("Must have an associated entity!");
    }
  }
}
