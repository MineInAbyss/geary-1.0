package com.mineinabyss.geary.ecs.system.systems;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;

import com.google.common.collect.ImmutableSet;
import com.mineinabyss.geary.ecs.GearyEngine;
import com.mineinabyss.geary.ecs.component.Component;
import com.mineinabyss.geary.ecs.component.components.ProjectileHitComponents;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.entity.GearyEntityFactory;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.junit.Before;
import org.junit.Test;

public class ProjectileHitComponentsCollisionSystemTest {

  private GearyEngine gearyEngine;
  private GearyEntityFactory gearyEntityFactory;
  private ProjectileCollisionSystem projectileCollisionSystem;

  @Before
  public void setUp() throws Exception {
    gearyEngine = mock(GearyEngine.class);

    gearyEntityFactory = new GearyEntityFactory();
    projectileCollisionSystem = new ProjectileCollisionSystem();
    projectileCollisionSystem.setEngine(gearyEngine);
  }

  @Test
  public void applyCollisionComponents() {
    GearyEntity gearyEntity = gearyEntityFactory
        .createEntity(mock(Entity.class), UUID.randomUUID());
    gearyEntity.addComponent(
        new ProjectileHitComponents(
            () -> ImmutableSet.of(new TestComponent1(), new TestComponent2())));

    projectileCollisionSystem.update(gearyEntity);

    assertThat(gearyEntity.hasComponent(ProjectileHitComponents.class)).isFalse();
    assertThat(gearyEntity.hasComponent(TestComponent1.class)).isTrue();
    assertThat(gearyEntity.hasComponent(TestComponent2.class)).isTrue();
  }

  private static class TestComponent1 implements Component {

  }

  private static class TestComponent2 implements Component {

  }
}