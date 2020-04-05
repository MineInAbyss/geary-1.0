package com.mineinabyss.geary.ecs.system.systems.tools;

import com.google.common.collect.ImmutableSet;
import com.mineinabyss.geary.ecs.Family;
import com.mineinabyss.geary.ecs.component.components.ProjectileHitComponents;
import com.mineinabyss.geary.ecs.component.components.control.Activated;
import com.mineinabyss.geary.ecs.component.components.effect.PullToLocation;
import com.mineinabyss.geary.ecs.component.components.equipment.Degrading;
import com.mineinabyss.geary.ecs.component.components.equipment.Durability;
import com.mineinabyss.geary.ecs.component.components.grappling.GrapplingHook;
import com.mineinabyss.geary.ecs.component.components.grappling.GrapplingHookExtended;
import com.mineinabyss.geary.ecs.component.components.rendering.DisplayState;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.system.IteratingSystem;
import java.util.Optional;
import org.bukkit.entity.Entity;

/**
 * Disconnects grappling hooks from the ground.
 */
public class GrapplingHookDisconnectingSystem extends IteratingSystem {

  public GrapplingHookDisconnectingSystem() {
    super(Family.builder().setAll(ImmutableSet.of(GrapplingHook.class, GrapplingHookExtended.class))
        .build());
  }

  @Override
  protected void update(GearyEntity gearyEntity) {

    GrapplingHookExtended grapplingHookExtended = gearyEntity
        .getComponent(GrapplingHookExtended.class);
    Optional<GearyEntity> projectileEntity = getEngine()
        .getEntity(grapplingHookExtended.getExtendedEntity());

    if (projectileEntity.isPresent() && shouldRemove(gearyEntity, projectileEntity.get())) {
      GrapplingHook grapplingHook = gearyEntity.getComponent(GrapplingHook.class);

      if (projectileEntity.get().getDataHolder() instanceof Entity) {
        ((Entity) projectileEntity.get().getDataHolder()).remove();
      }

      // Since there was not an associated projectile, the hook made contact
      if (!(projectileEntity.get().getDataHolder() instanceof ProjectileHitComponents)) {
        if (gearyEntity.hasComponent(Durability.class)) {
          gearyEntity.addComponent(new Degrading());
        }
      }

      getEngine().removeEntity(projectileEntity.get());

      if (gearyEntity.hasComponent(DisplayState.class)) {
        gearyEntity.getComponent(DisplayState.class).setModelNo(grapplingHook.getStaticModel());
      }

      gearyEntity.removeComponent(GrapplingHookExtended.class);
      gearyEntity.removeComponent(Activated.class);
    }

    if (!projectileEntity.isPresent()) {
      gearyEntity.removeComponent(GrapplingHookExtended.class);
      gearyEntity.removeComponent(Activated.class);
    }
  }


  private boolean shouldRemove(GearyEntity entity, GearyEntity extendedEntity) {
    return entity.hasComponent(Activated.class) || isFinishedPulling(extendedEntity);
  }

  private boolean isFinishedPulling(GearyEntity extendedEntity) {
    return !extendedEntity.hasComponent(PullToLocation.class) && !extendedEntity
        .hasComponent(ProjectileHitComponents.class);
  }
}
