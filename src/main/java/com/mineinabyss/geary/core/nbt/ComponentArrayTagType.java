package com.mineinabyss.geary.core.nbt;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.mineinabyss.geary.ecs.component.Component;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * PersistentDataType used to persist/read arrays of components.
 */
public class ComponentArrayTagType implements
    PersistentDataType<PersistentDataContainer, Component[]> {

  private static final ClassNameArrayType CLASS_NAME_ARRAY_TYPE = new ClassNameArrayType();
  private final NamespacedKey componentsDataKey;
  private final NamespacedKey componentKeyListKey;
  private final NamespacedKey componentsKey;
  private final Function<String, NamespacedKey> keyCreator;
  private final Logger logger;

  public ComponentArrayTagType(NamespacedKey componentsDataKey, NamespacedKey componentKeyListKey,
      NamespacedKey componentsKey,
      Function<String, NamespacedKey> keyCreator,
      Logger logger) {

    this.componentsDataKey = componentsDataKey;
    this.componentKeyListKey = componentKeyListKey;
    this.componentsKey = componentsKey;
    this.keyCreator = keyCreator;
    this.logger = logger;
  }

  @Override
  public Class<PersistentDataContainer> getPrimitiveType() {
    return PersistentDataContainer.class;
  }

  @Override
  public Class<Component[]> getComplexType() {
    return Component[].class;
  }

  @Override
  public PersistentDataContainer toPrimitive(Component[] components,
      PersistentDataAdapterContext persistentDataAdapterContext) {
    Stream<String> knownClasses = Arrays.stream(components)
        .filter(component -> !(component instanceof PlaceholderComponent))
        .map(Object::getClass)
        .map(Class::getCanonicalName);
    Stream<String> placeholderClasses = Arrays.stream(components)
        .filter(component -> (component instanceof PlaceholderComponent))
        .map(component -> (PlaceholderComponent) component)
        .map(PlaceholderComponent::getClassName);

    String[] classNames = Streams.concat(knownClasses, placeholderClasses).toArray(String[]::new);

    PersistentDataContainer rootContainer = persistentDataAdapterContext
        .newPersistentDataContainer();

    PersistentDataContainer componentsContainer = persistentDataAdapterContext
        .newPersistentDataContainer();

    rootContainer.set(componentKeyListKey, CLASS_NAME_ARRAY_TYPE, classNames);

    for (Component component : components) {
      if (!(component instanceof PlaceholderComponent)) {
        componentsContainer.set(keyCreator.apply(component.getClass().getCanonicalName()),
            new ComponentTagType(component.getClass(), keyCreator, componentsKey, this), component);
      } else {
        PlaceholderComponent placeHolderComponent = (PlaceholderComponent) component;
        componentsContainer
            .set(keyCreator.apply(placeHolderComponent.getClassName()), TAG_CONTAINER,
                placeHolderComponent.getData());
      }
    }

    rootContainer.set(componentsDataKey, TAG_CONTAINER, componentsContainer);

    return rootContainer;
  }

  @Override
  public Component[] fromPrimitive(PersistentDataContainer container,
      PersistentDataAdapterContext persistentDataAdapterContext) {
    String[] classNames = container.get(componentKeyListKey, CLASS_NAME_ARRAY_TYPE);

    if (classNames == null) {
      logger.warning("No component list set, skipping.");
      return new Component[0];
    }

    ContainedTypes classes = getClasses(classNames);
    PersistentDataContainer componentsContainer = container.get(componentsDataKey, TAG_CONTAINER);

    Stream<Component> knownComponents = classes.getComponents().stream()
        .map(clazz -> componentsContainer
            .get(keyCreator.apply(clazz.getCanonicalName()),
                new ComponentTagType(clazz, keyCreator, componentsKey, this)));

    Stream<Component> placeholderComponents = classes.getUnknownTypes().stream()
        .map(className -> new PlaceholderComponent(className, componentsContainer
            .get(keyCreator.apply(className),
                TAG_CONTAINER)));

    return Streams.concat(knownComponents, placeholderComponents).toArray(Component[]::new);
  }

  // Classes contained within a component array are assumed to be subclassing component.
  @SuppressWarnings("unchecked")
  private ContainedTypes getClasses(String[] classNames) {
    ImmutableSet.Builder<Class<? extends Component>> classes = ImmutableSet.builder();
    ImmutableSet.Builder<String> unknownTypes = ImmutableSet.builder();

    for (String className : classNames) {
      try {
        Class<?> clazz = Class.forName(className);

        if (Component.class.isAssignableFrom(clazz)) {
          classes.add((Class<? extends Component>) clazz);
        }
      } catch (ClassNotFoundException e) {
        unknownTypes.add(className);
      }
    }

    return new ContainedTypes(classes.build(), unknownTypes.build());
  }

  private static class ContainedTypes {

    private final ImmutableSet<Class<? extends Component>> components;
    private ImmutableSet<String> unknownTypes;

    public ContainedTypes(
        ImmutableSet<Class<? extends Component>> components,
        ImmutableSet<String> unknownTypes) {
      this.components = components;
      this.unknownTypes = unknownTypes;
    }

    public ImmutableSet<Class<? extends Component>> getComponents() {
      return components;
    }

    public ImmutableSet<String> getUnknownTypes() {
      return unknownTypes;
    }
  }

  public static class PlaceholderComponent implements Component {

    private String className;
    private PersistentDataContainer data;

    public PlaceholderComponent(String className, PersistentDataContainer data) {
      this.className = className;
      this.data = data;
    }

    public String getClassName() {
      return className;
    }

    public PersistentDataContainer getData() {
      return data;
    }
  }
}
