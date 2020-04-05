package com.mineinabyss.geary.ecs;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import com.mineinabyss.geary.ecs.component.Component;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import java.util.Set;

/**
 * Represents a group of components that a system works on.
 */
@AutoValue
public abstract class Family {

  /**
   * Components that an entity must have in order to be considered part of this family.
   */
  public abstract ImmutableSet<Class<? extends Component>> all();

  /**
   * Components that an entity must have at least one of to be considered part of this family.
   */
  public abstract ImmutableSet<Class<? extends Component>> one();

  /**
   * Components that an entity must have none of to be considered part of this family.
   */
  public abstract ImmutableSet<Class<? extends Component>> none();

  public static Builder builder() {
    return new AutoValue_Family.Builder()
        .setAll(ImmutableSet.of())
        .setOne(ImmutableSet.of())
        .setNone(ImmutableSet.of());
  }

  @AutoValue.Builder
  public static abstract class Builder {

    public abstract Builder setAll(Set<Class<? extends Component>> keys);

    public Builder setAll(Class<? extends Component> key) {
      return setAll(ImmutableSet.of(key));
    }

    public abstract Builder setOne(Set<Class<? extends Component>> keys);

    public Builder setOne(Class<? extends Component> key) {
      return setOne(ImmutableSet.of(key));
    }

    public abstract Builder setNone(Set<Class<? extends Component>> keys);

    public Builder setNone(Class<? extends Component> key) {
      return setNone(ImmutableSet.of(key));
    }

    public abstract Family build();
  }

  public boolean matches(GearyEntity entity) {
    return all().stream().allMatch(entity::hasComponent)
        && (one().isEmpty() || one().stream().anyMatch(entity::hasComponent))
        && none().stream().noneMatch(entity::hasComponent);
  }
}
