package com.mineinabyss.geary.ecs.system.systems.movement;

import com.mineinabyss.geary.ecs.Family;
import com.mineinabyss.geary.ecs.component.components.effect.PullToLocation;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.system.IteratingSystem;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

/**
 * System that pulls MC entities around based on associated ECS entities.
 */
public class EntityPullingSystem extends IteratingSystem {

  private static final double MAX_SPEED = 1.5;

  public EntityPullingSystem() {
    super(Family.builder().setAll(PullToLocation.class).build());
  }

  @Override
  protected void update(GearyEntity gearyEntity) {
    PersistentDataHolder dataHolder = gearyEntity.getDataHolder();

    if (dataHolder instanceof Entity) {
      Location target = ((Entity) dataHolder).getLocation();

      Optional<GearyEntity> entityToPull = getEngine()
          .getEntity(gearyEntity.getComponent(PullToLocation.class).getEntityToPull());

      if (entityToPull.isPresent()) {
        if (target.getBlock().getRelative(BlockFace.UP).isPassable()) {
          target = target.getBlock().getLocation().add(.5, 1, .5);
        }
      }

      target.add(0, 1, 0);

      Player who = entityToPull.get().getHoldingPlayer().get();
      Location from = who.getLocation();

      double distance = from.distance(target);

      double speed = Math.max(.4, Math.min(MAX_SPEED, distance / 10.0));

      Vector normalize = target.toVector().subtract(from.toVector()).normalize();
      BoundingBox newbb = who.getBoundingBox().shift(normalize);

      boolean collides = false;
      for (int x = 0; x < newbb.getWidthX(); x++) {
        for (int y = 0; y < newbb.getHeight(); y++) {
          for (int z = 0; z < newbb.getWidthZ(); z++) {
            if (y >= 0 && y <= 255) {
              Vector vector = newbb.getMin().clone().add(new Vector(x, y, z));
              collides |= !from.getWorld().getBlockAt(vector.toLocation(from.getWorld()))
                  .isPassable();
            }
          }
        }
      }

      if (distance > 1 && !collides) {
        Vector velocity = normalize.multiply(speed);

        who.setVelocity(velocity);
      } else {
//        gearyEntity.setComponent(new Degrading());
        gearyEntity.removeComponent(PullToLocation.class);
      }
    }
  }
}
