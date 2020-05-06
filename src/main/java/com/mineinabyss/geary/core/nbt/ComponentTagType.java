package com.mineinabyss.geary.core.nbt;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Primitives;
import com.mineinabyss.geary.ecs.component.Component;
import com.mineinabyss.geary.ecs.component.ComponentSupplier;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ComponentTagType implements
    PersistentDataType<PersistentDataContainer, Component> {

  private final Map<Class<?>, PersistentDataType<?, ?>> CLASS_TO_DATATYPE = new HashMap<>();

  private static final ImmutableMap<Class<?>, PersistentDataType<?, ?>> ARRAY_CLASS_TO_DATA_TYPE = ImmutableMap
      .<Class<?>, PersistentDataType<?, ?>>builder()
      .put(Integer.class, PersistentDataType.INTEGER_ARRAY)
      .put(Byte.class, PersistentDataType.BYTE_ARRAY)
      .put(Long.class, PersistentDataType.LONG_ARRAY)
      .build();

  private final Class<? extends Component> clazz;
  private final Function<String, NamespacedKey> keyCreator;
  private final ImmutableList<Field> fields;


  public ComponentTagType(Class<? extends Component> clazz,
      Function<String, NamespacedKey> keyCreator, NamespacedKey componentsKey,
      ComponentArrayTagType componentArrayTagType) {
    this.clazz = clazz;
    this.keyCreator = keyCreator;
    fields = Arrays.stream(clazz.getDeclaredFields()).collect(toImmutableList());

    CLASS_TO_DATATYPE.put(String.class, PersistentDataType.STRING);
    CLASS_TO_DATATYPE.put(Integer.class, PersistentDataType.INTEGER);
    CLASS_TO_DATATYPE.put(Byte.class, PersistentDataType.BYTE);
    CLASS_TO_DATATYPE.put(Double.class, PersistentDataType.DOUBLE);
    CLASS_TO_DATATYPE.put(Float.class, PersistentDataType.FLOAT);
    CLASS_TO_DATATYPE.put(Long.class, PersistentDataType.LONG);
    CLASS_TO_DATATYPE.put(UUID.class, new UUIDType());
    CLASS_TO_DATATYPE.put(Color.class, new ColorType());
    CLASS_TO_DATATYPE.put(ComponentSupplier.class,
        new ComponentSupplierTagType(componentsKey, componentArrayTagType));

  }

  @Override
  public Class<PersistentDataContainer> getPrimitiveType() {
    return PersistentDataContainer.class;
  }

  @Override
  public Class<Component> getComplexType() {
    return Component.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PersistentDataContainer toPrimitive(Component component,
      PersistentDataAdapterContext persistentDataAdapterContext) {

    PersistentDataContainer
        container = persistentDataAdapterContext.newPersistentDataContainer();

    fields.forEach(field -> {
      Optional<PersistentDataType<?, ?>> dataTypeForType = getDataTypeForType(field.getType());
      if (dataTypeForType.isPresent()) {
        try {
          field.setAccessible(true);
          field.getType().isPrimitive();

          Object object = field.get(component);

          container.set(keyCreator.apply(field.getName()),
              (PersistentDataType<?, ? super Object>) dataTypeForType.get(),
              object);

        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (Exception e) {
          throw new RuntimeException(
              String.format("%s could not be parsed", field.getName()), e);
        }
      } else {
        throw new RuntimeException(
            String.format("%s is not a valid datatype", field.getType().getCanonicalName()));
      }
    });

    return container;
  }

  private Optional<PersistentDataType<?, ?>> getDataTypeForType(Class<?> type) {
    Class<?> primitiveType = type.isArray() ? type.getComponentType() : type;
    Class<?> boxedNonArrayType = Primitives.wrap(primitiveType);

    if (type.isArray()) {
      return Optional.ofNullable(ARRAY_CLASS_TO_DATA_TYPE.get(boxedNonArrayType));
    } else {
      return Optional.ofNullable(CLASS_TO_DATATYPE.get(boxedNonArrayType));
    }
  }

  @Override
  public Component fromPrimitive(PersistentDataContainer container,
      PersistentDataAdapterContext persistentDataAdapterContext) {

    Component component = null;
    try {
      component = clazz.newInstance();

      for (Field field : fields) {
        field.setAccessible(true);
        NamespacedKey key = keyCreator.apply(field.getName());
        Object object = container.get(key, getDataTypeForType(field.getType()).get());
        field.set(component, object);
      }

    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return Optional.ofNullable(component).orElseThrow(RuntimeException::new);
  }
}
