package com.mineinabyss.geary.ecs.system.systems.tools;

import com.google.common.collect.ImmutableSet;
import com.mineinabyss.geary.ecs.Family;
import com.mineinabyss.geary.ecs.component.components.ProjectileHitComponents;
import com.mineinabyss.geary.ecs.component.components.Rope;
import com.mineinabyss.geary.ecs.component.components.control.Activated;
import com.mineinabyss.geary.ecs.component.components.effect.PullToLocation;
import com.mineinabyss.geary.ecs.component.components.grappling.GrapplingHook;
import com.mineinabyss.geary.ecs.component.components.grappling.GrapplingHookExtended;
import com.mineinabyss.geary.ecs.component.components.rendering.DisplayState;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.system.IteratingSystem;
import com.mineinabyss.geary.ecs.system.systems.ProjectileLaunchingSubSystem;
import java.util.Optional;
import org.bukkit.entity.Player;

/**
 * Extends grappling hooks.
 */
public class GrapplingHookExtendingSystem extends IteratingSystem {

  private final ProjectileLaunchingSubSystem projectileLaunchingSubSystem;

  public GrapplingHookExtendingSystem(ProjectileLaunchingSubSystem projectileLaunchingSubSystem) {
    super(Family.builder().setAll(ImmutableSet.of(GrapplingHook.class, Activated.class))
        .setNone(GrapplingHookExtended.class).build());
    this.projectileLaunchingSubSystem = projectileLaunchingSubSystem;
  }

  @Override
  protected void update(GearyEntity gearyEntity) {
    Optional<Player> player = gearyEntity.getHoldingPlayer();

    if (player.isPresent()) {
      Player owner = player.get();
      GrapplingHook grapplingHook = gearyEntity.getComponent(GrapplingHook.class);

      GearyEntity launchedEntity = projectileLaunchingSubSystem
          .launchProjectile(grapplingHook.getFiringSpeed(), grapplingHook.getHookModel(), owner);

      getEngine().addEntity(launchedEntity);

      ProjectileHitComponents projectileHitComponents = launchedEntity.getComponent(
          ProjectileHitComponents.class);

      launchedEntity.addComponent(new Rope(gearyEntity, launchedEntity, grapplingHook.getColor()));

      projectileHitComponents
          .setCollisionComponents(() -> ImmutableSet.of(new PullToLocation(gearyEntity)));

      if (gearyEntity.hasComponent(DisplayState.class)) {
        gearyEntity.getComponent(DisplayState.class).setModelNo(grapplingHook.getExtendedModel());
      }

      gearyEntity.addComponent(new GrapplingHookExtended(launchedEntity));

      gearyEntity.removeComponent(Activated.class);
    }
  }
}
