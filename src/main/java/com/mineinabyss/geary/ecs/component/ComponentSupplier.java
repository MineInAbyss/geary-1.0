package com.mineinabyss.geary.ecs.component;

import com.mineinabyss.geary.ecs.system.GearySystem;
import java.io.Serializable;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Supplier that supplies sets of components to be used in entities. When a {@link GearySystem}
 * attaches new components to an entity, this supplier should be used if the system expects fresh
 * components each time.
 */
public interface ComponentSupplier extends Serializable, Supplier<Set<Component>> {

}
