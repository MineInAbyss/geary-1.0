package com.mineinabyss.geary.core.nbt;

import com.google.common.base.Splitter;
import java.util.stream.StreamSupport;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class ClassNameArrayType implements
    PersistentDataType<String, String[]> {

  Splitter commaSplitter = Splitter.on(",").omitEmptyStrings();

  @Override
  public Class<String> getPrimitiveType() {
    return String.class;
  }

  @Override
  public Class<String[]> getComplexType() {
    return String[].class;
  }

  @Override
  public String toPrimitive(String[] classNames,
      PersistentDataAdapterContext persistentDataAdapterContext) {
    return String.join(",", classNames);
  }

  @Override
  public String[] fromPrimitive(String s,
      PersistentDataAdapterContext persistentDataAdapterContext) {
    return StreamSupport
        .stream(commaSplitter.split(s).spliterator(), false)
        .toArray(String[]::new);
  }
}
