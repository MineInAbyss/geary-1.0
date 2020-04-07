package com.mineinabyss.geary.ecs.system.systems;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;

import com.mineinabyss.geary.ecs.GearyEngine;
import com.mineinabyss.geary.ecs.component.components.Remove;
import com.mineinabyss.geary.ecs.component.components.equipment.Degrading;
import com.mineinabyss.geary.ecs.component.components.equipment.Durability;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.entity.GearyEntityFactory;
import org.bukkit.entity.Entity;
import org.junit.Before;
import org.junit.Test;

public class DegredationSystemTest {

  private GearyEngine gearyEngine;
  private GearyEntityFactory gearyEntityFactory;
  private DegredationSystem degredationSystem;

  @Before
  public void setUp() throws Exception {
    gearyEngine = mock(GearyEngine.class);

    gearyEntityFactory = new GearyEntityFactory();
    degredationSystem = new DegredationSystem();
    degredationSystem.setEngine(gearyEngine);
  }

  @Test
  public void degradeEntity() {
    GearyEntity gearyEntity = gearyEntityFactory.createEntity(mock(Entity.class));
    Durability durability = new Durability(3);
    gearyEntity.addComponent(durability);
    gearyEntity.addComponent(new Degrading());

    degredationSystem.update(gearyEntity);

    assertThat(gearyEntity.hasComponent(Degrading.class)).isFalse();
    assertThat(durability.getCurrentUses()).isEqualTo(2);
    assertThat(gearyEntity.hasComponent(Remove.class)).isFalse();
  }

  @Test
  public void markEntityForRemoval() {
    GearyEntity gearyEntity = gearyEntityFactory.createEntity(mock(Entity.class));
    Durability durability = new Durability(1);
    gearyEntity.addComponent(durability);
    gearyEntity.addComponent(new Degrading());

    degredationSystem.update(gearyEntity);

    assertThat(gearyEntity.hasComponent(Degrading.class)).isFalse();
    assertThat(durability.getCurrentUses()).isEqualTo(0);
    assertThat(gearyEntity.hasComponent(Remove.class));
  }
}