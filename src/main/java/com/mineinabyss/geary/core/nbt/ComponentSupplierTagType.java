package com.mineinabyss.geary.core.nbt;

import com.google.common.collect.ImmutableSet;
import com.mineinabyss.geary.ecs.component.Component;
import com.mineinabyss.geary.ecs.component.ComponentSupplier;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ComponentSupplierTagType implements
    PersistentDataType<PersistentDataContainer, ComponentSupplier> {

  private final NamespacedKey componentsKey;
  private final ComponentArrayTagType componentArrayTagType;

  public ComponentSupplierTagType(NamespacedKey componentsKey,
      ComponentArrayTagType componentArrayTagType) {
    this.componentsKey = componentsKey;
    this.componentArrayTagType = componentArrayTagType;
  }

  @Override
  public Class<PersistentDataContainer> getPrimitiveType() {
    return PersistentDataContainer.class;
  }

  @Override
  public Class<ComponentSupplier> getComplexType() {
    return ComponentSupplier.class;
  }

  @Override
  public PersistentDataContainer toPrimitive(ComponentSupplier componentSupplier,
      PersistentDataAdapterContext persistentDataAdapterContext) {

    PersistentDataContainer container = persistentDataAdapterContext.newPersistentDataContainer();

    Component[] components = componentSupplier.get().toArray(new Component[0]);

    container.set(componentsKey, componentArrayTagType, components);

    return container;
  }

  @Override
  public ComponentSupplier fromPrimitive(PersistentDataContainer persistentDataContainer,
      PersistentDataAdapterContext persistentDataAdapterContext) {

    // TODO if the container is mutable, this could break things?
    return () -> ImmutableSet
        .copyOf(persistentDataContainer.get(componentsKey, componentArrayTagType));
  }
}
