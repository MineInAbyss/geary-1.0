package com.mineinabyss.geary.ecs.component.components.grappling;

import com.mineinabyss.geary.ecs.component.Component;
import org.bukkit.Color;

/**
 * Represents an item that can be used to grapple.
 */
public class GrapplingHook implements Component {

  private double firingSpeed;
  private int staticModel;
  private int extendedModel;
  private int hookModel;
  private Color color;

  public GrapplingHook() {
  }

  public GrapplingHook(double firingSpeed, int staticModel, int extendedModel,
      Color color, int hookModel) {
    this.firingSpeed = firingSpeed;
    this.staticModel = staticModel;
    this.extendedModel = extendedModel;
    this.hookModel = hookModel;
    this.color = color;
  }

  /**
   * The speed at which the hook projectile is fired.
   */
  public double getFiringSpeed() {
    return firingSpeed;
  }

  /**
   * The model to show when the grapple is not in use.
   */
  public int getStaticModel() {
    return staticModel;
  }

  /**
   * The model to show when the grapple is extended.
   */
  public int getExtendedModel() {
    return extendedModel;
  }

  /**
   * The color of the rope this grapple uses when extended.
   */
  public Color getColor() {
    return color;
  }

  /**
   * The model to show for the hook when fired.
   */
  public int getHookModel() {
    return hookModel;
  }
}
