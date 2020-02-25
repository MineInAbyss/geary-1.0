package com.mineinabyss.geary;

import com.badlogic.ashley.core.Entity;
import com.mineinabyss.geary.ecs.components.control.ActivateOnPhysicalHit;
import com.mineinabyss.geary.ecs.components.control.RetainOriginalProperties;
import com.mineinabyss.geary.ecs.components.effect.Explosion;
import com.mineinabyss.geary.ecs.components.equipment.Durability;
import com.mineinabyss.geary.ecs.components.equipment.Equippable;
import com.mineinabyss.geary.ecs.components.grappling.GrapplingHook;
import com.mineinabyss.geary.ecs.components.rendering.DisplayState;
import org.bukkit.Color;

public class PredefinedArtifacts {

  public static Entity createGrapplingHook(double speedo, int staticModel, int firingModel,
      Color color, int hookModel, int maxUses) {
    return new Entity()
        .add(new GrapplingHook(speedo, staticModel, firingModel, color, hookModel))
        .add(new DisplayState(staticModel))
        .add(new Durability(maxUses, 1))
        .add(new Equippable());
  }

  public static Entity createBlazeReap() {
    return new Entity()
        .add(new Explosion(2, false, true))
        .add(new ActivateOnPhysicalHit())
        .add(new Durability(1000, 10))
        .add(new Equippable());
  }
}
