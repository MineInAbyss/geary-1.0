package com.mineinabyss.geary.core.nbt;

import org.bukkit.Color;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class ColorType implements PersistentDataType<Integer, Color> {

  @Override
  public Class<Integer> getPrimitiveType() {
    return Integer.class;
  }

  @Override
  public Class<Color> getComplexType() {
    return Color.class;
  }

  @Override
  public Integer toPrimitive(Color color,
      PersistentDataAdapterContext persistentDataAdapterContext) {
    return color.asRGB();
  }

  @Override
  public Color fromPrimitive(Integer integer,
      PersistentDataAdapterContext persistentDataAdapterContext) {
    return Color.fromRGB(integer);
  }
}
