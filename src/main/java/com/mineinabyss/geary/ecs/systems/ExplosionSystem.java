package com.mineinabyss.geary.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mineinabyss.geary.ecs.components.Actor;
import com.mineinabyss.geary.ecs.components.Position;
import com.mineinabyss.geary.ecs.components.control.Activated;
import com.mineinabyss.geary.ecs.components.effect.Explosion;
import com.mineinabyss.geary.ecs.components.equipment.Degrading;
import org.bukkit.Location;

public class ExplosionSystem extends IteratingSystem {

  ComponentMapper<Explosion> explosionComponentMapper = ComponentMapper.getFor(Explosion.class);
  ComponentMapper<Position> positionComponentMapper = ComponentMapper.getFor(Position.class);
  ComponentMapper<Actor> actorComponentMapper = ComponentMapper.getFor(Actor.class);

  public ExplosionSystem() {
    super(Family.all(Explosion.class, Position.class, Activated.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    Location location = positionComponentMapper.get(entity).getLocation();
    Explosion explosion = explosionComponentMapper.get(entity);

    org.bukkit.entity.Entity actor =
        actorComponentMapper.has(entity) ? actorComponentMapper.get(entity).getActor() : null;

    location.getWorld().createExplosion(location, explosion.getPower(), explosion.isSetFire(),
        explosion.isBreakBlocks(), actor);

    entity.add(new Degrading());
    entity.remove(Activated.class);
  }
}
