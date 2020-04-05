package com.mineinabyss.geary.ecs.component.components;

import com.mineinabyss.geary.ecs.component.Component;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import java.util.UUID;
import org.bukkit.Color;

/**
 * Component that represents a rope.
 */
public class Rope implements Component {

  private UUID start;
  private UUID end;
  private Color color;

  public Rope() {
  }

  public Rope(GearyEntity start, GearyEntity end, Color color) {
    this.start = start.getUUID();
    this.end = end.getUUID();
    this.color = color;
  }

  public UUID getStart() {
    return start;
  }

  public UUID getEnd() {
    return end;
  }

  public Color getColor() {
    return color;
  }
}
