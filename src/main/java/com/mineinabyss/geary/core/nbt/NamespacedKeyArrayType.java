package com.mineinabyss.geary.core.nbt;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class NamespacedKeyArrayType implements
    PersistentDataType<String, NamespacedKey[]> {

  Splitter commaSplitter = Splitter.on(",").omitEmptyStrings();
  Splitter colonSplitter = Splitter.on(":");

  public static final NamespacedKeyArrayType NAMESPACED_KEY_LIST_TYPE = new NamespacedKeyArrayType();

  @Override
  public Class<String> getPrimitiveType() {
    return String.class;
  }

  @Override
  public Class<NamespacedKey[]> getComplexType() {
    return NamespacedKey[].class;
  }
//  @Override
//  public Class<? super Set<NamespacedKey>> getComplexType() {
//    return new TypeToken<Set<NamespacedKey>>(){}.getRawType();
//  }

  @Override
  public String toPrimitive(NamespacedKey[] namespacedKeys,
      PersistentDataAdapterContext persistentDataAdapterContext) {
    return Arrays.stream(namespacedKeys).map(NamespacedKey::toString)
        .collect(Collectors.joining(","));
  }

  @Override
  public NamespacedKey[] fromPrimitive(String s,
      PersistentDataAdapterContext persistentDataAdapterContext) {
    ImmutableSet<NamespacedKey> keySet = StreamSupport
        .stream(commaSplitter.split(s).spliterator(), false)
        .map(this::toKey)
        .collect(toImmutableSet());

    return keySet.toArray(new NamespacedKey[0]);
  }

  @SuppressWarnings("deprecation")
  private NamespacedKey toKey(String s) {
    Iterator<String> key = colonSplitter.split(s).iterator();
    return new NamespacedKey(key.next(), key.next());
  }
}
